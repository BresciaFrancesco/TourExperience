package it.uniba.sms2122.tourexperience.holders;

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

/**
 * @author Catignano Francesco
 */
public class UserHolder extends AbstractHolder{
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static DatabaseReference reference;
    private static final String TABLE_NAME = "Users";

    private static UserHolder instance;

    private User user;

    /**
     * Crea l'utente registrandolo su Firebase
     * @param email L'email dell'utente
     * @param password La password
     * @param name Il nome dell'utente
     * @param surname Il cognome dell'utente
     * @param dateBirth La data di nascita dell'utente
     * @param success Interfaccia per realizzare il metodo di successo (realizzabile con una lambda expression)
     * @param failure Interfaccia per realizzare il metodo di fallimento (realizzabile con una lambda expression)
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
     * Restituisce l'utente prendendolo eventualmente da Firebase
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
                                currentUser.getEmail(),
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

    /**
     * Effettua il logout
     */
    public void logout() {
        firebaseAuth.signOut();
        user = null;
    }

    /**
     * Restituisce l'unica istanza di {@link UserHolder}.
     * @return L'istanza di {@link UserHolder}
     */
    public static UserHolder getInstance() {
        if(instance==null) {
            instance = new UserHolder();
        }
        return instance;
    }

    /*
     * Costruttore privato di UserHolder per applicare il pattern singleton.
     */
    private UserHolder() {
        super();
        reference = firebaseDatabase.getReference(TABLE_NAME);
    }
}
