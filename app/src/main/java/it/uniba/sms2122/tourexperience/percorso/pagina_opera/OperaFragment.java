package it.uniba.sms2122.tourexperience.percorso.pagina_opera;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.percorso.ImageAndDescriptionFragment;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;


public class OperaFragment extends Fragment {

    private Opera opera;
    private ImageAndDescriptionFragment fragment;
    private FragmentManager fragmentManager;
    private final int requestCodeGC = 900007;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentManager = getParentFragmentManager();

        Bundle bundle = getArguments();
        String operaJson = Objects.requireNonNull(bundle).getString("OperaJson");
        this.opera = new Gson().fromJson(Objects.requireNonNull(operaJson), Opera.class);

        setActionBar(opera.getNome());

        createFragmentImageAndDescription();
    }

    private void createFragmentImageAndDescription() {
        fragment = new ImageAndDescriptionFragment(opera.getPercorsoImg(), opera.getDescrizione());
        fragmentManager.beginTransaction()
            .setReorderingAllowed(true)
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
            createFragmentImageAndDescription();
            setActionBar(opera.getNome());
        }
        super.onViewStateRestored(savedInstanceState);
    }

    /**
     * Imposta la action bar con pulsante back e titolo.
     * @param title titolo da impostare per l'action bar.
     */
    private void setActionBar(final String title) {
        try {
            final ActionBar actionBar = ((PercorsoActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true); // abilita il pulsante "back" nella action bar
            actionBar.setTitle(title);
        } catch (NullPointerException e) {
            Log.e("OperaFragment", "ActionBar null");
            e.printStackTrace();
        }
    }

}