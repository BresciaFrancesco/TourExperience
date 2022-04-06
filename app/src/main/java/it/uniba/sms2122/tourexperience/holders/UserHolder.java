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

public class UserHolder extends AbstractHolder{
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static DatabaseReference reference;
    private static final String TABLE_NAME = "Users";

    private static UserHolder instance;

    private User user;

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
    public void register(String email, String password, String name, String surname, String dateBirth, SuccessListener success, FailureDataListener failure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    DatabaseReference actualReference = reference.child(userID);
                    actualReference.setValue(new User(email, name, surname, dateBirth)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> taskUserValueSet) {
                            if (taskUserValueSet.isSuccessful()) {
                                success.doSuccess();
                            } else {
                                firebaseAuth.getCurrentUser().delete();
                                failure.doFail(taskUserValueSet.getException()!=null? taskUserValueSet.getException().toString() : "");
                            }
                        }
                    });
                } else {
                    failure.doFail(task.getException()!=null? task.getException().toString() : "");
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
        if(instance==null) {
            instance = new UserHolder();
        }
        return instance;
    }

    private UserHolder() {
        super();
        reference = firebaseDatabase.getReference(TABLE_NAME);
    }
}
