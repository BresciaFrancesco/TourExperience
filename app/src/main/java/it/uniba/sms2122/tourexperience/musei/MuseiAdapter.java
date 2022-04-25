package it.uniba.sms2122.tourexperience.musei;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;
import it.uniba.sms2122.tourexperience.model.Museo;


public class MuseiAdapter extends RecyclerView.Adapter<MuseiAdapter.ViewHolder> implements Filterable {

    private List<Museo> listaMusei;
    private List<Museo> listaMuseiFiltered;
    private Context context;
    private boolean flagMusei;

    // Constructor for initialization
    public MuseiAdapter(Context context, List<Museo> listaMusei, boolean flagMusei) {
        this.context = context;
        this.listaMusei = listaMusei;
        this.listaMuseiFiltered = listaMusei;
        this.flagMusei = flagMusei;
    }

    @NonNull
    @Override
    public MuseiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view, flagMusei);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull MuseiAdapter.ViewHolder holder, int position) {
        Log.v("MuseiAdapter", "chiamato onBindViewHolder()");
        String fileUri = listaMusei.get(position).getFileUri();
        String nomeCitta = listaMusei.get(position).getNome() + "\n" + listaMusei.get(position).getCitta();
        if (fileUri.isEmpty()) {
            if (flagMusei)
                holder.images.setImageResource(R.drawable.ic_baseline_error_24);
            else
                holder.images.setImageResource(R.drawable.ic_baseline_museum_24);
        } else {
            holder.images.setImageURI(Uri.parse(listaMusei.get(position).getFileUri()));
        }
        holder.text.setText(nomeCitta);
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return listaMusei.size();
    }

    /**
     * Esegue la ricerca filtrando la lista. La ricerca avviene per nome
     * e per città del museo.
     * @return oggetti Filter.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filterResults.count = listaMuseiFiltered.size();
                    filterResults.values = listaMuseiFiltered;
                }
                else {
                    String searchChr = constraint.toString().toLowerCase();
                    List<Museo> resultData = new ArrayList<>();

                    for (Museo museo : listaMuseiFiltered) {
                        // Filtro per nome
                        if (museo.getNome().toLowerCase().contains(searchChr)) {
                            resultData.add(museo);
                        }
                        // se il nome non combacia, provo a filtrare per città
                        else if (museo.getCitta().toLowerCase().contains(searchChr)) {
                            resultData.add(museo);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listaMusei = (List<Museo>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    // Initializing the Views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        TextView text;
        private final static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

        public ViewHolder(View view, boolean flagMusei) {
            super(view);
            images = view.findViewById(R.id.icona_item_lista);
            text = view.findViewById(R.id.nome_item_lista);

            // click di un item
            if (flagMusei)
                view.setOnClickListener(this::listenerForMusei);
            else
                view.setOnClickListener(this::listenerForPercorsi);
        }

        private void listenerForMusei(View view) {
            Log.v("CLICK", "listenerForMusei cliccato");
        }

        private void listenerForPercorsi(View view) {
            Log.v("CLICK", "listenerForPercorsi cliccato");
            String[] percorso0_museo1 = text.getText().toString().split("\n");
            new AlertDialog.Builder(view.getContext())
                .setTitle(R.string.importa + " " + percorso0_museo1[0])
                .setMessage(R.string.importa_msg + " " + percorso0_museo1[1] + "?")
                .setIcon(R.drawable.ic_baseline_cloud_download_24)
                .setPositiveButton(R.string.SI, (dialog, whichButton) -> {
                    Toast.makeText(view.getContext(), "Work in progress...", Toast.LENGTH_SHORT).show();
                    if (cacheMuseums.get(percorso0_museo1[1]) == null &&
                            cacheMuseums.get(percorso0_museo1[1].toLowerCase()) == null) {
                        // Il museo non è presente nello storage locale,
                        // va importato e salvato insieme al percorso scelto.
                        // Successivamente va il nuovo museo va salvato nella cache
                    }
                    else {
                        // Il museo è già presente nello storage locale
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
        }
    }

}