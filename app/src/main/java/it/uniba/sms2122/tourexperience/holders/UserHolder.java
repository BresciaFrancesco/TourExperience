package it.uniba.sms2122.tourexperience.holders;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Console;

import it.uniba.sms2122.tourexperience.model.User;
import it.uniba.sms2122.tourexperience.profile.UserPasswordManager;

/**
 * @author Catignano Francesco
 */
public class UserHolder extends AbstractHolder {
    private static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static DatabaseReference reference;
    private static final String TABLE_NAME = "Users";
    private FirebaseUser currentUser;

    private static UserHolder instance;

    private User user;

    /**
     * Crea l'utente registrandolo su Firebase
     *
     * @param email     L'email dell'utente
     * @param password  La password
     * @param name      Il nome dell'utente
     * @param surname   Il cognome dell'utente
     * @param dateBirth La data di nascita dell'utente
     * @param success   Interfaccia per realizzare il metodo di successo (realizzabile con una lambda expression)
     * @param failure   Interfaccia per realizzare il metodo di fallimento (realizzabile con una lambda expression)
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
                                failure.doFail(taskUserValueSet.getException() != null ? taskUserValueSet.getException().toString() : "");
                            }
                        }
                    });
                } else {
                    failure.doFail(task.getException() != null ? task.getException().toString() : "");
                }
            }
        });
    }

    /**
     * Restituisce l'utente prendendolo eventualmente da Firebase
<<<<<<< HEAD
     *
     * @return
=======
>>>>>>> a178b1f435d60c7117480a583378893d24d6f4fa
     */
    public void getUser(SuccessDataListener success, FailureListener failure) {
        if (user != null) {
            success.doSuccess(user);
            return;
        }

        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            reference.child(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
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
     * Effettua l'update dell'oggetto user solamente se questo e' stato modificato.
     *
     * @param success Interfaccia per realizzare il metodo di successo (realizzabile con una lambda expression)
     * @param failure Interfaccia per realizzare il metodo di fallimento (realizzabile con una lambda expression)
     */
    public void updateIfDirty(SuccessListener success, FailureDataListener failure) {
        if (user != null && user.isDirty()) {
            //riautenticazione utente poiche firebase per operazioni delicate quali cambio email,password o dati di auth in genere richiede la riautenticazione
            AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), UserPasswordManager.getPassword().toString());
            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        //cambio la mail
                        currentUser.updateEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {//se si è riusciti a cambiare la mail

                                    //si cambia il nome dell'utente
                                    UserProfileChangeRequest profileUpdateReq = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(user.getName()).build();
                                    currentUser.updateProfile(profileUpdateReq).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {//se si è riusciti a cambiare anche l'username

                                                //si cambiano il resto dei campi
                                                DatabaseReference actualReference = reference.child(currentUser.getUid());
                                                //actualReference.
                                                actualReference.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            user.setDirty(false);
                                                            success.doSuccess();
                                                        } else {
                                                            failure.doFail(task.getException() == null ? "" : task.getException().toString());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {

                                    failure.doFail(task.getException() == null ? "" : task.getException().toString());
                                }
                            }
                        });
                    } else {
                        failure.doFail(task.getException() == null ? "" : task.getException().toString());
                    }


                }
            });
        }
    }

    /**
     * Restituisce l'unica istanza di {@link UserHolder}.
     *
     * @return L'istanza di {@link UserHolder}
     */
    public static UserHolder getInstance() {
        if (instance == null) {
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
