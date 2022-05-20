package it.uniba.sms2122.tourexperience.games.SpotDifference;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import java.util.HashMap;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.GameConfiguration;
import it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass.SetDifferencesButtonClick;

public class SpotDifferences extends AppCompatActivity {

    private GameConfiguration configuration;
    private SetDifferencesButtonClick configGame;
    private HashMap<String, ImageView> allDifferencesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_differences);


        configuration = new GameConfiguration(this, "artTOconfigure");


    }
}