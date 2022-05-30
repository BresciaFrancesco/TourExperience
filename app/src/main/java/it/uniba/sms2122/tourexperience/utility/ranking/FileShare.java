package it.uniba.sms2122.tourexperience.utility.ranking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Classe utile per la gestione di tutte le operazioni legate al file che dovrà essere condiviso
 */
public class FileShare {
    private File txt;

    public FileShare(File txt){
        this.txt = txt;
    }

    public void writeToFile(String args) {
        try(Writer writer = new FileWriter(txt, true)){
            writer.write(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getTxt() {
        return txt;
    }
}
