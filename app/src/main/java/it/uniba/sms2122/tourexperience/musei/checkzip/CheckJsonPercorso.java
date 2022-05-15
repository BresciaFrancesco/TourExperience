package it.uniba.sms2122.tourexperience.musei.checkzip;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;
import static it.uniba.sms2122.tourexperience.utility.Validate.*;

/**
 * Esegue controlli sui file .json dei percorsi da importare.
 */
public class CheckJsonPercorso {

    private final OpenFile dto;
    private final LocalFileManager localFileManager;

    public CheckJsonPercorso(final OpenFile dto, final LocalFileManager localFileManager) {
        this.dto = notNull(dto);
        this.localFileManager = notNull(localFileManager);
    }

    /**
     * Controllo approssimativo di un percorso .json
     * @return True se il file json è accettabile, False altrimenti.
     */
    public boolean check() {
        final Gson gson = new Gson();
        try ( Reader reader = new InputStreamReader(dto.openFile()) ) {
            final Percorso test = gson.fromJson(reader, Percorso.class);
            Percorso.checkAll(test);
            final Set<String> percorsi = cachePercorsiInLocale.get(test.getNomeMuseo());
            notNull(percorsi, "La cache dei percorsi in locale è nulla");
            isTrue(!percorsi.contains(test.getNomePercorso()), "Percorso già esistente");
            return save(test, gson);
        }
        catch (Exception e) {
            Log.e("LOCAL_IMPORT_JSON", "Json Percorso non valido\n" + e.getMessage());
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
            throws NullPointerException, IllegalArgumentException,
            IOException, JsonSyntaxException, JsonIOException
    {
        notNull(p, "Percorso vuoto");
        notNull(gson, "Gson vuoto");

        File filePercorsoJson = Paths.get(localFileManager.getGeneralPath(),
                p.getNomeMuseo(), "Percorsi", p.getNomePercorso()).toFile();

        notNull(cachePercorsiInLocale.get(p.getNomeMuseo()),
                "La cache dei percorsi in locale non ha il museo %s",
                p.getNomeMuseo())
        .add(p.getNomePercorso());

        isTrue(!filePercorsoJson.exists(), "File percorso o già esistente in locale");
        isTrue(filePercorsoJson.createNewFile());

        Writer targetFileWriter = new FileWriter(filePercorsoJson);
        targetFileWriter.write(gson.toJson(p));
        targetFileWriter.close();

        Log.v("LOCAL_IMPORT_JSON", "Json Percorso" + p.getNomePercorso() + " salvato correttamente");
        return true;
    }

}
