package it.uniba.sms2122.tourexperience.percorso;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import it.uniba.sms2122.tourexperience.QRscanner.QRScannerFragment;
import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.graph.exception.GraphException;
import it.uniba.sms2122.tourexperience.main.HomeFragment;
import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.percorso.OverviewPath.OverviewPathFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_museo.MuseoFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_opera.OperaFragment;
import it.uniba.sms2122.tourexperience.percorso.pagina_stanza.StanzaFragment;
import it.uniba.sms2122.tourexperience.percorso.stanze.SceltaStanzeFragment;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.connection.NetworkConnectivity;
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
    /** Attributo che memorizza il percorso scelto dall'utente */
    private Percorso path;

    // Gestione del risultato dell'attivazione del bluetooth
    private final ActivityResultLauncher<Intent> btActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()!= Activity.RESULT_OK) {
                    Log.d("Bluetooth", "Acceso");
                } else {
                    Log.d("Bluetooth", "Non acceso");
                }
            }
    );

    // Gestione del risultato dell'attivazione del gps
    private final ActivityResultLauncher<Intent> gpsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()!=Activity.RESULT_OK) {
                    Log.d("GPS", "Acceso");
                } else {
                    Log.d("GPS", "Non acceso");
                }
            }
    );

    private DatabaseReference db;
    private Task<DataSnapshot> snapshotVoti;
    private Task<DataSnapshot> snapshotNumStarts;

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

    /**
     * Funzione che imposta il valore dell'attributo path ogni volta che viene selezionato un
     * determinato percoso all'interno della lista percosi relativi ad un determinato museo
     */
    private void setValuePath() throws IllegalArgumentException {
        path = localFilePercorsoManager.getPercorso(nomeMuseo, nomePercorso);
        localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
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
        if (ni!=null && ni.isAvailable() && ni.isConnected()) {
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
     * Funzione che serve a sostituire il precedente fragment con OverviewPathFragment
     *
     * @param bundle, contiene il nome del percorso di cui si deve visualizzare la descrizione
     */
    public void nextPercorsoFragment(Bundle bundle) {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        try {
            Fragment secondPage = new OverviewPathFragment();
            nomePercorso = bundle.getString("nome_percorso");
            secondPage.setArguments(bundle);

            setValuePath();

            createFragment(secondPage);
        }
        catch (IllegalArgumentException e) {
            Log.e("PercorsoActivity.class", "nextPercorsoFragment -> IllegalArgumentException sollevata.");
            e.printStackTrace();
        }
    }

    /**
     * Funzione che serve a sostituire il precedente fragment con SceltaStanzeFragment
     */
    public void nextSceltaStanzeFragment() {
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        Fragment thirdPage = new SceltaStanzeFragment();

        createFragment(thirdPage);
    }

    /**
     * Crea il fragment per visualizzare un'opera.
     * @param bundle contiene la stringa json dell'oggetto opera da istanziare.
     */
    public void nextOperaFragment(Bundle bundle) {
        Fragment operaFragment = new OperaFragment();
        operaFragment.setArguments(bundle);

        createFragment(operaFragment);
    }

    /**
     * Funzione che si occupa si aprire il fragment per scannerrizare il qr code di una stanza,
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

                                        if (idClickedRoom.equals(path.getIdStanzaIniziale())) {
                                            //path.setIdStanzaCorrente(idClickedRoom);
                                            nextStanzaFragment();
                                        } else {
                                            path.moveTo(idClickedRoom);//aggiorno il grafo sull'id della stanza in cui si sta entrando
                                            nextStanzaFragment();
                                        }

                                    } catch (GraphException e) {
                                       Log.e("excpetion", e.getMessage());
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
     * Funzione che si occupa si aprire il fragment per scannerrizare il qr code di un opera,
     * quindi settare cosa fare una volta che il qr è stato letto
     */
    public void nextQRScannerFragmentOfSingleRomm() {

        actionPerfom = () -> {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.scannerFrag, new QRScannerFragment(
                            (scanResult) -> {//i dati letti dallo scannere qr verranno gestiti come segue

                                //code..
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
        // Controllo dei permessi
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            boolean permissionsAccepted = false;
            String[] permissions;

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {    // Se la versione dell'sdk è maggiore a 31
                permissions = new String[] {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.ACCESS_FINE_LOCATION};
            } else {
                permissions = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            }

            permissionsAccepted = permission.getPermission(permissions, Permesso.BLUETOOTH_PERMISSION_CODE, getString(R.string.bluetooth_permission_title), getString(R.string.bluetooth_permission_body));
            // Attivo il bluetooth e la geolocalizzazione solo se tutti i permessi sono stati accettati
            if(permissionsAccepted) {
                enableBt();
                enableLocation();
            }
        }
        //TODO instanziare il fragment contenente l'immagine e descrizione del percorso
        createFragment(new StanzaFragment());
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

            case Permesso.BLUETOOTH_PERMISSION_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableBt();
                    enableLocation();
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


    /**
     * Riuso del codice per creare ed istanziare un fragment
     * @param fragment
     */
    private void createFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setReorderingAllowed(true);
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.container_fragments_route, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Metodo per far attivare il bluetooth all'utente
     */
    private void enableBt() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) this.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btActivityResultLauncher.launch(enableBluetooth);
        }
    }

    /**
     * Metodo per far attivare la geolocalizzazione all'utente
     */
    private void enableLocation() {
        LocationManager locationManager = ((LocationManager) this.getSystemService(Context.LOCATION_SERVICE));
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !locationManager.isLocationEnabled()) || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            gpsActivityResultLauncher.launch(enableGps);
        }
    }
}