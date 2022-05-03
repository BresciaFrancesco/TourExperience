package it.uniba.sms2122.tourexperience.musei.checkzip;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

/**
 * Esegue controlli sui file .json dei percorsi da importare.
 */
public class CheckJsonPercorso {

    private final OpenFile dto;
    private final LocalFileManager localFileManager;

    public CheckJsonPercorso(final OpenFile dto, final LocalFileManager localFileManager) {
        this.dto = dto;
        this.localFileManager = localFileManager;
    }

    /**
     * Controllo approssimativo di un percorso .json
     * @return True se il file json è accettabile, False altrimenti.
     */
    public boolean check() {
        Gson gson = new Gson();
        Percorso test;
        try ( Reader reader = new InputStreamReader(dto.openFile()) ) {
            test = gson.fromJson(reader, Percorso.class);
            if (!cacheMuseums.containsKey(test.getNomeMuseo())) {
                Log.e("LOCAL_IMPORT_JSON",
                        "Il Percorso è associato ad un museo non presente in locale");
                return false;
            }
            if (test.getNomePercorso().isEmpty()) {
                Log.e("LOCAL_IMPORT_JSON", "Nome vuoto");
                return false;
            }

            Set<String> percorsi = cachePercorsiInLocale.get(test.getNomeMuseo());
            if (percorsi.contains(test.getNomePercorso())) {
                Log.e("LOCAL_IMPORT_JSON", "Percorso già esistente");
                return false;
            }

            if (test.getStanzaCorrente().getOpere().isEmpty()) {
                Log.e("LOCAL_IMPORT_JSON", "Opere vuote");
                return false;
            }
            if (test.getAdiacentNodes().isEmpty()) {
                Log.e("LOCAL_IMPORT_JSON", "Non è un percorso");
                return false;
            }
            if (test.getDescrizionePercorso().isEmpty()) {
                Log.e("LOCAL_IMPORT_JSON", "Descrizione vuota");
                return false;
            }

            return save(test, gson);
        }
        catch (NullPointerException | IOException | JsonSyntaxException | JsonIOException e) {
            Log.e("LOCAL_IMPORT_JSON", "Json Percorso non valido");
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Crea e salva un file con il json importato.
     * @param p oggetto percorso da salvare in file e precedentemente analizzato.
     * @param gson parser Gson.
     * @return True se il file è stato correttamente salvato, False altrimenti.
     * @throws NullPointerException
     * @throws IOException
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
    private boolean save(final Percorso p, final Gson gson)
            throws NullPointerException, IOException, JsonSyntaxException, JsonIOException {
        File filePercorsoJson = Paths.get(
                localFileManager.getGeneralPath(),
                p.getNomeMuseo(),
                "Percorsi",
                p.getNomePercorso()
        ).toFile();

        Set<String> percorsi = cachePercorsiInLocale.get(p.getNomeMuseo());
        percorsi.add(p.getNomePercorso());

        if (filePercorsoJson.exists() || !filePercorsoJson.createNewFile()) {
            Log.e("LOCAL_IMPORT_JSON",
                    "File percorso non creabile o già esistente in locale");
            return false;
        }
        Writer targetFileWriter = new FileWriter(filePercorsoJson);
        targetFileWriter.write(gson.toJson(p));
        targetFileWriter.close();

        Log.v("LOCAL_IMPORT_JSON", "Json Percorso valido");
        return true;
    }
}
