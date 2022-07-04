package it.uniba.sms2122.tourexperience.utility;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Classe che contiene solo metodi statici di validazione.
 */
public class Validate {

    private static final String DEFAULT_NOT_NAN_EX_MESSAGE = "Il valore validato non è un numero";
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "Il valore %s non è nel range inclusivo specificato [%s, %s]";
    private static final String DEFAULT_MATCHES_PATTERN_EX = "La stringa %s non combacia con il pattern %s";
    private static final String DEFAULT_IS_NULL_EX_MESSAGE = "L'oggetto validato è null";
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "L'espressione validata è false";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "La sequenza di char validata è blank";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "L'array validato è vuoto";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "La sequenza di char validata è vuota";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "La collection validata è empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "La Map validata è empty";

    /** Costruttore privato. La classe non può essere istanziata. */
    private Validate() {}

    // isTrue
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la condizione booleana "expression" passata come parametro sia vera,
     * altrimenti genera un'eccezione con il messaggio specificato.
     *
     * @param expression il valore booleano da controllare.
     * @param message il messaggio da passare a String.format().
     * @param value il valore di tipo long utilizzato in String.format().
     * @throws IllegalArgumentException
     */
    public static void isTrue(final boolean expression, final String message, final long value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, Long.valueOf(value)));
        }
    }

    /**
     * Controlla che la condizione booleana "expression" passata come parametro sia vera,
     * altrimenti genera un'eccezione con il messaggio specificato.
     *
     * @param expression il valore booleano da controllare.
     * @param message il messaggio da passare a String.format().
     * @param value il valore di tipo double utilizzato in String.format().
     * @throws IllegalArgumentException
     */
    public static void isTrue(final boolean expression, final String message, final double value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, Double.valueOf(value)));
        }
    }

    /**
     * Controlla che la condizione booleana "expression" passata come parametro sia vera,
     * altrimenti genera un'eccezione con il messaggio specificato.
     *
     * @param expression il valore booleano da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @throws IllegalArgumentException
     */
    public static void isTrue(final boolean expression, final String message, final Object... values) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    /**
     * Controlla che la condizione booleana "expression" passata come parametro sia vera,
     * altrimenti genera un'eccezione con il messaggio specificato.
     * Utilizza un messaggio di default come errore da stampare in caso di eccezione.
     *
     * @param expression il valore booleano da controllare.
     * @throws IllegalArgumentException
     */
    public static void isTrue(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }

    // notNull
    //---------------------------------------------------------------------------------

    /**
     * Metodo generico per controllare che un Object non sia null.
     * In caso di null solleva una NullPointerException.
     *
     * @param <T> il tipo di oggetto.
     * @param object l'oggetto da controllare.
     * @return l'oggetto validato.
     * @throws NullPointerException se l'oggetto è null.
     */
    public static <T> T notNull(final T object) {
        return notNull(object, DEFAULT_IS_NULL_EX_MESSAGE);
    }

    /**
     * Metodo generico per controllare che un Object non sia null.
     * In caso di null solleva una NullPointerException con il messaggio
     * passato come parametro.
     *
     * @param <T> il tipo di oggetto.
     * @param object l'oggetto da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return l'oggetto validato.
     * @throws NullPointerException  se l'oggetto è null.
     */
    public static <T> T notNull(final T object, final String message, final Object... values) {
        return Objects.requireNonNull(object, () -> String.format(message, values));
    }

    // notEmpty array
    //---------------------------------------------------------------------------------

    /**
     * Controlla che un array non sia null e nemmeno vuoto (lunght pari a 0).
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     *
     * @param <T> il tipo di array.
     * @param array l'array da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return l'array validato.
     * @throws NullPointerException se l'array è null.
     * @throws IllegalArgumentException se l'array è vuoto.
     */
    public static <T> T[] notEmpty(final T[] array, final String message, final Object... values) {
        Objects.requireNonNull(array, () -> String.format(message, values));
        if (array.length == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return array;
    }

    /**
     * Controlla che un array non sia null e nemmeno vuoto (lunght pari a 0).
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo di array.
     * @param array l'array da controllare.
     * @return l'array validato.
     * @throws NullPointerException se l'array è null.
     * @throws IllegalArgumentException se l'array è vuoto.
     */
    public static <T> T[] notEmpty(final T[] array) {
        return notEmpty(array, DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
    }

    // notEmpty collection
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la collection passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     *
     * @param <T> il tipo di collection.
     * @param collection la collection da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return la collection validata.
     * @throws NullPointerException se la collection è null.
     * @throws IllegalArgumentException se la collection è vuota.
     */
    public static <T extends Collection<?>> T notEmpty(final T collection, final String message, final Object... values) {
        Objects.requireNonNull(collection, () -> String.format(message, values));
        if (collection.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return collection;
    }

    /**
     * Controlla che la collection passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo di collection.
     * @param collection la collection da controllare.
     * @return la collection validata.
     * @throws NullPointerException se la collection è null.
     * @throws IllegalArgumentException se la collection è vuota.
     */
    public static <T extends Collection<?>> T notEmpty(final T collection) {
        return notEmpty(collection, DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
    }

    // notEmpty map
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la Map passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     *
     * @param <T> il tipo di Map
     * @param map la Map da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return la Map validata.
     * @throws NullPointerException se la Map è null.
     * @throws IllegalArgumentException se la Map è vuota.
     */
    public static <T extends Map<?, ?>> T notEmpty(final T map, final String message, final Object... values) {
        Objects.requireNonNull(map, () -> String.format(message, values));
        if (map.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return map;
    }

    /**
     * Controlla che la Map passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo di Map
     * @param map la Map da controllare.
     * @return la Map validata.
     * @throws NullPointerException se la Map è null.
     * @throws IllegalArgumentException se la Map è vuota.
     */
    public static <T extends Map<?, ?>> T notEmpty(final T map) {
        return notEmpty(map, DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
    }

    // notEmpty string
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la sequenza di caratteri passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     *
     * @param <T> il tipo di sequenza di caratteri.
     * @param chars i caratteri da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return la sequenza validata.
     * @throws NullPointerException se la sequenza è null.
     * @throws IllegalArgumentException se la sequenza è vuota.
     */
    public static <T extends CharSequence> T notEmpty(final T chars, final String message, final Object... values) {
        Objects.requireNonNull(chars, () -> String.format(message, values));
        if (chars.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    /**
     * Controlla che la sequenza di caratteri passata come argomento non sia null e nemmeno vuota.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo di sequenza di caratteri.
     * @param chars i caratteri da controllare.
     * @return la sequenza validata.
     * @throws NullPointerException se la sequenza è null.
     * @throws IllegalArgumentException se la sequenza è vuota.
     */
    public static <T extends CharSequence> T notEmpty(final T chars) {
        return notEmpty(chars, DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
    }

    // notBlank string
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la sequenza di char passata come argomento non sia null, lenght 0,
     * vuota o solo withspace.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     *
     * @param <T> il tipo di char sequence.
     * @param chars la char sequence da controllare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @return la sequenza validata.
     * @throws NullPointerException se la sequenza è null.
     * @throws IllegalArgumentException se la sequenza è vuota.
     */
    public static <T extends CharSequence> T notBlank(final T chars, final String message, final Object... values) {
        Objects.requireNonNull(chars, () -> String.format(message, values));
        if (StringUtility.isBlank(chars)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
        return chars;
    }

    /**
     * Controlla che la sequenza di char passata come argomento non sia null, lenght 0,
     * vuota o solo withspace.
     * Altrimenti solleva una di 2 eccezioni: NullPointerException o IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo di char sequence.
     * @param chars la char sequence da controllare.
     * @return la sequenza validata.
     * @throws NullPointerException se la sequenza è null.
     * @throws IllegalArgumentException se la sequenza è vuota.
     */
    public static <T extends CharSequence> T notBlank(final T chars) {
        return notBlank(chars, DEFAULT_NOT_BLANK_EX_MESSAGE);
    }


    // matchesPattern
    //---------------------------------------------------------------------------------

    /**
     * Controlla che la sequenza di char passata come argomento rispetti
     * un determinato pattern passato come argomento.
     * Ritorna IllegalArgumentException se la sequenza non rispetta il pattern.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param input la sequenza di char da controllare.
     * @param pattern l'espressione regolare da usare per il controllo.
     * @throws IllegalArgumentException se la sequenza di char non rispetta il pattern.
     */
    public static void matchesPattern(final CharSequence input, final String pattern) {
        if (!Pattern.matches(pattern, input)) {
            throw new IllegalArgumentException(String.format(DEFAULT_MATCHES_PATTERN_EX, input, pattern));
        }
    }

    /**
     * Controlla che la sequenza di char passata come argomento rispetti
     * un determinato pattern passato come argomento.
     * Ritorna IllegalArgumentException se la sequenza non rispetta il pattern.
     *
     * @param input la sequenza di char da controllare.
     * @param pattern l'espressione regolare da usare per il controllo.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @throws IllegalArgumentException se la sequenza di char non rispetta il pattern.
     */
    public static void matchesPattern(final CharSequence input, final String pattern, final String message, final Object... values) {
        if (!Pattern.matches(pattern, input)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    // notNaN
    //---------------------------------------------------------------------------------

    /**
     * Controlla che l'argomento double non sia NaN. Altrimenti
     * solleva un'eccezione IllegalArgumentException.Utilizza un
     * messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param value il valore double da validare.
     * @throws IllegalArgumentException se il valore non è un numero.
     */
    public static void notNaN(final double value) {
        notNaN(value, DEFAULT_NOT_NAN_EX_MESSAGE);
    }

    /**
     * Controlla che l'argomento double non sia NaN. Altrimenti
     * solleva un'eccezione IllegalArgumentException.
     *
     * @param value il valore double da validare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @throws IllegalArgumentException se il valore non è un numero.
     */
    public static void notNaN(final double value, final String message, final Object... values) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }


    // inclusiveBetween
    //---------------------------------------------------------------------------------

    /**
     * Controlla che il valore passato come parametro cada all'interno di un certo range inclusivo.
     * Altrimenti solleva un'eccezione IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param <T> il tipo del valore.
     * @param start il valore iniziale inclusivo.
     * @param end il valore finale inclusivo.
     * @param value l'oggetto da validare.
     * @throws IllegalArgumentException se il valore cade al di fuori del range inclusivo.
     */
    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    /**
     * Controlla che il valore passato come parametro cada all'interno di un certo range inclusivo.
     * Altrimenti solleva un'eccezione IllegalArgumentException.
     *
     * @param <T> il tipo del valore.
     * @param start il valore iniziale inclusivo.
     * @param end il valore finale inclusivo.
     * @param value l'oggetto da validare.
     * @param message il messaggio da passare a String.format().
     * @param values i valori di tipo Object utilizzati in String.format().
     * @throws IllegalArgumentException se il valore cade al di fuori del range inclusivo.
     */
    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value, final String message, final Object... values) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    /**
     * Controlla che il valore double passato come parametro cada all'interno di un certo
     * range inclusivo. Altrimenti solleva un'eccezione IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param start il valore iniziale inclusivo.
     * @param end il valore finale inclusivo.
     * @param value il valore da validare.
     * @throws IllegalArgumentException se il valore cade al di fuori del range inclusivo.
     */
    public static void inclusiveBetween(final double start, final double end, final double value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    /**
     * Controlla che il valore double passato come parametro cada all'interno di un certo
     * range inclusivo. Altrimenti solleva un'eccezione IllegalArgumentException.
     * Utilizza un messaggio di errore standard nel caso sollevi un'eccezione.
     *
     * @param start il valore iniziale inclusivo.
     * @param end il valore finale inclusivo.
     * @param value il valore da validare.
     * @param message il messaggio da utilizzare con l'eccezione.
     * @throws IllegalArgumentException se il valore cade al di fuori del range inclusivo.
     */
    public static void inclusiveBetween(final double start, final double end, final double value, final String message) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(message);
        }
    }

}
