package it.uniba.sms2122.tourexperience.games.quiz;

import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.ID;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.IsRispostaEsatta;
import it.uniba.sms2122.tourexperience.games.quiz.domainprimitive.Testo;

/**
 * Pattern: Value Object (from DDD) with Domain Primitives (from "Secure by Design").
 * Rappresenta sempre un oggetto Risposta completo e corretto.
 * Un oggetto Risposta o è corretto o non esiste.
 */
public class Risposta {
    private final ID id;
    private final Testo risposta;
    private final IsRispostaEsatta isTrue;

    public Risposta(final ID id, final Testo risposta, final IsRispostaEsatta isTrue) {
        this.id = notNull(id);
        this.risposta = notNull(risposta);
        this.isTrue = notNull(isTrue);
    }

    public ID getId() {
        return id;
    }

    public Testo getRisposta() {
        return risposta;
    }

    public IsRispostaEsatta isTrue() {
        return isTrue;
    }
}
