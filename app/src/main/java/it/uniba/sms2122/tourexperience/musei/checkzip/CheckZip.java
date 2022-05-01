package it.uniba.sms2122.tourexperience.musei.checkzip;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author GerardoGiannetta
 */
public class CheckZip {

    private final long MAX_DIM = 30000000; // 30 MB
    private final String IMG_EXTENSION = ".png";
    private final Tree root = new Tree("root", new HashMap<>());
    private final String infoOperaJson = "Info_opera.json";
    private final String infoStanzaJson = "Info_stanza.json";
    private final String infoMuseoJson = "Info.json";

    public static void print(String s) {
        Log.v("CheckZip", s);
    }

    // TEST
    public void levelOrderTraversal() {
        levelOrder(root);
    }

    // TEST
    private void levelOrder(Tree tmpRoot) {
        Queue<Tree> q = new LinkedList<>();
        q.add(tmpRoot);
        int dim;

        while (!q.isEmpty()) {

            dim = q.size();

            for (int i = 0; i < dim; i++) {
                Tree node =  q.remove();
                if (node.children != null && !node.children.isEmpty()) {
                    print(node.value() + ":");

                    Set<String> keys = node.children.keySet();
                    for (String key : keys) {
                        print(key);
                        q.add(node.children.get(key));
                    }
                    print("\n");
                }
            }
        }
    }

    // CHECK ZIP
    public String createVirtualFileSystem(File zipFile) throws IOException, IllegalArgumentException {
        String zipFileName = zipFile.getName();
        zipFileName = zipFileName.substring(0, zipFileName.length()-4);
        long dimAccum = 0;
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;

            while ((ze = zis.getNextEntry()) != null) {
                print(ze.getName());
                dimAccum += ze.getSize();

                if (dimAccum > MAX_DIM) {
                    throw new IllegalArgumentException("Errore, dimensione accumulata per ora: " + dimAccum);
                }
                if (ze.isDirectory()) {
                    createNodes(ze.getName().substring(0, ze.getName().length()-1), true);
                }
                else {
                    createNodes(ze.getName(), false);
                }
            }

        }
        print("Dimensione accumulata: " + dimAccum);
        return zipFileName;
    }


    private void createNodes(final String filePath, final boolean isDir) {
        Tree tmpRoot = root;
        String[] tokens = filePath.split("/");
        int size = tokens.length, i;
        // tutte i token sono directory tranne l'ultima che può esserlo o no
        for (i = 0; i < size-1; i++) {
            tmpRoot = createOneNode(tmpRoot, tokens[i], true);
        }
        // l'ultimo token viene impostato dinamicamente
        createOneNode(tmpRoot, tokens[i], isDir);
    }

    private Tree createOneNode(final Tree tmpRoot, final String token, final boolean isDir) {
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


    public void checkZipStructure(final String museumName) throws IllegalArgumentException {
        // CHECK root
        Tree tmpRoot = root;
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
                throw new IllegalArgumentException("I json dei percorsi sono errati");
        }

        // CHECK directory Stanze
        checkDim(stanze, 0, false);
        // CHECK directories delle stanze
        Set<String> stanzeKey = stanze.children.keySet();
        for (String stanza : stanzeKey) {
            Tree s = stanze.children.get(stanza);
            if (s.children == null)
                throw new IllegalArgumentException(stanza + " non è una directory");
            checkDim(s, 0, false);
            getIfExists(s, infoStanzaJson, true);
            // CHECK opere
            checkOpere(s);
        }
    }

    private void checkOpere(final Tree stanza) throws IllegalArgumentException {
        Set<String> opereKey = stanza.children.keySet();
        opereKey.remove(infoStanzaJson);
        if (opereKey.isEmpty())
            throw new IllegalArgumentException("La stanza " + stanza.value() + " non ha opere");
        for (String nomeOpera : opereKey) {
            Tree opera = stanza.children.get(nomeOpera);
            if (opera.children == null)
                throw new IllegalArgumentException(stanza + " non è una directory");
            checkDim(opera, 0, false);
            getIfExists(opera, infoOperaJson, true);
            getIfExists(opera, nomeOpera + IMG_EXTENSION, true);
        }
    }

    private void checkDim(Tree node, int equalDim, final boolean equal) throws IllegalArgumentException {
        if (equal) {
            if (node.children.size() != equalDim)
                throw new IllegalArgumentException("I figli di " + node.value() + " non sono " + equalDim);
        } else {
            if (node.children.size() == equalDim)
                throw new IllegalArgumentException("I figli di " + node.value() + " sono " + equalDim);
        }
    }

    private Tree getIfExists(final Tree n, final String key, final boolean isFile) {
        Tree node = n;
        node = node.children.get(key);
        if (node == null)
            throw new IllegalArgumentException(n.value() + " non contiene " + key + " come figlio");
        if (isFile) {
            if (node.children != null)
                throw new IllegalArgumentException(n.value() + " non è un file");
        } else {
            if (node.children == null)
                throw new IllegalArgumentException(n.value() + " non è una directory");
        }
        return node;
    }

}
