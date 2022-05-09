package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class StanzaFragment extends Fragment {
    private static final int REQUEST_ENABLE_BT = 1;

    ImageView imageView;
    TextView textView;
    RecyclerView recycleView;
    ArrayList<String> nomiOpereVicine;
    ArrayList<Integer> immaginiOpereVicine;
    private PercorsoActivity percorsoActivity;
    private Permesso permission;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private Percorso path;
    private Stanza stanza;
    private Map<String, Opera> opere;

    // Gestione del risultato dell'attivazione del bluetooth
    private final ActivityResultLauncher<Intent> btActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()!=Activity.RESULT_OK) {
                    Log.v("Bluetooth", "Acceso");
                } else {
                    Log.v("Bluetooth", "Non acceso");
                }
            }
    );

    // Gestione del risultato dell'attivazione del gps
    private final ActivityResultLauncher<Intent> gpsActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode()!=Activity.RESULT_OK) {
                    Log.v("GPS", "Acceso");
                } else {
                    Log.v("GPS", "Non acceso");
                }
            }
    );

    // Gestione del risultato dei permessi
    private final ActivityResultLauncher<String[]> permissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                boolean permissionGranted = false;
                // Gestione del risultato del bluetooth
                if(result.containsKey(Manifest.permission.BLUETOOTH_SCAN) && result.get(Manifest.permission.BLUETOOTH_SCAN)) {
                    enableBt();
                    permissionGranted = true;
                } else {
                    Log.v("Permesso", "Bluetooth rifiutato");
                    permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.S;
                }

                // Gestione del risultato della geolocalizzazione
                if(result.containsKey(Manifest.permission.ACCESS_FINE_LOCATION) && result.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    LocationManager locationManager = ((LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE));
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !locationManager.isLocationEnabled()) || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        Intent enableGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        gpsActivityResultLauncher.launch(enableGps);
                    }
                    permissionGranted = true;
                } else {
                    Log.v("Permesso", "GPS rifiutato");
                    permissionGranted = false;
                }

                if(permissionGranted) {
                    // TODO Far partire il bluetooth
                }
            }
    );

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        permission = new Permesso(percorsoActivity);
        percorsoActivity = (PercorsoActivity) getActivity();
        path = percorsoActivity.getPath();
        localFilePercorsoManager = new LocalFilePercorsoManager(requireContext().getFilesDir().toString());

        /* Caricamento delle opere */
        String idStanzaCorrente = path.getIdStanzaCorrente();
        if(idStanzaCorrente.equals(path.getIdStanzaIniziale())) {   // Se la stanza attuale è la prima del percorso...
            // Caricamento delle opere nelle stanze adiacenti
            localFilePercorsoManager.createStanzeAndOpereInThisAndNextStanze(path);
        } else {
            // Caricamento delle opere nelle stanze adiacenti
            new Thread(() -> localFilePercorsoManager.createStanzeAndOpereOnlyInNextStanze(path)).start();
        }

        stanza = path.getStanzaCorrente();
        opere = stanza.getOpere();
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Controllo dei permessi */
        // Check dei permessi sul bluetooth. Questi vanno fatti solo se la versione dell'SDK è superiore alla 31 e se il dispositivo ha il ble
        if(percorsoActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Controllo se è necessario spiegare all'utente i permessi per il bluetooth e la localizzazione
                if(shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_SCAN) && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permission.showRationaleDialog(getString(R.string.bluetooth_permission_title), getString(R.string.bluetooth_permission_body),
                            (dialogInterface, i) -> permission.showRationaleDialog(getString(R.string.location_permission_title), getString(R.string.location_permission_body),
                                    (dialogInterface1, i1) -> permissionResult.launch(new String[] {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION})
                            )
                    );
                // Chiedo il permesso direttamente
                } else {
                    permissionResult.launch(new String[] {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION});
                }

            } else {
                // Controllo se c'è bisogno di spiegare all'utente il permesso per la localizzazione
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permission.showRationaleDialog(getString(R.string.location_permission_title), getString(R.string.location_permission_body),
                            (dialogInterface, i) -> permissionResult.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}));
                // Chiedo il permesso senza spiegazioni
                } else {
                    permissionResult.launch(new String[] {Manifest.permission.ACCESS_FINE_LOCATION});
                    enableBt();
                }
            }
        }
    }

    View inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater =  inflater.inflate(R.layout.fragment_stanza, container, false);
        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = view.findViewById(R.id.stanza_description);
        //TODO: settare descrizione dal json
        textView.setText("Descrizione stanza...");

        recycleView = view.findViewById(R.id.opere_recycle_view);

        //TODO: Recuperare lista opere vicine
        nomiOpereVicine = new ArrayList<>();
        immaginiOpereVicine = new ArrayList<>();

        /* Dati di prova
        nomiOpereVicine.add("opera1");
        nomiOpereVicine.add("Opera 2");
        immaginiOpereVicine.add(R.drawable.ic_museum);
        immaginiOpereVicine.add(R.drawable.ic_puzzle);
         */

        it.uniba.sms2122.tourexperience.percorso.pagina_stanza.RecycleViewAdapter adapter =
                new it.uniba.sms2122.tourexperience.percorso.pagina_stanza.RecycleViewAdapter(getContext(),nomiOpereVicine,immaginiOpereVicine);
        recycleView.setAdapter(adapter);

        /* TODO: implementare bundle per opera_activity
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(str -> {
            Bundle bundle = new Bundle();
            bundle.putString("nome_percorso", nomiPercorsi.get(Integer.parseInt(str)));
            ((PercorsoActivity) getActivity()).nextPercorsoFragment(bundle);
        }); */

        triggerOperaScanButton();
    }

    /**
     * funzione per triggerare il click sul pulsante scansione un opera
     */
    private void triggerOperaScanButton() {

        ConstraintLayout operaScanButton = inflater.findViewById(R.id.operaScannButton);

        operaScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((PercorsoActivity)getActivity()).nextQRScannerFragmentOfSingleRomm();
            }
        });

    }

    private void enableBt() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) requireContext().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btActivityResultLauncher.launch(enableBluetooth);
        }
    }
}