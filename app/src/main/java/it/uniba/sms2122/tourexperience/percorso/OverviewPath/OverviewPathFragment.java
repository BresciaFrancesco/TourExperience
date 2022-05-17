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
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.graph.VotiPercorsi;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

public class OverviewPathFragment extends Fragment {

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
        Percorso path = parent.getPath();

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

}
