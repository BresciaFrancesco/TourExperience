package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.File;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;

/**
 * Interfaccia per classi che effettuano controlli sulla struttura di file .zip
 */
public interface ZipChecker {

    /**
     * Fa partire il controllo della struttura del file .zip
     * @param zipFile file .zip da controllare
     * @throws ZipCheckerException
     * @throws ZipCheckerRunTimeException
     */
    void start(final File zipFile) throws ZipCheckerException, ZipCheckerRunTimeException;
}
