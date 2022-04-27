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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
                    downloadMuseoPercorso(percorso0_museo1[0], percorso0_museo1[1], context);
                })
                .setNegativeButton(android.R.string.no, null).show();
        }

        /**
         * Esegue il download e salvataggio in locale del percorso selezionato e,
         * nel caso il museo corrispondente non fosse presente in locale, scarica
         * e salva in locale anche tutte le altre informazioni del museo.
         * @param nomePercorso
         * @param nomeMuseo
         * @param context
         */
        private void downloadMuseoPercorso(final String nomePercorso,
                                 final String nomeMuseo,
                                 final Context context) {
            if (cacheMuseums.get(nomeMuseo) == null &&
                    cacheMuseums.get(nomeMuseo.toLowerCase()) == null) {
                downloadAll(nomePercorso, nomeMuseo, context);
            } else {
                downloadPercorso(nomePercorso, nomeMuseo, context);
            }
        }

        /**
         * Esegue il download e salvataggio in locale di un museo e tutte
         * le informazioni ad esso associato, tranne i file json dei percorsi.
         * @param nomeMuseo
         * @param context
         */
        private void downloadAll(final String nomePercorso,
                                 final String nomeMuseo,
                                 final Context context) {
            StorageReference storage = firebaseStorage.getReference("Museums/" + nomeMuseo);
            storage.listAll().addOnSuccessListener(listResult -> {
                if (!listResult.getPrefixes().isEmpty() && !listResult.getItems().isEmpty()) {
                    Log.v("DOWNLOAD_MUSEO", "Non Vuoto");

                    // Creo i riferimenti alle cartelle e i files principali
                    MuseoLocalStorageDTO dto = localFileManager
                            .createMuseoDirWithFiles(context.getFilesDir(), nomeMuseo);

                    // Scarico l'immagine principale del museo
                    dto.getImmaginePrincipale().ifPresent(immagine ->
                    storage.child(nomeMuseo + ".png").getFile(immagine)
                    .addOnFailureListener(e -> Log.v("ERROR_immagine_principale", e.getMessage()))
                    .addOnSuccessListener(taskSnapImage -> {
                        // Scarico il json di info del museo
                        // La creazione del file Info.json del museo, avviene solo dopo
                        // la creazione dell'immagine principale
                        dto.getInfo().ifPresent(info -> storage.child("Info.json").getFile(info)
                        .addOnFailureListener(e -> Log.v("ERROR_info", e.getMessage()))
                        .addOnSuccessListener(taskSnapshot -> {
                            try ( Reader reader = new FileReader(info) ) {
                                Museo museo = new Gson().fromJson(reader , Museo.class);
                                museo.setFileUri(context.getFilesDir().toString()
                                        + "/Museums/" + museo.getNome()
                                        + "/" + museo.getNome()
                                        + ".png");
                                cacheMuseums.put(nomeMuseo, museo);
                                Log.v("CACHE", nomeMuseo + " scaricato e cachato correttamente");
                            } catch (IOException | JsonSyntaxException | JsonIOException e) {
                                e.printStackTrace();
                            }
                        }));
                    }));

                    // TODO per ora scarico solo i file json delle varie stanze
                    // Scarico tutte le cartelle delle stanze con i file json delle stanze
                    dto.getStanzeDir().ifPresent(stanzeDir -> {
                        storage.child("Stanze").listAll().addOnSuccessListener(listStanze -> {
                            for (StorageReference dirStanza : listStanze.getPrefixes()) {
                                File dirStanzaLocale = localFileManager
                                        .createLocalDirectoryIfNotExists(stanzeDir, dirStanza.getName());
                                File jsonStanza = new File(
                                        dirStanzaLocale, "Info_stanza.json");
                                dirStanza.child("Info_stanza.json").getFile(jsonStanza)
                                .addOnFailureListener(e -> Log.e("ERROR_Info_stanza.json", e.getMessage()));
                            }
                        }).addOnFailureListener(e -> Log.e("ERROR_stanze", e.getMessage()));
                    });

                    downloadPercorso(nomePercorso, nomeMuseo, context);
                }
                else {
                    Log.e("DOWNLOAD_MUSEO",
                            String.format("museo %s non esistente nello storage in cloud", nomeMuseo));
                }
            }).addOnFailureListener(error -> Log.e("DOWNLOAD_MUSEO", error.getMessage()));
        }

        /**
         * Scarica il percorso scelto da firebase e lo salva in locale
         * @param nomePercorso
         * @param nomeMuseo
         * @param context
         */
        private void downloadPercorso(final String nomePercorso,
                                      final String nomeMuseo,
                                      Context context) {
            final String prefix = "Museums/" + nomeMuseo + "/Percorsi/";
            Log.v("PERCORSO", nomePercorso);
            StorageReference filePercorso = firebaseStorage.getReference(prefix + nomePercorso + ".json");
            File dirPercorsi = localFileManager.createLocalDirectoryIfNotExists(
                    context.getFilesDir(), "Museums/" + nomeMuseo + "/Percorsi");
            File jsonPercorso = new File(dirPercorsi, nomePercorso+".json");
            filePercorso.getFile(jsonPercorso)
            .addOnFailureListener(e -> Log.e("DOWNLOAD_PERCORSO", e.getMessage()))
            .addOnSuccessListener(taskSnapshot ->
                    Log.v("DOWNLOAD_PERCORSO", "Download del percorso eseguito correttamente"));
        }
    }

}