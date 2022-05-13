package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;
import static it.uniba.sms2122.tourexperience.utility.Validate.matchesPattern;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto Testo o è corretto o non esiste.
 */
public class Testo {
    private static final String REGEX_PATTERN = "[\\p{Print}&&[^<>;]]+";
    private final String value;

    public Testo(final String domanda) {
        notBlank(domanda);
        inclusiveBetween(1, 100, domanda.length());
        matchesPattern(domanda, REGEX_PATTERN, "La domanda non è accettata dal pattern " + REGEX_PATTERN);
        value = domanda;
    }

    public String value() {
        return value;
    }
}
