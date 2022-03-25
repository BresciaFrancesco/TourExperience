package it.uniba.sms2122.tourexperience.graph;

import java.util.Set;

import it.uniba.sms2122.tourexperience.model.Stanza;

public class Vertex {
    private Stanza stanza;
    private Set<String> archi;


    public Vertex(Stanza stanza, Set<String> archi) {
        this.stanza = stanza;
        this.archi = archi;
    }

    public Stanza getStanza() {
        return stanza;
    }

    public String getStanzaId() {
        return stanza.getId();
    }

    public boolean containsEdge(String idStanza) {
        return archi.contains(idStanza);
    }
}