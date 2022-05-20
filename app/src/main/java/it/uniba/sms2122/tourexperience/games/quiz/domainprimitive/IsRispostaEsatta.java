package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto IsRispostaEsatta o è corretto o non esiste.
 */
public class IsRispostaEsatta {
    private final boolean isTrue;

    public IsRispostaEsatta(final Boolean isTrue) {
        this.isTrue = notNull(isTrue);
    }

    public boolean value() {
        return isTrue;
    }
}
