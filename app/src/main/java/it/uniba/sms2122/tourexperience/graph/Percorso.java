package it.uniba.sms2122.tourexperience.graph;

import static it.uniba.sms2122.tourexperience.utility.Validate.notNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.graph.exception.GraphRunTimeException;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive.Descrizione;
import it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive.IdStanza;
import it.uniba.sms2122.tourexperience.musei.checkzip.domainprimitive.Nome;

/**
 * Classe che rappresenta un grafo per muoversi all'interno di percorso
 */
public class Percorso {

    // ---------------------------- Attributi ---------------------------- //

    /** Mappa che rappresenta il grafo del percorso. */
    private Map<String, Vertex> mappaStanze;

    /** Puntatore alla stanza iniziale del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private transient  String idStanzaIniziale;


    /** Puntatore alla stanza corrente all'interno del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private String idStanzaCorrente;

    /** Puntatore alla stanza finale del percorso. */
    private String idStanzaFinale;

    /** Nome del percorso. */
    private String nomePercorso;

    /** Nome del museo che contiene questo percorso. */
    private String nomeMuseo;

    /** Descrizione del percorso, da visualizzare come anteprima del percorso stesso */
    private String descrizionePercorso;

    // ------------------------------ Getters ------------------------------ //

    public String getNomeMuseo() {
        return nomeMuseo;
    }

    public String getIdStanzaCorrente() {
        return idStanzaCorrente;
    }

    public String getIdStanzaIniziale() {
        return idStanzaIniziale;
    }

    public void setIdStanzaCorrente(String idStanzaCorrente) {
        this.idStanzaCorrente = idStanzaCorrente;
    }

    public void setIdStanzaIniziale(String idStanzaIniziale) {
        this.idStanzaIniziale = idStanzaIniziale;
    }

    public String getIdStanzaFinale() {
        return idStanzaFinale;
    }

    public String getDescrizionePercorso() { return descrizionePercorso; }

    public String getNomePercorso() { return nomePercorso; }

    // ------------------------------ Metodi ------------------------------ //

    /**
     * Muove il puntatore della stanza corrente sulla prossima stanza indicata
     * dall'ID passato come parametro, previo controllo dell'esistenza di tale arco
     * nel grafo del percorso.
     *
     * @param idProssimaStanza identificativo univoco della prossima stanza da visitare.
     * @return la stanza con id "idStanza".
     * @throws GraphRunTimeException se il percorso salvato è sbagliato.
     * @throws GraphException se si sta cercando di visitare una stanza già visitata
     *                        (ed è vietato) oppure se manca manca il collegamento
     *                        tra stanze richiesto.
     */
    public Stanza moveTo(String idProssimaStanza) throws GraphException, GraphRunTimeException {
        Vertex currVertex = mappaStanze.get(idStanzaCorrente);
        try {
            assert currVertex != null;
            if ((!currVertex.containsEdge(idProssimaStanza) ) && (!idProssimaStanza.equals(idStanzaIniziale))  && (!idProssimaStanza.equals(idStanzaCorrente))) {
                throw new GraphException("La stanza con ID: " + idProssimaStanza
                        + " non è collegata alla stanza corrente o è inesistente.");
            }
            Stanza stanza = Objects.requireNonNull(mappaStanze.get(idProssimaStanza)).getStanza();
            idStanzaCorrente = idProssimaStanza;
            if(idStanzaIniziale == null)
                idStanzaIniziale = idStanzaCorrente;
            return stanza;
        }
        catch (NullPointerException err) {
            throw new GraphRunTimeException("Il percorso è errato!", err);
        }
    }

    /**
     * Ritorna una lista di stanze adiacenti a quella in cui ci si trova in quel momento.
     * @return lista di stanze adiacenti a quella corrente.
     */
    public List<Stanza> getAdiacentNodes() throws GraphRunTimeException {
        try {
            Vertex v = mappaStanze.get(idStanzaCorrente);
            assert v != null;
            Set<String> archi = v.getEdges();
            List<Stanza> stanzeAdiacenti = new ArrayList<>(archi.size());
            int i = 0;
            for (String idS : archi) {
                stanzeAdiacenti.add(i++, Objects.requireNonNull(mappaStanze.get(idS)).getStanza());
            }
            return stanzeAdiacenti;
        }
        catch (NullPointerException e) {
            throw new GraphRunTimeException("Il percorso è errato!", e);
        }
    }

    public Stanza getStanzaCorrente() {
        return Objects.requireNonNull(mappaStanze.get(idStanzaCorrente)).getStanza();
    }


    /**
     * Controlla un oggetto Percorso arbitrario secondo tale contratto:
     * 0. percorso non null.
     * 1. nome museo non Blank.
     * 2. nome percorso non Blank.
     * 3. id stanza corrente non Blank.
     * 4. id stanza finale non Blank.
     * 5. descrizione non Blank.
     * 6. per ogni stanza nel grafo:
     *    6.0. stanza non null.
     *    6.1. per ogni arco del vertice del grafo:
     *         6.1.0. stanza collegata all'arco non null.
     *
     * Per la definizione di "non Blank" guardare la documentazione del metodo
     * notBlank della classe it.uniba.sms2122.tourexperience.utility.Validate
     * @param test percorso da controllare.
     */
    public static void checkAll(final Percorso test) throws NullPointerException, IllegalArgumentException {
        notNull(test);

        Nome.check(test.getNomeMuseo());
        Nome.check(test.getNomePercorso());
        IdStanza.check(test.getIdStanzaCorrente());
        IdStanza.check(test.getIdStanzaFinale());
        Descrizione.check(test.getDescrizionePercorso());

        final Set<String> keySet = test.mappaStanze.keySet();
        for (String key : keySet) {
            final Vertex v = notNull(test.mappaStanze.get(key), "Non è presente una stanza nella mappa del grafo");
            IdStanza.check(key);

            final Set<String> edges = v.getEdges();
            for (String edge : edges) {
                notNull(test.mappaStanze.get(edge), "Archi sbagliati");
                IdStanza.check(edge);
            }
        }
    }

}