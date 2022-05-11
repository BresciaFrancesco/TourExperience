package it.uniba.sms2122.tourexperience.percorso.pagina_opera;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.imageanddescription.ImageAndDescriptionFragment;
import it.uniba.sms2122.tourexperience.main.HomeFragment;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;


public class OperaFragment extends Fragment {

    private Opera opera;
    private String nomeStanza;
    private ImageAndDescriptionFragment fragment;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private final FragmentManager fragmentManager = getParentFragmentManager();
    private final String IMG_EXTENSION = ".webp";


    public OperaFragment() {}

    public OperaFragment(final Opera opera, final String nomeStanza) {
        this.opera = opera;
        this.nomeStanza = nomeStanza;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        localFilePercorsoManager = new LocalFilePercorsoManager(getContext().getFilesDir().toString());
        final List<String> immaginiOpera = Collections.singletonList(opera.getPercorsoImg());
        fragment = new ImageAndDescriptionFragment(immaginiOpera, opera.getDescrizione());
        fragmentManager.beginTransaction()
            .setReorderingAllowed(true)
            //.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
            .add(R.id.imageanddescription_fragment_container_view, fragment)
            .commit();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.v("OperaFragment", "chiamato onSaveInstanceState()");
        if (opera != null) {
            outState.putString("id", opera.getId());
            outState.putString("nome", opera.getNome());
            outState.putString("percorsoImg", opera.getPercorsoImg());
            outState.putString("descrizione", opera.getDescrizione());
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onViewStateRestored(@NonNull Bundle savedInstanceState) {
        Log.v("OperaFragment", "chiamato onViewStateRestored()");
        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            this.opera = new Opera(
                savedInstanceState.getString("id"),
                savedInstanceState.getString("nome"),
                savedInstanceState.getString("percorsoImg"),
                savedInstanceState.getString("descrizione")
            );
        }
        super.onViewStateRestored(savedInstanceState);
    }
}