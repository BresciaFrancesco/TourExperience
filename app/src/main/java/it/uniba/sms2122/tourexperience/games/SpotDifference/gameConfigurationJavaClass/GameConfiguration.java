package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;

import java.io.IOException;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.configurationObject.DifferencesCoordinates;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileGamesManager;

public class GameConfiguration {

    AppCompatActivity activityToConfig;
    String artNameToConfig;
    LocalFileGamesManager gameFileManager;

    ConstraintLayout image1Container;
    ConstraintLayout image2Container;

    SetDifferencesButtonCordinates setDifferencesButtonCordinates;
    SetDifferencesButtonClick setAllClickOfDifferencesBtn;

    public GameConfiguration(AppCompatActivity activityToConfig, LocalFileGamesManager gameFileManager, String artNameToConfig) throws IOException {

        this.activityToConfig = activityToConfig;
        this.artNameToConfig = artNameToConfig;
        this.gameFileManager = gameFileManager;

        setConfigFile();
        setGameImages();//setto la due immagini da far stampare sull'activity

        //setto la posizione delle differenze e triggero il click su di esse
        setDifferencesButtonCordinates = new SetDifferencesButtonCordinates(image1Container, image2Container, setConfigFile());
        setAllClickOfDifferencesBtn = new SetDifferencesButtonClick(activityToConfig);

    }


    /**
     * leggo il file di configurazione e lo parso creando un instanza
     */
    private DifferencesCoordinates setConfigFile() throws IOException {

        String configurationFile = gameFileManager.loadSpotTheDifferenceConfigurationFile();//da inizializzare con il json di configurazione del gioco

        Gson gson = new Gson();
        return gson.fromJson(configurationFile,DifferencesCoordinates.class);
    }


    /**
     * funzione per settare la immagine da visualizzare con cui giocare
     */
    private void setGameImages() throws IOException {

        //setto la prima immagine
        image1Container = activityToConfig.findViewById(R.id.image1Container);//recupero la view su cui settare il bacìkground
        Bitmap image1 = gameFileManager.loadSpotTheDifferenceImageOf(artNameToConfig);//da inizilizzare con l'immagine da recuperare in locale
        Drawable image1Drawable = new BitmapDrawable(image1);//converto la bitmap in risorsa drawable
        image1Container.setBackground(image1Drawable);


        //setto la seconda immagine
        image2Container = activityToConfig.findViewById(R.id.image2Container);//recupero la view su cui settare il bacìkground
        Bitmap image2 = gameFileManager.loadSpotTheDifferenceImageOf(artNameToConfig + "_diff");//da inizilizzare con l'immagine da recuperare in locale
        Drawable image2Drawable = new BitmapDrawable(image2);//converto la bitmap in risorsa drawable
        image2Container.setBackground(image2Drawable);
    }
}
