package it.uniba.sms2122.tourexperience.percorso.pagina_museo;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getPercorsiByMuseo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.StringUtility;

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
        this.nomeMuseo = ((PercorsoActivity) requireActivity()).getNomeMuseo();
        return inflater.inflate(R.layout.fragment_museo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nomiPercorsi = new ArrayList<>();

        // Recupera le immagini con il metodo getMuseoImages()
        immagini = ((PercorsoActivity) requireActivity())
                .getLocalFileMuseoManager().getMuseoImages(nomeMuseo);

        viewPager = view.findViewById(R.id.museum_viewpager);
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
        nomiPercorsi.addAll(getPercorsiByMuseo(nomeMuseo, view.getContext()));

        RecycleViewAdapter adapter = new RecycleViewAdapter(getContext(),nomiPercorsi);
        recycleView.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(str -> {
            ((PercorsoActivity) requireActivity()).setValuePath(nomiPercorsi.get(Integer.parseInt(str)));
            ((PercorsoActivity) requireActivity()).getFgManagerOfPercorso().nextPercorsoFragment();
        });
    }

    /**
     * Setta immagine e descrizione museo
     */
    private void setDynamicValues() throws IOException {

        PercorsoActivity parent = (PercorsoActivity) requireActivity();
        assert parent != null;
        Museo museo = parent.getLocalFileMuseoManager().getMuseoByName(nomeMuseo);

        // Setta descrizione museo
        textView.setText(StringUtility.decodeUTF8(museo.getDescrizione()));
    }
}