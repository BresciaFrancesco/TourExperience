package it.uniba.sms2122.tourexperience.pagina_stanza;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import it.uniba.sms2122.tourexperience.R;

public class StanzaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stanza);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}