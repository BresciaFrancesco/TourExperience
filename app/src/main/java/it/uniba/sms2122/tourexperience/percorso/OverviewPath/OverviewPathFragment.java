package it.uniba.sms2122.tourexperience.percorso.OverviewPath;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.percorso.PercorsoActivity;

public class OverviewPathFragment extends Fragment {

    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://tour-experience.appspot.com");
    StorageReference storageReference;

    View inflater;
    ImageView pathPicture;
    TextView pathName;
    TextView pathDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater.inflate(R.layout.overview_path_fragment, container, false);

        setStorageReference();

        setDynamicValuesOnView();

        return this.inflater;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        inflater.findViewById(R.id.startPathButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PercorsoActivity parentActivity = (PercorsoActivity) getActivity();
                parentActivity.nextStanzeFragment();
            }
        });
    }

    private void setDynamicValuesOnView(){

        pathPicture = inflater.findViewById(R.id.pathPicture);
        //code for set dynamyc picture

        pathName = inflater.findViewById(R.id.pathName);
        pathName.setText("path name dinamyc");

        pathDescription = inflater.findViewById(R.id.pathDescription);
        pathDescription.setText("dinamyc descriptio");


    }

    private void setStorageReference(){

        storageReference = firebaseStorage.getReference();
        storageReference = storageReference.child("Museums");
        storageReference = storageReference.child("Louvre");
        storageReference = storageReference.child("Percorsi");

    }
}
