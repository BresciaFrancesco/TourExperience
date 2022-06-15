package it.uniba.sms2122.tourexperience.percorso.pagina_stanza;

import android.content.Context;
import android.net.Uri;
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
import it.uniba.sms2122.tourexperience.model.Opera;

public class NearbyOperasAdapter extends RecyclerView.Adapter<NearbyOperasAdapter.MyViewHolder> {
    private static final int MAX_LETTERS_VISIBLE = 15;

    private Context context;
    private ArrayList<Opera> nearbyOperas;

    private NearbyOperasAdapter.onItemClickListener onItemClickListener;

    public NearbyOperasAdapter(Context context) {
        this.context = context;
        this.nearbyOperas = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = getAdaptedString(nearbyOperas.get(position).getNome());
        holder.textview.setText(name);
        holder.imageView.setImageURI(Uri.parse(nearbyOperas.get(position).getPercorsoImg()));
    }

    @Override
    public int getItemCount() {
        return nearbyOperas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addOperas(ArrayList<Opera> operas) {
        if(operas != null) {
            nearbyOperas.clear();
            nearbyOperas.addAll(operas);
        }
    }

    public void clear() {
        nearbyOperas.clear();
    }

    /**
     * Adatta la stringa presa in input alle dimensioni dell'item in modo da poterla visualizzare correttamente
     * @param s La stringa da adattare
     * @return La stringa adattata
     */
    private static String getAdaptedString(String s) {
        if(s==null)
            return null;
        if(s.isEmpty() || s.length() < 15)
            return s;

        String[] splitted = s.split(" ");
        if(splitted.length == 1)
            return s;

        StringBuilder builder = new StringBuilder();
        int letterCount = 0;

        for(String word : splitted) {
            if(letterCount==0) {
                builder.append(word);
                letterCount += word.length();
            }
            else if(letterCount >= MAX_LETTERS_VISIBLE) {
                builder.append("\n");
                builder.append(word);
                letterCount = word.length();    // ricomincia una nuova riga
            }
            else {
                builder.append(" ");
                builder.append(word);
                letterCount += word.length();
            }
        }
        return builder.toString();
    }


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
        }
    }

    public void setOnItemClickListener(NearbyOperasAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface onItemClickListener{
        void onClick(String str);
    }
}