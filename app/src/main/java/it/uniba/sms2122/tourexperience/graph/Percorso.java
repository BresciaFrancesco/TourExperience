package it.uniba.sms2122.tourexperience.graph;

import android.util.Log;

import java.util.Map;

import it.uniba.sms2122.tourexperience.model.Stanza;


public class Percorso {

    /** Mappa che rappresenta il grafo del percorso. */
    private Map<String, Vertex> mapStanze;

    /** Puntatore alla stanza corrente all'interno del grafo.
     *  Inizialmente, punta alla stanza di partenza del percorso. */
    private String idStanzaCorrente;


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
     */
    public Stanza moveTo(String idStanza) {
        try {
            Vertex currVertex = mapStanze.get(idStanzaCorrente);
            Stanza stanza = null;
            if (currVertex.containsEdge(idStanza)) {
                idStanzaCorrente = idStanza;
                stanza = mapStanze.get(idStanza).getStanza();
            }
            return stanza;
        }
        catch (NullPointerException e) {
            Log.e("NullPointerException", "Percorso::moveTo");
            return null;
        }
    }

}