package it.uniba.sms2122.tourexperience.utility.filesystem;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.utility.filesystem.LocalFileManager;

public class LocalFileStanzaManager extends LocalFileManager {

    public LocalFileStanzaManager(String generalPath) {
        super(generalPath);
    }


    public List<Stanza> getListStanze(String nomeMuseo) throws IOException {
        Log.v("LocalFileStanzaManager", "chiamato getListStanze()");
        List<Stanza> listaStanze = new ArrayList<>();
        Gson gson = new Gson();
        try (
                DirectoryStream<Path> stream =
                        Files.newDirectoryStream(Paths.get(generalPath + nomeMuseo + "/Stanze/"));
        ) {
            for (Path path : stream) {
                try ( Reader reader = new FileReader(path + "/Info_stanza.json") )
                {
                    System.out.println(path.toString());
                    Stanza stanza = gson.fromJson(reader , Stanza.class);
                    listaStanze.add(stanza);
                }
                catch (IOException | JsonSyntaxException | JsonIOException e) {
                    e.printStackTrace();
                }
            }
        }
        return listaStanze;
    }
}