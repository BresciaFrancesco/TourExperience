package it.uniba.sms2122.tourexperience.imageanddescription;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    ArrayList<Integer> imageIds;

    public ImageAdapter(ArrayList<Integer> imageIds) {
        this.imageIds = imageIds;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        int currentImage = imageIds.get(position);
        holder.imageItem.setImageResource(currentImage);
    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.image_item);
        }
    }
}
