package it.uniba.sms2122.tourexperience.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.uniba.sms2122.tourexperience.model.DTO.MuseoLocalStorageDTO;
import it.uniba.sms2122.tourexperience.model.Museo;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.*;

/**
 * Classe che gestisce tutti salvati nel filesystem locale relativi ai musei.
 */
public class LocalFileMuseoManager extends LocalFileManager {

    private final int BUFFER_DIM = 8192; // 8.2 KB
    private final long MAX_DIM = 30000000; // 30 MB
    private final List<String> acceptedExtensions = Arrays.asList(".json", ".png");
    private final List<String> filesMustHaveGeneral = Arrays.asList("/Stanze/", "/Opere/", "/Info.json");

    public LocalFileMuseoManager(String generalPath) {
        super(generalPath);
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
                    museo.setFileUri(generalPath + museo.getNome() + "/" + museo.getNome() + ".png");
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
     * Prepara il filesystem per il download dei percorsi/musei da cloud.
     * @param filesDir file come riferimento allo storage interno dell'app.
     * @param nomeMuseo nome del museo per creare la directory nella quale salvare tutto.
     * @return data transfer object contenente i riferimenti creati.
     */
    public MuseoLocalStorageDTO createMuseoDirWithFiles(final File filesDir, final String nomeMuseo) {
        final String prefix = "Museums/" + nomeMuseo + "/";
        File museoDir = createLocalDirectoryIfNotExists(filesDir, prefix);
        File stanzeDir = createLocalDirectoryIfNotExists(filesDir,prefix + "Stanze");
        File info = new File(filesDir, prefix + "Info.json");
        File immagine = new File(filesDir, prefix + nomeMuseo + ".png");

        return MuseoLocalStorageDTO.newBuilder()
                .setMuseoDir(museoDir)
                .setStanzeDir(stanzeDir)
                .setInfo(info)
                .setImmaginePrincipale(immagine);
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


    public void unzip(final File zipFile) throws IOException, IllegalArgumentException {
        this.checkZip(zipFile);
        final File targetDirectory = new File(generalPath);
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[BUFFER_DIM];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                try (FileOutputStream fout = new FileOutputStream(file)) {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                }
            }
        }
    }

    private boolean checkExtension(final String fileName) {
        for (String extension : acceptedExtensions) {
            if (fileName.endsWith(extension))
                return true;
        }
        return false;
    }

    private void cointains(final String filePath,
                              final String zipFileName,
                              final List<String> filesMustHave) {
        Iterator<String> i = filesMustHave.iterator();
        while (i.hasNext()) {
            String next = i.next();
            if (filePath.startsWith(zipFileName + next)) {
                i.remove();
                return;
            }
        }
    }

    private void checkZip(File zipFile) throws IOException, IllegalArgumentException {
        final List<String> filesMustHave = new LinkedList<>(filesMustHaveGeneral);
        String zipFileName = zipFile.getName();
        zipFileName = zipFileName.substring(0, zipFileName.length()-4);
        Log.v("CHECK_ZIP", "zip file name: " + zipFileName);
        long dimAccum = 0;
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                Log.v("CHECK_ZIP", ze.getName());

                dimAccum += ze.getSize();

                if (dimAccum > MAX_DIM) {
                    throw new IllegalArgumentException("Errore, dimensione accumulata per ora: " + dimAccum);
                }
                if (!ze.isDirectory() && !checkExtension(ze.getName())) {
                    throw new IllegalArgumentException("Estensione di un file non valida.");
                }
                cointains(ze.getName(), zipFileName, filesMustHave);
            }
        }
        if (!filesMustHave.isEmpty()) {
            throw new IllegalArgumentException("Nel file .zip mancano file/cartelle essenziali.");
        }
        Log.v("CHECK_ZIP","Dimensione accumulata: " + dimAccum);
    }

}
