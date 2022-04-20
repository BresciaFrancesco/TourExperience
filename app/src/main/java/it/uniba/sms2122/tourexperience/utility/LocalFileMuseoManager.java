package it.uniba.sms2122.tourexperience.utility;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Museo;

public class LocalFileMuseoManager extends LocalFileManager {

    public LocalFileMuseoManager(String generalPath) {
        super(generalPath);
        super.generalPath += "Museums/";
    }


    public List<Museo> getListMusei() throws IOException {
        List<Museo> listaMusei = new ArrayList<>();
        Gson gson = new Gson();
        try (
                DirectoryStream<Path> stream =
                        Files.newDirectoryStream(Paths.get(generalPath))
        ) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                //String nomeMuseo = path.getFileName().toString();
                /*listaMusei.add(new Museo(
                        nomeMuseo,"","",
                        generalPath + nomeMuseo + "/" + nomeMuseo + ".png"
                ));*/
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

}
