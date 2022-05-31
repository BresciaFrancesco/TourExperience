package it.uniba.sms2122.tourexperience.percorso;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.MuseoFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class PercorsoActivity extends AppCompatActivity {


    private FragmentManagerOfPercorsoActivity fgManagerOfPercorso;
    private Permesso permission;
    private PermissionGrantedManager actionPerfom;

    private String nomeMuseo;

    protected LocalFilePercorsoManager localFilePercorsoManager;
    private LocalFileMuseoManager localFileMuseoManager;
    /** Attributo che memorizza il percorso scelto dall'utente */
    public Percorso path;

    public PercorsoActivity() {
        this.fgManagerOfPercorso = new FragmentManagerOfPercorsoActivity(this);
    }

    public FragmentManagerOfPercorsoActivity getFgManagerOfPercorso() {
        return fgManagerOfPercorso;
    }

    public LocalFilePercorsoManager getLocalFilePercorsoManager() {
        return localFilePercorsoManager;
    }

    public void setValuePath(String nomePercorso) throws IllegalArgumentException {
        path = localFilePercorsoManager.getPercorso(nomeMuseo, nomePercorso);
        localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_percorso);
        try {
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        localFilePercorsoManager = new LocalFilePercorsoManager(getApplicationContext().getFilesDir().toString());
        localFileMuseoManager = new LocalFileMuseoManager(getApplicationContext().getFilesDir().toString());

        // Viene aggiunto il fragment MuseoFragment all'activity
        if (!ripristino(savedInstanceState)) {
            Fragment firstPage = new MuseoFragment();
            fgManagerOfPercorso.createFragment(firstPage, "museoFragment");
            nomeMuseo = getIntent().getStringExtra("nome_museo");
        }

    }

    /**
     * Funzione che verifica se l'utimo fragment presente nel back stack è SceltaStanzeFragment
     * @return true in caso affermativo, false altrimenti
     */
    private boolean lastFragmentIsSceltaStanzeFragment() {
        boolean flag = false;
        try {
            if(getSupportFragmentManager().getBackStackEntryCount() != 0){
                String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                assert fragmentTag != null;
                if (fragmentTag.equals("sceltaStanzeFragment")){
                    flag = true;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * Funzione che verifica se l'utimo fragment presente nel back stack è FinePercorsoFragment
     * @return true in caso affermativo, false altrimenti
     */
    private boolean lastFragmentIsFinePercorsoFragment() {
        boolean flag = false;
        try {
            if(getSupportFragmentManager().getBackStackEntryCount() != 0){
                String fragmentTag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
                assert fragmentTag != null;
                if (fragmentTag.equals("finePercorsoFragment")){
                    flag = true;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return flag;
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
     * Funzione che serve per terminare il percorso e ritornare alla home
     */
    public void endPath() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Funzione che gestiste il funzionamento del tasto back
     */
    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container_fragments_route);

        if(!lastFragmentIsFinePercorsoFragment()){
            //Caso: l'ultimo fragment non è un FinePercorsoFragment
            if(!lastFragmentIsSceltaStanzeFragment()){
                //Caso: l'ultimo fragment non è un SceltaStanzeFragment
                if (fragment instanceof StanzaFragment) {
                    ((StanzaFragment) fragment).unBindService();
                }
                if(getSupportFragmentManager().getBackStackEntryCount() < 2){
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                //Interruzione anticipata del percorso
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.interrupt);
                builder.setTitle(R.string.attention);
                builder.setIcon(R.drawable.ic_baseline_error_24);

                builder.setCancelable(false);
                builder.setPositiveButton(R.string.SI, (dialogInterface, i) -> endPath());

                builder.setNegativeButton(R.string.NO, null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        }
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        outState.putSerializable("path", gson.toJson(this.path));
        outState.putString("nomeMuseo", this.nomeMuseo);
    }

    /**
     * Ripristina i dati dopo la distruzione dell'istanza da parte di android.
     * @param savedInstanceState, bundle per il ripristiono dello stato
     * @return true se il ripristino è stato eseguito, false altrimenti.
     */
    private boolean ripristino(final Bundle savedInstanceState) {
        if (savedInstanceState == null || savedInstanceState.isEmpty())
            return false;
        final Percorso tmpPath = new Gson().fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);
        final String tmpNomeMuseo = savedInstanceState.getString("nomeMuseo");
        if (tmpNomeMuseo == null || tmpNomeMuseo.isEmpty() || tmpPath == null )
            return false;
        path = tmpPath;
        nomeMuseo = tmpNomeMuseo;
        return true;
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

    public String getNomeMuseo() {
        return nomeMuseo;
    }

    public Percorso getPath() {
        return path;
    }

    public LocalFileMuseoManager getLocalFileMuseoManager() {
        return localFileMuseoManager;
    }

}