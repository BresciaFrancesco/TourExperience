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


/**
 * Classe per gestire il bluetooth low energy utilizzato per lo scan delle opere
 * @author Catignano Francesco
 */
public class BleController {
    private static final int RSSI_CALIBRATION = 194;    // Valore per calibrare l'rssi restituito dal bluetooth per calcolare una distanza più precisa
    private static final String TAG = BleController.class.getSimpleName();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private static BleController bleController;

    /* Attributo che contiene il metodo di callback che verrà eseguito quando ogni volta che lo scan trova un risultato. */
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            // TODO Trovare l'opera
        }
    };

    /*
     * Costruttore privato per la realizzazione del pattern singleton
     */
    private BleController(BluetoothManager bluetoothManager) {
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * Metodo per cominciare lo scan con il Bluetooth Low Energy dei dispositivi e per cominciare a trovare i dispositivi.
     * @param context
     */
    public void startLeScan(Context context) {
        // TODO Qui parte il thread per capire ogni 4 secondi se il dispositivo è vicino all'opera
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            scanner.startScan(scanCallback);
        } else {
            Log.e(TAG, "Non è possibile effettuare lo scan in quanto non sono stati garantiti i permessi per BLUETOOTH_SCAN");
        }
    }

    public void stopLeScan(Context context) {
        // TODO Qui bisogna stoppare il thread
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            scanner.stopScan(scanCallback);
        } else {
            Log.e(TAG, "Non è possibile stoppare lo scan in quanto non sono stati garantiti i permessi per BLUETOOTH_SCAN");
        }
    }

    /**
     * Metodo per ottenere l'unica istanza di BleController
     * @param bluetoothManager Un'istanza di BluetoothManager ottenibile dall'activity chiamando il metodo <pre>(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)</pre>
     * @return L'unica istanza di BleController
     */
    public static BleController getInstance(BluetoothManager bluetoothManager) {
        if(bleController==null) {
            bleController = new BleController(bluetoothManager);
        }
        return bleController;
    }
}
