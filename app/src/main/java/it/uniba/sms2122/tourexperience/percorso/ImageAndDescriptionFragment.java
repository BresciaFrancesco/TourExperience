package it.uniba.sms2122.tourexperience.percorso;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.uniba.sms2122.tourexperience.R;

/**
 * Fragment per racchiudere quella parte di UI che mostra una serie di immagini e una descrizione associata
 * (come nella pagina dei musei o delle opere)
 */
public class ImageAndDescriptionFragment extends Fragment {
    private static final String IMAGE_PATH = "imagePath";
    private static final String DESCRIPTION = "description";
    private String imagePath;
    private ImageView image;
    private String description;
    private TextView descriptionTextView;

    /**
     * Costruttore senza parametri per il ripristino del fragment automatico di android
     */
    public ImageAndDescriptionFragment() {
        this.imagePath = "";
        this.description = "Empty Description";
    }

    public ImageAndDescriptionFragment(String imagePath, String description) {
        this.imagePath = imagePath;
        this.description = description;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_and_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ripristino(savedInstanceState);

        // Inizializzo la descrizione
        descriptionTextView = view.findViewById(R.id.description);
        descriptionTextView.setText(description);

        // Inizializzo l'immagine
        image = view.findViewById(R.id.image);
        image.setImageURI(Uri.parse(imagePath));
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("ImageAndDescriptionFragment", "chiamato onSaveInstanceState()");
        if (this.imagePath != null) {
            outState.putString(IMAGE_PATH, this.imagePath);
        }
        if (this.description != null) {
            outState.putString(DESCRIPTION, this.description);
        }
    }

    /**
     * Ripristina i dati salvati da uno stato precedente se e solo se non sono nulli.
     * @param savedInstanceState bundle dello stato precedente.
     */
    public void ripristino(final Bundle savedInstanceState) {
        if (savedInstanceState == null || savedInstanceState.isEmpty())
            return;
        if (savedInstanceState.getString(IMAGE_PATH) == null ||
            savedInstanceState.getString(DESCRIPTION) == null)
            return;
        imagePath = savedInstanceState.getString(IMAGE_PATH);
        description = savedInstanceState.getString(DESCRIPTION);
    }


    @Override
    public void onViewStateRestored(@NonNull Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}