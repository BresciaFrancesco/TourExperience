package it.uniba.sms2122.tourexperience;

import android.util.Log;

/**
 * Classe di gestione di un singolo thread statico. Permette solo una gestione semplice del thread.
 * I casi di uso di questo Thread devono essere solo 2:
 *  - Unzip del museo di test al primo accesso all'applicazione.
 *  - Riempimento della cache cachePercorsiInLocale.
 *
 * ***************** ATTENZIONE! *****************
 * L'uso di questa classe in altri casi inoltre quelli già elencati, potrebbe portare
 * ad errori inaspettati.
 */
public class ThreadManager {
    private static Thread thread;

    /**
     * Imposta il thread (se possibile) con un metodo eseguibile.
     * @param r metodo che deve eseguire il thread.
     */
    public static void setThread(final Runnable r) {
        if (thread == null) {
            thread = new Thread(r);
            return;
        }
        if (thread.isAlive()) {
            Log.e("setThread", "Thread ancora vivo");
            return;
        }
        thread = new Thread(r);
    }

    /**
     * Avvia il thread se possibile.
     */
    public static void startThread() {
        if (thread == null) {
            Log.e("startThread", "Thread nullo. Chiamare setThread");
            return;
        }
        if (thread.isAlive()) {
            Log.e("startThread", "Thread già avviato");
            return;
        }
        thread.start();
    }

    /**
     * Effettua il join del thread se possibile.
     * @throws InterruptedException
     */
    public static void joinThread() throws InterruptedException {
        if (thread == null) {
            Log.e("joinThread", "Thread nullo. Chiamare setThread");
            return;
        }
        thread.join();
    }
}
