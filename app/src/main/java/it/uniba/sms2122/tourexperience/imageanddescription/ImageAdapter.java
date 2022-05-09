package it.uniba.sms2122.tourexperience.imageanddescription;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.R;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    List<String> imagesPaths;

    public ImageAdapter(List<String> imagesPaths) {
        this.imagesPaths = imagesPaths;
    }

    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        Uri currentImage = Uri.parse(imagesPaths.get(position));
        holder.imageItem.setImageURI(currentImage);
    }

    @Override
    public int getItemCount() {
        return imagesPaths.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.image_item);
        }
    }
}
