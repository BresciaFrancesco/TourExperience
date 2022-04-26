package it.uniba.sms2122.tourexperience.percorso.stanze;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Stanza;

public class StanzeAdpter extends RecyclerView.Adapter<StanzeAdpter.ViewHolder> {

    private Context context;
    private List<Stanza> stanzaList;

    public StanzeAdpter (Context context, List<Stanza> stanzaList){
        this.context = context;
        this.stanzaList = stanzaList;
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
        //TODO togliere il commento
        //return stanzaList.size();
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textview.setText(stanzaList.get(position).getNome());
        holder.imageView.setImageResource(R.drawable.icon_stanza);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textview;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.nameRoute);
            imageView = itemView.findViewById(R.id.imageRoute);
        }
    }
}
