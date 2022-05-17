package it.uniba.sms2122.tourexperience.percorso.pagina_museo;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;

public class MuseoFragment extends Fragment {

    String nomeMuseo;
    TextView textView;
    RecyclerView recycleView;
    ArrayList<String> nomiPercorsi;
    List<String> immagini;

    // Posso recuperarlo anche nell'adapter
    public static ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.nomeMuseo = ((PercorsoActivity)getActivity()).getNomeMuseo();
        return inflater.inflate(R.layout.fragment_museo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nomiPercorsi = new ArrayList<>();

        // Recupera le immagini con il metodo getMuseoImages()
        immagini = ((PercorsoActivity)getActivity())
                .getLocalFileMuseoManager().getMuseoImages(nomeMuseo);

        viewPager = (ViewPager)view.findViewById(R.id.museum_viewpager);
        // Passa la lista di immagini al viewPagerAdapter
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getContext(),immagini);
        viewPager.setAdapter(viewPagerAdapter);

        textView = view.findViewById(R.id.museum_description);
        recycleView = view.findViewById(R.id.routes_recycle_view);

        try {
            setDynamicValues();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prende i nomi dei percorsi dalla cache locale
        nomiPercorsi.addAll(cachePercorsiInLocale.get(nomeMuseo));

        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(),nomiPercorsi);
        recycleView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(str -> {
            Bundle bundle = new Bundle();
            bundle.putString("nome_percorso", nomiPercorsi.get(Integer.parseInt(str)));
            ((PercorsoActivity) getActivity()).getFgManagerOfPercorso().nextPercorsoFragment(bundle);
        });
    }

    /**
     * Setta immagine e descrizione museo
     */
    private void setDynamicValues() throws IOException {

        PercorsoActivity parent = (PercorsoActivity) getActivity();
        Museo museo = parent.getLocalFileMuseoManager().getMuseoByName(nomeMuseo);

        // Setta descrizione museo
        textView.setText(museo.getDescrizione());
    }
}