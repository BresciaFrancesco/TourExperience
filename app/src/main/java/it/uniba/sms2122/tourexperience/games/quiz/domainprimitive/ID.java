package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNaN;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto ID o è corretto o non esiste.
 */
public class ID {
    private final int value;

    public ID(final Integer id) {
        notNull(id);
        notNaN(id);
        inclusiveBetween(Integer.MIN_VALUE, Integer.MAX_VALUE, id.intValue());
        value = id;
    }

    public int value() {
        return value;
    }
}
