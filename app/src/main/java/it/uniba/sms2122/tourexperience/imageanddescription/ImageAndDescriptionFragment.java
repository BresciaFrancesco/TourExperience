package it.uniba.sms2122.tourexperience.imageanddescription;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import it.uniba.sms2122.tourexperience.R;

/**
 * Fragment per racchiudere quella parte di UI che mostra una serie di immagini e una descrizione associata
 * (come nella pagina dei musei o delle opere).
 * @author Catignano Francesco
 */
public class ImageAndDescriptionFragment extends Fragment {
    private ArrayList<Integer> imageIds;
    private ViewPager2 imgViewPager;
    private String description;

    public ImageAndDescriptionFragment(ArrayList<Integer> imageIds, String description) {
        this.imageIds = imageIds;
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

        // Inizializzo il view pager
        imgViewPager = view.findViewById(R.id.image_viewpager);
        imgViewPager.setAdapter(new ImageAdapter(imageIds));
    }
}