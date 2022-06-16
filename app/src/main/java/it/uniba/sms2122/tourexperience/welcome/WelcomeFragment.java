package it.uniba.sms2122.tourexperience.welcome;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.Serializable;

import it.uniba.sms2122.tourexperience.R;

/**
 * @author Catignano Francesco
 * Primo fragment della welcome page
 */
public class WelcomeFragment extends Fragment {
    private static final String ARG_IMAGE = "imageRes";
    private static final String ARG_DESCRIPTION = "description";

    private int imageRes;
    private String description;

    public static WelcomeFragment newInstance(int imageRes, String description) {
        WelcomeFragment welcomeFragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_IMAGE, imageRes);
        bundle.putString(ARG_DESCRIPTION, description);
        welcomeFragment.setArguments(bundle);
        return welcomeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            this.imageRes = getArguments().getInt(ARG_IMAGE);
            this.description = getArguments().getString(ARG_DESCRIPTION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_welcome, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ImageView) view.findViewById(R.id.welcome_image)).setImageResource(imageRes);
        ((TextView) view.findViewById(R.id.welcome_description)).setText(description);
    }
}