package it.uniba.sms2122.tourexperience.percorso.stanze;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SceltaStanzeFragment} factory method to
 * create an instance of this fragment.
 */
public class SceltaStanzeFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<Stanza> listaStanze;
    private ImageView imageView;
    private TextView textView;
    private Bundle savedInstanceState;
    private FrameLayout listaStanzeLayout;
    private RelativeLayout rateLayout;
    private RatingBar ratingBar;
    private Button buttonVote;
    private ImageButton imageButton;

    private PercorsoActivity parent;
    private boolean isFirst = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.savedInstanceState = savedInstanceState;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scelta_stanze, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewRooms);
        // Setting the layout as linear layout for vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        textView = (TextView) view.findViewById(R.id.nome_item_museo);
        imageView = (ImageView) view.findViewById(R.id.icona_item_museo);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        //finding the specific RatingBar with its unique ID
        LayerDrawable stars=(LayerDrawable)ratingBar.getProgressDrawable();

        //Use for changing the color of RatingBar
        stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        buttonVote = (Button) view.findViewById(R.id.btnEndPath);
        buttonVoteSetOnClickListener();

        imageButton = (ImageButton) view.findViewById(R.id.share);
        shareButtonSetOnClick();

        listaStanzeLayout = (FrameLayout) view.findViewById(R.id.rooms_layout);
        rateLayout = (RelativeLayout) view.findViewById(R.id.votePath);
        imageButton = (ImageButton) view.findViewById(R.id.share);

        parent = (PercorsoActivity) getActivity();
        listaStanze = new ArrayList<>();
    }

    private void shareButtonSetOnClick() {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeMuseo = parent.getNomeMuseo();
                String nomePercorso = parent.getNomePercorso();

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_share)
                        + "\n" +getString(R.string.museum ,nomeMuseo)
                        + "\n" + getString(R.string.path, nomePercorso)
                        + "\n" + getString(R.string.vote, Float.toString(ratingBar.getRating())));
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);
            }
        });
    }

    private void buttonVoteSetOnClickListener() {
        buttonVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(parent.checkConnectivity()){
                    String result = Float.toString(ratingBar.getRating());
                    parent.getSnapshotVoti().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            String voti = dataSnapshot.getValue(String.class);

                            if(voti.equals("-1"))
                                voti = result;
                            else
                                voti = voti.concat(";" + result);
                            parent.getDb().child("Voti").setValue(voti).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(getContext(),R.string.path_end_success,Toast.LENGTH_LONG).show();
                                        parent.endPath();
                                    }
                                    else{
                                        Toast.makeText(getContext(),R.string.path_end_fail,Toast.LENGTH_LONG).show();
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
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.msg_attention);
                    builder.setTitle(R.string.attention);

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
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        String nomeMuseo = parent.getNomeMuseo();
        String nomePercorso = parent.getNomePercorso();

        if(isFirst){
            listaStanze.add(parent.getPath().getStanzaCorrente());
            textView.setText(getString(R.string.museum ,nomeMuseo) + "\n" + getString(R.string.path, nomePercorso));
            isFirst = false;
        }else if(parent.getPath().getIdStanzaCorrente().equals(parent.getPath().getIdStanzaFinale())){
            textView.setText(getString(R.string.museum ,nomeMuseo) + "\n" + getString(R.string.path, nomePercorso));
            listaStanzeLayout.setVisibility(View.GONE);
            rateLayout.setVisibility(View.VISIBLE);
        }else {
            listaStanze = parent.getPath().getAdiacentNodes();
            textView.setText(getString(R.string.museum ,nomeMuseo ) + "\n" + getString(R.string.area, parent.getPath().getStanzaCorrente().getNome()));
        }

        try{
            imageView.setImageURI(Uri.parse(cacheMuseums.get(nomeMuseo).getFileUri()));
        }catch (NullPointerException e){
            e.printStackTrace();
        }



        // Sending reference and data to Adapter
        StanzeAdpter adapter = new StanzeAdpter(getContext(), listaStanze, parent);
        // Setting Adapter to RecyclerView
        recyclerView.setAdapter(adapter);
    }

    public Bundle getSavedInstanceState() {
        return savedInstanceState;
    }
}