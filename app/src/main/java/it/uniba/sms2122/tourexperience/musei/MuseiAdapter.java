package it.uniba.sms2122.tourexperience.musei;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;

import it.uniba.sms2122.tourexperience.main.MainActivity;
import it.uniba.sms2122.tourexperience.model.Museo;


public class MuseiAdapter extends RecyclerView.Adapter<MuseiAdapter.ViewHolder> implements Filterable {

    private List<Museo> listaMusei;
    private List<Museo> listaMuseiFiltered;
    private boolean flagMusei;
    private static MainActivity mainActivity = null;
    private ProgressBar progressBar;

    // Constructor for initialization
    public MuseiAdapter(final MainActivity activity, final ProgressBar pb,
                        final List<Museo> listaMusei, final boolean flagMusei) {
        if (mainActivity == null) {
            mainActivity = activity;
        }
        if (listaMusei == null) {
            this.listaMusei = new ArrayList<>();
        } else this.listaMusei = listaMusei;
        this.progressBar = pb;
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
        ViewHolder vh = new ViewHolder(view, progressBar, flagMusei);
        if (flagMusei) {
            vh.addMainActivity(mainActivity);
        }
        return vh;
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

    /**
     * Permette di aggiungere musei alla lista dinamicamente.
     * @param museo nuovo museo da aggiungere alla lista.
     */
    public void addMuseum(Museo museo) {
        listaMusei.add(museo);
    }

    public List<Museo> getListaMusei() {
        return listaMusei;
    }

    /**
     * Classe che inizializza la View. Rappresenta un elemento della lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView images;
        private TextView text;
        private final ImportPercorsi importPercorsi;
        private final ProgressBar progressBar;
        private static MainActivity mainActivity = null;

        public ViewHolder(View view, ProgressBar pb, boolean flagMusei) {
            super(view);
            this.images = view.findViewById(R.id.icona_item_lista);
            this.text = view.findViewById(R.id.nome_item_lista);
            this.progressBar = pb;
            this.importPercorsi = new ImportPercorsi(view.getContext());
            // click di un item
            view.setOnClickListener((flagMusei)
                   ? this::listenerForMusei
                   : this::listenerForPercorsi
            );
        }

        public void addMainActivity(final MainActivity activity) {
            if (mainActivity == null) {
                mainActivity = activity;
            }
        }

        /**
         * Listener per click su un museo. Serve per passare alla prossima activity
         * con le informazioni del museo selezionato.
         * @param view
         */
        private void listenerForMusei(View view) {
            if (mainActivity == null) {
                Log.e("listenerForMusei", "mainActivity è null, ma non dovrebbe esserlo!");
                return;
            }
            mainActivity.startPercorsoActivity(text.getText().toString().split("\n")[0].trim());
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
                    importPercorsi.downloadMuseoPercorso(
                            percorso0_museo1[0],
                            percorso0_museo1[1],
                            progressBar
                    );
                }).setNegativeButton(context.getString(R.string.NO), null).show();
        }
    }

}