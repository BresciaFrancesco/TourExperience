package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getAllCachedMuseums;

import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;


public class BackToMuseumsList implements Back {
    private final SceltaMuseiFragment fragment;
    private final Button backCloudButton;
    private final FloatingActionButton fab;

    public BackToMuseumsList(final SceltaMuseiFragment fragment,
                             final Button backCloudButton,
                             final FloatingActionButton fab) {
        this.fragment = fragment;
        this.backCloudButton = backCloudButton;
        this.fab = fab;
    }

    @Override
    public void back(View view) {
        try {
            List<Museo> listaForAdapter;
            if (cacheMuseums.isEmpty()) {
                listaForAdapter = new ArrayList<>();
                listaForAdapter.add(Museo.getMuseoVuoto(fragment.getResources()));
            } else {
                listaForAdapter = getAllCachedMuseums();
            }
            fragment.setListaMusei(listaForAdapter);

            fragment.attachNewAdapter(new MuseiAdapter(
                    fragment,
                    fragment.getListaMusei(),
                    true
            ));
            backCloudButton.setVisibility(View.GONE);
            fab.setVisibility(View.VISIBLE);
            Objects.requireNonNull(((MainActivity) fragment.requireActivity())
                    .getSupportActionBar()).setTitle(R.string.museums);
        }
        catch (NullPointerException | IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
