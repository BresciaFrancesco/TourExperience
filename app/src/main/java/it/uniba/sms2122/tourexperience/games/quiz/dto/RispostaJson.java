package it.uniba.sms2122.tourexperience.games.quiz.dto;

/**
 * Rappresenta un oggetto Risposta ottenuto dal parsing di un json.
 */
public class RispostaJson {
    private String risposta;
    private Boolean isTrue;

    private RispostaJson() {}

    public String getRisposta() {
        return risposta;
    }

    public boolean isTrue() {
        return (isTrue != null) ? isTrue : false;
    }

}
