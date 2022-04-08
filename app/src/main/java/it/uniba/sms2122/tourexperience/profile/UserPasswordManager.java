package it.uniba.sms2122.tourexperience.profile;

public class UserPasswordManager {

    private static String psw;

    public static void setPassword(String password){
        psw = password;
    }

    public static String getPassword() {
        return psw;
    }
}
