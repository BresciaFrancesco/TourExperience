package it.uniba.sms2122.tourexperience.musei;

import android.view.View;

public interface Back {
    /**
     * Torna indietro e ripristina lo stato precedente.
     * @param view parametro View view serve per usare
     *             questo metodo come listener.
     */
    void back(View view);
}
