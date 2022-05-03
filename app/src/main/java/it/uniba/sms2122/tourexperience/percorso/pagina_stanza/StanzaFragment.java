package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.os.Bundle;

import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.RecycleViewAdapter;

public class StanzaFragment extends Fragment {

    ImageView imageView;
    TextView textView;
    RecyclerView recycleView;
    ArrayList<String> nomiOpereVicine;
    ArrayList<Integer> immaginiOpereVicine;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stanza, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.stanza_description);
        //TODO: settare descrizione dal json
        textView.setText("Descrizione stanza...");

        recycleView = view.findViewById(R.id.opere_recycle_view);

        //TODO: Recuperare lista opere vicine
        nomiOpereVicine = new ArrayList<>();
        immaginiOpereVicine = new ArrayList<>();

        /* Dati di prova
        nomiOpereVicine.add("opera1");
        nomiOpereVicine.add("Opera 2");
        immaginiOpereVicine.add(R.drawable.ic_museum);
        immaginiOpereVicine.add(R.drawable.ic_puzzle);
         */

        it.uniba.sms2122.tourexperience.percorso.pagina_stanza.RecycleViewAdapter adapter =
                new it.uniba.sms2122.tourexperience.percorso.pagina_stanza.RecycleViewAdapter(getContext(),nomiOpereVicine,immaginiOpereVicine);
        recycleView.setAdapter(adapter);

        /* TODO: implementare bundle per opera_activity
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(str -> {
            Bundle bundle = new Bundle();
            bundle.putString("nome_percorso", nomiPercorsi.get(Integer.parseInt(str)));
            ((PercorsoActivity) getActivity()).nextPercorsoFragment(bundle);
        }); */
    }
}