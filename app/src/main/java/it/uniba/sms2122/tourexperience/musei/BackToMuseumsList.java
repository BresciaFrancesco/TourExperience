package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.getAllCachedMuseums;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;

public class BackToMuseumsList implements Back {
    private final MainActivity mainActivity;
    private final SceltaMuseiFragment fragment;
    private final RecyclerView recyclerView;
    private final FloatingActionButton fab;
    private final ProgressBar progressBar;

    public BackToMuseumsList(final MainActivity mainActivity,
                             final SceltaMuseiFragment fragment,
                             final RecyclerView recyclerView,
                             final FloatingActionButton fab,
                             final ProgressBar progressBar) {
        this.mainActivity = mainActivity;
        this.fragment = fragment;
        this.recyclerView = recyclerView;
        this.fab = fab;
        this.progressBar = progressBar;
    }

    @Override
    public void back(View view) {
        fragment.setListaMusei(getAllCachedMuseums());
        MuseiAdapter adapterMusei = new MuseiAdapter(mainActivity, progressBar, fragment.getListaMusei(), true);
        recyclerView.setAdapter(adapterMusei);
        fragment.attachQueryTextListener(adapterMusei);
        fab.setImageResource(R.drawable.ic_baseline_add_24);
        fab.setOnClickListener(fragment::listenerFabMusei);
        fab.setBackgroundTintList(
            ColorStateList.valueOf(
                fragment.getResources().getColor(R.color.mtrl_fab_button_default, null))
        );
        mainActivity.getSupportActionBar().setTitle(R.string.museums);
    }
}
