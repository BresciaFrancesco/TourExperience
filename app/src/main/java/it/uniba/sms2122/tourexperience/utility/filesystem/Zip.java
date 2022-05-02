package it.uniba.sms2122.tourexperience.utility.filesystem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.uniba.sms2122.tourexperience.musei.checkzip.CheckZipMuseum;
import it.uniba.sms2122.tourexperience.musei.checkzip.ZipChecker;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;

import android.util.Log;

/**
 * Classe che effettua l'unzip di un file .zip.
 */
public class Zip {
    private final int bufferDim = 8192; // 8.2 KB
    private final ZipChecker checker;
    private final LocalFileManager localFileManager;


    public Zip(final LocalFileManager localFileManager) {
        this.checker = new CheckZipMuseum();
        this.localFileManager = localFileManager;
    }

    /**
     * Comincia l'unzip di un file .zip.
     * @param zipFile file .zip da unzippare.
     * @return true se l'unzip è andato a buon fine, false altrimenti.
     */
    public boolean startUnzip(File zipFile) {
        try {
            checker.start(zipFile);
        }
        catch (ZipCheckerException | ZipCheckerRunTimeException e) {
            Log.e("CHECK_ZIP", "ECCEZIONE in CHECK ZIP...");
            e.printStackTrace();
            return false;
        }
        Log.v("CHECK_ZIP", "CHECK ZIP superato...");

        try {
            Log.v("CHECK_ZIP", "inizio UNZIP...");
            unzip(zipFile, new File(localFileManager.getGeneralPath()));
            Log.v("CHECK_ZIP", "fine UNZIP...");
        }
        catch (IOException e) {
            Log.e("CHECK_ZIP", "ECCEZIONE in UNZIP...");
            try {
                localFileManager.deleteDir(
                        Paths.get(localFileManager.generalPath, zipFile.getName()).toFile()
                );
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


    // UNZIP
    private void unzip(File zipFile, File targetDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {

            ZipEntry ze;
            int count;
            byte[] buffer = new byte[bufferDim];
            while ((ze = zis.getNextEntry()) != null) {

                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();

                if (dir == null)
                    throw new FileNotFoundException("La variabile dir è null in unzip");

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

}
