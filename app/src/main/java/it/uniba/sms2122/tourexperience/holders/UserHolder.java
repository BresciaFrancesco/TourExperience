package it.uniba.sms2122.tourexperience.holders;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.model.User;
import it.uniba.sms2122.tourexperience.profile.UserPasswordManager;
import it.uniba.sms2122.tourexperience.utility.listeners.FailureListener;
import it.uniba.sms2122.tourexperience.utility.listeners.SuccessDataListener;
import it.uniba.sms2122.tourexperience.utility.listeners.SuccessListener;

/**
 * @author Catignano Francesco
 */
public class UserHolder {
    private static final String URI = "https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app";
    private static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(URI);

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
    public void register(String email, String password, String name, String surname, String dateBirth, SuccessListener success, FailureListener failure) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                String userID = firebaseUser.getUid();
                DatabaseReference actualReference = reference.child(userID);
                actualReference.setValue(new User(email, name, surname, dateBirth)).addOnCompleteListener(taskUserValueSet -> {
                    if (taskUserValueSet.isSuccessful()) {
                        success.onSuccess();
                    } else {
                        firebaseAuth.getCurrentUser().delete();
                        failure.onFail(taskUserValueSet.getException() != null ? taskUserValueSet.getException().toString() : "");
                    }
                });
            } else {
                failure.onFail(task.getException() != null ? task.getException().toString() : "");
            }
        });
    }

    /**
     * Restituisce l'utente prendendolo eventualmente da Firebase
     */
    public void getUser(SuccessDataListener<User> success, FailureListener failure) {
        if (user != null) {
            success.onSuccess(user);
            return;
        }

        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            reference.child(currentUser.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    user = new User(
                            currentUser.getEmail(),
                            (String) dataSnapshot.child("name").getValue(),
                            (String) dataSnapshot.child("surname").getValue(),
                            (String) dataSnapshot.child("dateBirth").getValue()
                    );
                    success.onSuccess(user);
                }
            });
        } else {
            failure.onFail(null);
        }
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
    public void updateIfDirty(SuccessListener success, FailureListener failure) {
        if (user != null && user.isDirty()) {
            //riautenticazione utente poiche firebase per operazioni delicate quali cambio email,password o dati di auth in genere richiede la riautenticazione
            AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), UserPasswordManager.getPassword());
            currentUser.reauthenticate(credential).addOnCompleteListener(task -> {

                if (task.isSuccessful()) {
                    //cambio la mail
                    currentUser.updateEmail(user.getEmail()).addOnCompleteListener(task1 -> {

                        if (task1.isSuccessful()) {//se si è riusciti a cambiare la mail

                            //si cambia il nome dell'utente
                            UserProfileChangeRequest profileUpdateReq = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.getName()).build();
                            currentUser.updateProfile(profileUpdateReq).addOnCompleteListener(task11 -> {

                                if (task11.isSuccessful()) {//se si è riusciti a cambiare anche l'username

                                    //si cambiano il resto dei campi
                                    DatabaseReference actualReference = reference.child(currentUser.getUid());
                                    //actualReference.
                                    actualReference.setValue(user).addOnCompleteListener(task111 -> {
                                        if (task111.isSuccessful()) {
                                            user.setDirty(false);
                                            success.onSuccess();
                                        } else {
                                            failure.onFail(task111.getException() == null ? "" : task111.getException().toString());
                                        }
                                    });
                                }
                            });
                        } else {

                            failure.onFail(task1.getException() == null ? "" : task1.getException().toString());
                        }
                    });
                } else {
                    failure.onFail(task.getException() == null ? "" : task.getException().toString());
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
        reference = firebaseDatabase.getReference(TABLE_NAME);
    }
}
