package it.uniba.sms2122.tourexperience.utility.filesystem.zip;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.uniba.sms2122.tourexperience.R;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.musei.SceltaMuseiFragment;
import it.uniba.sms2122.tourexperience.musei.checkzip.CheckZipMuseum;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileMuseoManager;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

/**
 * Classe che effettua l'unzip di un file .zip.
 */
public class Zip {
    private final int bufferDim = 32768; // 32 KB
    private final CheckZipMuseum checker;
    private final LocalFileMuseoManager localFileManager;


    public Zip(final LocalFileMuseoManager localFileManager) {
        this.checker = new CheckZipMuseum();
        this.localFileManager = localFileManager;
    }

    /**
     * Comincia l'unzip di un file .zip.
     * @param zipName nome del file .zip con l'estensione .zip integrata.
     * @param of oggetto che implementa l'interfaccia OpenFile, per
     *           poter aprire il file .zip in lettura.
     * @param frag fragment dalla quale è chiamato questo metodo
     * @return true se l'unzip è andato a buon fine, false altrimenti.
     */
    public boolean startUnzip(final String zipName, final OpenFile of, final SceltaMuseiFragment frag) {
        String nomeMuseo;
        Set<String> nomiPercorsi;
        try {
            nomiPercorsi = checker.start(of, zipName);
            nomeMuseo = zipName.substring(0, zipName.length()-4);
        }
        catch (ZipCheckerException | ZipCheckerRunTimeException e) {
            Log.e("CHECK_ZIP", "ECCEZIONE in CHECK ZIP...");
            e.printStackTrace();
            return false;
        }
        Log.v("CHECK_ZIP", "CHECK ZIP superato - inizio UNZIP...");

        // ESEGUO L'UNZIP
        try {
            unzip(of);
            Log.v("CHECK_ZIP", "fine UNZIP...");

            // RIEMPIO LA CACHE
            updateUI(nomeMuseo, nomiPercorsi, frag);
        }
        catch (IOException | JsonSyntaxException | JsonIOException | Error e) {
            Log.e("CHECK_ZIP", "ECCEZIONE in UNZIP...");
            try {
                localFileManager.deleteDir(
                        Paths.get(localFileManager.getGeneralPath(), zipName).toFile()
                );
                Log.v("CHECK_ZIP", "DELETE dei files completato...");
            }
            catch (IOException t) {
                Log.e("CHECK_ZIP", "ECCEZIONE in DELETE DIR...");
                t.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * Esegue l'unzip di un file .zip in modo sicuro ed efficiente.
     * @param of oggetto che implementa l'interfaccia OpenFile, per
     *           poter aprire il file .zip in lettura
     * @throws IOException se avviene qualunque errore di lettura/scrittura.
     */
    public void unzip(final OpenFile of) throws IOException {
        File targetDirectory = new File(localFileManager.getGeneralPath());
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(of.openFile(), bufferDim))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[bufferDim];
            while ((ze = zis.getNextEntry()) != null) {

                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (!dir.isDirectory() && !dir.mkdirs()) {
                    throw new FileNotFoundException("Impossibile garantire la directory: " +
                            dir.getAbsolutePath());
                }
                if (ze.isDirectory()) continue;

                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }
            }
        }
    }


    /**
     * Aggiorna la UI, aggiornando prima di tutto le cache.
     * @param nomeMuseo nome del museo
     * @param nomiPercorsi Set contenete il nome di percorsi aggiunti
     * @param frag fragment da aggiornare
     * @throws IOException
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
    private void updateUI(final String nomeMuseo,
                          final Set<String> nomiPercorsi,
                          final SceltaMuseiFragment frag)
            throws IOException, JsonSyntaxException, JsonIOException
    {
        Museo museo = localFileManager.getMuseoByName(nomeMuseo);
        if (cacheMuseums.get(nomeMuseo) == null) {
            cacheMuseums.put(nomeMuseo, museo);
            List<Museo> listaPrincipale = frag.getListaMusei();
            if (frag.isListaMuseiEmpty()) {
                listaPrincipale.clear();
            }
            listaPrincipale.add(museo);

            // DA TESTARE
            /*DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Museums");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Map<String, String> res = (Map<String, String>) snap.getValue();
                        Log.v("RES", res.toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });*/

            // DA TESTARE
            /*Map<String, String> mappa = new HashMap<>();
            mappa.put("citta", museo.getCitta());
            mappa.put("nome", museo.getNome());
            mappa.put("tipologia", museo.getTipologia());

            FirebaseDatabase.getInstance().getReference("Museums").push().setValue(mappa)
            .addOnSuccessListener(snap -> Log.e("FIREBASE_MUSEUM_IMPORT", "Museo aggiunto correttamente"))
            .addOnFailureListener(err -> Log.e("FIREBASE_MUSEUM_IMPORT", err.getMessage()));*/
        }
        if (cachePercorsiInLocale.get(nomeMuseo) == null) {
            cachePercorsiInLocale.put(nomeMuseo, nomiPercorsi);
        } else {
            cachePercorsiInLocale.get(nomeMuseo).addAll(nomiPercorsi);
        }
        Log.v("CHECK_ZIP", "Museo/Percorsi inseriti in cache...");
    }

}
