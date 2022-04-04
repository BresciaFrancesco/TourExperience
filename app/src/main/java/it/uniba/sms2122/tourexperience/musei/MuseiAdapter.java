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

import it.uniba.sms2122.tourexperience.R;


public class MuseiAdapter extends RecyclerView.Adapter<MuseiAdapter.ViewHolder> {
    ArrayList courseImg, courseName;
    Context context;

    // Constructor for initialization
    public MuseiAdapter(Context context, ArrayList courseImg, ArrayList courseName) {
        this.context = context;
        this.courseImg = courseImg;
        this.courseName = courseName;
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
        // TypeCast Object to int type
        int res = (int) courseImg.get(position);
        holder.images.setImageResource(res);
        holder.text.setText((String) courseName.get(position));
    }

    @Override
    public int getItemCount() {
        // Returns number of items
        // currently available in Adapter
        return courseImg.size();
    }

    // Initializing the Views
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView images;
        TextView text;

        public ViewHolder(View view) {
            super(view);
            images = (ImageView) view.findViewById(R.id.icona_item_lista);
            text = (TextView) view.findViewById(R.id.nome_item_lista);

            // click di un item
//            view.setOnClickListener(_view -> {
//
//            });
        }
    }
}