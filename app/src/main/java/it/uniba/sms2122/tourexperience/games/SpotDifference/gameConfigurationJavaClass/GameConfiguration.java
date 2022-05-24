package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.gson.Gson;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.configurationObject.DifferencesCoordinates;

public class GameConfiguration {

    AppCompatActivity activityToConfig;
    String artToConfig;

    ConstraintLayout image1Container;
    ConstraintLayout image2Container;

    SetDifferencesButtonCordinates setDifferencesButtonCordinates;

    SetDifferencesButtonClick setAllClickOfDifferencesBtn;
    //SetDifferenceBtnCordinates setAllCordinatesOfDifferencesBtn;

    public GameConfiguration(AppCompatActivity activityToConfig, String artToConfig) {

        this.activityToConfig = activityToConfig;
        this.artToConfig = artToConfig;

        setConfigFile();

        setGameImages();//setto la due immagini da far stampare sull'activity

        //setto la posizione delle differenze e triggero il click su di esse
        setDifferencesButtonCordinates = new SetDifferencesButtonCordinates(image1Container, image2Container);
        setAllClickOfDifferencesBtn = new SetDifferencesButtonClick(activityToConfig);

    }


    /**
     * leggo il file di configurazione e lo parso
     */
    private void setConfigFile() {

        String configurationFile = null;//da inizializzare con il json di configurazione del gioco

        Gson gson = new Gson();
        gson.fromJson(configurationFile, DifferencesCoordinates.class);


    }


    /**
     * funzione per settare la immagine da visualizzare con cui giocare
     */
    private void setGameImages(){

        //setto la prima immagine
        image1Container = activityToConfig.findViewById(R.id.image1Container);//recupero la view su cui settare il bacìkground
        Bitmap image1 = null;//da inizilizzare con l'immagine da recuperare in locale
        Drawable image1Drawable = new BitmapDrawable(image1);//converto la bitmap in risorsa drawable
        image1Container.setBackground(image1Drawable);


        //setto la seconda immagine
        image2Container = activityToConfig.findViewById(R.id.image2Container);//recupero la view su cui settare il bacìkground
        Bitmap image2 = null;//da inizilizzare con l'immagine da recuperare in locale
        Drawable image2Drawable = new BitmapDrawable(image2);//converto la bitmap in risorsa drawable
        image2Container.setBackground(image1Drawable);
    }






}
