package it.uniba.sms2122.tourexperience.percorso.OverviewPath;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.database.CacheGames;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;
import it.uniba.sms2122.tourexperience.utility.ranking.VotiPercorsi;

public class OverviewPathFragment extends Fragment {

    Percorso path;
    ArrayList<Stanza> stanze;
    ArrayList<String> opere;
    View inflater;

    RecyclerView recyclerView;
    TextView pathNameTextView;
    TextView pathDescriptionTextView;
    Button startPathButton;
    RatingBar ratingBar;
    TextView textRatingBar;

    DatabaseReference db;
    Task<DataSnapshot> snapshotVoti;

    LocalFilePercorsoManager localFilePercorsoManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater.inflate(R.layout.overview_path_fragment, container, false);
        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        PercorsoActivity parent = (PercorsoActivity) requireActivity();

        if (savedInstanceState == null) {
            path = parent.getPath();

        } else {
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati

                path = parent.getPath();
            }
        }
        // Elimino tutti i dati da questa cache,
        // ovvero elimino tutti i minigiochi già svolti,
        // affinché possa svolgerli ancora
        final CacheGames cacheGames = new CacheGames(view.getContext());
        cacheGames.deleteAll();

        // Serve per caricare immediatamente le stanze e le opere
        localFilePercorsoManager = parent.getLocalFilePercorsoManager();

        stanze = new ArrayList<>();
        opere = new ArrayList<>();

        // Visita del grafo, caricamento stanze e opere negli array
        String idStanzaCorrente = path.getIdStanzaCorrente();
        setListaStanze();

        recyclerView = view.findViewById(R.id.rooms_recycle_view);
        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(),getListaNomiStanze(),getListaOpereStanze());
        recyclerView.setAdapter(adapter);

        // Durante la visita al grafo, il puntatore alla stanza corrente viene spostato,
        // quindi occorre ripristinarlo dopo la visita.
        path.setIdStanzaCorrente(idStanzaCorrente);

        setDynamicValuesOnView();
        triggerStartPathButton();
    }

    /**
     * Funzione per triggerare il click sul pulsante per far partire la guida
     */
    private void triggerStartPathButton() {

        startPathButton = inflater.findViewById(R.id.startPathButton);
        startPathButton.setOnClickListener(view -> ((PercorsoActivity)requireActivity()).getFgManagerOfPercorso().nextSceltaStanzeFragment());
    }

    /**
     * Funzione che si occupa di settare i reali valori dinamici delle viste che formano questa fragment
     */
    private void setDynamicValuesOnView() {
        pathNameTextView = inflater.findViewById(R.id.pathName);
        pathNameTextView.setText(path.getNomePercorso());

        pathDescriptionTextView = inflater.findViewById(R.id.pathDescription);
        pathDescriptionTextView.setText(path.getDescrizionePercorso());

        ratingBar = inflater.findViewById(R.id.scorePath);
        textRatingBar = inflater.findViewById(R.id.txtScorePath);

        if(checkConnectivity()) {
            snapshotVoti.addOnSuccessListener(dataSnapshot -> {
                String voti = dataSnapshot.getValue(String.class);
                VotiPercorsi votiPercorsi = new VotiPercorsi(voti);
                float media = votiPercorsi.calcolaMedia();
                if(media == -1){
                    ratingBar.setVisibility(View.GONE);
                } else {
                    ratingBar.setRating(media);
                    textRatingBar.setText(String.valueOf(Math.round(votiPercorsi.calcolaMedia() * 100.0) / 100.0));
                }
            });
        } else {
            ratingBar.setVisibility(View.GONE);
        }
    }

    private void setListaStanze() {

        // Coda per le stanze visitate
        LinkedList<Stanza> coda = new LinkedList<>();

        // Elementi false di default
        boolean[] visitato = new boolean[10];

        // Leggo la prima stanza e la aggiungo alla coda
        Stanza corrente = path.getStanzaCorrente();
        coda.add(corrente);

        while(!coda.isEmpty()) {
            // Rimuovo la stanza corrente dalla coda

            corrente = coda.pop();
            int correnteID = getStanzaID(corrente);

            // Sposto il puntatore sul nodo adiacente
            try {
                path.moveTo(corrente.getId());
                localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
            } catch (Exception ignored) {}

            if(!visitato[correnteID]) {
                // Segno il nodo come visitato
                visitato[correnteID] = true;
                stanze.add(corrente);

                // Recupero la lista dei nodi adiacenti a aggiungo alla coda quelli non ancora visitati
                List<Stanza> nodiAdiacenti = path.getAdiacentNodes();
                for(Stanza stanza : nodiAdiacenti) {
                    int stanzaID = getStanzaID(stanza);
                    if(!visitato[stanzaID])
                        coda.add(stanza);
                }
            }
        }
    }

    // Ritorna l'ID della stanza in formato intero
    // Nota: si possono avere massimo 10 stanze in questo caso
    private int getStanzaID(Stanza stanza) {
        String id = stanza.getId();
        id = id.substring(id.length() - 1);
        return Integer.parseInt(id);
    }

    // Ritorna la lista dei nomi delle stanze del percorso
    private ArrayList<String> getListaNomiStanze() {
        ArrayList<String> lista = new ArrayList<>();
        for(Stanza stanza : stanze)
            lista.add(stanza.getNome());
        return lista;
    }

    // Ritorna la lista delle opere principali delle stanze del percorso
    private ArrayList<String> getListaOpereStanze() {
        ArrayList<String> lista = new ArrayList<>();
        for(Stanza stanza : stanze) {
            try{
                lista.add(Objects.requireNonNull(stanza.getOpere().get(stanza.getId() + "0000")).getPercorsoImg());
            }catch (NullPointerException ignored){}

        }
        return lista;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outState.putSerializable("path", gson.toJson(this.path));
    }

    public boolean checkConnectivity() {
        if (NetworkConnectivity.check(requireContext())) {
            db = FirebaseDatabase.getInstance().getReference("Museums").child(path.getNomeMuseo()).child(path.getNomePercorso());
            snapshotVoti = db.child("Voti").get();
            return true;
        } else {
            Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}