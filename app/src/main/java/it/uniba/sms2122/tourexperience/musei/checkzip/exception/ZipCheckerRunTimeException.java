package it.uniba.sms2122.tourexperience.musei.checkzip.exception;

public class ZipCheckerRunTimeException extends RuntimeException {

    public ZipCheckerRunTimeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
