package it.uniba.sms2122.tourexperience.utility;

import android.util.Log;

/**
 * Classe per processare le stringhe da visualizzare all'interno di un'activity o di un fragment
 */
public class StringUtility {
    private static final String ENDING = "...";
    private static final int MAX_CHARS = 15;    // Numero massimo di caratteri visualizzabili per ogni linea

    private StringUtility() {}

    /**
     * Prende in input una stringa da visualizzare e la manipola al fine di renderne più sicura la visualizzazione, andando a capo quand necessario o inserendo i puntini di sospensione.
     * @param input
     * @return
     */
    public static String safeViewing(String input) {
        if(input == null) {
            return null;
        }
        if(input.length() <= MAX_CHARS) {
            return input;
        }

        String[] lines = input.split("\n");
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<lines.length; i++) {
            String tmp;
            if(lines[i].length() >= MAX_CHARS) {
                tmp = lines[i].substring(0, MAX_CHARS-3) + ENDING;
            } else {
                tmp = lines[i];
            }

            if(i > 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(tmp);
        }

        return stringBuilder.toString();
    }

    /**
     * Decodifica manuale dei caratteri in UTF-8 contenuti nei JSON .
     * @param input Stringa da decodificare
     * @return Stringa decodificata
     */
    public static String decodeUTF8(String input) {
        String temp = input.replaceAll("&agrave","à");
        temp = temp.replaceAll("&igrave","ì");
        temp = temp.replaceAll("&ograve","ò");
        temp = temp.replaceAll("&ugrave","ù");

        temp = temp.replaceAll("&eacute","é");
        temp = temp.replaceAll("&egrave","è");
        temp = temp.replaceAll("&Eacute","É");
        temp = temp.replaceAll("&Egrave","È");
        return temp;
    }
}
