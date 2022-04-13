package it.uniba.sms2122.tourexperience.model;

import android.net.Uri;

public class Museo {
    private String nome;
    private String citta;
    private String tipologia;
    private Uri fileUri;

    public Museo(String nome, String citta, String tipologia, String filePath) {
        this.nome = nome;
        this.citta = citta;
        this.tipologia = tipologia;
        this.fileUri = Uri.parse(filePath);
    }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCitta() { return citta; }

    public void setCitta(String citta) { this.citta = citta; }

    public String getTipologia() { return tipologia; }

    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }
}