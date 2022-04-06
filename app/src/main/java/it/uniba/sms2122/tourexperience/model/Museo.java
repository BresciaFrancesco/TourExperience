package it.uniba.sms2122.tourexperience.model;

public class Museo {

    private int id;
    private String nome;
    private String citta;
    private double valutazioneMedia;

    public Museo(int id, String nome, String citta, double valutazioneMedia) {
        this.id = id;
        this.nome = nome;
        this.citta = citta;
        this.valutazioneMedia = valutazioneMedia;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCitta() {
        return citta;
    }

    public double getValutazioneMedia() {
        return valutazioneMedia;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public void setValutazioneMedia(double valutazioneMedia) {
        this.valutazioneMedia = valutazioneMedia;
    }
}
