package it.uniba.sms2122.tourexperience.model;

import java.util.Map;


public class Stanza {

    private String id;
    private String nome;
    private Map<String, Opera> opere;


    public Stanza() {}

    public Stanza(String id, String nome, Map<String, Opera> opere) {
        this.id = id;
        this.nome = nome;
        this.opere = opere;
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

}