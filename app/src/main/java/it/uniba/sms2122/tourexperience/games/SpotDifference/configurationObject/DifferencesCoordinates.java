package it.uniba.sms2122.tourexperience.games.SpotDifference.configurationObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DifferencesCoordinates {

    ArrayList<HashMap<String, Float>> difference1;
    ArrayList<HashMap<String, Float>> difference2;
    ArrayList<HashMap<String, Float>> difference3;

    /*public void setDifference1(ArrayList<HashMap<String, Float>> difference1) {
        this.difference1 = difference1;
    }

    public void setDifference2(ArrayList<HashMap<String, Float>> difference2) {
        this.difference2 = difference2;
    }

    public void setDifference3(ArrayList<HashMap<String, Float>> difference3) {
        this.difference3 = difference3;
    }*/

    /**
     * Funzione per ottenere la cordinata x dell 1 differenze
     * @return la cordinata x della prima differenza
     */
    public float getXofDifference1(){

        return  difference1.get(0).get("xCordinate");
    }

    /**
     * Funzione per ottenere la cordinata y dell 1 differenze
     * @return la cordinata y della prima differenza
     */
    public float getYofDifference1(){

        return  difference1.get(1).get("yCordinate");
    }

    /**
     * Funzione per ottenere la cordinata x dell 1 differenze
     * @return la cordinata x della prima differenza
     */
    public float getXofDifference2(){

        return  difference2.get(0).get("xCordinate");
    }

    /**
     * Funzione per ottenere la cordinata y dell 1 differenze
     * @return la cordinata y della prima differenza
     */
    public float getYofDifference2(){

        return  difference2.get(1).get("yCordinate");
    }

    /**
     * Funzione per ottenere la cordinata x dell 1 differenze
     * @return la cordinata x della prima differenza
     */
    public float getXofDifference3(){

        return  difference3.get(0).get("xCordinate");
    }

    /**
     * Funzione per ottenere la cordinata y dell 1 differenze
     * @return la cordinata y della prima differenza
     */
    public float getYofDifference3(){

        return  difference3.get(1).get("yCordinate");
    }


}
