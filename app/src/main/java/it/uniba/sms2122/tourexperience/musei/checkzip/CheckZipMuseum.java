package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;


public class CheckZipMuseum implements ZipChecker {

    /** Classe che crea una versione virtuale del filesystem
     * del file .zip sotto forma di albero n-ario. */
    private final TreeVirtualZipFileSystem virtualZipFileSystem;

    private final String IMG_EXTENSION = ".png";
    private final String infoOperaJson = "Info_opera.json";
    private final String infoStanzaJson = "Info_stanza.json";
    private final String infoMuseoJson = "Info.json";

    /**
     * Unico costruttore della classe.
     */
    public CheckZipMuseum() {
        this.virtualZipFileSystem = new TreeVirtualZipFileSystem();
    }


    @Override
    public void start(File zipFile) throws ZipCheckerException, ZipCheckerRunTimeException {
        try {
            String zipDirName = virtualZipFileSystem.createVirtualFileSystem(zipFile);
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
        checkDim(tmpRoot, 6, true);
        Tree percorsi = getIfExists(tmpRoot, "Percorsi", false);
        Tree stanze = getIfExists(tmpRoot, "Stanze", false);
        getIfExists(tmpRoot, infoMuseoJson, true);
        getIfExists(tmpRoot, museumName + IMG_EXTENSION, true);
        getIfExists(tmpRoot, "Immagine_1" + IMG_EXTENSION, true);
        getIfExists(tmpRoot, "Immagine_2" + IMG_EXTENSION, true);

        // CHECK directory Percorsi
        checkDim(percorsi, 0, false);
        Set<String> jsonPercorsi = percorsi.children.keySet();
        for (String percorso : jsonPercorsi) {
            if (!percorso.endsWith(".json") || percorsi.children.get(percorso).children != null)
                throw new ZipCheckerException("I json dei percorsi sono errati");
        }

        // CHECK directory Stanze
        checkDim(stanze, 0, false);
        // CHECK directories delle stanze
        Set<String> stanzeKey = stanze.children.keySet();
        for (String stanza : stanzeKey) {
            Tree s = stanze.children.get(stanza);
            if (s.children == null)
                throw new ZipCheckerException(stanza + " non è una directory");
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
                throw new ZipCheckerException(stanza + " non è una directory");
            checkDim(opera, 0, false);
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
     * @param n nodo dell'albero, si suppone padre diretto di "key".
     * @param key si suppone figlio diretto di "n".
     * @param isFile se impostato a True, si testerà il nodo n come un file,
     *               altrimenti si testerà come una directory.
     * @return ritorna il nodo "key" figlio diretto di "n".
     * @throws ZipCheckerException se il "key" non è figlio diretto di "n" o
     * n è null o ha fallito il controllo come file o directory.
     */
    private Tree getIfExists(final Tree n, final String key,
                             final boolean isFile) throws ZipCheckerException {
        Tree node = n;
        node = node.children.get(key);
        if (node == null)
            throw new ZipCheckerException(n.value() + " non contiene " + key + " come figlio");
        if (isFile) {
            if (node.children != null)
                throw new ZipCheckerException(n.value() + " non è un file");
        } else {
            if (node.children == null)
                throw new ZipCheckerException(n.value() + " non è una directory");
        }
        return node;
    }

}
