package it.uniba.sms2122.tourexperience.games.quiz.dto;

import java.util.List;

/**
 * Rappresenta un oggetto Quiz ottenuto dal parsing di un json.
 */
public class QuizJson {
    private String titolo;
    private List<DomandaJson> domande;

    private QuizJson() {}

    public String getTitolo() {
        return titolo;
    }

    public List<DomandaJson> getDomande() {
        return domande;
    }

}
