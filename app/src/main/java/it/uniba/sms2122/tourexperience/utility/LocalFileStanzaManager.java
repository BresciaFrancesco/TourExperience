package it.uniba.sms2122.tourexperience.utility;

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

public class LocalFileStanzaManager extends LocalFileManager {

    public LocalFileStanzaManager(String generalPath) {
        super(generalPath);
    }


    public List<Stanza> getListStanze() throws IOException {
        Log.v("LocalFileStanzaManager", "chiamato getListStanze()");
        List<Stanza> listaStanze = new ArrayList<>();
        Gson gson = new Gson();
        try (
                DirectoryStream<Path> stream =
                        Files.newDirectoryStream(Paths.get(generalPath))
        ) {
            for (Path path : stream) {
                try (
                        DirectoryStream<Path> stream1 =
                                Files.newDirectoryStream(Paths.get(path + "/Stanze/"))
                ) {
                    for (Path path1 : stream1) {
                        if (!Files.isDirectory(path)) continue;
                        try (Reader reader = new FileReader(path + "/Info_stanza.json")) {

                            Stanza stanza = gson.fromJson(reader, Stanza.class);
                            listaStanze.add(stanza);
                        } catch (IOException | JsonSyntaxException | JsonIOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return listaStanze;
    }
}