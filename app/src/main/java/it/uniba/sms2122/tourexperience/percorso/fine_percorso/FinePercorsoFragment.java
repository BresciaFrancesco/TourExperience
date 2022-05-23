package it.uniba.sms2122.tourexperience.percorso.fine_percorso;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import it.uniba.sms2122.tourexperience.FirstActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.SplashScreenActivity;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinePercorsoFragment} factory method to
 * create an instance of this fragment.
 */
public class FinePercorsoFragment extends Fragment {

    private Bundle savedInstanceState;
    private ImageView imageView;
    private TextView textView;

    private RatingBar ratingBar;
    private Button buttonVote;
    private ImageButton imageButton;

    private PercorsoActivity parent;
    private String nomeMuseo;
    private String nomePercorso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fine_percorso, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        parent = (PercorsoActivity) getActivity();
        parent.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            nomeMuseo = parent.getNomeMuseo();
            nomePercorso = parent.getNomePercorso();
        } else {
            this.nomePercorso = savedInstanceState.getString("nomePercorso");
            this.nomeMuseo = savedInstanceState.getString("nomeMuseo");

            //lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati
            if (this.nomeMuseo == null) {
                nomeMuseo = parent.getNomeMuseo();
            }
            if (this.nomePercorso == null){
                nomePercorso = parent.getNomePercorso();
            }
        }

        textView = (TextView) view.findViewById(R.id.nome_item_museo_end);
        imageView = (ImageView) view.findViewById(R.id.icona_item_museo_end);

        textView.setText(getString(R.string.museum, nomeMuseo) + "\n" + getString(R.string.path, nomePercorso));
        try{
            imageView.setImageURI(Uri.parse(cacheMuseums.get(nomeMuseo).getFileUri()));
        } catch (NullPointerException e){
            e.printStackTrace();
        }

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);

        //finding the specific RatingBar with its unique ID
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();

        //Use for changing the color of RatingBar
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        buttonVote = (Button) view.findViewById(R.id.btnEndPath);
        buttonVoteSetOnClickListener();

        imageButton = (ImageButton) view.findViewById(R.id.share);
        shareButtonSetOnClick();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(savedInstanceState != null){
            try{
                imageView.setImageURI(Uri.parse(cacheMuseums.get(nomeMuseo).getFileUri()));
            } catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        parent.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("nomePercorso", this.nomePercorso);
        outState.putString("nomeMuseo", this.nomeMuseo);
    }

    private void buttonVoteSetOnClickListener() {
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NetworkConnectivity.check(isConnected -> {
                    if (!isConnected) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage(R.string.msg_attention);
                        builder.setTitle(R.string.attention);
                        builder.setIcon(R.drawable.ic_baseline_error_24);

                        builder.setCancelable(false);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                parent.endPath();
                            }
                        });

                        builder.setNegativeButton(R.string.try_again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        return;
                    }else {
                        String result = Float.toString(ratingBar.getRating());
                        parent.getSnapshotVoti().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                String voti = dataSnapshot.getValue(String.class);

                                if (voti.equals("-1"))
                                    voti = result;
                                else
                                    voti = voti.concat(";" + result);
                                parent.getDb().child("Voti").setValue(voti).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getContext(), R.string.path_end_success, Toast.LENGTH_LONG).show();
                                            parent.endPath();
                                        } else {
                                            Toast.makeText(getContext(), R.string.path_end_fail, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                        parent.getSnapshotNumStarts().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                Integer numStarts = dataSnapshot.getValue(Integer.class);
                                numStarts++;
                                parent.getDb().child("Numero_stats").setValue(numStarts);
                            }
                        });
                    }
                });
            }
        });
    }

    private void shareButtonSetOnClick() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share)
                        + "\n" + getString(R.string.museum, nomeMuseo)
                        + "\n" + getString(R.string.path, nomePercorso)
                        + "\n" + getString(R.string.vote, Float.toString(ratingBar.getRating())));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }
}