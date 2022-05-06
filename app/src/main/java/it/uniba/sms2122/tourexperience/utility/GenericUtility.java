package it.uniba.sms2122.tourexperience.utility;

import java.io.IOException;

public class GenericUtility {

    /**
     * Controlla se è presente connessione a Internet.
     * Prende in input una funzione callback da eseguire quando manca la connessione.
     * @param callback funzione da eseguire quando manca la connessione.
     * @return True se c'è connessione, False altrimenti.
     */
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
