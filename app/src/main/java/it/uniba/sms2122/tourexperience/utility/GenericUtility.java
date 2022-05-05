package it.uniba.sms2122.tourexperience.utility;

import java.io.IOException;

public class GenericUtility {

    public static boolean thereIsConnection(Runnable callback) {
        try {
            String command = "ping -c 1 google.com";
            if (Runtime.getRuntime().exec(command).waitFor() == 0) return true;
            callback.run();
        }
        catch (IOException | InterruptedException e) {
            callback.run();
            e.printStackTrace();
        }
        return false;
    }
}
