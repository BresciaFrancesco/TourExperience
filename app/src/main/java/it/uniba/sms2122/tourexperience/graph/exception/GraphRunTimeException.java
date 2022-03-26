package it.uniba.sms2122.tourexperience.graph.exception;

public class GraphRunTimeException extends RuntimeException {

    public GraphRunTimeException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
