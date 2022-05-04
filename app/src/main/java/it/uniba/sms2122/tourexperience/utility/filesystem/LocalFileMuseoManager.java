package it.uniba.sms2122.tourexperience.utility.filesystem;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.musei.checkzip.CheckJsonPercorso;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.Zip;

import static it.uniba.sms2122.tourexperience.utility.filesystem.zip.MimeType.*;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

/**
 * Classe che gestisce tutti salvati nel filesystem locale relativi ai musei.
 */
public class LocalFileMuseoManager extends LocalFileManager {

    public LocalFileMuseoManager(String generalPath) {
        super(generalPath);
    }

    /**
     * Ottiene il file di info json del museo scelto e ne ritorna un oggetto Museo.
     * @param nomeMuseo nome del museo da ottenere.
     * @return oggetto Museo scelto.
     * @throws IOException
     * @throws JsonSyntaxException
     * @throws JsonIOException
     */
    public Museo getMuseoByName(String nomeMuseo)
            throws IOException, JsonSyntaxException, JsonIOException {
        try (
                Reader reader = new
                    FileReader(Paths.get(generalPath, nomeMuseo, "Info.json").toFile())
        ) {
            Museo museo = new Gson().fromJson(reader, Museo.class);
            museo.setFileUri(
                Paths.get(generalPath, nomeMuseo, nomeMuseo + IMG_EXTENSION).toString()
            );
            return museo;
        }
    }

    /**
     * Ritorna la lista di musei prelevata dal filesystem locale.
     * Recupera i file di ogni museo e crea gli oggetti corrispondenti
     * inserendoli in una lista.
     * @return lista dei musei presenti nel filesystem.
     * @throws IOException
     */
    public List<Museo> getListMusei() throws IOException {
        Log.v("LocalFileMuseoManager", "chiamato getListMusei()");
        List<Museo> listaMusei = new ArrayList<>();
        Gson gson = new Gson();
        try (
                DirectoryStream<Path> stream =
                        Files.newDirectoryStream(Paths.get(generalPath))
        ) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                try ( Reader reader = new FileReader(path + "/Info.json") )
                {
                    Museo museo = gson.fromJson(reader , Museo.class);
                    museo.setFileUri(generalPath + museo.getNome() + "/" + museo.getNome() + IMG_EXTENSION);
                    listaMusei.add(museo);
                }
                catch (IOException | JsonSyntaxException | JsonIOException e) {
                    e.printStackTrace();
                }
            }
        }
        return listaMusei;
    }

    /**
     * Riempie la cache dei percorsi in locale, ma solo con i nomi dei percorsi.
     * @throws IOException
     */
    public void getPercorsiInLocale() throws IOException {
        try (
                DirectoryStream<Path> stream =
                    Files.newDirectoryStream(Paths.get(generalPath))
        ) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                String pathMuseo = path + "/Percorsi";
                try {
                    File[] files = new File(pathMuseo).listFiles();
                    if (files == null) continue;
                    addNewPercorsoToCache(path.getFileName().toString(), Stream.of(files)
                        .filter(file -> !file.isDirectory())
                        .map(file -> {
                            String name = file.getName();
                            return name.substring(0, name.length()-5);
                        })
                        .collect(Collectors.toList()));
                }
                catch (NullPointerException e) {
                    Log.e("NullPointerException",
                        "catturata in LocalFileMuseoManager.getPercorsiInLocale()\n" + e.getMessage());
                }
            }
        }
    }

    /**
     * Tenta di salvare il file ottenuto dalla selezione dell'utente.
     * @param fileName nome del file selezionato dall'utente.
     * @param mimeType mime type del file selezionato dall'utente (valgono solo zip e json).
     * @param dto Data Transfer Object contenente i dati utili all'apertura del file.
     * @return True se il file scelto è corretto, accettabile ed è stato salvato in locale,
     *         False altrimenti.
     */
    public boolean save(final String fileName, final String mimeType, final OpenFile dto) {
        boolean result = false;
        if (mimeType.equals(JSON.mimeType())) {
            CheckJsonPercorso cjp = new CheckJsonPercorso(dto, this);
            result = cjp.check();
        }
        else if (mimeType.equals(ZIP.mimeType())) {
            Zip zip = new Zip(this);
            result = zip.startUnzip(fileName, dto);
            if (result) {

            }
        }
        else {
            Log.e("LOCAL_IMPORT", "ALTRO NON PREVISTO");
        }
        return result;
    }

    /**
     * Elimina un la cartella di un museo, anche ricorsivamente se non vuota.
     * @param nomeMuseo nom del museo da eliminare.
     */
    public void deleteMuseo(final String nomeMuseo) {
        try {
            deleteDir(Paths.get(generalPath, nomeMuseo).toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
