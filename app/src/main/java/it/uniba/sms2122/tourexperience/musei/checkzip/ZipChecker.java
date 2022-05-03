package it.uniba.sms2122.tourexperience.musei.checkzip;

import java.io.InputStream;

import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerException;
import it.uniba.sms2122.tourexperience.musei.checkzip.exception.ZipCheckerRunTimeException;

/**
 * Interfaccia per classi che effettuano controlli sulla struttura di file .zip
 */
public interface ZipChecker {

    /**
     * Fa partire il controllo della struttura del file .zip
     * @param in
     * @param zipName
     * @throws ZipCheckerException
     * @throws ZipCheckerRunTimeException
     */
    void start(final InputStream in, final String zipName) throws ZipCheckerException, ZipCheckerRunTimeException;
}
