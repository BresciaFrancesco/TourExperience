package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.checkRouteExistence;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.replacePercorsiInCache;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    private final MuseiAdapter adapterPercorsi;
    private final SceltaMuseiFragment fragment;
    private final ProgressBar progressBar;
    private final RecyclerView recyclerView;

    public ListaPercorsiFromCloud(final MuseiAdapter adapterPercorsi,
                                  final SceltaMuseiFragment fragment,
                                  final ProgressBar progressBar,
                                  final RecyclerView recyclerView) {
        this.adapterPercorsi = adapterPercorsi;
        this.fragment = fragment;
        this.progressBar = progressBar;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        recyclerView.setAdapter(adapterPercorsi);
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
        fragment.attachQueryTextListener(adapterPercorsi);
        replacePercorsiInCache(adapterPercorsi.getListaMusei());
        Log.v("IMPORT_CLOUD", "finish download...");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(fragment.getContext(), "Server error", Toast.LENGTH_SHORT).show();
    }

}
