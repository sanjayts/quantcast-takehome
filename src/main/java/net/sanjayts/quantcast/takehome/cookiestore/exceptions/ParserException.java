package net.sanjayts.quantcast.takehome.cookiestore.exceptions;

/**
 * The exception thrown when our cookie parser encounters an unrecoverable problem like empty data source etc.
 */
public class ParserException extends RuntimeException {

    public ParserException(String message) {
        super(message);
    }

}
