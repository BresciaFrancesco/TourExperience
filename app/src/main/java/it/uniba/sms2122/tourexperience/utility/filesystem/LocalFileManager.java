package it.uniba.sms2122.tourexperience.utility.filesystem;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFileManager {

    protected final String generalPath;
    protected final String IMG_EXTENSION = ".webp";

    public LocalFileManager(String generalPath) {
        if (generalPath.charAt(generalPath.length()-1) != '/') {
            generalPath += "/";
        }
        this.generalPath = generalPath + "Museums/";
    }

    /**
     * Ritorna il path generale utilizzato da tutte le classi LocalFileManager.
     * @return path generale.
     */
    public String getGeneralPath() {
        return generalPath;
    }

    /**
     * Crea una directory nel filesystem locale, nel path specificato come parametro,
     * se non esiste già.
     */
    public static File createLocalDirectoryIfNotExists(final File filesDir,
                                                       final String pathFileWithFile) {
        File directory = new File(filesDir, pathFileWithFile);
        if (!directory.exists()) {
            if (directory.mkdir())
                Log.v("CREATE_DIRECTORY: " + pathFileWithFile, "Created now!");
            else
                Log.e("CREATE_DIRECTORY: " + pathFileWithFile, "Error!");
        }
        return directory;
    }

    /**
     * Eliminare una directory "non vuota", eliminando i file
     * contenuti al suo interno in modo ricorsivo.
     * @param dir file directory da eliminare.
     * @throws IOException
     */
    public void deleteDir(final File dir) throws IOException {
        File[] files = new File(dir.toURI()).listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDir(file);
            }
            Files.deleteIfExists(file.toPath());
        }
        Files.deleteIfExists(dir.toPath());
    }

    /**
     * Costruisce un path e tutte le stringhe che lo compongono verranno capitalizzate
     * @param strs array di stringhe che comporranno il path, in ordine.
     * @return path costruito.
     * @throws IllegalArgumentException se l'array è vuoto.
     */
    public static Path buildPath(final String[] strs) throws IllegalArgumentException {
        if (strs.length == 0)
            throw new IllegalArgumentException("L'array di stringhe per costruire il path è vuoto");
        Path p = Paths.get(capitalize(strs[0]));
        for (int i = 1; i < strs.length; i++) {
            p = Paths.get(p.toString(), capitalize(strs[i]));
        }
        return p;
    }

    /**
     * Costruisce un path con tutte le stringhe capitalizzate tranne generalPath. Infine
     * ritorna un path che inizia per generalPath e viene concatenato alle varie stringhe capitalizzate.
     * @param generalPath path da inserire all'inizio e concatenargli il resto delle stringhe.
     * @param strs array di stringhe che comporranno il path, in ordine.
     * @return path costruito.
     * @throws IllegalArgumentException se l'array è vuoto.
     */
    public static Path buildGeneralPath(final String generalPath, final String[] strs) throws IllegalArgumentException {
        return Paths.get(generalPath, buildPath(strs).toString());
    }

    /**
     * Capitalizza la stringa passata come parametro.
     * @param str stringa da capitalizzare.
     * @return empty string se la stringa è null o vuota, altrimenti la stringa capitalizzata.
     */
    public static String capitalize(final String str) {
        if (str == null || str.isEmpty())
            return "";
        if (str.length() < 2)
            return str.toUpperCase();
        return str.substring(0,1).toUpperCase() + str.substring(1).toLowerCase();
    }

}
