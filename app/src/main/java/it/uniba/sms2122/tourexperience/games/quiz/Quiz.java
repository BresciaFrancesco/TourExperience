package it.uniba.sms2122.tourexperience.games.quiz;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.isTrue;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.ID;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.IsRispostaEsatta;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Punteggio;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Testo;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Titolo;
import it.uniba.sms2122.tourexperience.games.quiz.dto.DomandaJson;
import it.uniba.sms2122.tourexperience.games.quiz.dto.QuizJson;
import it.uniba.sms2122.tourexperience.games.quiz.dto.RispostaJson;


/**
 * Pattern: Value Object (from DDD) with Domain Primitives (from "Secure by Design").
 * Rappresenta sempre un oggetto Quiz completo e corretto.
 * Un oggetto Quiz o è corretto o non esiste.
 */
public class Quiz {
    private final Titolo titolo;
    private final Punteggio valoreTotale;
    private final List<Domanda> domande;
    private Punteggio punteggioCorrente;

    private Quiz(final Titolo titolo, final List<Domanda> domande) {
        this.titolo = notNull(titolo);
        notNull(domande);
        inclusiveBetween(1, 15, domande.size());

        Punteggio acc = new Punteggio(0.0);
        for (Domanda dom : domande) {
            notNull(dom);
            acc = acc.add(dom.getValore());
        }
        this.valoreTotale = acc;
        this.domande = domande;
        this.punteggioCorrente = new Punteggio(0.0);
    }

    /**
     * Ritorna il titolo del Quiz.
     * @return titolo del Quiz.
     */
    public Titolo getTitolo() {
        return titolo;
    }

    /**
     * Ritorna il massimo punteggio raggiungibile per questo quiz.
     * @return il massimo punteggio raggiungibile.
     */
    public Punteggio getValoreTotale() {
        return valoreTotale;
    }

    /**
     * Ritorna le domande del quiz.
     * @return le domande del quiz.
     */
    public List<Domanda> getDomande() {
        return domande.stream().collect(Collectors.toList());
    }

    /**
     * Ritorna il punteggio corrente del quiz (può variare a runtime).
     * @return il punteggio corrente del quiz.
     */
    public Punteggio getPunteggioCorrente() {
        return punteggioCorrente;
    }

    /**
     * Aumenta il punteggio corrente del quiz aggiungendo il punteggio ottenuto
     * dal parametro punteggio. Se il parametro punteggio vale 0, il punteggio
     * corrente non aumenta. Il nuovo punteggio ottenuto dalla somma non deve
     * superare il valore totale.
     * @param punteggio punteggio da sommare a quello corrente.
     * @throws IllegalArgumentException se il nuovo punteggio totale supera il
     * valore totale consentito.
     */
    public void increasePunteggio(final Punteggio punteggio) throws IllegalArgumentException {
        final Punteggio p = punteggioCorrente.add(punteggio);
        isTrue(p.value() <= valoreTotale.value(),
            "Il nuovo valore %d supererebbe il totale %d", p.value(), valoreTotale.value());
        punteggioCorrente = p;
    }

    /**
     * Azzera il punteggio corrente, ponendolo a 0.0 .
     */
    public void resetPunteggio() {
        punteggioCorrente = new Punteggio(0.0);
    }

    /**
     * Crea un oggetto Quiz completo e corretto.
     * Come ID di domande e risposte, utilizza la classe di Android View
     * per generare viewId univoci da utilizzare nell'interfaccia.
     * @param quizJson oggetto DTO creato da un parser json come Gson.
     * @return un nuovo oggetto Quiz completo e corretto.
     * @throws NullPointerException se l'oggetto Quiz non è corretto.
     * @throws IllegalArgumentException se l'oggetto Quiz non è corretto.
     */
    public static Quiz buildFromJson(final QuizJson quizJson)
            throws NullPointerException, IllegalArgumentException
    {
        List<Domanda> listaTempDom = new ArrayList<>();

        List<DomandaJson> domande = quizJson.getDomande();
        for (DomandaJson dom : domande) {
            List<Risposta> listaTempRis = new ArrayList<>();
            List<RispostaJson> risposte = dom.getRisposte();
            for (RispostaJson ris : risposte) {
                Risposta r = new Risposta(
                        new ID(View.generateViewId()),
                        new Testo(ris.getRisposta()),
                        new IsRispostaEsatta(ris.isTrue()));
                listaTempRis.add(r);
            }
            Domanda d = new Domanda(
                    new ID(View.generateViewId()),
                    new Testo(dom.getDomanda()),
                    new Punteggio(dom.getValore()),
                    listaTempRis);
            listaTempDom.add(d);
        }

        return new Quiz(new Titolo(quizJson.getTitolo()), listaTempDom);
    }

}
