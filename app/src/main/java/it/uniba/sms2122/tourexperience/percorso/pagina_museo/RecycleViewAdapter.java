package it.uniba.sms2122.tourexperience.percorso.pagina_museo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.utility.StringUtility;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> names;

    private RecycleViewAdapter.onItemClickListener onItemClickListener;

    public RecycleViewAdapter(Context context, ArrayList<String> names) {
        this.context = context;
        this.names = names;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item ,parent,false);
        return new MyViewHolder(view);
    }

    // Binding data to the into specified position
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.textview.setText(StringUtility.safeViewing(names.get(position)));
        holder.itemView.setOnClickListener(view -> onItemClickListener.onClick(Integer.toString(holder.getAbsoluteAdapterPosition())));
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    /**
     * Classe che rappresenta un elemento della lista.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textview;
        ImageView imageView;
        Button deleteButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.nome_item_lista);
            imageView = itemView.findViewById(R.id.icona_item_lista);
            deleteButton = itemView.findViewById(R.id.delete_button);
            deleteButton.setVisibility(View.GONE);
            imageView.setImageResource(R.drawable.ic_path);
        }
    }

    public void setOnItemClickListener(RecycleViewAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener{
        void onClick(String str);
    }
}