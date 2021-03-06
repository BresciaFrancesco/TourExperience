package it.uniba.sms2122.tourexperience.percorso.stanze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;
import it.uniba.sms2122.tourexperience.utility.StringUtility;

public class StanzeAdpter extends RecyclerView.Adapter<StanzeAdpter.ViewHolder> {

    private final Context context;
    /**
     * lista di stanza da visualizzare
     */
    private final List<Stanza> stanzaList;
    /**
     * riferimento all'activity genitore PercorsoActivity
     */
    private final PercorsoActivity parent;

    public StanzeAdpter(Context context, List<Stanza> stanzaList, PercorsoActivity parent) {
        this.context = context;
        this.stanzaList = stanzaList;
        this.parent = parent;
    }

    @NonNull
    @Override
    public StanzeAdpter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        // Passing view to ViewHolder
        return new StanzeAdpter.ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return stanzaList.size();
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull StanzeAdpter.ViewHolder holder, int position) {

        int indexOfStanzaList = position;
        holder.textview.setText(stanzaList.get(position).getNome());
        holder.imageView.setImageResource(R.drawable.icon_stanza);

        holder.itemView.setOnClickListener(view -> parent.getFgManagerOfPercorso().openQRScannerFragmentOfRoomSelection(stanzaList.get(indexOfStanzaList).getId()));
    }

    /**
     * Classe che rappresenta un elemento della lista.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView textview;
        private final Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.nome_item_lista);
            imageView = itemView.findViewById(R.id.icona_item_lista);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.GONE);
        }
    }
}
