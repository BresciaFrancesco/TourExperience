package it.uniba.sms2122.tourexperience.games.quiz;

import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.ID;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Punteggio;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Testo;

/**
 * Pattern: Value Object (from DDD) with Domain Primitives (from "Secure by Design").
 * Rappresenta sempre un oggetto Risposta completo e corretto.
 * Un oggetto Risposta o è corretto o non esiste.
 */
public class Risposta {
    private final ID id;
    private final Testo risposta;
    private final Punteggio punti;

    public Risposta(final ID id, final Testo risposta, final Punteggio punti) {
        this.id = notNull(id);
        this.risposta = notNull(risposta);
        this.punti = notNull(punti);
    }

    public ID getId() {
        return id;
    }

    public Testo getRisposta() {
        return risposta;
    }

    public Punteggio getPunti() {
        return punti;
    }
}
