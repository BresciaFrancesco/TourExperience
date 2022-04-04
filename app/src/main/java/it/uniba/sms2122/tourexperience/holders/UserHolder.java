package it.uniba.sms2122.tourexperience.holders;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.model.User;

public class UserHolder {
    private static final String URI = "https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app";
    private static final String USER_TABLE = "Users";

    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(URI);
    private static DatabaseReference reference;

    private static UserHolder instance;
    private Result result;

    private User user;

    /**
     * Interfacce per le lambda expression
     */
    public interface SuccessListener {
        void doSuccess();
    }

    public interface SuccessDataListener {
        void doSuccess(User user);
    }

    public interface FailureListener {
        void doFail();
    }

    /**
     * Classe per i risultati delle esecuzioni (forse è da cancellare)
     */
    public class Result {
        private boolean isSuccessful;
        private String resultError;

        public Result(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public Result() {
            isSuccessful = false;
        }

        public void setSuccessful(boolean isSuccessful) {
            this.isSuccessful = isSuccessful;
        }

        public void setSuccessful(boolean isSuccessful, String error) {
            this.isSuccessful = isSuccessful;
            this.resultError = error;
        }

        public String getError() {
            return resultError;
        }

        public boolean isSuccessful() {
            return isSuccessful;
        }
    }

    /**
     * Registrazione
     * @param email
     * @param password
     * @param name
     * @param surname
     * @param dateBirth
     * @param success
     * @param failure
     * @return
     */
    public void register(String email, String password, String name, String surname, String dateBirth, SuccessListener success, FailureListener failure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                    String userID = currentUser.getUid();

                    DatabaseReference actualReference = reference.child(userID);

                    actualReference.setValue(new User(email, name, surname, dateBirth)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> taskUserValueSet) {
                            if (taskUserValueSet.isSuccessful()) {
                                result.setSuccessful(true);
                                success.doSuccess();
                            } else {
                                firebaseAuth.getCurrentUser().delete();
                                failure.doFail();
                            }
                        }
                    });
                } else {
                    failure.doFail();
                }
            }
        });
    }

    /**
     * Ottenimento dell'utente
     * @return
     */
    public void getUser(SuccessDataListener success, FailureListener failure) {
        if(user != null) {
            success.doSuccess(user);
        }

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null) {
            reference.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        user = new User(
                                (String) dataSnapshot.child("email").getValue(),
                                (String) dataSnapshot.child("name").getValue(),
                                (String) dataSnapshot.child("surname").getValue(),
                                (String) dataSnapshot.child("dateBirth").getValue()
                        );
                        success.doSuccess(user);
                    }
                }
            });
        }
        failure.doFail();
    }

    public static UserHolder getInstance() {
        if(instance!=null) {
            return instance;
        }
        instance = new UserHolder();
        return instance;
    }

    private UserHolder() {
        result = new Result(false);
        reference = firebaseDatabase.getReference(USER_TABLE);
    }
}
