package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.matchesPattern;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto Testo o è corretto o non esiste.
 */
public class Testo {
    private static final String REGEX_PATTERN = "¿?[\\p{Print}&&[^<>\";]]+";
    private final String value;

    public Testo(final String testo) {
        notBlank(testo);
        inclusiveBetween(1, 100, testo.length());
        matchesPattern(testo, REGEX_PATTERN, "Il testo non è accettato dal pattern " + REGEX_PATTERN);
        value = testo;
    }

    public String value() {
        return value;
    }

    public static String getRegexPattern() {
        return REGEX_PATTERN;
    }
}
