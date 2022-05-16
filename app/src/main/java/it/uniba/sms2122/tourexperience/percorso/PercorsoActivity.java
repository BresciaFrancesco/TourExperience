package it.uniba.sms2122.tourexperience.percorso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.MuseoFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

import java.io.File;

public class PercorsoActivity extends AppCompatActivity {


    FragmentManagerOfPercorsoActivity fgManagerOfPercorso;

    Permesso permission;
    private PermissionGrantedManager actionPerfom;

    private String nomeMuseo;
    private String nomePercorso;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private LocalFileMuseoManager localFileMuseoManager;
    /**
     * Attributo che memorizza il percorso scelto dall'utente
     */
    public Percorso path;

    private DatabaseReference db;
    private Task<DataSnapshot> snapshotVoti;
    private Task<DataSnapshot> snapshotNumStarts;

    public PercorsoActivity() {
        this.fgManagerOfPercorso = new FragmentManagerOfPercorsoActivity(this);
    }

    public FragmentManagerOfPercorsoActivity getFgManagerOfPercorso() {
        return fgManagerOfPercorso;
    }


    /**
     * Funzione che imposta il valore dell'attributo path ogni volta che viene selezionato un
     * determinato percoso all'interno della lista percosi relativi ad un determinato museo
     */
    protected void setValuePath() throws IllegalArgumentException {
        path = localFilePercorsoManager.getPercorso(nomeMuseo, nomePercorso);
        localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
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

        // cacheMuseums.get(nomeMuseo); // per ottenere l'oggetto Museo, basta fare così
    }

    public boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(PercorsoActivity.this.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni != null && ni.isAvailable() && ni.isConnected()) {
            db = FirebaseDatabase.getInstance().getReference("Museums").child(nomeMuseo).child(nomePercorso);
            snapshotVoti = db.child("Voti").get();
            snapshotNumStarts = db.child("Numero_starts").get();
            return true;
        } else {
            Toast.makeText(PercorsoActivity.this.getApplicationContext(), PercorsoActivity.this.getApplicationContext().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return false;
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
     * Funzione che serve a ritornare alla home
     */
    public void endPath() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //TODO quando l'utente termina il percorso bisogna settare l'id della stanza corrente all'id della stanza iniziale

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Stop del service
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragments_route);
        if (fragment instanceof StanzaFragment) {
            ((StanzaFragment) fragment).unBindService();
        }

        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    /**
     * funzione per sapere se il permesso della camera è gia stato concesso o meno,
     * in caso di permesso gia concesso setto l'attributo della classe che segnala che il permesso è gia stato concesso
     */
    public void checkCameraPermission() {

        permission = new Permesso(this);

        if (permission.getPermission(new String[]{Manifest.permission.CAMERA},
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

                    getSupportFragmentManager().popBackStack();
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


    public Permesso getPermission() {
        return permission;
    }

    public void setActionPerfom(PermissionGrantedManager actionPerfom) {
        this.actionPerfom = actionPerfom;
    }

    public void setNomeMuseo(String nomeMuseo) {
        this.nomeMuseo = nomeMuseo;
    }

    public void setNomePercorso(String nomePercorso) {
        this.nomePercorso = nomePercorso;
    }

    public String getNomeMuseo() {
        return nomeMuseo;
    }

    public String getNomePercorso() {
        return nomePercorso;
    }

    public DatabaseReference getDb() {
        return db;
    }

    public Task<DataSnapshot> getSnapshotVoti() {
        return snapshotVoti;
    }

    public Task<DataSnapshot> getSnapshotNumStarts() {
        return snapshotNumStarts;
    }

    public Percorso getPath() {
        return path;
    }

    public LocalFileMuseoManager getLocalFileMuseoManager() {
        return localFileMuseoManager;
    }

}