package it.uniba.sms2122.tourexperience.games.quiz;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import java.util.List;
import java.util.stream.Collectors;

import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.ID;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Punteggio;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Testo;

/**
 * Pattern: Value Object (from DDD) with Domain Primitives (from "Secure by Design").
 * Rappresenta sempre un oggetto Domanda completo e corretto.
 * Un oggetto Domanda o è corretto o non esiste.
 */
public class Domanda {
    private final ID id;
    private final Testo domanda;
    private final Punteggio valore;
    private final List<Risposta> risposte;
    private final int countRisposteCorrette;

    /**
     * Unico costruttore con tutti i parametri obbligatori.
     * Il numero minimo di risposte consentito è 2, il massimo è 6.
     * Il numero minimo di risposte giuste consentite è 1,
     * il massimo è il numero totale di risposte - 1.
     * @param id identificativo per la domanda.
     * @param domanda domanda.
     * @param valore valore della domanda.
     * @param risposte risposte per questa domanda.
     */
    public Domanda(final ID id, final Testo domanda, final Punteggio valore, final List<Risposta> risposte) {
        this.id = notNull(id);
        this.domanda = notNull(domanda);
        notNull(risposte);
        inclusiveBetween(2, 6, risposte.size());

        int countTrueAnswer = 0;
        for (Risposta ris : risposte) {
            notNull(ris);
            if (ris.isTrue().value())
                countTrueAnswer++;
        }
        inclusiveBetween(1, risposte.size() - 1, countTrueAnswer);

        this.risposte = risposte;
        this.valore = notNull(valore);
        this.countRisposteCorrette = countTrueAnswer;
    }

    public ID getId() {
        return id;
    }

    public Testo getDomanda() {
        return domanda;
    }

    public Punteggio getValore() {
        return valore;
    }

    public List<Risposta> getRisposte() {
        return risposte.stream().collect(Collectors.toList());
    }

    public int countRisposteCorrette() {
        return countRisposteCorrette;
    }
}
