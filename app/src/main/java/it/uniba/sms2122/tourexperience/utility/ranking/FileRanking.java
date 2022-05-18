package it.uniba.sms2122.tourexperience.utility.ranking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;

public class FileRanking {
    private File txtRanking;

    public FileRanking (File txtRanking){
        this.txtRanking = txtRanking;
    }

    public void writeToFile(String args) {
        try(Writer writer = new FileWriter(txtRanking, true)){
            writer.write(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getTxtRanking() {
        return txtRanking;
    }
}
