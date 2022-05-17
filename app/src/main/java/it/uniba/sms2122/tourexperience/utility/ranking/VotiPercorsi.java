package it.uniba.sms2122.tourexperience.utility.ranking;

import java.util.ArrayList;
import java.util.List;

public class VotiPercorsi {
    private String stringaVoti;
    private List<Float> voti;

    public VotiPercorsi(String stringaVoti){
        this.stringaVoti = stringaVoti;
        inizializzaVoti();
    }

    private void inizializzaVoti (){
        voti = new ArrayList<>();
        String aux = "";

        for (int i = 0; i < stringaVoti.length(); i++){
            if(stringaVoti.charAt(i) != ';') {
                aux = aux.concat(String.valueOf(stringaVoti.charAt(i)));
            } else {
                voti.add(Float.valueOf(aux));
                aux = "";
            }
        }

        if(!aux.isEmpty()){
            voti.add(Float.valueOf(aux));
            aux = "";
        }
    }

    public float calcolaMedia(){
        float media = 0;

        for(int i = 0; i < voti.size(); i++){
            media += voti.get(i);
        }

        return media/voti.size();
    }

    public String getStringaVoti() {
        return stringaVoti;
    }
}
