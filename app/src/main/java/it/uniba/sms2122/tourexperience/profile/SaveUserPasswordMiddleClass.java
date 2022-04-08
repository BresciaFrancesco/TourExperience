package it.uniba.sms2122.tourexperience.profile;

public class SaveUserPasswordMiddleClass {

    private static String psw;

    public static void setSavedUserPassword(String password){

        psw = password;

    }

    public static  String getSavedUserPassword(){
        return psw;
    }
}
