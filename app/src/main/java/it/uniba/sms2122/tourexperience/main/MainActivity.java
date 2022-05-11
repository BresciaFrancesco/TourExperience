package it.uniba.sms2122.tourexperience.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.holders.UserHolder;
import it.uniba.sms2122.tourexperience.musei.SceltaMuseiFragment;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.profile.ProfileActivity;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;


public class MainActivity extends AppCompatActivity {
    private UserHolder userHolder;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    private SceltaMuseiFragment sceltaMuseiFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NetworkConnectivity.init(this);

        userHolder = UserHolder.getInstance();
        userHolder.getUser(
                (user) -> {
                    String title = getString(R.string.hello, user.getName());
                    Objects.requireNonNull(getSupportActionBar()).setTitle(title);
                },
                () -> {}
        );
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        /*
         * Riempio una cache apposita con i nomi dei percorsi presenti in locale.
         * Ottiene i percorsi salvati in locale (solo museo e nome del percorso),
         * salvandoli in un'apposita cache.
         */
        new Thread(() -> {
            Log.v("THREAD_Cache_Percorsi_Locale", "chiamato il thread");
            try {
                new LocalFileMuseoManager(getFilesDir().toString()).getPercorsiInLocale();
            }
            catch (IOException e) {
                Log.e("THREAD_Cache_Percorsi_Locale",
                        "Problemi nella lettura dei file o delle cartelle");
                e.printStackTrace();
            }
        }).start();

        sceltaMuseiFragment = new SceltaMuseiFragment();
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
                            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
                            .replace(R.id.content_fragment_container_view, HomeFragment.class, null)
                            .commit();
                    return true;
                case R.id.history:

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

                    return true;
                default:
                    return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        switch (itemId){

            case R.id.profile_pic:

                Intent openProfileIntent = new Intent(this, ProfileActivity.class);
                startActivity(openProfileIntent);
                return  true;

            case R.id.language:

                //code for language setting

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Funzione utile a gestire la pressione del pulsante back in modo tale che quando viene premuto il pulsante non venga chiusa l'app ma si scorra a ritroso
     * scorrendo tra i vari fragment visitati dall'ultimo sino alla home
     */
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if(!getTopFragment())
            super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    /**
     * Funzione utile a individuare se ci sono entry all'interno del backstack
     * Se c'è solo una entry setta il focus sull'icona della home
     * Altrimenti verifica quale sia il penultimo fragment attivato e ne attiva l'icona corrispondente
     * @return Restuisce false se non ci sono entry, altrimenti esegue le varie operazioni e restituisce true
     */
    private boolean getTopFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            return false;
        }

        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
        } else{
            String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
            switch (fragmentTag){
                case "HomeFragment":
                    bottomNavigationView.getMenu().getItem(0).setChecked(true);
                    break;
                case "SceltaMuseiFragment":
                    bottomNavigationView.getMenu().getItem(2).setChecked(true);
                    break;

                    //TODO da aggiornare lo switch con casi per i fragment riguardanti la history e le statistiche e verificare che funziona
            }
        }

        return true;

    }

    /**
     * Funzione che serve a sostituire il precedente fragment con SceltaMuseiFragment
     */
    public void replaceSceltaMuseiFragment(Bundle bundle){
        SceltaMuseiFragment sceltaMuseiFragment = new SceltaMuseiFragment();
        sceltaMuseiFragment.setArguments(bundle);

        bottomNavigationView.getMenu().getItem(2).setChecked(true);
        //passare al SceltaMuseiFragment
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right)
                .replace(R.id.content_fragment_container_view, sceltaMuseiFragment)
                .addToBackStack(null)
                .commit();
    }

    /**
     * Passa alla prossima activity e le fornisce il nome del museo selezionato.
     * @param nomeMuseo nome del museo selezionato, da passare alla prossima activity.
     */
    public void startPercorsoActivity(String nomeMuseo){
        ActivityOptions options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left);

        Intent intent = new Intent(this, PercorsoActivity.class);
        intent.putExtra("nome_museo", nomeMuseo);
        startActivity(intent, options.toBundle());
    }

    /**
     * Funzione utile a nascondere la tastiera una volta che un fragment è stata sostituito con un altro
     * @param ctx
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
}