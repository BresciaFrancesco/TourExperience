package it.uniba.sms2122.tourexperience.utility.filesystem;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

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


}
