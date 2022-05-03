package it.uniba.sms2122.tourexperience.musei;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Optional;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.DTO.MuseoLocalStorageDTO;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;

public class ImportPercorsi implements Runnable {

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
     */
    public void downloadMuseoPercorso(final String nomePercorso,
                                      final String nomeMuseo,
                                      final ProgressBar progressBar) {
        if (cacheMuseums.get(nomeMuseo) == null &&
                cacheMuseums.get(nomeMuseo.toLowerCase()) == null) {
            downloadAll(nomePercorso, nomeMuseo, progressBar);
        } else {
            downloadPercorso(nomePercorso, nomeMuseo, progressBar);
        }
    }

    /**
     * Esegue il download e salvataggio in locale di un museo e tutte
     * le informazioni ad esso associato, con in più il file json del
     * percorso scelto dall'utente.
     * @param nomeMuseo
     */
    public void downloadAll(final String nomePercorso,
                            final String nomeMuseo,
                            final ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        StorageReference storage = firebaseStorage.getReference("Museums/" + nomeMuseo);
        storage.listAll().addOnSuccessListener(listResult -> {
            if (!listResult.getPrefixes().isEmpty() && !listResult.getItems().isEmpty()) {
                Log.v("DOWNLOAD_MUSEO", "Non Vuoto");

                // Creo i riferimenti alle cartelle e i files principali
                MuseoLocalStorageDTO dto = localFileManager
                        .createMuseoDirWithFiles(filesDir, nomeMuseo);

                // Comincio il download
                downloadStepOne(dto, storage, nomeMuseo, nomePercorso, progressBar);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, context.getString(R.string.path_from_cloud_empty),
                        Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(error ->
                fail(progressBar, "DOWNLOAD_MUSEO", error.getMessage()));
    }


    /**
     * Step 1 del download.
     * Scarico l'immagine principale del museo e il json di info del museo.
     * Il resto viene eseguito nello Step 2.
     * @param dto
     * @param storage
     * @param nomeMuseo
     * @param nomePercorso
     */
    private void downloadStepOne(final MuseoLocalStorageDTO dto,
                                   final StorageReference storage,
                                   final String nomeMuseo,
                                   final String nomePercorso,
                                   final ProgressBar progressBar) {
        dto.getImmaginePrincipale().ifPresent(immagine ->
            storage.child(nomeMuseo + ".png").getFile(immagine)
            .addOnSuccessListener(taskSnapImage -> {
                // Scarico il json di info del museo
                dto.getInfo().ifPresent(info -> storage.child("Info.json").getFile(info)
                .addOnSuccessListener(taskSnapshot -> {
                    try ( Reader reader = new FileReader(info) ) {
                        Museo museo = new Gson().fromJson(reader , Museo.class);
                        museo.setFileUri(filesDir.toString()
                                + "/Museums/" + museo.getNome()
                                + "/" + museo.getNome()
                                + ".png");
                        cacheMuseums.put(nomeMuseo, museo);
                        Log.v("CACHE", nomeMuseo + " scaricato e cachato correttamente");

                        // TODO per ora scarico solo i file json delle varie stanze
                        // Scarico tutte le cartelle delle stanze con i file json delle stanze
                        downloadStepTwo(dto, storage, nomeMuseo, nomePercorso, progressBar);

                    } catch (IOException | JsonSyntaxException | JsonIOException e) {
                        fail(progressBar, "exception_file_or_gson", e.getMessage());
                        e.printStackTrace();
                    }
                }).addOnFailureListener(e -> fail(progressBar, "info", e.getMessage())));
                fail(progressBar, dto.getInfo(), "info");
            }).addOnFailureListener(e ->
                    fail(progressBar, "immagine_principale", e.getMessage())));
        fail(progressBar, dto.getImmaginePrincipale(), "immagine_principale");
    }

    /**
     * Step 2 del download.
     * Scarico tutte le cartelle delle stanze con i file json delle stanze
     * @param dto
     * @param storage
     * @param nomeMuseo
     * @param nomePercorso
     * @throws IOException
     */
    private void downloadStepTwo(final MuseoLocalStorageDTO dto,
                                   final StorageReference storage,
                                   final String nomeMuseo,
                                   final String nomePercorso,
                                   final ProgressBar progressBar) throws IOException {
        dto.getStanzeDir().ifPresent(stanzeDir -> {
            storage.child("Stanze").listAll()
            .addOnSuccessListener(listStanze -> {
                for (StorageReference dirStanza : listStanze.getPrefixes()) {
                    File dirStanzaLocale = localFileManager
                        .createLocalDirectoryIfNotExists(stanzeDir, dirStanza.getName());
                    File jsonStanza = new File(
                        dirStanzaLocale, "Info_stanza.json");
                    dirStanza.child("Info_stanza.json").getFile(jsonStanza)
                        .addOnFailureListener(e -> Log.e("ERROR_Info_stanza.json", e.getMessage()));
                }
                // Scarica infine il percorso scelto
                downloadPercorso(nomePercorso, nomeMuseo, progressBar);
            })
            .addOnFailureListener(e -> fail(progressBar, "stanze", e.getMessage()));
        });
        fail(progressBar, dto.getStanzeDir(), "stanze");
    }

    /**
     * Scarica il percorso scelto da firebase e lo salva in locale
     * @param nomePercorso
     * @param nomeMuseo
     */
    public void downloadPercorso(final String nomePercorso,
                                 final String nomeMuseo,
                                 final ProgressBar progressBar) {
        if (progressBar.getVisibility() != View.VISIBLE)  {
            progressBar.setVisibility(View.VISIBLE);
        }
        final String prefix = "Museums/" + nomeMuseo + "/Percorsi/";
        StorageReference filePercorso = firebaseStorage.getReference(prefix + nomePercorso + ".json");
        File dirPercorsi = localFileManager.createLocalDirectoryIfNotExists(filesDir, prefix);
        File jsonPercorso = new File(dirPercorsi, nomePercorso+".json");
        filePercorso.getFile(jsonPercorso)
        .addOnCompleteListener(task -> progressBar.setVisibility(View.GONE))
        .addOnFailureListener(e -> Log.e("DOWNLOAD_PERCORSO", e.getMessage()))
        .addOnSuccessListener(taskSnapshot -> {
            addNewPercorsoToCache(nomeMuseo, Collections.singletonList(nomePercorso));
            //cachePercorsiInLocale.add(String.format("%s_%s", nomeMuseo, nomePercorso));
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
     * Quando un dto è vuoto, questo metodo viene usato per gestire e segnalare l'errore.
     * @param pb progressBar da fermare.
     * @param file file inserito in un Optional da controllare.
     * @param errorTag tag da aggiungere al tag del log di errore.
     */
    private void fail(final ProgressBar pb, final Optional<File> file,
                      final String errorTag) {
        if (file.isPresent()) return;
        pb.setVisibility(View.GONE);
        Log.e("ERROR_"+errorTag, "Non è presente nel DTO.");
    }

    /**
     * Quando avviene un fallimento catturato da un apposito listener, questo metodo
     * si occupa di gestirle e segnalare l'errore.
     * @param pb progressBar da fermare.
     * @param errorTag tag da aggiungere al tag del log di errore.
     * @param errorMessage messaggio di errore dinamico.
     */
    private void fail(final ProgressBar pb, final String errorTag,
                      final String errorMessage) {
        pb.setVisibility(View.GONE);
        Log.e("ERROR_"+errorTag, errorMessage);
    }

    /**
     * Metodo da eseguire su un altro Thread.
     * Ottiene i percorsi salvati in locale (solo museo e nome del percorso),
     * salvandoli in un'apposita cache.
     */
    @Override
    public void run() {
        Log.v("THREAD_Cache_Percorsi_Locale", "chiamato il thread");
        try { localFileManager.getPercorsiInLocale(); }
        catch (IOException e) {
            Log.e("THREAD_Cache_Percorsi_Locale",
                    "Problemi nella lettura dei file o delle cartelle");
            e.printStackTrace();
        }
    }
}
