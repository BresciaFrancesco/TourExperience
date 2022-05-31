package it.uniba.sms2122.tourexperience.games;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.BuildConfig;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.database.CacheGames;
import it.uniba.sms2122.tourexperience.database.CacheScoreGames;
import it.uniba.sms2122.tourexperience.database.GameTypes;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;

public class SaveScore {
    private final Activity act;
    private final GameTypes gt;
    private final double score;
    private final Map<GameTypes, ScoreTag> tagMap;

    /**
     * Prepara il metodo di salvataggio dello score per un generico minigioco.
     * @param act activity nella quale il minigioco viene eseguito.
     * @param gt enumerativo per indicare il tipo di minigioco eseguito.
     * @param score punteggio da aggiungere a quello presente per l'utente.
     */
    public SaveScore(final Activity act, final GameTypes gt, final double score) {
        this.act = act;
        this.gt = gt;
        this.score = score;
        tagMap = new HashMap<>();
        tagMap.put(GameTypes.QUIZ, ScoreTag.score_quiz);
        tagMap.put(GameTypes.DIFF, ScoreTag.score_diff);
    }

    /**
     * Salva il risultato di un minigioco in cloud.
     * Si comporta diversamente in base alla presenza/assenza
     * di connessione e alla tipologia di utente (normale o guest).
     * @param view view android.
     * @param nomeOpera nome dell'opera sulla quale il minigioco è stato eseguito.
     */
    public void save(View view, final String nomeOpera) {
        try {
            // Controllo connessione internet
            if (NetworkConnectivity.check(view.getContext())) {

                final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    getAndSaveOnCloud(currentUser);
                } else {
                    Toast.makeText(act.getApplicationContext(), act.getString(R.string.quiz_score_guest_user), Toast.LENGTH_SHORT).show();
                }
            }
            else { // Non c'è connessione
                // salvo il punteggio del minigioco in locale nel caso manchi la connessione
                final SharedPreferences sp = view.getContext().getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
                final String uid = sp.getString(act.getString(R.string.uid_preferences), null);
                if (uid != null) {
                    final CacheScoreGames cacheScoreGames = new CacheScoreGames(view.getContext());
                    cacheScoreGames.saveOne(uid, gt, (int)score);
                }
                Toast.makeText(act.getApplicationContext(),
                    (uid != null)
                        ? act.getString(R.string.no_connection_saved_score)
                        : act.getString(R.string.no_connection),
                    Toast.LENGTH_LONG).show();
            }
            // Salvo nel db locale lo svolgimento di questo quiz durante questo percorso
            final CacheGames cacheGames = new CacheGames(view.getContext());
            if (!cacheGames.addOne(nomeOpera, gt)) {
                Log.e("Salvataggio svolgimento " + gt.toString(), "cacheGames.addOne ha ritornato false");
            }
        }
        catch (NullPointerException | IllegalStateException e) {
            Log.e("SaveScore", "Salvataggio dello score non riuscito");
            e.printStackTrace();
        }
    }


    /**
     * Ottiene lo score (se presente) dal Cloud e lo somma al nuovo score ottenuto.
     * Se non è presente nessuno score, inserisce direttamente il nuovo score ottenuto.
     * @param currentUser utente correntemente loggato in Cloud.
     */
    private void getAndSaveOnCloud(final FirebaseUser currentUser) {
        if (tagMap.get(gt) == null) {
            throw new NullPointerException("tagMap.get(" + gt.toString() + ") è null");
        }
        final DatabaseReference dbUser = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.getUid())
                .child(Objects.requireNonNull(tagMap.get(gt)).toString());
        dbUser.get().addOnCompleteListener(taskGet -> {
            if (taskGet.isSuccessful()) {
                final double totalUserScore = ((taskGet.getResult().getValue() != null)
                        ? Double.parseDouble(taskGet.getResult().getValue().toString())
                        : 0.0)
                        + score;
                saveScoreOnCloud(dbUser, totalUserScore);
            } else {
                try {
                    Toast.makeText(act.getApplicationContext(), act.getString(R.string.quiz_score_not_saved), Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(taskGet.getException()).printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Salva il nuovo score in cloud.
     * @param dbUser Reference del database in cloud allo score del quiz.
     * @param totalUserScore Nuovo score totale da salvare in Cloud.
     */
    private void saveScoreOnCloud(final DatabaseReference dbUser, final double totalUserScore) {
        dbUser.setValue(totalUserScore).addOnCompleteListener(task -> {
            try {
                if (task.isSuccessful()) {
                    Toast.makeText(act.getApplicationContext(), act.getString(R.string.quiz_score_saved), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(act.getApplicationContext(), act.getString(R.string.quiz_score_not_saved), Toast.LENGTH_SHORT).show();
                    Objects.requireNonNull(task.getException()).printStackTrace();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        });
    }
}
