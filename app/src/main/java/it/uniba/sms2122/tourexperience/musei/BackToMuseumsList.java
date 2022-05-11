package it.uniba.sms2122.tourexperience.musei;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getAllCachedMuseums;


public class BackToMuseumsList implements Back {
    private final SceltaMuseiFragment fragment;
    private final RecyclerView recyclerView;
    private final FloatingActionButton fab;

    public BackToMuseumsList(final SceltaMuseiFragment fragment,
                             final RecyclerView recyclerView,
                             final FloatingActionButton fab) {
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.fab = fab;
    }

    @Override
    public void back(View view) {
        List<Museo> listaForAdapter = null;
        if (cacheMuseums.isEmpty()) {
            listaForAdapter = new ArrayList<>();
            listaForAdapter.add(Museo.getMuseoVuoto(fragment.getResources()));
        } else {
            listaForAdapter = getAllCachedMuseums();
        }
        fragment.setListaMusei(listaForAdapter);

        fragment.attachNewAdapter(new MuseiAdapter(fragment, fragment.getListaMusei(), true));
        fab.setImageResource(R.drawable.ic_baseline_add_24);
        fab.setOnClickListener(fragment::listenerFabMusei);
        ((MainActivity)fragment.getActivity()).getSupportActionBar().setTitle(R.string.museums);
    }
}
