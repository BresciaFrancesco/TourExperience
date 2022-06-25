package it.uniba.sms2122.tourexperience.main;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.FirstActivity;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.ThreadManager;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.musei.SceltaMuseiFragment;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.profile.ProfileActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.listeners.FailureListener;
import it.uniba.sms2122.tourexperience.utility.listeners.SuccessDataListener;
import it.uniba.sms2122.tourexperience.utility.ranking.MuseoDatabase;
import it.uniba.sms2122.tourexperience.utility.ranking.VotiPercorsi;


public class MainActivity extends AppCompatActivity {
    private UserHolder userHolder;
    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private SceltaMuseiFragment sceltaMuseiFragment;
    private StatsFragment statsFragment;
    private List<MuseoDatabase> museoDatabaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        //Ottengo l'utente attualmente loggato
        userHolder = UserHolder.getInstance();

        /*
         * Riempio una cache apposita con i nomi dei percorsi presenti in locale.
         * Ottiene i percorsi salvati in locale (solo museo e nome del percorso),
         * salvandoli in un'apposita cache.
         */
        ThreadManager.setThread(() -> {
            Log.v("THREAD_Cache_Percorsi_Locale", "chiamato il thread");
            try {
                if (!cachePercorsiInLocale.isEmpty()) return;
                new LocalFileMuseoManager(getFilesDir().toString()).getPercorsiInLocale();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
        ThreadManager.startThread();

        sceltaMuseiFragment = new SceltaMuseiFragment();
        statsFragment = new StatsFragment();
        museoDatabaseList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment f = fragmentManager.findFragmentById(R.id.content_fragment_container_view);
            switch (item.getItemId()) {
                case R.id.home:
                    if (f instanceof HomeFragment) return false;
                    bottomNavigationView.setItemActiveIndicatorEnabled(true);
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.content_fragment_container_view, HomeFragment.class, null)
                            .addToBackStack("HomeFragment")
                            .commit();
                    return true;
                case R.id.museums:
                    if (f instanceof SceltaMuseiFragment) return false;
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.content_fragment_container_view, safeGetSceltaMuseiFragment(), null)
                            .addToBackStack("SceltaMuseiFragment")
                            .commit();
                    Objects.requireNonNull(MainActivity.this.getSupportActionBar()).setTitle(R.string.museums);
                    return true;
                case R.id.game_statistics:
                    if (f instanceof StatsFragment) return false;
                    fragmentManager.beginTransaction()
                            .setReorderingAllowed(true)
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.content_fragment_container_view, safeGetStatsFragment(), null)
                            .addToBackStack("StatsFragment")
                            .commit();
                    Objects.requireNonNull(MainActivity.this.getSupportActionBar()).setTitle(R.string.stats);
                    return true;
                default:
                    return false;
            }
        });
    }

    /**
     * Imposta il titolo della action bar
     * @param title Il titolo da impostare
     */
    public void setActionBarTitle(String title) {
        try {
            Objects.requireNonNull(getSupportActionBar()).setTitle(title);
        }
        catch(NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.profile_pic) {
            userHolder.getUser(
                    user -> {
                        Intent openProfileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(openProfileIntent);
                    },
                    (String errorMsg) -> new AlertDialog.Builder(MainActivity.this)
                            .setTitle(getString(R.string.profile_pic_alert_title))
                            .setMessage(getString(R.string.profile_pic_alert_message))
                            .setNegativeButton(R.string.NO, null)
                            .setPositiveButton(R.string.SI, (dialogInterface, i) -> {
                                Intent firstPageIntent = new Intent(MainActivity.this, FirstActivity.class);
                                startActivity(firstPageIntent);
                                finish();
                            }).show()
            );
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Funzione utile a gestire la pressione del pulsante back in modo tale che quando viene premuto il pulsante non venga chiusa l'app ma si scorra a ritroso
     * scorrendo tra i vari fragment visitati dall'ultimo sino alla home
     */
    @Override
    public void onBackPressed() {
        getTopFragment();
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    /**
     * Funzione utile a individuare se ci sono entry all'interno del backstack
     * Se c'è solo una entry setta il focus sull'icona della home
     * Altrimenti verifica quale sia il penultimo fragment attivato e ne attiva l'icona corrispondente
     */
    private void getTopFragment() {
        if(getSupportFragmentManager().getBackStackEntryCount() <= 1){
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        } else {
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
            try {
                switch (Objects.requireNonNull(fragmentTag)) {
                    case "HomeFragment":
                        bottomNavigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case "SceltaMuseiFragment":
                        bottomNavigationView.getMenu().getItem(1).setChecked(true);
                        break;
                    case "StatsFragment": ;
                        bottomNavigationView.getMenu().getItem(2).setChecked(true);
                        break;
                }
            } catch (NullPointerException ignored) {}
        }
    }

    /**
     * Funzione che serve a sostituire l'attuale fragment con SceltaMuseiFragment al quale viene passato un bundle
     * @param bundle I dati da passare al fragment
     */
    public void replaceSceltaMuseiFragment(Bundle bundle){
        SceltaMuseiFragment sceltaMuseiFragment = new SceltaMuseiFragment();
        sceltaMuseiFragment.setArguments(bundle);

        bottomNavigationView.getMenu().getItem(1).setChecked(true);
        //passare al SceltaMuseiFragment
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.content_fragment_container_view, sceltaMuseiFragment)
                .addToBackStack("SceltaMuseiFragment")
                .commit();
    }

    /**
     * Funzione che serve a sostituire l'attuale fragment con RankingFragment al quale viene passato un bundle
     * @param bundle I dati da passare al fragment
     */
    public void replaceRankingFragment(Bundle bundle){
        RankingFragment rankingFragment = new RankingFragment();
        rankingFragment.setArguments(bundle);

        //passare al SceltaMuseiFragment
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.content_fragment_container_view, rankingFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Funzione che serve per passare sll'activity PercorsoActivity passandogli il nome del museo selezionato come parametro.
     * @param nomeMuseo nome del museo selezionato, da passare alla prossima activity.
     */
    public void startPercorsoActivity(String nomeMuseo){
        Intent intent = new Intent(this, PercorsoActivity.class);
        intent.putExtra("nome_museo", nomeMuseo);
        startActivity(intent);
    }

    /**
     * Funzione utile a nascondere la tastiera una volta che un fragment è stata sostituito con un altro
     * @param ctx, context di riferimento all'interno di cui si vuole nascondere la tastiera
     */
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    /**
     * Ritorna il fragment sceltaMuseiFragment assicurandosi che non
     * sia null, in tal caso crea un nuovo fragment.
     * @return sceltaMuseiFragment, mai null.
     */
    private SceltaMuseiFragment safeGetSceltaMuseiFragment() {
        if (sceltaMuseiFragment == null) {
            sceltaMuseiFragment = new SceltaMuseiFragment();
        }
        return sceltaMuseiFragment;
    }

    /**
     * Ritorna il fragment statsFragment assicurandosi che non
     * sia null, in tal caso crea un nuovo fragment.
     * @return statsFragment, mai null.
     */
    private StatsFragment safeGetStatsFragment() {
        if (statsFragment == null) {
            statsFragment = new StatsFragment();
        }
        return statsFragment;
    }

    /**
     * Permette di estarrre il nome del museo da una striga passata come paramtro
     * @param child stinga generica contenente il nome del museo da estrarre
     * @return      nome del museo
     */
    private String getNomeMuseo(String child){
        String nome = null;
        if (child.contains("nome")) {
            nome = child.substring(child.indexOf("=") + 1);
        }

        return nome;
    }

    /**
     * Permette di estarrre il nome del percorso da una striga passata come paramtro
     * @param child stinga generica contenente il nome del percorso da estrarre
     * @return      nome del percorso
     */
    private String getNomePercorso(String child){
        String percorso = null;
        if (child.contains("Nome_percorso")) {
            percorso = child.substring((child.lastIndexOf("=") + 1));
        }
        return percorso;
    }

    /**
     * Permette di estarrre la stringa dei voti espressi dagli utenti per un percorso a partire da una striga passata come paramtro
     * @param child stinga generica contenente la string dei voti da estrarre
     * @return      stinga dei voti
     */
    private String getVoto(String child){
        String voto = null;
        if (child.contains("Voti")) {
            voto = child.substring(child.lastIndexOf("=") + 1);
        }
        return voto;
    }

    /**
     * Permette di estarrre il numero di avvii per un percorso a partire da una striga passata come paramtro
     * @param child stinga generica contenente il numero di avvii da estrarre
     * @return      numero di avvii
     */
    private String getNumeroStarts(String child){
        String numeroStarts = null;
        if (child.contains("Numero_starts")) {
            numeroStarts = (child.substring(child.lastIndexOf("=") + 1)).replace("}", "");
        }
        return numeroStarts;
    }

    /**
     * Funzione che si occupa di interpellare Firebase e recuperare tutte le informazioni da esso
     * Più nello specifico nome museo, nome percorso, voti espressi dagli utenti e numero di avvii per ciascun percorso
     * @param successListener, listener per quando è presente la connessione internet
     * @param failureListener, listener per quando manca la connessione internet
     */
    public void getMuseoDatabaseList(SuccessDataListener<List<MuseoDatabase>> successListener, FailureListener failureListener) {
        if(NetworkConnectivity.check(getApplicationContext())) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Museums");
            Task<DataSnapshot> snapshot = db.get();
            snapshot.addOnSuccessListener(dataSnapshot -> {
                String[] children = Objects.requireNonNull(dataSnapshot.getValue()).toString().split("tipologia=");
                for (int j = 0; j < children.length-1; j++){
                    String[] child = children[j].split(",");

                    MuseoDatabase museoDatabase = new MuseoDatabase();
                    for (String s : child) {
                        String nomeMuseo = getNomeMuseo(s);
                        if (nomeMuseo != null) {
                            museoDatabase.setNomeMuseo(nomeMuseo);
                        } else {
                            String percorso = getNomePercorso(s);
                            if (percorso != null) {
                                museoDatabase.addNomePercorso(percorso);
                            } else {
                                String voto = getVoto(s);
                                if (voto != null){
                                    VotiPercorsi votiPercorsi = new VotiPercorsi(voto);
                                    museoDatabase.addVoti(votiPercorsi);
                                } else {
                                    String numeroStarts = getNumeroStarts(s);
                                    if (numeroStarts != null) {
                                        museoDatabase.addNumeroStarts(numeroStarts);
                                    }
                                }
                            }
                        }
                    }
                    museoDatabaseList.add(museoDatabase);
                }
                successListener.onSuccess(museoDatabaseList);
            });
        } else {
            failureListener.onFail(getString(R.string.no_connection));
        }
    }
}