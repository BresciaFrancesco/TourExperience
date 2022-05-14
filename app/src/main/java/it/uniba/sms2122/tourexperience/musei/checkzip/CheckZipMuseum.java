package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.sms2122.tourexperience.graph.Percorso;
import it.uniba.sms2122.tourexperience.model.Museo;
import it.uniba.sms2122.tourexperience.model.Opera;
import it.uniba.sms2122.tourexperience.model.Stanza;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class CheckZipMuseum {

    /** Classe che crea una versione virtuale del filesystem
     * del file .zip sotto forma di albero n-ario. */
    private final TreeVirtualZipFileSystem virtualZipFileSystem;

    public static final String IMG_EXTENSION = ".webp";
    public static final String infoOperaJson = "Info_opera.json";
    public static final String infoStanzaJson = "Info_stanza.json";
    public static final String infoMuseoJson = "Info.json";

    /**
     * Unico costruttore della classe.
     */
    public CheckZipMuseum() {
        this.virtualZipFileSystem = new TreeVirtualZipFileSystem();
    }


    public void start(final OpenFile of, final String zipName)
            throws ZipCheckerException, ZipCheckerRunTimeException {
        try {
            String zipDirName = virtualZipFileSystem.createVirtualFileSystem(of.openFile(), zipName);
            checkZipStructure(zipDirName);
        }
        catch (NullPointerException e) {
            throw new ZipCheckerRunTimeException(e.getMessage(), e);
        }
        catch (IOException | ZipCheckerException e) {
            throw new ZipCheckerException(e.getMessage());
        }
    }


    /**
     * Primo metodo principale di controllo. Effettua il controllo della struttua
     * interna del file .zip, ma lo fa basandosi sull'albero costruito dalla
     * classe TreeVirtualZipFileSystem. Il controllo sulla struttura è molto
     * rigoroso, un qualunque file/directory fuori posto o mal nominata, e il
     * metodo fallisce sollevando una ZipCheckerException.
     * @param museumName nome del museo.
     * @throws ZipCheckerException se un qualunque controllo fallisce.
     */
    private void checkZipStructure(final String museumName) throws ZipCheckerException {
        // CHECK root
        Tree tmpRoot = virtualZipFileSystem.getRoot();
        checkDim(tmpRoot, 1, true);
        tmpRoot = getIfExists(tmpRoot, museumName, false);

        // CHECK directory museo
        checkDim(tmpRoot, 7, true);
        Tree percorsi = getIfExists(tmpRoot, "Percorsi", false);
        Tree stanze = getIfExists(tmpRoot, "Stanze", false);
        getIfExists(tmpRoot, infoMuseoJson, true);
        getIfExists(tmpRoot, museumName + IMG_EXTENSION, true);
        getIfExists(tmpRoot, "Immagine_1" + IMG_EXTENSION, true);
        getIfExists(tmpRoot, "Immagine_2" + IMG_EXTENSION, true);
        getIfExists(tmpRoot, "Immagine_3" + IMG_EXTENSION, true);

        // CHECK directory Percorsi
        Set<String> nomiPercorsi = new HashSet<>();
        checkDim(percorsi, 0, false);
        Set<String> jsonPercorsi = percorsi.children.keySet();
        if (jsonPercorsi.isEmpty()) {
            throw new ZipCheckerException("Non ci sono json di percorsi");
        }
        Set<String> cachedPercorsi = cachePercorsiInLocale.get(museumName);
        for (String percorso : jsonPercorsi) {
            if (!percorso.endsWith(".json") || percorsi.children.get(percorso).children != null)
                throw new ZipCheckerException("I json dei percorsi sono errati");
            String p = percorso.substring(0, percorso.length()-5);
            if (cachedPercorsi != null && cachedPercorsi.contains(p)) {
                throw new ZipCheckerException(p + " esiste già come percorso per il museo " + museumName);
            }
            if (!nomiPercorsi.add(p))
                throw new ZipCheckerException(p + " è duplicato nel file .zip");
        }

        // CHECK directory Stanze
        checkDim(stanze, 0, false);
        // CHECK directories delle stanze
        Set<String> stanzeKey = stanze.children.keySet();
        for (String stanza : stanzeKey) {
            Tree s = stanze.children.get(stanza);
            checkDim(s, 0, false);
            getIfExists(s, infoStanzaJson, true);
            // CHECK opere
            checkOpere(s);
        }
    }

    /**
     * Secondo metodo principale di controllo. Controlla le opere di una stanza.
     * Questo metodo deve lavorare insieme al primo principale di controllo.
     * @param stanza nodo dell'albero rappresentante una stanza.
     * @throws ZipCheckerException
     */
    private void checkOpere(final Tree stanza) throws ZipCheckerException {
        Set<String> opereKey = stanza.children.keySet();
        opereKey.remove(infoStanzaJson);
        if (opereKey.isEmpty())
            throw new ZipCheckerException("La stanza " + stanza.value() + " non ha opere");
        for (String nomeOpera : opereKey) {
            Tree opera = stanza.children.get(nomeOpera);
            if (opera.children == null)
                throw new ZipCheckerException(nomeOpera + " non è una directory oppure è vuota");
            getIfExists(opera, infoOperaJson, true);
            getIfExists(opera, nomeOpera + IMG_EXTENSION, true);
        }
    }

    /**
     * Metodo di utility per controllare la dimensione di una directory del
     * filesystem del file .zip ma sfruttando l'albero creato.
     * @param node nodo dell'albero da controllare.
     * @param equalDim valore intero con il quale effettuare testare
     *                 l'uguaglianza o la disuguaglianza.
     * @param equal decide se testare l'uguaglianza o la disuguaglianza.
     *              True fa fallire la disuguaglianza con equalDim,
     *              False fa fallire l'uguaglianza.
     * @throws ZipCheckerException eccezione sollevata al fallimento del metodo
     */
    private void checkDim(Tree node, int equalDim,
                          final boolean equal) throws ZipCheckerException {
        if (node.children == null)
            throw new ZipCheckerException(node.value() + " non ha figli");
        if (equal) {
            if (node.children.size() != equalDim)
                throw new ZipCheckerException("I figli di " + node.value() + " non sono " + equalDim);
        } else {
            if (node.children.size() == equalDim)
                throw new ZipCheckerException("I figli di " + node.value() + " sono " + equalDim);
        }
    }

    /**
     * Metodo che, dopo attenti controlli, ritorna il figlio key del nodo n dell'albero,
     * oppure fallisce sollevando ZipCheckerException.
     * @param fatherNode nodo dell'albero, si suppone padre diretto di "key".
     * @param key si suppone figlio diretto di "n".
     * @param isFile se impostato a True, si testerà il nodo n come un file,
     *               altrimenti si testerà come una directory.
     * @return ritorna il nodo "key" figlio diretto di "n".
     * @throws ZipCheckerException se il "key" non è figlio diretto di "n" o
     * n è null o ha fallito il controllo come file o directory.
     */
    private Tree getIfExists(final Tree fatherNode, final String key,
                             final boolean isFile) throws ZipCheckerException {
        Tree sonNode = fatherNode.children.get(key);
        if (sonNode == null)
            throw new ZipCheckerException(fatherNode.value() + " non contiene " + key + " come figlio");
        if (isFile) {
            if (sonNode.children != null)
                throw new ZipCheckerException(sonNode.value() + " non è un file");
        } else {
            if (sonNode.children == null)
                throw new ZipCheckerException(sonNode.value() + " non è una directory oppure è vuota");
        }
        return sonNode;
    }

    /**
     * Esegue il check completo di tutti i file json presenti nel filesystem del museo importato.
     * Vengono caricati tutti i file json e deserializzati in oggetti con la libreria Gson.
     * Vengono analizzati tutti gli oggetti caricati dai json, ovvero ne vengono controllati
     * tutti gli attributi secondo i loro rispettivi contratti.
     * Il check sfrutta un secondo Thread nella quale vengono fatti eseguire i 3 check
     * meno lunghi. Il check più lungo da eseguire, cioè quello sui json delle opere,
     * viene eseguito nel thread principale. In questo modo il carico computazionale è ben
     * diviso tra i thread.
     * @param generalPath path generale che include la cartella generale Museums.
     * @param nomeMuseo nome del museo.
     * @return una lista di 2 oggetti. ALl'indice 0 troviamo un booleano:
     * true se il check è andato a buon fine, false altrimenti. All'indice
     * 1 troviamo un Set di stringhe, contenente i nomi dei percorsi importati
     * insieme allo zip del museo.
     */
    public static List<Object> checkAllJson(final String generalPath, final String nomeMuseo) {
        String pathMuseo = Paths.get(generalPath, nomeMuseo).toString();
        CheckZipJson checker = new CheckZipJson(pathMuseo);
        try {
            final Thread thread = new Thread(checker);
            thread.start();
            checker.checkOpere();
            thread.join();
            if (checker.getException().get() != null) {
                Log.e("checkAllJson", checker.getException().get().getMessage());
                checker.getException().get().printStackTrace();
                return Arrays.asList(Boolean.FALSE, new HashSet<>());
            }
            return Arrays.asList(Boolean.TRUE, checker.getNomiPercorsi().get());
        }
        catch (InterruptedException | NullPointerException | IllegalArgumentException |
                JsonSyntaxException | JsonIOException | IOException e) {
            Log.e("checkAllJson", e.getMessage());
            e.printStackTrace();
        }
        return Arrays.asList(Boolean.FALSE, new HashSet<>());
    }

    /**
     * Inner Class per effettuare check sui file json già salvati in locale.
     */
    private static class CheckZipJson implements Runnable {
        private final Gson gson;
        private final String pathMuseo;
        private final AtomicReference<Exception> exception = new AtomicReference<>();
        private final AtomicReference<Set<String>> nomiPercorsi = new AtomicReference<>();

        CheckZipJson(final String pathMuseo) {
            this.gson = new Gson();
            this.pathMuseo = pathMuseo;
        }

        /**
         * Esegue il check del json del Museo.
         * @throws IOException
         * @throws NullPointerException
         * @throws IllegalArgumentException
         * @throws JsonSyntaxException
         * @throws JsonIOException
         */
        public void checkMuseo() throws IOException, NullPointerException,
                IllegalArgumentException, JsonSyntaxException, JsonIOException
        {
            final String path = Paths.get(pathMuseo, CheckZipMuseum.infoMuseoJson).toString();
            try (Reader r = new FileReader(path)) {
                Museo.checkMuseo(gson.fromJson(r, Museo.class));
            }
        }

        /**
         * Esegue il check dei json delle Stanze.
         * @throws IOException
         * @throws NullPointerException
         * @throws IllegalArgumentException
         * @throws JsonSyntaxException
         * @throws JsonIOException
         */
        public void checkStanze() throws IOException, NullPointerException,
                IllegalArgumentException, JsonSyntaxException, JsonIOException
        {
            final String pathStanze = Paths.get(pathMuseo, "Stanze").toString();
            try ( DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(pathStanze)) ) {
                for (Path path : stream) {
                    final String pathJson = Paths.get(path.toString(), CheckZipMuseum.infoStanzaJson).toString();
                    try (Reader r = new FileReader(pathJson)) {
                        Stanza.checkStanza(gson.fromJson(r, Stanza.class));
                    }
                }
            }
        }

        /**
         * Esegue il check dei json delle Opere.
         * @throws IOException
         * @throws NullPointerException
         * @throws IllegalArgumentException
         * @throws JsonSyntaxException
         * @throws JsonIOException
         */
        public void checkOpere() throws IOException, NullPointerException,
                IllegalArgumentException, JsonSyntaxException, JsonIOException
        {
            final String pathStanze = Paths.get(pathMuseo, "Stanze").toString();
            try ( DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(pathStanze)) ) {
                for (Path stanza : stream) {
                    try ( DirectoryStream<Path> streamOpere = Files.newDirectoryStream(stanza.toAbsolutePath()) ) {
                        for (Path pathOpera : streamOpere) {
                            if (!Files.isDirectory(pathOpera)) continue;
                            final String pathJson = Paths.get(pathOpera.toString(), CheckZipMuseum.infoOperaJson).toString();
                            try (Reader r = new FileReader(pathJson)) {
                                Opera.checkOpera(gson.fromJson(r, Opera.class));
                            }
                        }
                    }
                }
            }
        }

        /**
         * Esegue il check dei json dei Percorsi.
         * @throws IOException
         * @throws NullPointerException
         * @throws IllegalArgumentException
         * @throws JsonSyntaxException
         * @throws JsonIOException
         */
        public void checkPercorsi() throws IOException, NullPointerException,
                IllegalArgumentException, JsonSyntaxException, JsonIOException
        {
            final Set<String> nPercorsi = new HashSet<>();
            final String pathPercorsi = Paths.get(pathMuseo, "Percorsi").toString();
            try ( DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(pathPercorsi)) ) {
                for (Path percorsi : stream) {
                    try (Reader r = new FileReader(percorsi.toFile())) {
                        final Percorso p = gson.fromJson(r, Percorso.class);
                        Percorso.checkAll(p);
                        nPercorsi.add(p.getNomePercorso());
                    }
                }
                nomiPercorsi.set(nPercorsi);
            }
        }

        /**
         * Ritorna l'eccezione in forma di AtomicReference.
         * @return eccezione in forma di AtomicReference.
         */
        public synchronized AtomicReference<Exception> getException() {
            return exception;
        }

        /**
         * Ritorna il set di nomi percorsi in forma di AtomicReference.
         * @return set di nomi percorsi in forma di AtomicReference.
         */
        public synchronized AtomicReference<Set<String>> getNomiPercorsi() {
            return nomiPercorsi;
        }

        /**
         * Esegue i 3 check meno lunghi in un thread a parte.
         * Check json museo, check json delle stanze e check json dei percorsi.
         */
        @Override
        public void run() {
            try {
                checkMuseo();
                checkStanze();
                checkPercorsi();
            }
            catch (NullPointerException | IllegalArgumentException | IOException |
                    JsonSyntaxException | JsonIOException e) {
                exception.set(e);
            }
        }

    } // END INNER CLASS

} // END MAIN CLASS
