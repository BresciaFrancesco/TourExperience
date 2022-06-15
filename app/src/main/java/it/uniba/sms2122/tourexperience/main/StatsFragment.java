package it.uniba.sms2122.tourexperience.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.database.CacheGames;
import it.uniba.sms2122.tourexperience.database.CacheScoreGames;
import it.uniba.sms2122.tourexperience.database.GameTypes;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;

public class StatsFragment extends Fragment {

    private UserHolder userHolder;
    private TextView livello;
    private TextView puntiTotali;
    private TextView quiz;
    private TextView spotDiff;
    private View divider1;
    private View divider2;

    private TextView puntiQuiz;
    private TextView puntiDiff;
    private ProgressBar progress_quiz;
    private ProgressBar progress_diff;

    private TextView yourMedQuiz_text;
    private TextView nextMedQuiz_text;
    private ImageView bronzoQuiz;
    private ImageView argentoQuiz;
    private ImageView oroQuiz;
    private ImageView nextMedQuiz;

    private TextView yourMedDiff_text;
    private TextView nextMedDiff_text;
    private ImageView bronzoDiff;
    private ImageView argentoDiff;
    private ImageView oroDiff;
    private ImageView nextMedDiff;

    int[] medals = {R.drawable.ic_bronze_medal,R.drawable.ic_silver_medal,R.drawable.ic_gold_medal};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        livello = view.findViewById(R.id.livello);
        puntiTotali = view.findViewById(R.id.punti_totali);
        quiz = view.findViewById(R.id.quiz);
        spotDiff = view.findViewById(R.id.spotDifference);

        progress_quiz = view.findViewById(R.id.progressQuiz);
        progress_diff = view.findViewById(R.id.progressDiff);
        puntiQuiz = view.findViewById(R.id.punti_quiz);
        puntiDiff = view.findViewById(R.id.punti_diff);

        yourMedQuiz_text = view.findViewById(R.id.yourMedals_quiz);
        nextMedQuiz_text = view.findViewById(R.id.nextMedals_quiz);
        bronzoQuiz = view.findViewById(R.id.bronzoQuiz);
        argentoQuiz = view.findViewById(R.id.argentoQuiz);
        oroQuiz = view.findViewById(R.id.oroQuiz);
        nextMedQuiz = view.findViewById(R.id.nextMedal_quiz);

        yourMedDiff_text = view.findViewById(R.id.yourMedals_diff);
        nextMedDiff_text = view.findViewById(R.id.nextMedals_diff);
        bronzoDiff = view.findViewById(R.id.bronzoDiff);
        argentoDiff = view.findViewById(R.id.argentoDiff);
        oroDiff = view.findViewById(R.id.oroDiff);
        nextMedDiff = view.findViewById(R.id.nextMedal_diff);

        divider1 = view.findViewById(R.id.divider);
        divider2 = view.findViewById(R.id.divider2);

        // Check connessione
        if(!NetworkConnectivity.check(getContext())) {
            livello.setText(R.string.no_connection);

            quiz.setVisibility(View.GONE);
            spotDiff.setVisibility(View.GONE);
            puntiQuiz.setVisibility(View.GONE);
            puntiDiff.setVisibility(View.GONE);
            progress_quiz.setVisibility(View.GONE);
            progress_diff.setVisibility(View.GONE);

            yourMedQuiz_text.setVisibility(View.GONE);
            yourMedDiff_text.setVisibility(View.GONE);
            nextMedQuiz_text.setVisibility(View.GONE);
            nextMedDiff_text.setVisibility(View.GONE);

            divider1.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
            return;
        }

        // Controllo se l'utente è loggato con la classe UserHolder
        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                user -> { // Utente loggato

                    quiz.setText(R.string.quiz_game);
                    spotDiff.setText(R.string.spot_the_difference_title_game);

                    final String[] scoreQuiz = new String[1];
                    final String[] scoreDiff = new String[1];
                    scoreQuiz[0] = "0";
                    scoreDiff[0] = "0";

                    // Recupero i dati da Firebase
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    final String userid = firebaseUser.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

                    // Ottengo i punti salvati in locale quando l'app era offline
                    CacheScoreGames db = new CacheScoreGames(requireContext());
                    final double quizLS = db.getScore(userid, GameTypes.QUIZ);
                    final double diffLS = db.getScore(userid, GameTypes.DIFF);

                    reference.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // Recupero gli score dell'utente
                            Object tempQuiz = dataSnapshot.child("score_quiz").getValue();
                            Object tempDiff = dataSnapshot.child("score_diff").getValue();
                            if (tempQuiz != null) scoreQuiz[0] = tempQuiz.toString();
                            if (tempDiff != null) scoreDiff[0] = tempDiff.toString();

                            // Calcolo punti totali utente
                            int scoreQuizInt = (int)Double.parseDouble(scoreQuiz[0]);
                            int scoreDiffInt = (int)Double.parseDouble(scoreDiff[0]);

                            if (quizLS > 0 || diffLS > 0) {
                                // aggiungo il punteggio salvato in locale a quello ottenuto da cloud
                                scoreQuizInt += quizLS;
                                scoreDiffInt += diffLS;

                                // salvo i nuovi punteggi in cloud
                                final Map<String, Object> mappa = new HashMap<>();
                                mappa.put("dateBirth", dataSnapshot.child("dateBirth").getValue());
                                mappa.put("name", dataSnapshot.child("name").getValue());
                                mappa.put("surname", dataSnapshot.child("surname").getValue());
                                mappa.put("score_quiz", scoreQuizInt);
                                mappa.put("score_diff", scoreDiffInt);
                                reference.child(userid).setValue(mappa).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Elimino i punti salvati in locale associati all'utente correntemente loggato
                                        db.deleteByUid(userid);
                                    } else {
                                        Log.e("StatsFragment", "dati non salvati -> exception: " + task.getException());
                                    }
                                });
                            }

                            int punti_totali = (int)(scoreQuizInt+scoreDiffInt);
                            puntiTotali.setText(getString(R.string.total_score) + " " + Integer.toString(punti_totali));
                            livello.setText(getString(R.string.level) + " " + Integer.toString(punti_totali/10));

                            /* Sistema medaglie
                             *   Bronzo:  100 punti
                             *   Argento: 200 punti
                             *   Oro:     300 punti
                             */

                            // Il livello può essere: 0,1,2,3
                            int livelloQuiz = (int)scoreQuizInt/100;
                            int livelloDiff = (int)scoreDiffInt/100;

                            puntiQuiz.setText("" + scoreQuizInt);
                            progress_quiz.setProgress((int)((scoreQuizInt >= 300) ? 100 : (scoreQuizInt%100)));
                            puntiDiff.setText("" + scoreDiffInt);
                            progress_diff.setProgress((int)((scoreDiffInt >= 300) ? 100 : (scoreDiffInt%100)));

                            setMedals(livelloQuiz, livelloDiff);
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                },
                (String errorMsg) -> { // Utente guest
                    livello.setText(R.string.game_stats_not_available);

                    quiz.setVisibility(View.GONE);
                    spotDiff.setVisibility(View.GONE);
                    puntiQuiz.setVisibility(View.GONE);
                    puntiDiff.setVisibility(View.GONE);
                    progress_quiz.setVisibility(View.GONE);
                    progress_diff.setVisibility(View.GONE);

                    yourMedQuiz_text.setVisibility(View.GONE);
                    yourMedDiff_text.setVisibility(View.GONE);
                    nextMedQuiz_text.setVisibility(View.GONE);
                    nextMedDiff_text.setVisibility(View.GONE);

                    divider1.setVisibility(View.GONE);
                    divider2.setVisibility(View.GONE);
                }
        );
    }

    private void setMedals(int livelloQuiz, int livelloDiff) {
        switch (livelloQuiz) {
            case 0:
                nextMedQuiz.setImageResource(medals[0]);
                bronzoQuiz.setVisibility(View.GONE);
                argentoQuiz.setVisibility(View.GONE);
                oroQuiz.setVisibility(View.GONE);
                break;
            case 1:
                bronzoQuiz.setImageResource(medals[0]);
                argentoQuiz.setVisibility(View.GONE);
                oroQuiz.setVisibility(View.GONE);
                nextMedQuiz.setImageResource(medals[1]);
                break;
            case 2:
                bronzoQuiz.setImageResource(medals[0]);
                argentoQuiz.setImageResource(medals[1]);
                oroQuiz.setVisibility(View.GONE);
                nextMedQuiz.setImageResource(medals[2]);
                break;
            case 3:
                bronzoQuiz.setImageResource(medals[0]);
                argentoQuiz.setImageResource(medals[1]);
                oroQuiz.setImageResource(medals[2]);
                nextMedQuiz.setVisibility(View.GONE);
                nextMedQuiz_text.setVisibility(View.GONE);
                break;
        }

        switch (livelloDiff) {
            case 0:
                nextMedDiff.setImageResource(medals[0]);
                bronzoDiff.setVisibility(View.GONE);
                argentoDiff.setVisibility(View.GONE);
                oroDiff.setVisibility(View.GONE);
                break;
            case 1:
                bronzoDiff.setImageResource(medals[0]);
                argentoDiff.setVisibility(View.GONE);
                oroDiff.setVisibility(View.GONE);
                nextMedDiff.setImageResource(medals[1]);
                break;
            case 2:
                bronzoDiff.setImageResource(medals[0]);
                argentoDiff.setImageResource(medals[1]);
                oroDiff.setVisibility(View.GONE);
                nextMedDiff.setImageResource(medals[2]);
                break;
            case 3:
                bronzoDiff.setImageResource(medals[0]);
                argentoDiff.setImageResource(medals[1]);
                oroDiff.setImageResource(medals[2]);
                nextMedDiff.setVisibility(View.GONE);
                nextMedDiff_text.setVisibility(View.GONE);
                break;
        }
    }
}