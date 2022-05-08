package it.uniba.sms2122.tourexperience.percorso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import it.uniba.sms2122.tourexperience.QRscanner.QRScannerFragment;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
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
    private PermissionGrantedManager actionPerfom;

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
     *
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
     * Funzione che si occupa si apprire il fragment per scannerrizare il qr code di una stanza,
     * quindi settare cosa fare una volta che il qr è stato letto
     *
     * @param idClickedRoom, l'id della stanza che è stata clicccata
     */
    public void nextQRScannerFragmentOfRoomSelection(String idClickedRoom) {

        actionPerfom = () -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(
                            (scanResult) -> {//i dati letti dallo scannere qr verranno gestiti come segue

                                if (scanResult.equals(idClickedRoom)) {
                                    try {

                                        if (idClickedRoom.equals(path.getIdStanzaCorrente())) {
                                            path.setIdStanzaCorrente(idClickedRoom);
                                            nextStanzaFragment();
                                        } else {
                                            path.moveTo(idClickedRoom);//aggiorno il grafo sull'id della stanza in cui si sta entrando
                                            nextStanzaFragment();
                                        }

                                    } catch (GraphException e) {
                                        e.printStackTrace();
                                    }
                                } else {

                                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                                    alert.setTitle(getString(R.string.error_room_title));
                                    alert.setMessage(getString(R.string.error_room_body));
                                    alert.setCancelable(true);
                                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alert.show();
                                }
                            }
                    ), null)
                    .commit();
        };

        checkCameraPermission();
    }


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
     * funzione per sapere se il permesso della camera è gia stato concesso o meno,
     * in caso di permesso gia concesso setto l'attributo della classe che segnala che il permesso è gia stato concesso
     */
    public void checkCameraPermission() {

        permission = new Permesso(this);

        if (permission.getPermission(Manifest.permission.CAMERA,
                Permesso.CAMERA_PERMISSION_CODE,
                getString(R.string.permission_required_title),
                getString(R.string.permission_required_body))) {

            actionPerfom.doAction();//eseguo le operazione richieste se il permesso della camera è concesso
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Permesso.CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    actionPerfom.doAction();//eseguo le operazione richieste se il permesso della camera è concesso
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

    /**
     * Classe che viene instanziata per rendere dinamiche le operazioni che il programma deve fare l'utente ha concesso il permesso della camera
     */
    public interface PermissionGrantedManager {

        void doAction();
    }
}