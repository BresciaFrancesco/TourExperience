package it.uniba.sms2122.tourexperience.percorso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.percorso.OverviewPath.OverviewPathFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.MuseoFragment;
import it.uniba.sms2122.tourexperience.percorso.stanze.SceltaStanzeFragment;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

public class PercorsoActivity extends AppCompatActivity {

    private String nomeMuseo;
    private String positionPercorsoSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percorso);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomeMuseo = getIntent().getStringExtra("nome_museo");

        // cacheMuseums.get(nomeMuseo); // per ottenere l'oggetto Museo, basta fare così

        if (savedInstanceState == null) {
            Fragment firstPage = new MuseoFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setReorderingAllowed(true);  //ottimizza i cambiamenti di stato dei fragment in modo che le animazioni funzionino correttammente
            transaction.add(R.id.container_fragments_route, firstPage);
            transaction.commit();
        }
    }

    // Gestisce il pulsante "back" nella action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // API 5+ solution
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void nextPercorsoFragment(Bundle bundle) {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment secondPage = new OverviewPathFragment();


        secondPage.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, secondPage);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void nextStanzeFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment thirdPage = new SceltaStanzeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right,R.anim.slide_out_left,R.anim.slide_in_left,R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, thirdPage);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }
}