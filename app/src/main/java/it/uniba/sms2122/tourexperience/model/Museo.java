package it.uniba.sms2122.tourexperience.model;

import androidx.annotation.Nullable;

public class Museo {
    private String nome;
    private String citta;
    private String descrizione;
    private String tipologia;
    private String fileUri;

    public Museo(String nome) {
        this.nome = nome;
        this.citta = "";
        this.descrizione = "";
        this.tipologia = "";
        this.fileUri = "";
    }

    public Museo(String nome, String citta) {
        this.nome = nome;
        this.citta = citta;
        this.tipologia = "";
        this.fileUri = "";
    }

    public Museo(String nome, String citta, String descrizione, String tipologia, String fileUri) {
        this.nome = nome;
        this.citta = citta;
        this.descrizione = descrizione;
        this.tipologia = tipologia;
        this.fileUri = fileUri;
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCitta() { return citta; }

    public void setCitta(String citta) { this.citta = citta; }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getTipologia() { return tipologia; }

    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        return builder.append("Nome: ").append(this.nome)
                .append(", Citta': ").append(this.citta)
                .append(", Tipologia: ").append(this.tipologia)
                .append(", URI: ").append(this.fileUri)
                .toString();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Museo)) return false;
        Museo m = (Museo) obj;
        if (!m.getNome().equals(this.getNome())) return false;
        return m.getCitta().equals(this.getCitta());
    }
}