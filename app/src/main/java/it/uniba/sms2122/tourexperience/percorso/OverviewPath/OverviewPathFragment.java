package it.uniba.sms2122.tourexperience.percorso.OverviewPath;


import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.utility.ranking.VotiPercorsi;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

public class OverviewPathFragment extends Fragment {

    Percorso path;
    ArrayList<Stanza> stanze;
    View inflater;

    TextView pathNameTextView;
    TextView pathDescriptionTextView;
    TextView pathRoomsOverview;
    Button startPathButton;
    RatingBar ratingBar;
    TextView textRatingBar;

    DatabaseReference db;
    Task<DataSnapshot> snapshotVoti;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater.inflate(R.layout.overview_path_fragment, container, false);
        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            path = ((PercorsoActivity)getActivity()).getPath();

        } else {
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati

                path = ((PercorsoActivity)getActivity()).getPath();
            }
        }

        // Durante la visita al grafo, il puntatore viene alla stanza corrente viene spostato,
        // quindi occorre ripristinarlo dopo la visita.
        stanze = new ArrayList<>();
        String idStanzaCorrente = path.getIdStanzaCorrente();
        setListaStanze();
        path.setIdStanzaCorrente(idStanzaCorrente);

        setDynamicValuesOnView();
        triggerStartPathButton();
    }

    /**
     * Funzione per triggerare il click sul pulsante per far partire la guida
     */
    private void triggerStartPathButton() {

        startPathButton = inflater.findViewById(R.id.startPathButton);
        startPathButton.setOnClickListener(view -> ((PercorsoActivity)getActivity()).getFgManagerOfPercorso().nextSceltaStanzeFragment());
    }


    /**
     * Funzione che si occupa di settare i reali valori dinamici delle viste che formano questa fragment
     */
    private void setDynamicValuesOnView() {

        PercorsoActivity parent = (PercorsoActivity) getActivity();

        pathNameTextView = inflater.findViewById(R.id.pathName);
        pathNameTextView.setText(path.getNomePercorso());

        pathDescriptionTextView = inflater.findViewById(R.id.pathDescription);
        pathDescriptionTextView.setText(path.getDescrizionePercorso());

        pathRoomsOverview = inflater.findViewById(R.id.listaStanze);
        pathRoomsOverview.setText(printList());

        ratingBar = inflater.findViewById(R.id.scorePath);
        textRatingBar = inflater.findViewById(R.id.txtScorePath);

        if(checkConnectivity()) {
            snapshotVoti.addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    String voti = dataSnapshot.getValue(String.class);
                    VotiPercorsi votiPercorsi = new VotiPercorsi(voti);
                    Float media = votiPercorsi.calcolaMedia();
                    if(media == -1){
                        ratingBar.setVisibility(View.GONE);
                    } else {
                        ratingBar.setRating(media);
                        textRatingBar.setText(String.valueOf(Math.round(votiPercorsi.calcolaMedia() * 100.0) / 100.0));
                    }
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
            } catch (Exception e) {
                e.printStackTrace();
            }

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

    // Stampa la lista di stanze del percorso
    // Nota: le opere per ogni stanza le posso recuperare da qui
    private String printList() {
        String lista = "";
        for(Stanza stanza : stanze) {
            lista += stanza.getNome();
            lista += "\n";
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
        if (NetworkConnectivity.check(getContext())) {
            db = FirebaseDatabase.getInstance().getReference("Museums").child(path.getNomeMuseo()).child(path.getNomePercorso());
            snapshotVoti = db.child("Voti").get();
            return true;
        } else {
            Toast.makeText(getContext(), getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
