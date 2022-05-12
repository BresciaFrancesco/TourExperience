package it.uniba.sms2122.tourexperience.games.quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notNull;

import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.ID;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Punteggio;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Testo;
import it.uniba.sms2122.tourexperience.games.quiz.dto.DomandaJson;
import it.uniba.sms2122.tourexperience.games.quiz.dto.QuizJson;
import it.uniba.sms2122.tourexperience.games.quiz.dto.RispostaJson;

/**
 * Pattern: Value Object (from DDD) with Domain Primitives (from "Secure by Design").
 * Rappresenta sempre un oggetto Quiz completo e corretto.
 * Un oggetto Quiz o è corretto o non esiste.
 */
public class Quiz {
    private final Testo titolo;
    private final Punteggio valoreTotale;
    private Punteggio punteggioCorrente;
    private final List<Domanda> domande;

    public Quiz(final Testo titolo, final List<Domanda> domande) {
        this.titolo = notNull(titolo);
        notNull(domande);
        inclusiveBetween(1, 15, domande.size());

        Punteggio acc = new Punteggio(0.0);
        for (Domanda dom : domande) {
            notNull(dom);
            acc = acc.add(dom.getValore());
        }
        this.valoreTotale = acc;
        this.punteggioCorrente = new Punteggio(0.0);
        this.domande = domande;
    }

    public Testo getTitolo() {
        return titolo;
    }

    public Punteggio getValoreTotale() {
        return valoreTotale;
    }

    public Punteggio getPunteggioCorrente() {
        return punteggioCorrente;
    }

    public List<Domanda> getDomande() {
        return domande.stream().collect(Collectors.toList());
    }

    /**
     * Crea un oggetto Quiz completo e corretto.
     * @param quizJson oggetto DTO creato da un parser json come Gson.
     * @return un nuovo oggetto Quiz completo e corretto.
     * @throws NullPointerException se l'oggetto Quiz non è corretto.
     * @throws IllegalArgumentException se l'oggetto Quiz non è corretto.
     */
    public static Quiz buildFromJson(final QuizJson quizJson)
            throws NullPointerException, IllegalArgumentException
    {
        List<Risposta> listaTempRis = new ArrayList<>();
        List<Domanda> listaTempDom = new ArrayList<>();

        List<DomandaJson> domande = quizJson.getDomande();
        for (DomandaJson dom : domande) {
            listaTempRis.clear();
            List<RispostaJson> risposte = dom.getRisposte();
            for (RispostaJson ris : risposte) {
                Risposta r = new Risposta(
                        new ID(ris.getId()),
                        new Testo(ris.getRisposta()),
                        new Punteggio(ris.getPunti()));
                listaTempRis.add(r);
            }
            Domanda d = new Domanda(
                    new ID(dom.getId()),
                    new Testo(dom.getDomanda()),
                    listaTempRis);
            listaTempDom.add(d);
        }

        return new Quiz(new Testo(quizJson.getTitolo()), listaTempDom);
    }
}
