package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.operadetection.BleService;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class StanzaFragment extends Fragment {
    TextView textView;
    RecyclerView recycleView;
    private Permesso permission;
    private NearbyOperasAdapter adapter;
    private PercorsoActivity percorsoActivity;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private Percorso path;
    private Stanza stanza;
    private Map<String, Opera> opereInStanza;
    private BleService service;
    private boolean bounded = false;
    private boolean canScanWithBluetooh = false;
    private View inflater;
    private ScrollView nearbyOperasScrollView;
    private TextView stanzaDescription;

    // Gestione del risultato dell'attivazione del bluetooth
    private final ActivityResultLauncher<Intent> btActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == Activity.RESULT_OK) {
                    Log.d("Bluetooth", "Acceso");
                    checkSensorsStateAndStartService();
                } else {
                    Log.d("Bluetooth", "Non acceso");
                }
            }
    );

    // Gestione del risultato dell'attivazione del gps
    private final ActivityResultLauncher<Intent> locationActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() != Activity.RESULT_OK) {
                    Log.d("GPS", "Acceso");
                    checkSensorsStateAndStartService();
                } else {
                    Log.d("GPS", "Non acceso");
                }
            }
    );

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BleService.LocalBinder binder = (BleService.LocalBinder) iBinder;
            service = binder.getService();
            bounded = true;
            nearbyOperasScrollView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bounded = false;
        }
    };


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater =  inflater.inflate(R.layout.fragment_stanza, container, false);
        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        percorsoActivity = (PercorsoActivity) getActivity();
        permission = new Permesso(percorsoActivity);
        localFilePercorsoManager = new LocalFilePercorsoManager(requireContext().getFilesDir().toString());

        if(savedInstanceState == null){
            path = percorsoActivity.getPath();}
        else{
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati

                path = percorsoActivity.getPath();
            }
        }

        textView = view.findViewById(R.id.stanza_description);
        recycleView = view.findViewById(R.id.opere_recycle_view);
        nearbyOperasScrollView = view.findViewById(R.id.nearby_operas_scroll_view);

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
        opereInStanza = stanza.getOpere();
        textView.setText(stanza.getDescrizione());

        adapter = new NearbyOperasAdapter(getContext());
        recycleView.setAdapter(adapter);

        triggerOperaScanButton();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check dei permessi
        String[] permessi;
        if(percorsoActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permessi = new String[] {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION};
            } else {
                permessi = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            }

            /*
             * Se i permessi sono stati accettati, vengono abilitati i sensori
             */
            if(permission.hasPermissions(permessi)) {
                checkSensorsStateAndStartService();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unBindService();
    }

    /**
     * Gestisce il click del pulsante per la scannerizzazione dei QR per un'opera.
     */
    private void triggerOperaScanButton() {
        ConstraintLayout operaScanButton = inflater.findViewById(R.id.operaScanButton);

        operaScanButton.setOnClickListener(view -> {
            try {
                ((PercorsoActivity) requireActivity()).getFgManagerOfPercorso()
                    .openQRScannerFragmentOfForOpera(stanza, percorsoActivity.getNomeMuseo());
            }
            catch (NullPointerException | IllegalStateException  e) {
                Log.e("StanzaFragment.triggerOperaScanButton", "requireActivity return null ex or illegal state ex");
                e.printStackTrace();
            }
        });

    }

    private void checkSensorsStateAndStartService() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) percorsoActivity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        LocationManager locationManager = ((LocationManager) percorsoActivity.getSystemService(Context.LOCATION_SERVICE));

        /*
         * In questo caso uso un costrutto if-else if per fare in modo che le chiamate alle activity di attivazione
         * dei sensori siano fatte in ordine.
         * Solo se le condizioni sono soddisfatte entrambe contemporeaneamente viene startato il service
         */
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            btActivityResultLauncher.launch(enableBluetooth);
        }
        else if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !locationManager.isLocationEnabled()) || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent enableGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            locationActivityResultLauncher.launch(enableGps);
        }
        else if(bluetoothAdapter.isEnabled() &&
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && locationManager.isLocationEnabled()) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            bindService();
        }
    }

    /**
     * Chiama bindService() e fa partire il thread
     */
    private void bindService() {
        Intent intent = new Intent(requireContext(), BleService.class);
        intent.putExtra("opere", (Serializable) opereInStanza);
        percorsoActivity.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Thread per prendere le opere
        new Thread(() -> {
            do {
                try {
                    Thread.sleep(BleService.SECONDS_FOR_DETECTION * 1000);
                }
                catch(InterruptedException e) {
                    e.printStackTrace();
                }

                if(service != null) {
                    percorsoActivity.runOnUiThread(() -> {
                        adapter.addOperas(service.getNearbyOperas());
                        adapter.notifyDataSetChanged();
                    });
                }
            } while(bounded);
        }).start();
    }

    /**
     * Chiama unBindService()
     */
    public void unBindService() {
        if(bounded) {
            percorsoActivity.unbindService(serviceConnection);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        outState.putSerializable("path", gson.toJson(this.path));
    }

}