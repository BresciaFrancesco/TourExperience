package it.uniba.sms2122.tourexperience.games.quiz.dto;

import java.util.List;

/**
 * Rappresenta un oggetto Domanda ottenuto dal parsing di un json.
 */
public class DomandaJson {
    private String domanda;
    private Integer valore;
    private List<RispostaJson> risposte;

    private DomandaJson() {}

    public String getDomanda() {
        return domanda;
    }

    public List<RispostaJson> getRisposte() {
        return risposte;
    }

    public Integer getValore() {
        return valore;
    }

}
