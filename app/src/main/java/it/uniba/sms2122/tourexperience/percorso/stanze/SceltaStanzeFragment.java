package it.uniba.sms2122.tourexperience.percorso.stanze;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.cache.CacheMuseums;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaStanzeFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaStanzeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Stanza> listaStanze;
    private ImageView imageView;
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scelta_stanze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRooms);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        textView = (TextView) view.findViewById(R.id.nome_item_museo);
        imageView = (ImageView) view.findViewById(R.id.icona_item_museo);
    }

    @Override
    public void onResume() {
        super.onResume();

        PercorsoActivity parentActivity = (PercorsoActivity) getActivity();

        String nomePercorso = parentActivity.getNomePercorso();
        String nomeMuseo = parentActivity.getNomeMuseo();

        Optional<Percorso> pathContainer = parentActivity.getLocalFilePercorsoManager().getPercorso(nomeMuseo, nomePercorso);
        Percorso pathObj = new Percorso();

        if (pathContainer.isPresent()) {
            pathObj = pathContainer.get();
            parentActivity.getLocalFilePercorsoManager().createStanzeAndOpereInThisAndNextStanze(pathObj);

            listaStanze = pathObj.getAdiacentNodes();

        } else {
            Log.e("percorso non trovato", "percorso non trovato");
        }

        imageView.setImageURI(Uri.parse(cacheMuseums.get(nomeMuseo).getFileUri()));
        textView.setText("Museo: " + nomeMuseo + "\nStanza: " + pathObj.getStanzaCorrente().getNome());

        // Sending reference and data to Adapter
        StanzeAdpter adapter = new StanzeAdpter(getContext(), listaStanze);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }
}