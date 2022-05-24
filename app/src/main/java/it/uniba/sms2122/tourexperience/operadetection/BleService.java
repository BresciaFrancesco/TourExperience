package it.uniba.sms2122.tourexperience.operadetection;

import android.Manifest;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
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
import java.util.Set;
import java.util.TreeSet;

import it.uniba.sms2122.tourexperience.model.Opera;

/**
 * Service per il rilevamento delle opere tramite bluetooth low energy.
 */
public class BleService extends IntentService {
    private final IBinder binder = new LocalBinder();
    private final Map<String, Queue<DistanceRecord>> distanceRecordMap = new HashMap<>();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Map<String, Opera> opereInStanza;

    private static final int RSSI_CALIBRATION = 210;    // Valore per calibrare l'rssi restituito dal bluetooth per calcolare una distanza più precisa
    private static final String TAG = "BleService";
    private static final double MAX_DISTANCE = 0.5; // Valore espresso in metri
    public static final int SECONDS_FOR_DETECTION = 4;

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
        if(bluetoothAdapter != null) {
            scanner = bluetoothAdapter.getBluetoothLeScanner();
            startLeScan();
        } else {
            Log.e(TAG, "onBind: bluetoothAdapter is null");
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        stopLeScan();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLeScan();
    }

    /**
     * Metodo per effettuare lo scan con il BLE.
     */
    private void startLeScan() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "stopLeScan: permission error");
            return;
        }
        scanner.stopScan(scanCallback);
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

        return (double) sum/ queue.size();
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
                nearbyOperas.add(queue.peek().getOpera());
            }
        }
        return nearbyOperas;
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