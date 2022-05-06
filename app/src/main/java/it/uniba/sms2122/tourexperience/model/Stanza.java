package it.uniba.sms2122.tourexperience.model;

import com.google.gson.annotations.Expose;

import java.util.Map;


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
}