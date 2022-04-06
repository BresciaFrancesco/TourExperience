package it.uniba.sms2122.tourexperience.musei;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.LocalFileMuseoManager;


public class SceltaMusei extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Museo> listaMusei;
    private LocalFileMuseoManager localFileManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scelta_musei);

        recyclerView = findViewById(R.id.recyclerViewMusei);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        localFileManager = new LocalFileMuseoManager(getApplicationContext().getFilesDir().toString());
    }


    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (listaMusei == null) {
            try {
                listaMusei = (ArrayList<Museo>) localFileManager.getListMusei();
            }
            catch (IOException e) {
                Log.e("SCELTA_MUSEI_ERROR", "Lista musei non caricata.");
                listaMusei = new ArrayList<>();
                e.printStackTrace();
            }
        }
        if (listaMusei.isEmpty()) {
            listaMusei.add(new Museo("Non ci sono musei", ""));
        }

        // Sending reference and data to Adapter
        MuseiAdapter adapter = new MuseiAdapter(this, listaMusei);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        ArrayList<String> nomiMusei = new ArrayList<>();
        ArrayList<String> uriImmagini = new ArrayList<>();
        for (int i = 0; i < listaMusei.size(); i++) {
            nomiMusei.add(listaMusei.get(i).getNome());
            uriImmagini.add(listaMusei.get(i).getFileUri().toString());
        }
        outState.putStringArrayList("nomi_musei", nomiMusei);
        outState.putStringArrayList("immagini_musei", uriImmagini);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        listaMusei = new ArrayList<>();
        ArrayList<String> nomiMusei = savedInstanceState.getStringArrayList("nomi_musei");
        ArrayList<String> uriImmagini = savedInstanceState.getStringArrayList("immagini_musei");
        for (int i = 0; i < nomiMusei.size(); i++) {
            listaMusei.add(new Museo(
                    nomiMusei.get(i),
                    uriImmagini.get(i)
            ));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }
}