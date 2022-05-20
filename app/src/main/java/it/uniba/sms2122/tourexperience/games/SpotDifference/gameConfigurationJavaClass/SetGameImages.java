package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import it.uniba.sms2122.tourexperience.R;

public class SetGameImages {

    AppCompatActivity activityToSet;

    //StorageReference image1Ref;
    //StorageReference image2Ref;

    ConstraintLayout image1Container;
    ConstraintLayout image2Container;

    public SetGameImages(AppCompatActivity activityToSet) {
        this.activityToSet = activityToSet;
    }

/**
 * ottengo le immagini dallo storage e chiamo le rispettive funzioni per settarle sull'activity
 */
    /*public void setImagesToGame() {

        this.image1Ref = storageRef.child(artToConfig + ".jpg");
        setImage1Game();

        this.image2Ref = storageRef.child(artToConfig + "Diff.jpg");
        setImage2Game();

    }*/


    /**
     * setto la prima immagine come background
     * <p>
     * in questo caso sto provando a settarla facendo usa della memoria diretta dell'app, quindi non scarico niente nel file managere del device
     */
    public void setImage1Game(StorageReference image1Ref) {

        image1Container = activityToSet.findViewById(R.id.image1Container);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        image1Ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                image1Container = activityToSet.findViewById(R.id.image1Container);//recupero la view su cui settare il bacìkground
                Bitmap image1 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);//mi recupero dall array bytes il bitmap della imagine recuperata
                Drawable image1Drawable = new BitmapDrawable(image1);//converto la bitmap in risorsa drawable
                image1Container.setBackground(image1Drawable);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception", e.toString());
            }
        });

        Log.e("image1ref", image1Ref.toString());
    }

    /**
     * setto la seconda immagine come background
     * <p>
     * in questo caso provo a scaricare nella memoria del device
     */
    public void setImage2Game(StorageReference image2Ref) {


    }
}
