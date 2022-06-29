package it.uniba.sms2122.tourexperience.operadetection;

import android.Manifest;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import it.uniba.sms2122.tourexperience.model.Opera;

/**
 * Service per il rilevamento delle opere tramite bluetooth low energy.
 */
public class BleService extends IntentService {
    private boolean bound = false;
    private final IBinder binder = new LocalBinder();
    private final Map<String, Queue<DistanceRecord>> distanceRecordMap = new HashMap<>();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Map<String, Opera> opereInStanza;
    private boolean btEnabled, gpsEnabled;

    private static final int RSSI_CALIBRATION = 210;    // Valore per calibrare l'rssi restituito dal bluetooth per calcolare una distanza più precisa
    private static final String TAG = "BleService";
    private static final double MAX_DISTANCE = 0.5; // Valore espresso in metri
    public static final int SECONDS_FOR_DETECTION = 4;

    /**
     * Controllo dell'evento dell'accensione o spegnimento del bluetooth
     */
    private final BroadcastReceiver btBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if(state == BluetoothAdapter.STATE_ON) {
                    btEnabled = true;

                    if(scanner == null)
                        scanner = bluetoothAdapter.getBluetoothLeScanner();

                    if(gpsEnabled)
                        startLeScan();
                }

                if(state == BluetoothAdapter.STATE_TURNING_OFF) {
                    btEnabled = false;
                    stopLeScan();
                }
            }
        }
    };

    /**
     * Controllo dell'evento dell'accensione o spegnimento del gps
     */
    private final BroadcastReceiver gpsBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(isGpsEnabled || isNetworkEnabled) {
                    gpsEnabled = true;
                    if(btEnabled)
                        startLeScan();
                } else {
                    gpsEnabled = false;
                }
            }
        }
    };

    /**
     * Contiene il metodo di callback chiamato quando è stato trovato un risultato.
     */
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            // Ottenimento dei dati
            String operaId = getRawData(result).substring(16, 56);
            double distance = estimateDistance(result.getRssi(), result.getTxPower());

            // Inserimento dei dati nella mappa
            if(opereInStanza.containsKey(operaId)) {    // Se il dispositivo ble trovato è un'opera nella stanza
                Queue<DistanceRecord> queue;
                if(distanceRecordMap.containsKey(operaId)) {
                    queue = distanceRecordMap.get(operaId);
                } else {
                    queue = new LinkedList<>();
                    distanceRecordMap.put(operaId, queue);
                }
                assert queue != null;
                queue.add(new DistanceRecord(opereInStanza.get(operaId), distance));
            }
        }
    };


    public BleService() {
        super("BleService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        opereInStanza = (HashMap<String, Opera>) intent.getSerializableExtra("opere");
        bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        checkSensorState();

        if(bluetoothAdapter != null) {
            if(btEnabled) {
                scanner = bluetoothAdapter.getBluetoothLeScanner();
                if(gpsEnabled)
                    startLeScan();
            }

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            intentFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);

            registerReceiver(gpsBroadcastReceiver, intentFilter);
            registerReceiver(btBroadcastReceiver, intentFilter);
        } else {
            Log.e(TAG, "onBind: bluetoothAdapter is null");
        }

        bound = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLeScan();
        bound = false;

        try {
            unregisterReceiver(btBroadcastReceiver);
            unregisterReceiver(gpsBroadcastReceiver);
        }
        catch(IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLeScan();
        bound = false;
    }

    /**
     * Metodo per effettuare lo scan con il BLE.
     */
    private void startLeScan() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "startLeScan: permission error");
            return;
        }

        scanner.startScan(scanCallback);
        Log.d(TAG, "startLeScan: scan started");
    }

    /**
     * Metodo per stoppare lo scan con il BLE.
     */
    private void stopLeScan() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "stopLeScan: permission error");
            return;
        }

        if(bluetoothAdapter.isEnabled()) {
            scanner.stopScan(scanCallback);
        }

        Log.d(TAG, "stopLeScan: scan stopped");
    }

    /**
     * Partendo da un array di byte restituito dal dispositivo, viene restituita la stringa contenente tutti i dati necessari.
     * @param result L'oggetto di tipo ScanResult, ottenuto come risultato di uno scan.
     * @return Il dato convertito in stringa esadecimale.
     */
    private String getRawData(ScanResult result) {
        if (result == null) {
            return null;
        }

        byte[] data = result.getScanRecord().getBytes();
        StringBuilder builder = new StringBuilder();
        for (byte b : data) {
            builder.append(String.format("%02X", b));
        }

        return builder.toString().toLowerCase();
    }

    /**
     * Stima la distanza tra il dispositivo corrente e il dispositivo trovato nella scansione con il BLE.
     * @param rssi La potenza effettiva del segnale.
     * @param tx L'rssi teorico ad 1 m di distanza.
     * @return La distanza in metri.
     */
    private double estimateDistance(int rssi, int tx) {
        int n = 2;
        double exp = (double) (tx-(rssi+RSSI_CALIBRATION))/(10*n);
        return Math.pow(10, exp);
    }

    /**
     * Metodo per calcolare la media delle distanze tra il dispositivo e l'opera.
     * @param queue La coda contenente le distanze
     * @return La media delle distanze
     */
    private double avgDistance(Queue<DistanceRecord> queue) {
        if(queue == null || queue.isEmpty())
            return -1;

        int sum = 0;
        for(DistanceRecord record : queue)
            sum += record.getDistance();

        return (double) sum / queue.size();
    }

    private void checkSensorState() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btEnabled = bluetoothAdapter != null && bluetoothAdapter.isEnabled();
        gpsEnabled = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && locationManager.isLocationEnabled()) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) { }


    /*
    --------------
        PUBLIC
    --------------
     */

    /**
     * Metodo per ottenere le opere vicine al dispositivo corrente, calcolando la media delle distanze negli ultimi {@value BleService#SECONDS_FOR_DETECTION} secondi.
     * Le opere vicine sono tutte quelle la cui distanza media è inferiore o uguale a {@value BleService#MAX_DISTANCE} metri.
     * @return Le opere vicine
     */
    public ArrayList<Opera> getNearbyOperas() {
        if(distanceRecordMap.isEmpty()) {
            return null;
        }

        ArrayList<Opera> nearbyOperas = new ArrayList<>();
        for (Map.Entry<String, Queue<DistanceRecord>> entry : distanceRecordMap.entrySet()) {
            // Pulizia della coda
            Queue<DistanceRecord> queue = entry.getValue();
            queue.removeIf(distanceRecord -> LocalTime.now().minusSeconds(SECONDS_FOR_DETECTION).isAfter(distanceRecord.getTimestamp()));

            // Inserimento nel set
            double avg = avgDistance(queue);
            if(avg != -1 && avg <= MAX_DISTANCE) {
                assert queue.peek() != null;
                nearbyOperas.add(queue.peek().getOpera());
            }
        }
        return nearbyOperas;
    }

    public boolean isBound() {
        return bound;
    }


    /**
     * Inner class che contiene il binder per la comunicazione tra activity e service.
     */
    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }
}