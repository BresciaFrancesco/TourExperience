package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

import static it.uniba.sms2122.tourexperience.utility.GenericUtility.*;
import static it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager.createLocalDirectoryIfNotExists;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.Zip;

public class ImportPercorsi {

    private final static FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private final LocalFileMuseoManager localFileManager;
    private final Context context;
    private final File filesDir;
    private static Back backToMuseumsList;

    public ImportPercorsi(final Context context) {
        this.context = context;
        this.filesDir = context.getFilesDir();
        this.localFileManager = new LocalFileMuseoManager(this.filesDir.toString());
    }

    public static void setBackToMuseumsList(Back back) {
        backToMuseumsList = back;
    }

    /**
     * Esegue il download e salvataggio in locale del percorso selezionato e,
     * nel caso il museo corrispondente non fosse presente in locale, scarica
     * e salva in locale anche tutte le altre informazioni del museo.
     * @param nomePercorso
     * @param nomeMuseo
     * @param progressBar
     */
    public void downloadMuseoPercorso(final String nomePercorso,
                                      final String nomeMuseo,
                                      final ProgressBar progressBar) {
        if (!thereIsConnection(() -> Toast.makeText(
                context,
                context.getString(R.string.no_connection),
                Toast.LENGTH_SHORT).show()
        )) return;
        if (cacheMuseums.get(nomeMuseo) == null &&
                cacheMuseums.get(nomeMuseo.toLowerCase()) == null) {
            downloadAll_v2(nomePercorso, nomeMuseo, progressBar);
        } else {
            downloadPercorso(nomePercorso, nomeMuseo, progressBar);
        }
    }


    private void downloadAll_v2(final String nomePercorso,
                                final String nomeMuseo,
                                final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference storage = firebaseStorage
            .getReference(String.format("Museums_v2/%s/%s.zip", nomeMuseo, nomeMuseo));
        try {
            File tempFile = File.createTempFile("temp", null);
            storage.getFile(tempFile).addOnSuccessListener(taskSnapshot -> {

                createLocalDirectoryIfNotExists(filesDir, "Museums/" + nomeMuseo);
                createLocalDirectoryIfNotExists(filesDir, "Museums/" + nomeMuseo + "/Percorsi");

                new Thread(() -> {
                    try {
                        Zip zip = new Zip(localFileManager);
                        zip.unzip(() -> new FileInputStream(tempFile.getAbsoluteFile()));
                        Museo museo = localFileManager.getMuseoByName(nomeMuseo);
                        cacheMuseums.put(nomeMuseo, museo);
                        Log.v("CACHE MUSEI", nomeMuseo + " scaricato e cachato correttamente");
                    }
                    catch (IOException | JsonSyntaxException | JsonIOException e) {
                        ((Activity)context).runOnUiThread(() -> {
                            fail(progressBar, "DOWNLOAD",
                                    "Errore di unzip o downloadPercorso o parser Gson",
                                    "Download fallito");
                        });
                        e.printStackTrace();
                        localFileManager.deleteMuseo(nomeMuseo);
                    }
                    new Thread(tempFile::delete).start();
                }).start();

                downloadPercorso(nomePercorso, nomeMuseo, progressBar);

            }).addOnFailureListener(error -> {
                fail(progressBar, "DOWNLOAD", error.getMessage(), "Download fallito");
                new Thread(tempFile::delete).start();
            });
        } catch (IOException e) {
            fail(progressBar, "DOWNLOAD",
                    "Impossibile creare un file temporaneo per il file .zip",
                    "Download fallito");
        }
    }

    /**
     * Scarica il percorso scelto da firebase e lo salva in locale
     * @param nomePercorso
     * @param nomeMuseo
     * @param progressBar
     */
    public void downloadPercorso(final String nomePercorso,
                                 final String nomeMuseo,
                                 final ProgressBar progressBar) {
        if (progressBar.getVisibility() != View.VISIBLE)  {
            progressBar.setVisibility(View.VISIBLE);
        }
        final String suffix = Paths.get(nomeMuseo, "Percorsi", nomePercorso+".json").toString();
        final String fileCloud = Paths.get("Museums_v2", suffix).toString();
        final File localFile = Paths.get(filesDir.getAbsolutePath(), "Museums", suffix).toFile();
        firebaseStorage.getReference(fileCloud).getFile(localFile)
        .addOnCompleteListener(task -> progressBar.setVisibility(View.GONE))
        .addOnFailureListener(e -> Log.e("DOWNLOAD_PERCORSO", e.getMessage()))
        .addOnSuccessListener(taskSnapshot -> {
            addNewPercorsoToCache(nomeMuseo, Collections.singletonList(nomePercorso));
            // Eseguo la rimozione del percorso dalla cache
            cachePercorsi.remove(new Museo(nomePercorso, nomeMuseo));
            if (backToMuseumsList != null) {
                backToMuseumsList.back(null);
            }
            Toast.makeText(context,
                nomePercorso + ", del museo " + nomeMuseo + ", scaricato correttamente!",
                Toast.LENGTH_LONG).show();

            Log.v("DOWNLOAD_PERCORSO",
                    String.format("Download del percorso %s eseguito correttamente", nomePercorso));
        });
    }

    /**
     * Quando avviene un fallimento catturato da un apposito listener, questo metodo
     * si occupa di gestirle e segnalare l'errore.
     * @param pb progressBar da fermare.
     * @param errorTag tag da aggiungere al tag del log di errore.
     * @param errorMessage messaggio di errore dinamico.
     */
    private void fail(final ProgressBar pb, final String errorTag,
                      final String errorMessage, final String toastMessage) {
        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
        pb.setVisibility(View.GONE);
        Log.e("ERROR_"+errorTag, errorMessage);
    }

}
