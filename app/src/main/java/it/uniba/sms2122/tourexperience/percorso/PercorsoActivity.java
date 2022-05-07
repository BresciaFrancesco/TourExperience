package it.uniba.sms2122.tourexperience.percorso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import it.uniba.sms2122.tourexperience.QRscanner.QRScannerFragment;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.percorso.OverviewPath.OverviewPathFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.MuseoFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;
import it.uniba.sms2122.tourexperience.percorso.stanze.SceltaStanzeFragment;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

import java.io.File;
import java.util.Optional;

public class PercorsoActivity extends AppCompatActivity {

    Permesso permission;

    private String nomeMuseo;
    private String nomePercorso;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private LocalFileMuseoManager localFileMuseoManager;
    /**
     * Attributo che memorizza il percorso scelto dall'utente
     */
    private Percorso path;

    public String getNomeMuseo() {
        return nomeMuseo;
    }

    public String getNomePercorso() {
        return nomePercorso;
    }

    public LocalFilePercorsoManager getLocalFilePercorsoManager() {
        return localFilePercorsoManager;
    }

    /**
     * Funzione che imposta il valore dell'attributo path ogni volta che viene selezionato un
     * determinato percoso all'interno della lista percosi relativi ad un determinato museo
     */
    private void setValuePath() {
        Optional<Percorso> pathContainer = localFilePercorsoManager.getPercorso(nomeMuseo, nomePercorso);

        if (pathContainer.isPresent()) {
            path = pathContainer.get();
            localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);

        } else {
            Log.e("percorso non trovato", "percorso non trovato");
        }
    }

    public Percorso getPath() {
        return path;
    }

    public LocalFileMuseoManager getLocalFileMuseoManager() {
        return localFileMuseoManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_percorso);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nomeMuseo = getIntent().getStringExtra("nome_museo");
        localFilePercorsoManager = new LocalFilePercorsoManager(getApplicationContext().getFilesDir().toString());
        localFileMuseoManager = new LocalFileMuseoManager(getApplicationContext().getFilesDir().toString());

        File filesDir = getApplicationContext().getFilesDir();


        // cacheMuseums.get(nomeMuseo); // per ottenere l'oggetto Museo, basta fare così

        /**
         * Viene aggiunto il fragment MuseoFragment all'activity
         */
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

    /**
     * Funzione che serve a sostituire il precedente fragment con OverviewPathFragment
     * @param bundle, contiene il nome del percorso di cui si deve visualizzare la descrizione
     */
    public void nextPercorsoFragment(Bundle bundle) {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment secondPage = new OverviewPathFragment();
        nomePercorso = bundle.getString("nome_percorso");
        secondPage.setArguments(bundle);

        setValuePath();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, secondPage);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Funzione che serve a sostituire il precedente fragment con SceltaStanzeFragment
     */
    public void nextSceltaStanzeFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment thirdPage = new SceltaStanzeFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, thirdPage);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Funzione che si occupa si apprire il fragment per scannerrizare il qr code di una stanza
     *
     * @param idClickedRoom, l'id della stanza che è stata clicccata
     */
    public void nextQRScannerFragmentOfRoomSelection(String idClickedRoom) {

        if (checkCameraPermission() == true) {

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(StanzaFragment.class), null)
                    .commit();
        }

    }

    /**
     * Funzione che si occupa si apprire il fragment per scannerrizare il qr code di una stanza
     */
   /* public void nextQRScannerFragmentOfSingleRoom() {

        if (checkCameraPermission() == true) {

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(OperaActivity.class), null)
                    .commit();
        }

    }*/

    /**
     * Funzione che serve a sostituire il precedente fragment con StanzaFragment
     */
    public void nextStanzaFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment fifthPage = new StanzaFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, fifthPage);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //TODO quando l'utente termina il percorso bisogna settare l'id della stanza corrente all'id della stanza iniziale

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    /**
     * funzione per sapere se il permesso della camera è gia stato concesso o meno
     *
     * @return true se il permesso è gia stato concesso,false altrimeti
     */
    public boolean checkCameraPermission() {

        permission = new Permesso(this);

        if (permission.getPermission(Manifest.permission.CAMERA,
                Permesso.CAMERA_PERMISSION_CODE,
                getString(R.string.permission_required_title),
                getString(R.string.permission_required_body))) {
            return true;
        }

        return false;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Permesso.CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nextQRScannerFragmentOfRoom();
                    continueExecute();
                } else {
                    /* Explain to the user that the feature is unavailable because
                     * the features requires a permission that the user has denied.
                     * At the same time, respect the user's decision. Don't link to
                     * system settings in an effort to convince the user to change
                     * their decision. */
                    permission.showRationaleDialog(getString(R.string.permission_denied_title),
                            getString(R.string.permission_denied_body), null);
                }
                break;
            default:
                Log.v("switch", "default");
        }
    }

    private void continueExecute(){

    }
}