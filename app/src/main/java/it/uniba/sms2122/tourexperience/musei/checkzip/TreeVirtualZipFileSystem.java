package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;

/**
 * Classe che crea una versione virtuale del filesystem
 * del file .zip sotto forma di albero n-ario.
 */
public class TreeVirtualZipFileSystem {

    private final long MAX_DIM = 30000000; // 30 MB
    private final Tree root = new Tree("root", new HashMap<>());
    private final String separatore = "/";

    /**
     * Crea una versione virtuale del filesystem interno al file .zip
     * sfruttando una struttura dati che è un albero n-ario.
     * @param zipFile file .zip
     * @return nome del file .zip
     * @throws IOException
     * @throws ZipCheckerException
     */
    public String createVirtualFileSystem(File zipFile) throws IOException, ZipCheckerException {
        String zipFileName = zipFile.getName();
        zipFileName = zipFileName.substring(0, zipFileName.length()-4);
        long dimAccum = 0;
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
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
        return zipFileName;
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
