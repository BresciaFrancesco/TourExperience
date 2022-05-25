package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.checkRouteExistence;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import it.uniba.sms2122.tourexperience.model.Museo;

/**
 * Classe listener che serve solamente per implementare l'interfaccia
 * ValueEventListener e ritorna la lista di percorsi presenti sul
 * realtime database di Firebase. Contiene, come attributi, gli
 * oggetti che servono per eseguire correttamente il listaggio
 * dei percorsi.
 */
public class ListaPercorsiFromCloud implements ValueEventListener {

    private final SceltaMuseiFragment fragment;
    private final ProgressBar progressBar;

    public ListaPercorsiFromCloud(final SceltaMuseiFragment fragment,
                                  final ProgressBar progressBar) {
        this.fragment = fragment;
        this.progressBar = progressBar;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        final MuseiAdapter adapterPercorsi = fragment.getGeneralAdapter();
        progressBar.setVisibility(View.GONE);
        for (DataSnapshot snap: snapshot.getChildren()) {
            String nomeMuseo = snap.getKey();
            List<String> percorsi = (List<String>) snap.getValue();
            if (percorsi == null) continue;
            for (String nomePercorso : percorsi) {
                if (checkRouteExistence(nomeMuseo, nomePercorso)) continue;
                adapterPercorsi.addMuseum(new Museo(nomePercorso, nomeMuseo));
            }
        }
        fragment.attachNewAdapter(adapterPercorsi);
        Log.v("IMPORT_CLOUD", "finish download...");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {}

}
