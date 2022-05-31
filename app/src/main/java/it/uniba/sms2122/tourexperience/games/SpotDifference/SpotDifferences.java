package it.uniba.sms2122.tourexperience.games.SpotDifference;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.database.GameTypes;
import it.uniba.sms2122.tourexperience.games.SaveScore;
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

    private int gameScore = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_differences);

        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent parentIntent = getIntent();

        artName = parentIntent.getStringExtra("artName");

        gameFileManager = new LocalFileGamesManager(getApplicationContext().getFilesDir().toString(),
                parentIntent.getStringExtra("museumName"),
                parentIntent.getStringExtra("roomName"),
                artName);

        try {
            configuration = new GameConfiguration(this, gameFileManager, artName.replace(" ", "_").toLowerCase(Locale.ROOT));
        } catch (IOException e) {
           e.printStackTrace();
        }

    }

    /**
     * funzione che si occupa di incrementare il punteggio ottenuto nel minigioco
     */
    public void incraseGameScore() {
        this.gameScore++;

        if(gameScore == 3){
            closeGameAndSaveScore();
        }
    }


    /**
     * funzione che si occupa di gestire la chiusura del gioco se questo è stato completato quindi presenta un punteggio da salvare
     */
    public void closeGameAndSaveScore(){

        SaveScore saveScore = new SaveScore(this, GameTypes.DIFF, (double)gameScore);
        saveScore.save(new View(this), artName);

        AppCompatActivity thisActivity = this;

        new AlertDialog.Builder(this).setTitle(getString(R.string.spot_the_difference_title_game))
                .setMessage(getString(R.string.spot_the_difference_close_message))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        thisActivity.finish();
                    }
                }).show();
    }

    /**
     * funzione che si occupa di gestire la chiusura del gioco se questo non è stato completato
     */
    public void quitGame(){

        AppCompatActivity thisActivity = this;
        new AlertDialog.Builder(this).setTitle(getString(R.string.spot_the_difference_title_game))
                .setMessage(getString(R.string.spot_the_difference_quit_message))
                .setPositiveButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                thisActivity.finish();
            }
        }).show();
    }


    public boolean onOptionsItemSelected(MenuItem item){
        this.quitGame();
        return true;
    }
}