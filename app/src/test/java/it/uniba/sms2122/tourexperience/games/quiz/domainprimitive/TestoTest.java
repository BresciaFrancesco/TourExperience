package it.uniba.sms2122.tourexperience.games.quiz.domainprimitive;

import static it.uniba.sms2122.tourexperience.utility.Validate.*;

import org.junit.Assert;
import org.junit.Test;

public class TestoTest {

    private final String pattern;

    public TestoTest() {
        pattern = Testo.getRegexPattern();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPatternNotCorrectForEmptyString() throws Exception {
        String value = "";
        matchesPattern(value, pattern);
    }

    @Test(expected = IllegalArgumentException.class)
    public void isPatternNotCorrectForString_angularParentheses() throws Exception {
        String value = "<script></script>";
        matchesPattern(value, pattern);
    }

    @Test
    public void isPatternCorrectForString_withFirstCharLowerCase() throws Exception {
        String value = "prova";
        matchesPattern(value, pattern);
    }

    @Test
    public void isPatternCorrectForString_withSpanishInterrogation_and_FirstCharLowerCase() throws Exception {
        String value = "¿como estas?";
        matchesPattern(value, pattern);
    }

    @Test
    public void isPatternCorrectForString_withSpanishInterrogation_and_FirstCharUpperCase() throws Exception {
        String value = "¿Como estas?";
        try {
            matchesPattern(value, pattern);
        }
        catch (IllegalArgumentException e) {
            Assert.fail("La stringa " + value + " non è compatibile con il pattern " + pattern);
        }
    }

}