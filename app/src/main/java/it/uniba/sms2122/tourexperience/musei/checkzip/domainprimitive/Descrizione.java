package it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.matchesPattern;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;

public class Descrizione {
    private static final int MAX_DESCRIZIONE = 800;
    private static final String PATTERN = "[a-zA-Z0-9 _()'.,]+";

    private Descrizione() {}

    public static void check(final String descrizione) {
        notBlank(descrizione, "Descrizione nulla");
        inclusiveBetween(1, MAX_DESCRIZIONE, descrizione.length());
        matchesPattern(descrizione, PATTERN, "Descrizione incompatibile con pattern %s", PATTERN);
    }
}
