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
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

import it.uniba.sms2122.tourexperience.model.DTO.MuseoLocalStorageDTO;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.LocalFileMuseoManager;


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

    public void addMuseo(Museo museo) {
        listaMusei.add(museo);
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
        ImageView images; // provare privato
        TextView text;    // provare privato
        private final static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        private static LocalFileMuseoManager localFileManager = null;

        public ViewHolder(View view, boolean flagMusei) {
            super(view);
            images = view.findViewById(R.id.icona_item_lista);
            text = view.findViewById(R.id.nome_item_lista);
            if (localFileManager == null) { // come se fosse final
                localFileManager = new LocalFileMuseoManager(view.getContext().getFilesDir().toString());
            }

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
            Context context = view.getContext();
            new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.importa) + " " + percorso0_museo1[0])
                .setMessage(context.getString(R.string.importa_msg) + " " + percorso0_museo1[1] + "?")
                .setIcon(R.drawable.ic_baseline_cloud_download_24)
                .setPositiveButton(context.getString(R.string.SI), (dialog, whichButton) -> {
                    Toast.makeText(context, "Work in progress...", Toast.LENGTH_SHORT).show();
                    downloadMuseoPercorso(percorso0_museo1[0], percorso0_museo1[1], context);
                })
                .setNegativeButton(android.R.string.no, null).show();
        }

        private void downloadMuseoPercorso(final String nomePercorso,
                                 final String nomeMuseo,
                                 final Context context) {
            if (cacheMuseums.get(nomeMuseo) != null ||
                    cacheMuseums.get(nomeMuseo.toLowerCase()) != null) {
                // il museo esiste già, dobbiamo scaricare solo il percorso
                // Si dà per scontato che tutti i file e le cartelle, come Percorsi, Stanze,
                // le opere, le immagini e i json sono già tutti presenti.
            }
            else {
                downloadAll(nomeMuseo, context);
            }
        }

        private void downloadAll(final String nomeMuseo, final Context context) {
            // Il museo non è presente nello storage locale,
            // va importato e salvato insieme al percorso scelto.
            // Successivamente va il nuovo museo va salvato nella cache
            StorageReference storage = firebaseStorage.getReference("Museums/" + nomeMuseo);
            storage.listAll().addOnSuccessListener(listResult -> {
                if (!listResult.getPrefixes().isEmpty() && !listResult.getItems().isEmpty()) {
                    Log.v("DOWNLOAD_MUSEO", "Non Vuoto");
                    MuseoLocalStorageDTO dto = localFileManager
                            .createMuseoDirWithFiles(context.getFilesDir(), nomeMuseo);

                    dto.getInfo().ifPresent(info -> storage.child("Info.json").getFile(info)
                            .addOnFailureListener(e -> Log.v("ERROR_info", e.getMessage())));

                    dto.getImmaginePrincipale().ifPresent(immagine -> storage.child(nomeMuseo + ".png").getFile(immagine)
                            .addOnFailureListener(e -> Log.v("ERROR_immagine_principale", e.getMessage())));

//                    dto.getStanzeDir().ifPresent(stanzeDir -> storage.child("Stanze").getFile(stanzeDir)
//                            .addOnFailureListener(e -> Log.v("ERROR_stanzeDir", e.getMessage())));
                    // download cartella stanze e tutti i suoi file in modo ricorsivo
                }
                else {
                    Log.e("DOWNLOAD_MUSEO",
                            String.format("museo %s non esistente nello storage in cloud",
                                    nomeMuseo));
                }
            }).addOnFailureListener(error -> Log.e("DOWNLOAD_MUSEO", error.getMessage()));
        }
    }

}