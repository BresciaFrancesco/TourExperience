package it.uniba.sms2122.tourexperience.percorso.stanze;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getAllCachedMuseums;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.utility.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.LocalFileStanzaManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaStanzeFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaStanzeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Museo> listaMusei;
    private List<Stanza> listaStanze;
    private ImageView imageView;
    private TextView textView;
    private LocalFileMuseoManager localFileMuseoManager;
    private LocalFileStanzaManager localFileStanzaManager;

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

        localFileMuseoManager = new LocalFileMuseoManager(getContext().getFilesDir().toString());
        localFileStanzaManager = new LocalFileStanzaManager(getContext().getFilesDir().toString());
    }

    @Override
    public void onResume() {
        super.onResume();

        int position = 0;
        String nomePercorso = "percorso_1";
        listaMusei = getAllCachedMuseums();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            position = bundle.getInt("position");
            nomePercorso = bundle.getString("nomePercorso");
        }

        imageView.setImageURI(Uri.parse(listaMusei.get(position).getFileUri()));
        textView.setText(listaMusei.get(position).getNome());

        // Sending reference and data to Adapter
        StanzeAdpter adapter = new StanzeAdpter(getContext(), listaStanze);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }
}