package it.uniba.sms2122.tourexperience.model;

import static it.uniba.sms2122.tourexperience.utility.Validate.isTrue;
import static it.uniba.sms2122.tourexperience.utility.Validate.notBlank;
import static it.uniba.sms2122.tourexperience.utility.Validate.notEmpty;
import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import java.util.Map;
import java.util.Set;


public class Stanza {

    private String id;
    private String nome;
    private String descrizione;
    /** Mappa che contiene le opere presenti all'interno della stanza. */
    private Map<String, Opera> opere;

    public Stanza() {}

    public Stanza(String id, String nome, String descrizione, Map<String, Opera> opere) {
        this.id = id;
        this.nome = nome;
        this.opere = opere;
        this.descrizione = descrizione;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<String, Opera> getOpere() {
        return opere;
    }

    public void setOpere(Map<String, Opera> opere) {
        this.opere = opere;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Opera getOperaByID(final String idOpera) {
        notBlank(idOpera);
        return notNull(opere.get(idOpera));
    }

    @Override
    public String toString() {
        return "Stanza{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", opere=" + opere +
                '}';
    }

    /**
     * Controlla un oggetto Stanza arbitrario secondo tale contratto:
     * 0. stanza non null.
     * 1. id non Blank.
     * 2. nome non Blank.
     * 3. descrizione non Blank.
     * 4. opere non null e non vuoto.
     * 5. per ogni opera nella collection di opere:
     *    5.0. opera non null.
     *    5.1. id dell'opera non Blank.
     *    5.2. tutto il resto dell'opera deve essere null.
     *
     * Per la definizione di "non Blank" guardare la documentazione del metodo
     * notBlank della classe it.uniba.sms2122.tourexperience.utility.Validate
     * @param s stanza da controllare.
     */
    public static void checkStanza(final Stanza s) throws NullPointerException, IllegalArgumentException {
        final String erroreBlank = "Stanza: Blank %s";
        final String erroreNull = "Stanza: Null %s";
        final String erroreEmpty = "Stanza: Empty %s";
        final String erroreNotNull = "Stanza: Not Null %s";

        notNull(s, erroreNull, "stanza");
        notBlank(s.getId(), erroreBlank, "id");
        notBlank(s.getNome(), erroreBlank, "nome");
        //notBlank(s.getDescrizione(), erroreBlank, "descrizione");
        final Map<String, Opera> opereTmp = notEmpty(s.getOpere(), erroreEmpty, "opere");
        final Set<String> keySet = opereTmp.keySet();
        for (final String key : keySet) {
            final Opera o = notNull(opereTmp.get(key), erroreNull, "opera");
            notBlank(o.getId(), erroreBlank, "id opera");
            isTrue(o.getNome() == null || o.getNome().isEmpty(), erroreNotNull, "nome opera");
            isTrue(o.getDescrizione() == null || o.getDescrizione().isEmpty(), erroreNotNull, "descrizione opera");
            isTrue(o.getPercorsoImg() == null || o.getPercorsoImg().isEmpty(), erroreNotNull, "percorso img opera");
        }
    }
}