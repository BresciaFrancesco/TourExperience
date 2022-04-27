package it.uniba.sms2122.tourexperience.operadetection;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import it.uniba.sms2122.tourexperience.model.Opera;


/**
 * Classe per gestire il bluetooth low energy utilizzato per lo scan delle opere
 * @author Catignano Francesco
 */
public class BleController {
    private static final int RSSI_CALIBRATION = 194;    // Valore per calibrare l'rssi restituito dal bluetooth per calcolare una distanza più precisa
    private static final String TAG = BleController.class.getSimpleName();

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private static BleController bleController;

    /* Attributo che contiene il metodo di callback che verrà eseguito quando ogni volta che lo scan trova un risultato. */
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            int rssi = result.getRssi() + RSSI_CALIBRATION;
            int tx = result.getTxPower();
            String rawData = getRawData(result);
            String museumId = getMuseumId(rawData);
            String operaId = museumId + getMajor(rawData) + getMinor(rawData);
        }
    };

    /*
     * Costruttore privato per la realizzazione del pattern singleton
     */
    private BleController(Context context) {
        this.context = context;
        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * Metodo per cominciare lo scan con il Bluetooth Low Energy dei dispositivi e per cominciare a trovare i dispositivi.
     * Lo scan è sempre attivo. Per fermarlo, chiamare il metodo {@code stopLeScan()}.
     */
    public void startLeScan() {
        // TODO Qui parte il thread per capire ogni 4 secondi se il dispositivo è vicino all'opera
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            scanner.startScan(scanCallback);
        } else {
            Log.e(TAG, "Non è possibile effettuare lo scan in quanto non sono stati garantiti i permessi per BLUETOOTH_SCAN");
        }
    }

    /**
     * Metodo per fermare lo scan dei dispositivi con il Bluetooth Low Energy. È consigliabile chiamare questo metodo all'interno del metodo di callback {@code onPause()}
     */
    public void stopLeScan() {
        // TODO Qui bisogna stoppare il thread
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            scanner.stopScan(scanCallback);
        } else {
            Log.e(TAG, "Non è possibile stoppare lo scan in quanto non sono stati garantiti i permessi per BLUETOOTH_SCAN");
        }
    }

    /**
     * Metodo per ottenere l'unica istanza di BleController
     * @param context Un'istanza del contesto dell'activity
     * @return L'unica istanza di BleController
     */
    public static BleController getInstance(Context context) {
        if(bleController==null) {
            bleController = new BleController(context);
        }
        return bleController;
    }

    /**
     * Partendo da un array di byte restituito dal dispositivo, viene restituita la stringa contenente tutti i dati necessari.
     * @param result L'oggetto di tipo ScanResult, ottenuto come risultato di uno scan.
     * @return Il dato convertito in stringa esadecimale.
     */
    private String getRawData(ScanResult result) {
        if(result==null) {
            return null;
        }

        byte[] data = result.getScanRecord().getBytes();
        StringBuilder builder = new StringBuilder();
        for(byte b : data) {
            builder.append(String.format("%02X", b));
        }

        return builder.toString().toLowerCase();
    }

    /**
     * @param rawData Il dato grezzo restituito dallo scan.
     * @return L'id del museo.
     */
    private String getMuseumId(String rawData) {
        int start = 12; // Punto nella stringa dove parte l'id del museo
        return rawData.substring(start, 32+start);
    }

    /**
     * @param rawData Il dato grezzo restituito dallo scan.
     * @return Il major (cioè l'id della stanza all'interno del museo).
     */
    private String getMajor(String rawData) {
        int start = 12+32;
        return rawData.substring(start, 4+start);
    }

    /**
     * @param rawData Il dato grezzo restituito dallo scan.
     * @return Il minor (cioè l'id dell'opera all'interno della stanza).
     */
    private String getMinor(String rawData) {
        int start = 12+32+4;
        return rawData.substring(start, 4+start);
    }

    /**
     * Stima la distanza tra il dispositivo corrente e il dispositivo trovato nella scansione con il BLE.
     * @param rssi La potenza effettiva del segnale.
     * @param tx L'rssi teorico ad 1 m di distanza.
     * @return La distanza in metri.
     */
    private double estimateDistance(int rssi, int tx) {
        int n = 2;
        double exp = (double) (tx-rssi)/(10*n);
        return Math.pow(10, exp);
    }
}
