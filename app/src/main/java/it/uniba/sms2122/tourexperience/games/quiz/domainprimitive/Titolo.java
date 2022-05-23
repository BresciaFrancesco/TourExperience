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
public class Titolo {
    private static final String REGEX_PATTERN = "[A-Z][\\p{Alnum} ]*[\\p{Alpha}]|[A-Z]";
    private final String value;

    public Titolo(final String titolo) {
        notBlank(titolo);
        inclusiveBetween(1, 40, titolo.length());
        matchesPattern(titolo, REGEX_PATTERN, "Il titolo non è accettato dal pattern " + REGEX_PATTERN);
        value = titolo;
    }

    public String value() {
        return value;
    }
}