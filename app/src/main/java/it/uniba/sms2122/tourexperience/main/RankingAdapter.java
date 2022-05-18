package it.uniba.sms2122.tourexperience.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.utility.ranking.FileRanking;
import it.uniba.sms2122.tourexperience.utility.ranking.MuseoDatabase;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private Context context;
    private List<MuseoDatabase> museoDatabaseList;
    private int ranking;
    private List<Map.Entry<String, String>> titleOrdered;

    public RankingAdapter (Context context, List<MuseoDatabase> museoDatabaseList, int ranking, List<Map.Entry<String, String>> titleOrdered){
        this.context = context;
        this.museoDatabaseList = museoDatabaseList;
        this.ranking = ranking;
        this.titleOrdered = titleOrdered;
    }

    @NonNull
    @Override
    public RankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ranking_item ,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.pos.setText(String.valueOf(position+1));
        holder.title.setText(titleOrdered.get(position).getKey() + titleOrdered.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return titleOrdered.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView pos;
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pos = itemView.findViewById(R.id.pos_item_classifica);
            title = itemView.findViewById(R.id.nome_item_classifica);
        }
    }
}
