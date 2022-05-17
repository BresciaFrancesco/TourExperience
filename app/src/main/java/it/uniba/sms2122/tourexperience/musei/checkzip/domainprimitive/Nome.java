package it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.inclusiveBetween;
import static it.uniba.sms2122.tourexperience.utility.Validate.matchesPattern;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;

public class Nome {
    private static final int MAX_NOME = 35;
    private static final String PATTERN = "[a-zA-Z0-9 _()']+";

    private Nome() {}

    public static void check(final String nome) {
        notBlank(nome);
        inclusiveBetween(1, MAX_NOME, nome.length());
        matchesPattern(nome, PATTERN);
    }
}
