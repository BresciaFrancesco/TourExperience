package it.uniba.sms2122.tourexperience.graph.exception;

public class GraphException extends Exception {

    public GraphException(String errorMessage) {
        super(errorMessage);
    }

    public GraphException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }

}
