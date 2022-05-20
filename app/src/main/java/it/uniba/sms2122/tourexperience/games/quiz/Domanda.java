package it.uniba.sms2122.tourexperience.games.quiz;

import java.util.List;
import java.util.stream.Collectors;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

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

    public Domanda(final ID id, final Testo domanda, final Punteggio valore, final List<Risposta> risposte) {
        this.id = notNull(id);
        this.domanda = notNull(domanda);
        notNull(risposte);
        inclusiveBetween(1, 6, risposte.size());

        for (Risposta ris : risposte) {
            notNull(ris);
        }
        this.risposte = risposte;
        this.valore = notNull(valore);
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
}
