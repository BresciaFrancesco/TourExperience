package it.uniba.sms2122.tourexperience.utility.filesystem.zip;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interfaccia utile per permettere il disaccoppiamento delle classi
 * riguardanti lo ZIP dagli oggetti di Android quando si apre un file.
 */
public interface OpenFile {

    /**
     * Apre un file attraverso InputStream.
     * @return InputStream aperto.
     * @throws IOException
     */
    InputStream openFile() throws IOException;
}
