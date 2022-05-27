package it.uniba.sms2122.tourexperience.games.SpotDifference;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.GameConfiguration;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.SetDifferencesButtonClick;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileGamesManager;

public class SpotDifferences extends AppCompatActivity {

    private GameConfiguration configuration;
    private SetDifferencesButtonClick configGame;
    private HashMap<String, ImageView> allDifferencesView;
    private LocalFileGamesManager gameFileManager;

    private String artName;
    private Bitmap image1;
    private  Bitmap image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_differences);

        Intent parentIntent = getIntent();

        artName = parentIntent.getStringExtra("artName");

        gameFileManager = new LocalFileGamesManager(getApplicationContext().getFilesDir().toString(),
                parentIntent.getStringExtra("museumName"),
                parentIntent.getStringExtra("roomName"),
                artName);

        /*try {
            Log.e("path jsonfile configuration", gameFileManager.loadSpotTheDifferenceConfigurationFile());
            Log.e("image jsonfile configuration", gameFileManager.loadSpotTheDifferenceImageOf(artName.replace(" ", "_").toLowerCase(Locale.ROOT)).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            configuration = new GameConfiguration(this, gameFileManager, artName.replace(" ", "_").toLowerCase(Locale.ROOT));
        } catch (IOException e) {
            Toast.makeText(this, "impossibile configurare il gioco",
                    Toast.LENGTH_LONG).show();
        }

    }

}