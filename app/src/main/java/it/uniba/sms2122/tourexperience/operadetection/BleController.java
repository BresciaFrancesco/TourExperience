package it.uniba.sms2122.tourexperience.operadetection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.os.Handler;

/**
 * Classe per gestire il bluetooth low energy utilizzato per lo scan delle opere
 * @author Catignano Francesco
 */
public class BleController {
    private static final int RSSI_CALIBRATION = 194;    // Valore per calibrare l'rssi restituito dal bluetooth per calcolare una distanza più precisa

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner scanner;
    private Handler handler;
}
