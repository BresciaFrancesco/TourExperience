package it.uniba.sms2122.tourexperience.utility.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MuseoDatabase {

    private String nomeMuseo;
    private List<String> nomePercorsi;
    private HashMap<String, String> numeroStarts;
    private HashMap<String, VotiPercorsi> voti;

    public MuseoDatabase() {
        this.nomePercorsi = new ArrayList<>();
        this.voti = new HashMap<>();
        this.numeroStarts = new HashMap<>();
    }

    public void addNumeroStarts(String stringaNumeroStarts) {
        numeroStarts.put(nomePercorsi.get(nomePercorsi.size()-1), stringaNumeroStarts);
    }

    public void addVoti(VotiPercorsi votiPercorsi) {
        voti.put(nomePercorsi.get(nomePercorsi.size()-1), votiPercorsi);
    }

    public void setNomeMuseo(String nomeMuseo) {
        this.nomeMuseo = nomeMuseo;
    }

    public void addNomePercorso(String nome) {
        nomePercorsi.add(nome);
    }

    public String getNomeMuseo() {
        return nomeMuseo;
    }

    public HashMap<String, String> getNumeroStarts() {
        return numeroStarts;
    }

    public HashMap<String, VotiPercorsi> getVoti() {
        return voti;
    }
}
