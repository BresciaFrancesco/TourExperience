package it.uniba.sms2122.tourexperience.percorso.OverviewPath;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.Optional;

import java.util.Optional;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class OverviewPathFragment extends Fragment {

    View inflater;

    String museoumName;
    String pathName;
    TextView pathNameTextView;
    String pathDescription;
    TextView pathDescriptionTextView;
    Button startPathButton;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater.inflate(R.layout.overview_path_fragment, container, false);

        museoumName = ((PercorsoActivity)getActivity()).getNomeMuseo();
        pathName = ((PercorsoActivity)getActivity()).getNomePercorso();

        setDynamicValuesOfPath();

        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        setDynamicValuesOnView();
        triggerStartPathButton();
    }

    /**
     * funzione per ottenere i reali valori relativi alla descrizione e al nome di un percorso
     */
    private void setDynamicValuesOfPath() {

        LocalFilePercorsoManager pathManager = new LocalFilePercorsoManager(getContext().getFilesDir().toString());

        Optional<Percorso> pathContainer = pathManager.getPercorso(museoumName, pathName);

        if (pathContainer.isPresent()) {

            Percorso pathObj = pathContainer.get();

            this.pathDescription = pathObj.getDescrizionePercorso();
            this.pathName = ((PercorsoActivity)getActivity()).getNomePercorso();

        } else {
            Log.e("percorso non trovato", "percorso non trovato");
        }


    }

    /**
     * funzione per triggerare il click sul pulsante per far partire la guida
     */
    private void triggerStartPathButton() {

        startPathButton = inflater.findViewById(R.id.startPathButton);

        startPathButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PercorsoActivity parentActivity = (PercorsoActivity) getActivity();
                parentActivity.nextStanzeFragment();
            }
        });
    }


    /**
     * Funzione che si occupa di settare i reali valori dinamici delle viste che formano questa fragment
     */
    private void setDynamicValuesOnView() {

        pathNameTextView = inflater.findViewById(R.id.pathName);
        pathNameTextView.setText(pathName);

        pathDescriptionTextView = inflater.findViewById(R.id.pathDescription);
        pathDescriptionTextView.setText(pathDescription);


    }

}
