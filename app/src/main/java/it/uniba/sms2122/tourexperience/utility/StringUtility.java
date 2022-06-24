package it.uniba.sms2122.tourexperience.utility;

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
}
