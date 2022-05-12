package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static org.apache.commons.lang3.Validate.inclusiveBetween;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.matchesPattern;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto ID o è corretto o non esiste.
 */
public class ID {
    private static final String REGEX_PATTERN = "[0-9][0-9]?";
    private final String value;

    public ID(final String id) {
        notBlank(id);
        inclusiveBetween(1, 2, id.length());
        matchesPattern(id, REGEX_PATTERN, "L'id non è accetato dal pattern " + REGEX_PATTERN);
        value = id;
    }

    public String value() {
        return value;
    }
}
