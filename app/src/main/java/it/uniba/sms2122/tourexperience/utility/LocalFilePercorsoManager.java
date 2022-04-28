package it.uniba.sms2122.tourexperience.utility;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import it.uniba.sms2122.tourexperience.graph.Percorso;

public class LocalFilePercorsoManager extends LocalFileManager {

    public LocalFilePercorsoManager(String generalPath) {
        super(generalPath);
    }


    public Optional<Percorso> getPercorso(final String nomeMuseo, final String nomePercorso) {
        File filePercorso = new File(String.format("%s/%s/Percorsi/%s", generalPath, nomeMuseo, nomePercorso));
        Optional<Percorso> optPercorso = Optional.empty();
        try ( Reader reader = new FileReader(filePercorso) ) {
            optPercorso = Optional.ofNullable(new Gson().fromJson(reader, Percorso.class));
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        }
        return optPercorso;
    }
}
