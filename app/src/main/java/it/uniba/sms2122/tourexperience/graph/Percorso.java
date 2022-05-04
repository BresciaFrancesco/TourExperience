package it.uniba.sms2122.tourexperience.graph;

import java.util.ArrayList;
import java.util.List;
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

    public void setIdStanzaCorrente(String idStanzaCorrente) {
        this.idStanzaCorrente = idStanzaCorrente;
    }

    public String getIdStanzaFinale() {
        return idStanzaFinale;
    }

    public String getDescrizionePercorso() { return descrizionePercorso; }

    public String getNomePercorso() { return nomePercorso; }

    public Map<String, Vertex> getMappa() {
        return mappaStanze;
    }

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

    /**
     * Ritorna una lista di stanze adiacenti a quella in cui ci si trova in quel momento.
     * @return lista di stanze adiacenti a quella corrente.
     */
    public List<Stanza> getAdiacentNodes() throws GraphRunTimeException {
        try {
            Vertex v = mappaStanze.get(idStanzaCorrente);
            Set<String> archi = v.getEdges();
            List<Stanza> stanzeAdiacenti = new ArrayList<>(archi.size());
            int i = 0;
            for (String idS : archi) {
                stanzeAdiacenti.add(i++, mappaStanze.get(idS).getStanza());
            }
            return stanzeAdiacenti;
        }
        catch (NullPointerException e) {
            throw new GraphRunTimeException("Il percorso è errato!", e);
        }
    }

    public Stanza getStanzaCorrente() {
        return mappaStanze.get(idStanzaCorrente).getStanza();
    }

}