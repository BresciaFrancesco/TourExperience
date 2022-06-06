package it.uniba.sms2122.tourexperience.utility.connection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Questa classe permette di controllare se è presenta la connessione ad internet.
 */
public class NetworkConnectivity {

    /**
     * Controlla se è presente connessione ad Internet.
     * @param context contesto dell'app android.
     * @return true se la connessione è presente, false altrimenti.
     */
    public static boolean check(final Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isAvailable() && ni.isConnected();
    }

}
