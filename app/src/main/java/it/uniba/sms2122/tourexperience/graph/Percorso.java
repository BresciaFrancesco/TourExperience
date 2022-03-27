package it.uniba.sms2122.tourexperience.graph;

import com.google.gson.annotations.Expose;

import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.graph.exception.GraphRunTimeException;
import it.uniba.sms2122.tourexperience.model.Stanza;

/**
 * Classe che rappresenta un grafo per muoversi all'interno di percorso
 */
public class Percorso {

    // ---------------------------- Attributi ---------------------------- //

    /** Mappa che rappresenta il grafo del percorso. */
    private Map<String, Vertex> mapStanze;

    /** Puntatore alla stanza corrente all'interno del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private String idStanzaCorrente;

    /** Set che conterrà dinamicamente le stanze visitate nel grafo. */
    @Expose(serialize = false, deserialize = false) // nasconde stanzeVisitate dalla classe Gson
    private Set<String> stanzeVisitate;

    /** Flag che determina se è possibile tornare indietro nelle stanze
     * già visitate o no */
    private boolean tornareIndietro;

    // ---------------------------- Costruttori ---------------------------- //

    /**
     * Costruttore protetto della classe Percorso.
     * Server per deserializzare la classe utilizzando anche il set dinamico stanzeVisitate.
     * @param stanzeVisitate
     */
    protected Percorso(Set<String> stanzeVisitate) {
        this.stanzeVisitate = stanzeVisitate;
    }

    // ------------------------------ Metodi ------------------------------ //

    /**
     * Muove il puntatore della stanza corrente sulla prossima stanza indicata
     * dall'ID passato come parametro, previo controllo dell'esistenza di tale arco
     * nel grafo del percorso.
     * Quando si cambia stanza, la stanza appena lasciata verrà aggiunta al set di
     * stanze visitate.
     * Se il flag "tornareIndietro" è false e "idStanza" fa riferimento ad una stanza
     * già visitata, verrà sollevata un'eccezione GraphException.
     *
     * @param idStanza identificativo univoco di una stanza adiacente
     *                 a quella corrente.
     *
     * @return la stanza con id "idStanza".
     *
     * @throws GraphRunTimeException se il percorso salvato è sbagliato.
     *
     * @throws GraphException se si sta cercando di visitare una stanza già visitata
     *                        (ed è vietato) oppure se manca manca il collegamento
     *                        tra stanze richiesto.
     */
    public Stanza moveTo(String idStanza) throws GraphException, GraphRunTimeException {
        Vertex currVertex = mapStanze.get(idStanzaCorrente);
        try {
            if (!currVertex.containsEdge(idStanza)) {
                throw new GraphException("La stanza con ID: " + idStanza
                        + " non è collegata alla stanza corrente o è inesistente.");
            }
            if (!tornareIndietro && stanzeVisitate.contains(idStanza)) {
                throw new GraphException("Non è possibile visitare stanze già visitate in precedenza.");
            }
            Stanza stanza = mapStanze.get(idStanza).getStanza();
            stanzeVisitate.add(idStanzaCorrente);
            idStanzaCorrente = idStanza;
            return stanza;
        }
        catch (NullPointerException err) {
            throw new GraphRunTimeException("Il percorso è errato!", err);
        }
    }

}