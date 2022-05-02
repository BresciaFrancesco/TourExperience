package it.uniba.sms2122.tourexperience.musei.checkzip.exception;

public class ZipCheckerException extends Exception {

    public ZipCheckerException(String errorMessage) {
        super(errorMessage);
    }

    public ZipCheckerException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
