package it.uniba.sms2122.tourexperience.model;


import java.util.Objects;

public class Opera {

    private String id;
    private String nome;
    private String percorsoImg;
    private String descrizione;

    public Opera() {}

    public Opera(String id, String nome, String percorsoImg, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.percorsoImg = percorsoImg;
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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getPercorsoImg() {
        return percorsoImg;
    }

    public void setPercorsoImg(String percorsoImg) {
        this.percorsoImg = percorsoImg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Opera opera = (Opera) o;
        return Objects.equals(id, opera.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}