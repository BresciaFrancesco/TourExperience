package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static org.apache.commons.lang3.Validate.notNaN;
import static org.apache.commons.lang3.Validate.notNull;
import static org.apache.commons.lang3.Validate.inclusiveBetween;

/**
 * Pattern: Domain Primitive.
 * Rappresenta una Domain Primitive, ovvero un Value Object la cui
 * correttezza è completamente controllata in fase di creazione.
 * Un oggetto Punteggio o è corretto o non esiste.
 */
public class Punteggio {
    private final Double value;

    public Punteggio(final Double punteggio) {
        notNull(punteggio);
        notNaN(punteggio);
        inclusiveBetween(0, 100, punteggio);
        value = punteggio;
    }

    public Double value() {
        return value;
    }

    public Punteggio add(final Punteggio aggiunta) {
        return new Punteggio(value + aggiunta.value);
    }
}
