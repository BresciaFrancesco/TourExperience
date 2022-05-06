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
import java.util.Map;
import java.util.Set;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.graph.Vertex;
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

            checkString(test.getNomeMuseo(), "Nome museo vuoto");
            checkString(test.getNomePercorso(), "Nome percorso vuoto");

            Set<String> percorsi = cachePercorsiInLocale.get(test.getNomeMuseo());
            if (percorsi == null) {
                Log.e("LOCAL_IMPORT_JSON",
                        "Il Percorso è associato ad un museo non presente in locale");
                return false;
            }
            if (percorsi.contains(test.getNomePercorso())) {
                Log.e("LOCAL_IMPORT_JSON", "Percorso già esistente");
                return false;
            }

            checkString(test.getIdStanzaCorrente(), "Id stanza corrente vuoto");
            checkString(test.getIdStanzaFinale(), "Id stanza finale vuoto");
            checkString(test.getDescrizionePercorso(), "Descrizione vuota");

            Map<String, Vertex> mappa = test.getMappa();
            Set<String> keySet = mappa.keySet();
            for (String key : keySet) {
                Set<String> edges = mappa.get(key).getEdges();
                for (String edge : edges) {
                    if (mappa.get(edge) == null) {
                        throw new NullPointerException("Archi sbagliati");
                    }
                }
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
        File filePercorsoJson = Paths.get(localFileManager.getGeneralPath(),
                p.getNomeMuseo(), "Percorsi", p.getNomePercorso()).toFile();

        cachePercorsiInLocale.get(p.getNomeMuseo()).add(p.getNomePercorso());

        if (filePercorsoJson.exists()) {
            Log.e("LOCAL_IMPORT_JSON", "File percorso o già esistente in locale");
            return false;
        }
        if (!filePercorsoJson.createNewFile()) {
            Log.e("LOCAL_IMPORT_JSON", "File percorso non creabile");
            return false;
        }
        Writer targetFileWriter = new FileWriter(filePercorsoJson);
        targetFileWriter.write(gson.toJson(p));
        targetFileWriter.close();

        Log.v("LOCAL_IMPORT_JSON", "Json Percorso" + p.getNomePercorso() + " salvato correttamente");
        return true;
    }

    /**
     * Controlla in modo generico se una stringa è vuota. Se lo è, stampa un log
     * di errore e ritorna una NullPointerException.
     * @param s stringa da controllare.
     * @param errorMessage messaggio di errore da stampare nel log in caso di stringa vuota.
     * @throws NullPointerException se la stringa "s" è vuota.
     */
    private void checkString(final String s, final String errorMessage) throws NullPointerException {
        if (!s.isEmpty()) return;
        Log.e("LOCAL_IMPORT_JSON", errorMessage);
        throw new NullPointerException("Stringa vuota trovata");
    }

}
