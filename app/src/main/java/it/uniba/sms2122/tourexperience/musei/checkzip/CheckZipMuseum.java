package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cachePercorsiInLocale;


public class CheckZipMuseum {

    /** Classe che crea una versione virtuale del filesystem
     * del file .zip sotto forma di albero n-ario. */
    private final TreeVirtualZipFileSystem virtualZipFileSystem;

    private final String IMG_EXTENSION = ".webp";
    private final String infoOperaJson = "Info_opera.json";
    private final String infoStanzaJson = "Info_stanza.json";
    private final String infoMuseoJson = "Info.json";

    /**
     * Unico costruttore della classe.
     */
    public CheckZipMuseum() {
        this.virtualZipFileSystem = new TreeVirtualZipFileSystem();
    }


    public Set<String> start(final OpenFile of, final String zipName)
            throws ZipCheckerException, ZipCheckerRunTimeException {
        try {
            String zipDirName = virtualZipFileSystem.createVirtualFileSystem(of.openFile(), zipName);
            return checkZipStructure(zipDirName);
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
    private Set<String> checkZipStructure(final String museumName) throws ZipCheckerException {
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

        return nomiPercorsi;
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

}
