package it.uniba.sms2122.tourexperience.games.SpotDifference;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.GameConfiguration;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.SetDifferencesButtonClick;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileGamesManager;

public class SpotDifferences extends AppCompatActivity {

    private GameConfiguration configuration;
    private SetDifferencesButtonClick configGame;
    private HashMap<String, ImageView> allDifferencesView;
    private LocalFileGamesManager gameFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_differences);

        Intent parentIntent = getIntent();

        gameFileManager = new LocalFileGamesManager(getApplicationContext().getFilesDir().toString(),
                parentIntent.getStringExtra("museumName"),
                parentIntent.getStringExtra("roomName"),
                parentIntent.getStringExtra("artName"));


        //configuration = new GameConfiguration(this, "artTOconfigure");


    }
}