package it.uniba.sms2122.tourexperience.games.SpotDifference.gameConfigurationJavaClass;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class GameConfiguration {

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://spotdifferencetest.appspot.com");
    StorageReference storageRef = storage.getReference();

    StorageReference XMLconfigFileRef;
    //byte[] xmlConfigFileToString;

    SetGameImages setImages;

    AppCompatActivity activityToConfig;
    String artToConfig;

    SetDifferencesButtonClick setAllClickOfDifferencesBtn;
    //SetDifferenceBtnCordinates setAllCordinatesOfDifferencesBtn;

    public GameConfiguration(AppCompatActivity activityToConfig, String artToConfig) {

        this.activityToConfig = activityToConfig;
        this.artToConfig = artToConfig;

        setImages = new SetGameImages(activityToConfig);

        XMLconfigFileRef = storageRef.child(artToConfig + "Config.xml");

        setConfigFile();

        //setImagesToGame();//setto la due immagini da far stampare sull'activity

        // setAllCordinatesOfDifferencesBtn = new SetDifferenceBtnCordinates(activityToConfig);


        setAllClickOfDifferencesBtn = new SetDifferencesButtonClick(activityToConfig);

    }

    /**
     * scarico il file di configurazione e lo parso
     */
    private void setConfigFile() {

        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        XMLconfigFileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {

            @Override
            public void onSuccess(byte[] bytes) {

                //parse json configuration file


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("exception", e.toString());
            }
        });
    }



}
