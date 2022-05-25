package it.uniba.sms2122.tourexperience.percorso.stanze;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaStanzeFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaStanzeFragment extends Fragment {

    private Percorso path;

    private Museo museumn;

    private RecyclerView recyclerView;
    private List<Stanza> listaStanze;
    private ImageView imageView;
    private TextView textView;
    private Bundle savedInstanceState;

    private String nomeMuseo;
    private String nomePercorso;

    private PercorsoActivity parent;
    private String lastStanza;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scelta_stanze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = (PercorsoActivity) getActivity();
        parent.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            path = parent.getPath();
            nomeMuseo = parent.getNomeMuseo();
            nomePercorso = parent.getNomePercorso();
        } else {
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati

                path = parent.getPath();
                nomeMuseo = parent.getNomeMuseo();
                nomePercorso = parent.getNomePercorso();

            } else {

                this.nomePercorso = savedInstanceState.getString("nomePercorso");
                this.nomeMuseo = savedInstanceState.getString("nomeMuseo");
            }
        }


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRooms);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        textView = (TextView) view.findViewById(R.id.nome_item_museo);
        imageView = (ImageView) view.findViewById(R.id.icona_item_museo);

        try{
            imageView.setImageURI(Uri.parse(cacheMuseums.get(nomeMuseo).getFileUri()));
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        listaStanze = new ArrayList<>();
        setActionBar(this.nomeMuseo + " - " + this.nomePercorso);
        ((PercorsoActivity)requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (lastStanza == null) { //Primo avvio del fragment
            listaStanze.add(path.getStanzaCorrente());
            textView.setText(getString(R.string.museum, nomeMuseo) + "\n" + getString(R.string.path, nomePercorso));
        } //Torna indietro o qr chiuso
        else if (path.getIdStanzaCorrente().equals(path.getIdStanzaFinale()) && !path.getIdStanzaCorrente().equals(lastStanza)) {
            ((PercorsoActivity) getActivity()).getFgManagerOfPercorso().nextFinePercorsoFragment();
        } else {
            listaStanze = path.getAdiacentNodes();
            textView.setText(getString(R.string.museum, nomeMuseo) + "\n" + getString(R.string.area, path.getStanzaCorrente().getNome()));
        }

        lastStanza = path.getIdStanzaCorrente();

        if (savedInstanceState == null) {
            museumn = cacheMuseums.get(nomeMuseo);
        } else {
            Gson gson = new GsonBuilder().create();
            this.museumn = gson.fromJson(savedInstanceState.getSerializable("museumn").toString(), Museo.class);
            if(museumn == null) //lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0
                museumn = cacheMuseums.get(nomeMuseo);
            else{
                try{
                    imageView.setImageURI(Uri.parse(museumn.getFileUri()));
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

        }

        // Sending reference and data to Adapter
        StanzeAdpter adapter = new StanzeAdpter(getContext(), listaStanze, parent);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        parent.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.path, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId) {
            case R.id.endPath:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.interrupt);
                builder.setTitle(R.string.attention);
                builder.setIcon(R.drawable.ic_baseline_error_24);

                builder.setCancelable(false);
                builder.setPositiveButton(R.string.SI, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        parent.endPath();
                    }
                });

                builder.setNegativeButton(R.string.NO, null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outState.putSerializable("path", gson.toJson(this.path));
        outState.putSerializable("museumn", gson.toJson(this.museumn));
        outState.putString("nomePercorso", this.nomePercorso);
        outState.putString("nomeMuseo", this.nomeMuseo);
    }

    /**
     * Imposta la action bar con pulsante back e titolo.
     * @param title titolo da impostare per l'action bar.
     */
    private void setActionBar(final String title) {
        try {
            final ActionBar actionBar = ((PercorsoActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true); // abilita il pulsante "back" nella action bar
            actionBar.setTitle(title);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}