package it.uniba.sms2122.tourexperience.operadetection;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

/**
 * Service per il rilevamento delle opere tramite bluetooth low energy.
 */
public class BleService extends IntentService {
    private final IBinder binder = new LocalBinder();

    /**
     * Costruttore
     */
    public BleService() {
        super("BleService");
    }

    /**
     * Metodo di callback chiamato al bind
     * @param intent
     * @return Il binder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Metodo chiamato per gestire l'intent
     * @param intent
     */
    @Override
    protected void onHandleIntent(@Nullable Intent intent) { }


    /**
     * Inner class che contiene il binder per la comunicazione tra activity e service.
     */
    public class LocalBinder extends Binder {
        BleService getService() {
            return BleService.this;
        }
    }
}