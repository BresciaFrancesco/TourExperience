package it.uniba.sms2122.tourexperience.percorso.stanze;

import android.content.Context;
import android.util.Log;
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

public class StanzeAdpter extends RecyclerView.Adapter<StanzeAdpter.ViewHolder> {

    private Context context;
    /**
     * lista di stanza da visualizzare
     */
    private List<Stanza> stanzaList;
    /**
     * riferimento all'activity genitore PercorsoActivity
     */
    private PercorsoActivity parent;

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

        StanzeAdpter.ViewHolder vh = new StanzeAdpter.ViewHolder(view);

        // Passing view to ViewHolder
        return vh;
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return stanzaList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull StanzeAdpter.ViewHolder holder, int position) {

        int indexOfStanzaList = position;
        holder.textview.setText(stanzaList.get(position).getNome());
        holder.imageView.setImageResource(R.drawable.icon_stanza);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parent.getFgManagerOfPercorso().nextQRScannerFragmentOfRoomSelection(stanzaList.get(indexOfStanzaList).getId());
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textview;
        private Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.nome_item_lista);
            imageView = itemView.findViewById(R.id.icona_item_lista);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.GONE);
        }
    }
}
