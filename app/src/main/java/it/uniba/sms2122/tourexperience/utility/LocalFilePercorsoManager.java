package it.uniba.sms2122.tourexperience.utility;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;

public class LocalFilePercorsoManager extends LocalFileManager {

    private final Gson gson = new Gson();

    public LocalFilePercorsoManager(String generalPath) {
        super(generalPath);
    }


    public Optional<Percorso> getPercorso(final String nomeMuseo, final String nomePercorso) {
        File filePercorso = new File(String.format("%s%s/Percorsi/%s.json", generalPath, nomeMuseo, nomePercorso));
        Optional<Percorso> optPercorso = Optional.empty();
        try ( Reader reader = new FileReader(filePercorso) ) {
            optPercorso = Optional.ofNullable(new Gson().fromJson(reader, Percorso.class));
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        }
        return optPercorso;
    }

    /**
     * Carica e crea gli oggetti Opera di un percorso, in particolare
     * carica e crea le opere della stanza corrente e delle stanze
     * direttamente collegate ad essa.
     * @param grafo percorso nella quale creare le opere.
     */
    public void createOpereInThisAndNextStanze(final Percorso grafo) {
        // carico le opere di questa stanza
        Stanza stanzaCorrente = grafo.getStanzaCorrente();
        loadOpere(grafo.getNomeMuseo(), stanzaCorrente);
        // e poi carico le opere delle stanze collegate nel grafo
        new Thread(() -> this.createOpereOnlyInNextStanze(grafo)).start();
    }

    /**
     * Carica e crea gli oggetti Opera di un percorso, in particolare
     * carica e crea le opere delle stanze direttamente collegate a
     * quella in cui ci si trova in quel momento.
     * @param grafo percorso nella quale creare le opere.
     */
    // Supposto che si abbiano già gli oggetti stanza completi
    // TODO non ancora testato: da testare prima dell'uso
    public void createOpereOnlyInNextStanze(final Percorso grafo) {
        List<Stanza> stanzeAdiacenti = grafo.getAdiacentNodes();
        for (int i = 0; i < stanzeAdiacenti.size(); i++) {
            loadOpere(grafo.getNomeMuseo(), stanzeAdiacenti.get(i));
        }
    }

    /**
     * Carica le opere di una stanza e ne crea gli oggetti Opera.
     * @param nomeMuseo nome del museo di cui fa parte il percorso.
     * @param stanza stanza del museo di cui ottenere le opere.
     */
    private void loadOpere(final String nomeMuseo, final Stanza stanza) {
        String pathOpere = String.format("%s%s/Stanze/%s",
                generalPath, nomeMuseo, stanza.getNome());
        try ( DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(pathOpere)) ) {
            Map<String, Opera> newOpereForStanza = new HashMap<>();
            for (Path path : stream) {
                if (!Files.isDirectory(path)) continue;
                File fileOpera = new File(path + "/Info_opera.json");
                try ( Reader reader = new FileReader(fileOpera) ) {
                    Opera opera = gson.fromJson(reader, Opera.class);
                    newOpereForStanza.put(opera.getId(), opera);
                }
            }
            stanza.setOpere(newOpereForStanza);
        }
        catch (IOException | JsonSyntaxException | JsonIOException e) {
            e.printStackTrace();
        }
    }
}
