package it.uniba.sms2122.tourexperience.percorso.OverviewPath;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.utility.ranking.VotiPercorsi;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

public class OverviewPathFragment extends Fragment {

    Percorso path;

    View inflater;

    TextView pathNameTextView;
    TextView pathDescriptionTextView;
    Button startPathButton;
    RatingBar ratingBar;
    TextView textRatingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater.inflate(R.layout.overview_path_fragment, container, false);

        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            path = ((PercorsoActivity)getActivity()).getPath();

        } else {
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            /*if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati

                path = parent.getPath();
                nomeMuseo = parent.getNomeMuseo();
                nomePercorso = parent.getNomePercorso();

            } */
        }

        setDynamicValuesOnView();
        triggerStartPathButton();
    }

    /**
     * funzione per triggerare il click sul pulsante per far partire la guida
     */
    private void triggerStartPathButton() {

        startPathButton = inflater.findViewById(R.id.startPathButton);

        startPathButton.setOnClickListener(view -> ((PercorsoActivity)getActivity()).getFgManagerOfPercorso().nextSceltaStanzeFragment());
    }


    /**
     * Funzione che si occupa di settare i reali valori dinamici delle viste che formano questa fragment
     */
    private void setDynamicValuesOnView() {

        PercorsoActivity parent = (PercorsoActivity) getActivity();

        pathNameTextView = inflater.findViewById(R.id.pathName);
        pathNameTextView.setText(path.getNomePercorso());

        pathDescriptionTextView = inflater.findViewById(R.id.pathDescription);
        pathDescriptionTextView.setText(path.getDescrizionePercorso());

        ratingBar = inflater.findViewById(R.id.scorePath);
        textRatingBar = inflater.findViewById(R.id.txtScorePath);

        if(parent.checkConnectivity()) {
            parent.getSnapshotVoti().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    String voti = dataSnapshot.getValue(String.class);
                    VotiPercorsi votiPercorsi = new VotiPercorsi(voti);
                    Float media = votiPercorsi.calcolaMedia();
                    if(media == -1){
                        ratingBar.setVisibility(View.GONE);
                    } else {
                        ratingBar.setRating(media);
                        textRatingBar.setText(String.valueOf(media));
                    }
                }
            });
        } else {
            ratingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outState.putSerializable("path", gson.toJson(this.path));
    }
}
