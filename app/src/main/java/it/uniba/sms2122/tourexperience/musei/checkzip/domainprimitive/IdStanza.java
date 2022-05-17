package it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.*;

public class IdStanza {
    private static final int MAX_ID_STANZA = 40;
    private static final String PATTERN = "[a-zA-Z0-9]+";

    private IdStanza() {}

    public static void check(final String idStanza) {
        notBlank(idStanza, "Id stanza vuoto");
        inclusiveBetween(1, MAX_ID_STANZA, idStanza.length());
        matchesPattern(idStanza, PATTERN);
    }
}
