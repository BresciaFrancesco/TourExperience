package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import static it.uniba.sms2122.tourexperience.cache.CacheMuseums.cacheMuseums;

/**
 * Classe che crea una versione virtuale del filesystem
 * del file .zip sotto forma di albero n-ario.
 */
public class TreeVirtualZipFileSystem {

    private final long MAX_DIM = 10000000; // 10 MB
    private final Tree root = new Tree("root", new HashMap<>());
    private final String separatore = "/";

    /**
     * Crea una versione virtuale del filesystem interno al file .zip
     * sfruttando una struttura dati che è un albero n-ario.
     * @param in InputStream per aprire file in lettura.
     * @param zipName nome del file .zip con l'estensione .zip integrata.
     * @return nome del file .zip
     * @throws IOException
     * @throws ZipCheckerException
     */
    public String createVirtualFileSystem(final InputStream in, String zipName)
            throws IOException, ZipCheckerException {
        zipName = zipName.substring(0, zipName.length()-4);
        if (cacheMuseums.get(zipName) != null)
            throw new ZipCheckerException("Il museo " + zipName + " esiste già");
        long dimAccum = 0;
        try (ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in))) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                dimAccum += ze.getSize();

                if (dimAccum > MAX_DIM) {
                    throw new ZipCheckerException("Errore, dimensione accumulata per ora: " + dimAccum);
                }
                if (ze.isDirectory()) {
                    createNodes(ze.getName().substring(0, ze.getName().length()-1));
                }
                else {
                    createNodes(ze.getName());
                }
            }
        }
        return zipName;
    }

    /**
     * Crea i nodi dell'albero a partire dai path esportati dal file .zip.
     * @param filePath path completo di un file esportato dal file .zip.
     */
    private void createNodes(final String filePath) {
        Tree tmpRoot = root;
        String[] tokens = filePath.split(separatore);
        int size = tokens.length, i;
        // tutti i token sono directory tranne l'ultima che può esserlo o no
        for (i = 0; i < size-1; i++) {
            tmpRoot = createOneNode(tmpRoot, tokens[i]);
        }
        // l'ultimo token viene impostato dinamicamente
        createOneNode(tmpRoot, tokens[i]);
    }

    /**
     * Crea il singolo nodo nell'albero.
     * @param tmpRoot nodo padre rispetto a quello da creare.
     * @param token nome del nodo da creare.
     * @return nodo creato.
     */
    private Tree createOneNode(final Tree tmpRoot, final String token) {
        Tree node = null;
        if (tmpRoot.children == null) {
            tmpRoot.children = new HashMap<>();
        } else {
            node = tmpRoot.children.get(token.trim());
        }
        if (node == null) {
            node = new Tree(token);
            tmpRoot.children.put(token, node);
        }
        return node;
    }

    /**
     * Ritorna il nodo root dell'albero.
     * @return nodo root dell'albero.
     */
    public Tree getRoot() {
        return root;
    }
}
