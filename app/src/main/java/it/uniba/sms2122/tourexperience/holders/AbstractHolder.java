package it.uniba.sms2122.tourexperience.holders;

import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.model.User;

public abstract class AbstractHolder {

    protected static final String URI = "https://tour-experience-default-rtdb.europe-west1.firebasedatabase.app";
    protected static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(URI);

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

    public interface FailureDataListener {
        void doFail(String error);
    }
}
