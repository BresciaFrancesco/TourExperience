package it.uniba.sms2122.tourexperience.musei;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;

import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;


public class MuseiAdapter extends RecyclerView.Adapter<MuseiAdapter.ViewHolder> implements Filterable {

    private List<Museo> listaMusei;
    private final List<Museo> listaMuseiFiltered;
    private final SceltaMuseiFragment fragment;
    private final boolean flagMusei;

    // Constructor for initialization
    public MuseiAdapter(final SceltaMuseiFragment fragment, final List<Museo> listaMusei,
                        final boolean flagMusei) {
        this.fragment = fragment;
        this.listaMusei = (listaMusei == null) ? new ArrayList<>() : listaMusei;
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
        return new ViewHolder(view, flagMusei, this);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull MuseiAdapter.ViewHolder holder, int position) {
        Log.v("MuseiAdapter", "chiamato onBindViewHolder()");
        String fileUri = listaMusei.get(position).getFileUri();
        String nomeMuseoECitta = listaMusei.get(position).getNome() + "\n" + listaMusei.get(position).getCitta();
        if (fileUri.isEmpty()) {
            if (flagMusei)
                holder.images.setImageResource(R.drawable.ic_baseline_error_24);
            else
                holder.images.setImageResource(R.drawable.ic_baseline_museum_24);
        } else {
            holder.images.setImageURI(Uri.parse(listaMusei.get(position).getFileUri()));
        }
        holder.text.setText(nomeMuseoECitta);
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

    /**
     * Permette di aggiungere musei alla lista dinamicamente.
     * @param museo nuovo museo da aggiungere alla lista.
     */
    public void addMuseum(Museo museo) {
        listaMusei.add(museo);
    }


    /**
     * Classe che rappresenta un elemento della lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView images;
        private final TextView text;
        private final MuseiAdapter adapter;

        public ViewHolder(View view, boolean flagMusei, final MuseiAdapter adapter) {
            super(view);
            this.images = view.findViewById(R.id.icona_item_lista);
            this.text = view.findViewById(R.id.nome_item_lista);
            this.adapter = adapter;
            final Button deleteButton = view.findViewById(R.id.delete_button);

            // click di un item
            view.setOnClickListener((flagMusei)
                    ? this::listenerForMusei
                    : this::listenerForPercorsi
            );

            // delete button vale solo per la lista dei musei, non per quella dei percorsi
            if (flagMusei && !this.adapter.fragment.isListaMuseiEmpty()) {
                deleteButton.setOnClickListener(this::deleteMuseum);
            } else {
                deleteButton.setVisibility(View.GONE);
            }
        }

        /**
         * Listener per click su un museo. Serve per passare alla prossima activity
         * con le informazioni del museo selezionato.
         * @param view
         */
        private void listenerForMusei(View view) {
            // se la lista musei è vuota, non imposta nessun listener per il click
            if (adapter.fragment.isListaMuseiEmpty())
                return;
            String txt = text.getText().toString();
            MainActivity mainActivity = (MainActivity) adapter.fragment.getActivity();
            if (mainActivity == null) {
                Log.e("listenerForMusei", "mainActivity è null, ma non dovrebbe esserlo!");
                return;
            }
            mainActivity.startPercorsoActivity(txt.split("\n")[0].trim());
        }

        /**
         * Listener per click su un percorso. Serve per chiedere all'utente di scaricare
         * il percorso selezionato e nel caso di risposta affermativa dell'utente, scaricarlo.
         * @param view
         */
        private void listenerForPercorsi(View view) {
            String[] percorso0_museo1 = text.getText().toString().split("\n");
            Context context = view.getContext();
            new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.importa) + " " + percorso0_museo1[0].trim())
                .setMessage(context.getString(R.string.importa_msg)
                        + " " + percorso0_museo1[1].trim() + "?")
                .setIcon(R.drawable.ic_baseline_cloud_download_24)
                .setPositiveButton(context.getString(R.string.SI), (dialog, whichButton) -> {
                    new ImportPercorsi(view.getContext()).downloadMuseoPercorso(
                            percorso0_museo1[0],
                            percorso0_museo1[1]
                    );
                }).setNegativeButton(context.getString(R.string.NO), null).show();
        }

        /**
         * Elimina un museo dal filesystem locale e dalla lista.
         * @param view
         */
        private void deleteMuseum(View view) {
            String[] museo0_citta1 = text.getText().toString().split("\n");
            final String nomeMuseo = museo0_citta1[0].trim();
            final String citta = museo0_citta1[1].trim();
            final SceltaMuseiFragment fragment = adapter.fragment;

            new AlertDialog.Builder(view.getContext())
            .setTitle(fragment.getString(R.string.delete_museum_title, nomeMuseo))
            .setMessage(fragment.getString(R.string.delete_museum_message))
            .setPositiveButton(fragment.getString(R.string.SI), (dialog, whichButton) -> {
                Museo museo = new Museo(nomeMuseo, citta);
                LocalFileManager manager = new LocalFileManager(view.getContext().getFilesDir().toString());
                try {
                    manager.deleteDir(Paths.get(manager.getGeneralPath(), nomeMuseo).toFile());
                    fragment.getListaMusei().remove(museo);
                    adapter.listaMusei.remove(museo);
                    cacheMuseums.remove(nomeMuseo);
                    cachePercorsiInLocale.remove(nomeMuseo);
                    Log.v("DELETE_MUSEUM", "museo " + nomeMuseo + " eliminato correttamente");
                    // refresh fragment
                    Toast.makeText(
                        adapter.fragment.getContext(),
                        fragment.getString(R.string.museum_deleted, nomeMuseo),
                        Toast.LENGTH_LONG
                    ).show();
                    fragment.refreshListaMusei();
                } catch (IOException e) {
                    Log.e("DELETE_MUSEUM", "museo " + nomeMuseo + " non eliminato");
                    e.printStackTrace();
                    dialog.dismiss();
                }
            })
            .setNegativeButton(adapter.fragment.getString(R.string.NO), (dialog, whichButton) -> dialog.dismiss())
            .show();
        }

    }

}