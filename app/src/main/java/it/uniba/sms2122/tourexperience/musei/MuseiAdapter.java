package it.uniba.sms2122.tourexperience.musei;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;


public class MuseiAdapter extends RecyclerView.Adapter<MuseiAdapter.ViewHolder> {
    private List<Museo> listaMusei;
    private Context context;

    // Constructor for initialization
    public MuseiAdapter(Context context, List<Museo> listaMusei) {
        this.context = context;
        this.listaMusei = listaMusei;
    }

    @NonNull
    @Override
    public MuseiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflating the Layout(Instantiates list_item.xml
        // layout file into View object)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        // Passing view to ViewHolder
        return new ViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull MuseiAdapter.ViewHolder holder, int position) {
        String fileUri = listaMusei.get(position).getFileUri().toString();
        if (fileUri.isEmpty()) {
            holder.images.setImageResource(R.drawable.ic_baseline_error_24);
        }
        else {
            holder.images.setImageURI(listaMusei.get(position).getFileUri());
        }
        holder.text.setText(listaMusei.get(position).getNome());
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return listaMusei.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        TextView text;

        public ViewHolder(View view) {
            super(view);
            images = view.findViewById(R.id.icona_item_lista);
            text = view.findViewById(R.id.nome_item_lista);

            // click di un item
//            view.setOnClickListener(_view -> {
//
//            });
        }
    }
}