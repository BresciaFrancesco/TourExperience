package it.uniba.sms2122.tourexperience.profile;

import android.widget.EditText;

import java.util.regex.Pattern;

public class ProfileDataChangeValidation {


    /**
     * Funzione che si occupa di controllare se la stringa passatogli rispetti l'espressione regolare pasatoli
     *
     * @param viewToValidate, stringa da validare
     * @param regExp,         espressione regolare che deve essere rispettat
     * @return true se la stringa rispetta l'espressione regolare, false altrimenti
     */
    private static boolean genericlValidator(EditText viewToValidate, String regExp, String errorTxt) {


        if (Pattern.matches(regExp, viewToValidate.getText().toString().trim())) {
            return true;
        }

        viewToValidate.setError(errorTxt);
        viewToValidate.requestFocus();
        return false;
    }


    /**
     * funzione per validare i dati di semplice stringhe alfabetice
     *
     * @param viewToValidate , la view di cui si vuole avere la convalida
     * @return true se il testo rispetta i criteri richiesti, false altrimenti
     */
    public static boolean validateGenericText(EditText viewToValidate, String errorTxt) {

        String regExp = "^[a-zA-Z_ ]+$";

        return genericlValidator(viewToValidate, regExp, errorTxt);
    }

    /**
     * funzione per validare la mail
     *
     * @param viewToValidate , la view di cui si vuole avere la convalida
     * @return true se la mail rispetta i criteri richiesti, false altrimenti
     */
    public static boolean validateEmail(EditText viewToValidate, String errorTxt) {

        String regExp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";

        return genericlValidator(viewToValidate, regExp, errorTxt);
    }

    /**
     * funzione per validare le date
     *
     * @param viewToValidate , la view di cui si vuole avere la convalida
     * @return true se la data rispetta i criteri richiesti, false altrimenti
     */
    public static boolean validateDate(EditText viewToValidate, String errorTxt) {

        String regExp = "^^\\d{1,2}\\ / \\d{1,2}\\ / \\d{4}$";
        String regExp2 = "^^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$";

        if(genericlValidator(viewToValidate, regExp, errorTxt) || genericlValidator(viewToValidate, regExp2, errorTxt))
            return true;
        else
            return false;

    }

}
