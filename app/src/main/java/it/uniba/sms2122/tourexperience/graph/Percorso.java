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
    private Map<String, Vertex> mappaStanze;

    /** Puntatore alla stanza corrente all'interno del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private String idStanzaCorrente;

    /** Puntatore alla stanza finale del percorso. */
    private String idStanzaFinale;

    /** Id univoco del museo che contiene questo percorso. */
    private String idMuseo;

    /** Nome del museo che contiene questo percorso. */
    private String nomeMuseo;





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
            if (!currVertex.containsEdge(idProssimaStanza)) {
                throw new GraphException("La stanza con ID: " + idProssimaStanza
                        + " non è collegata alla stanza corrente o è inesistente.");
            }
            Stanza stanza = mappaStanze.get(idProssimaStanza).getStanza();
            idStanzaCorrente = idProssimaStanza;
            return stanza;
        }
        catch (NullPointerException err) {
            throw new GraphRunTimeException("Il percorso è errato!", err);
        }
    }

}