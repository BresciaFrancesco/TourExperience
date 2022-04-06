package it.uniba.sms2122.tourexperience.model;

import android.net.Uri;

public class Museo {
    private String nome;
    private Uri fileUri;

    public Museo(String nome, String filePath) {
        this.nome = nome;
        this.fileUri = Uri.parse(filePath);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }
}