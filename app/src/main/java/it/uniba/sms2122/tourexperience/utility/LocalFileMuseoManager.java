package it.uniba.sms2122.tourexperience.utility;

import android.net.Uri;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import it.uniba.sms2122.tourexperience.model.Museo;

public class LocalFileMuseoManager extends LocalFileManager {

    public static final String IMMAGINE_MUSEO = "/immagine_museo.jpg";

    public LocalFileMuseoManager(String generalPath) {
        super(generalPath);
        generalPath = generalPath + "musei/";
    }


    public List<Museo> getListMusei() throws IOException {
        List<Museo> listaMusei = new ArrayList<>();
        try (
                DirectoryStream<Path> stream =
                        Files.newDirectoryStream(Paths.get(generalPath))
        ) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                String nomeMuseo = path.getFileName().toString();
                listaMusei.add(new Museo(
                        nomeMuseo,
                        generalPath + nomeMuseo + IMMAGINE_MUSEO
                ));
            }
        }
        return listaMusei;
    }

}
