package it.uniba.sms2122.tourexperience.utility;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;

/**
 * Questa classe, insieme ad NetworkConnectivity, permette di controllare se
 * è presenta la connessione ad internet senza l'utilizzo del comando ping,
 * non sempre utilizzabile. In particolare questa classe gestisce i thread per
 * eseguire il controllo della connessione.
 */
public class AppExecutors {

    private static AppExecutors sInstance;
    private final Executor networkIO;
    private final Executor diskIO;
    private final Executor mainThread;


    private AppExecutors() {
        networkIO = Executors.newSingleThreadExecutor();
        diskIO = Executors.newFixedThreadPool(3);
        mainThread = new MainThreadExecutor();
    }

    public static synchronized AppExecutors getInstance() {
        if (sInstance == null) {
            sInstance = new AppExecutors();
        }
        return sInstance;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {
            mainThreadHandler.post(command);
        }
    }

}