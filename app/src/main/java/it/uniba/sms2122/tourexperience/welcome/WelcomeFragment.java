package it.uniba.sms2122.tourexperience.welcome;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import it.uniba.sms2122.tourexperience.R;

/**
 * @author Catignano Francesco
 * Primo fragment della welcome page
 */
public class WelcomeFragment extends Fragment {
    private Drawable image;
    private String description;

    public WelcomeFragment(@NonNull Drawable image, @NonNull String description) {
        this.image = image;
        this.description = description;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ImageView) view.findViewById(R.id.welcome_image)).setImageDrawable(image);
        ((TextView) view.findViewById(R.id.welcome_description)).setText(description);
    }
}