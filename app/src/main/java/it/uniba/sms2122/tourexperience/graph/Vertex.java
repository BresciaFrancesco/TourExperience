package it.uniba.sms2122.tourexperience.graph;

import java.util.Set;

import it.uniba.sms2122.tourexperience.model.Stanza;

/**
 * Classe che rappresenta un vertice del grafo Percorso
 */
public class Vertex {

    /** Stanza rappresentante il vertice del grafo Percorso */
    private Stanza stanza;

    /** Collegamenti tra le stanze del grafo */
    private Set<String> archi;


    /**
     * Costruttore della classe Vertex.
     * @param stanza stanza rappresentante il vertice del grafo.
     * @param archi collegamenti tra le stanze del grafo.
     */
    public Vertex(Stanza stanza, Set<String> archi) {
        this.stanza = stanza;
        this.archi = archi;
    }

    /**
     * Ritorna la stanza che rappresenta questo vertice.
     * @return istanza della classe Stanza.
     */
    public Stanza getStanza() {
        return stanza;
    }

    /**
     * Chiama il metodo "contains" del Set "archi".
     *
     * @param idStanza id della stanza per cui controllare l'esistenza
     *                 di un collegamento nel grafo.
     *
     * @return true se esiste un collegamento nel grafo tra la stanza
     *         corrente e la stanza passata in input, false altrimenti.
     */
    public boolean containsEdge(String idStanza) {
        return archi.contains(idStanza);
    }
}