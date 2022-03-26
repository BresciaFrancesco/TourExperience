package it.uniba.sms2122.tourexperience.graph;

import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.graph.exception.GraphRunTimeException;
import it.uniba.sms2122.tourexperience.model.Stanza;


public class Percorso {

    /** Mappa che rappresenta il grafo del percorso. */
    private Map<String, Vertex> mapStanze;

    /** Puntatore alla stanza corrente all'interno del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private String idStanzaCorrente;

    private Set<String> stanzeVisitate;


    /**
     * Costruttore della classe Percorso
     * @param mapStanze mappa che rappresenta il grafo del percorso
     * @param idStanzaCorrente puntatore alla stanza di partenza del percorso
     */
    public Percorso(Map<String, Vertex> mapStanze, String idStanzaCorrente) {
        this.mapStanze = mapStanze;
        this.idStanzaCorrente = idStanzaCorrente;
    }

    /**
     * Muove il puntatore della stanza corrente sulla prossima stanza indicata
     * dall'ID passato come parametro, previo controllo dell'esistenza di tale arco
     * nel grafo del percorso.
     *
     * @param idStanza identificativo univoco di una stanza adiacente
     *                 a quella corrente
     *
     * @return un oggetto della classe Stanza o null
     *
     * @throws GraphRunTimeException se il percorso salvato è sbagliato o manca il
     *                               collegamento tra stanze richiesto
     */
    public Stanza moveTo(String idStanza) throws GraphException, GraphRunTimeException {
        Vertex currVertex = mapStanze.get(idStanzaCorrente);
        try {
            if (!currVertex.containsEdge(idStanza)) {
                throw new GraphException("La stanza con ID: "
                        + idStanza
                        + " non è collegata alla stanza corrente o è inesistente.");
            }
            idStanzaCorrente = idStanza;
            return mapStanze.get(idStanza).getStanza();
        }
        catch (NullPointerException err) {
            throw new GraphRunTimeException("Il percorso è errato!", err);
        }
    }

}