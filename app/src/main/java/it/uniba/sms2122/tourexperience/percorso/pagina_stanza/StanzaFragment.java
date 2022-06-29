package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.operadetection.BleService;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.Permesso;
import it.uniba.sms2122.tourexperience.utility.StringUtility;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFilePercorsoManager;

public class StanzaFragment extends Fragment {
    private static final String TAG = "StanzaFragment";
    private static final String CAN_SHOW_ALERT_BT_ON = "doNotShowAgainBtOnAlert";

    private TextView descriptionTextView, nameTextView, operasTextView;
    private RecyclerView recycleView;
    private Permesso permission;
    private NearbyOperasAdapter adapter;
    private PercorsoActivity percorsoActivity;
    private LocalFilePercorsoManager localFilePercorsoManager;
    private Percorso path;
    private Stanza stanza;
    private Map<String, Opera> opereInStanza;
    private BleService service;
    private View inflater;
    private LinearLayout nearbyOperasLayout;

    // Realizzazione dell'interfaccia che gestisce la connessione con il service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BleService.LocalBinder binder = (BleService.LocalBinder) iBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            service = null;
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

        percorsoActivity = (PercorsoActivity) requireActivity();
        permission = new Permesso(percorsoActivity);
        localFilePercorsoManager = new LocalFilePercorsoManager(requireContext().getFilesDir().toString());
        descriptionTextView = view.findViewById(R.id.stanza_description);
        nameTextView = view.findViewById(R.id.stanza_name);
        recycleView = view.findViewById(R.id.opere_recycle_view);
        operasTextView = view.findViewById(R.id.opere_title);
        nearbyOperasLayout = view.findViewById(R.id.opere_layout);

        if(savedInstanceState == null){
            path = percorsoActivity.getPath();
        } else{
            Gson gson = new GsonBuilder().create();
            this.path = gson.fromJson(savedInstanceState.getSerializable("path").toString(), Percorso.class);

            if (this.path == null) {//lo stato non è nullo ma il fragment è stato riaperto attraverso onBackPressed per cui comunque viene ricreato da 0 e non ha valori inzializzati
                path = percorsoActivity.getPath();
            }
        }

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
        nameTextView.setText(StringUtility.decodeUTF8(stanza.getNome()));
        descriptionTextView.setText(StringUtility.decodeUTF8(stanza.getDescrizione()));

        adapter = new NearbyOperasAdapter(getContext());
        recycleView.setAdapter(adapter);

        triggerOperaScanButton();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check dei permessi
        String[] permessi;
        if(percorsoActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permessi = new String[] {Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.ACCESS_FINE_LOCATION};
            } else {
                permessi = new String[] {Manifest.permission.ACCESS_FINE_LOCATION};
            }

            /*
             * Se i permessi sono stati accettati, vengono registrati gli intent filter per catturare l'evento di accensione o spegnimento di bluetooth o gps
             */
            boolean hasPermission = permission.hasPermissions(permessi);
            if(hasPermission) {
                bindService();

                if(!areBtAndGpsEnabled()) {
                    showAlertDialog();
                }
            }
            setVisibilityOfNearbyOperas(hasPermission);
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
     * Controlla che i sensori bluetooth e gps siano accesi.
     * @return Vero se entrambi i sensori sono accesi, falso altrimenti.
     */
    private boolean areBtAndGpsEnabled() {
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) percorsoActivity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        LocationManager locationManager = (LocationManager) percorsoActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean btEnabled, gpsEnabled;

        btEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        gpsEnabled = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && locationManager.isLocationEnabled()) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return btEnabled && gpsEnabled;
    }

    /**
     * Mostra un messaggio per avvisare l'utente di avviare il bluetooth ed il gps.
     */
    private void showAlertDialog() {
        SharedPreferences sharedPreferences = percorsoActivity.getPreferences(Context.MODE_PRIVATE);
        boolean canShowAlert = sharedPreferences.getBoolean(CAN_SHOW_ALERT_BT_ON, true);

        if(canShowAlert) {
            new AlertDialog.Builder(percorsoActivity)
                    .setTitle(R.string.bt_and_gps_on)
                    .setMessage(R.string.bt_and_gps_on_msg)
                    .setPositiveButton("Ok", null)
                    .setNeutralButton(R.string.do_not_show_again, (dialogInterface, i) -> {
                        sharedPreferences.edit()
                                .putBoolean(CAN_SHOW_ALERT_BT_ON, false)
                                .apply();
                    })
                    .show();
        }
    }

    /**
     * Metodo per visualizzare tutte le opere in una stanza se non bisogna mostrare le opere vicine.
     * @param visible Visibilità delle opere vicine
     */
    private void setVisibilityOfNearbyOperas(boolean visible) {
        if(!visible) {
            ArrayList<Opera> operas = new ArrayList<>(stanza.getOpere().values());
            operasTextView.setText(getString(R.string.operas_you_will_find));
            adapter.addOperas(operas);
            adapter.notifyDataSetChanged();
        } else {
            operasTextView.setText(getString(R.string.operas_close_to_you));
            adapter.clear();
        }
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

    /**
     * Chiama bindService() e fa partire il thread
     */
    private void bindService() throws NullPointerException {
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
            } while(service != null && service.isBound());
        }).start();
    }

    /**
     * Chiama unBindService()
     */
    public void unBindService() {
        if(service != null && service.isBound()) {
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