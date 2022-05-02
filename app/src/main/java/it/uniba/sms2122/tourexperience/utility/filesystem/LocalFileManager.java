package it.uniba.sms2122.tourexperience.utility.filesystem;


import android.util.Log;

import java.io.File;

public class LocalFileManager {

    protected String generalPath;

    public LocalFileManager(String generalPath) {
        if (generalPath.charAt(generalPath.length()-1) != '/') {
            generalPath += "/";
        }
        this.generalPath = generalPath + "Museums/";
    }

    /**
     * Crea una directory nel filesystem locale, nel path specificato come parametro,
     * se non esiste già.
     */
    public File createLocalDirectoryIfNotExists(final File filesDir, final String pathFileWithFile) {
        File directory = new File(filesDir, pathFileWithFile);
        if (directory == null || !directory.exists()) {
            if (directory.mkdir())
                Log.v("CREATE_DIRECTORY: " + pathFileWithFile, "Created now!");
            else
                Log.e("CREATE_DIRECTORY: " + pathFileWithFile, "Error!");
        }
        return directory;
    }


}
