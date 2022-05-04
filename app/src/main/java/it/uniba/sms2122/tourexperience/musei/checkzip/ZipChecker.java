package it.uniba.sms2122.tourexperience.musei.checkzip;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;
import it.uniba.sms2122.tourexperience.utility.filesystem.zip.OpenFile;

/**
 * Interfaccia per classi che effettuano controlli sulla struttura di file .zip
 */
public interface ZipChecker {

    /**
     * Fa partire il controllo della struttura del file .zip
     * @param of oggetto che implementa l'interfaccia OpenFile, per
     *           poter aprire il file .zip in lettura.
     * @param zipName nome del file .zip con l'estensione .zip integrata.
     * @throws ZipCheckerException
     * @throws ZipCheckerRunTimeException
     */
    void start(final OpenFile of, final String zipName) throws ZipCheckerException, ZipCheckerRunTimeException;
}
