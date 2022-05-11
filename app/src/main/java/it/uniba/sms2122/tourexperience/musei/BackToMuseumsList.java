package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getAllCachedMuseums;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;

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
        MuseiAdapter adapterMusei = null;
        List<Museo> listaForAdapter = null;
        if (cacheMuseums.isEmpty()) {
            listaForAdapter = new ArrayList<>();
            listaForAdapter.add(new Museo(
                fragment.getResources().getString(R.string.no_result_1),
                fragment.getResources().getString(R.string.no_result_2))
            );
        } else {
            listaForAdapter = getAllCachedMuseums();
        }
        fragment.setListaMusei(listaForAdapter);

        adapterMusei = new MuseiAdapter(fragment, fragment.getListaMusei(), true, fragment.isListaMuseiEmpty());
        recyclerView.setAdapter(adapterMusei);
        fragment.attachQueryTextListener(adapterMusei);
        fab.setImageResource(R.drawable.ic_baseline_add_24);
        fab.setOnClickListener(fragment::listenerFabMusei);
        ((MainActivity)fragment.getActivity()).getSupportActionBar().setTitle(R.string.museums);
    }
}
