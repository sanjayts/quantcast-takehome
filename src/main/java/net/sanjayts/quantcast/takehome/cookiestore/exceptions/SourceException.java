package net.sanjayts.quantcast.takehome.cookiestore.exceptions;

/**
 * The exception thrown in case we encounter a problem when dealing with the cookie source
 */
public class SourceException extends RuntimeException {

    public SourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
