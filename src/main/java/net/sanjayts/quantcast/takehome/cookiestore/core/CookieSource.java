package net.sanjayts.quantcast.takehome.cookiestore.core;

import net.sanjayts.quantcast.takehome.cookiestore.exceptions.SourceException;

import java.io.BufferedReader;
import java.io.Closeable;

/**
 * The class which represents a source of cookie data for our program. This accepts a Reader so that we can eventually
 * abstract over multiple sources as long as they spew out a char stream.
 *
 * Always ensure that this class is used inside 'try-with-resources' pattern to facilitate close of file handles if any.
 */
public class CookieSource implements Closeable {

    private final BufferedReader bufReader;

    public CookieSource(BufferedReader reader) {
        this.bufReader = reader;
    }

    /**
     * Retrieve the next line of data from our cookie source.
     *
     * @return The next line or null if no data exists
     */
    public String nextLine() {
        try {
            return bufReader.readLine();
        } catch (Exception e) {
            String msg = String.format("Unexpected error encountered when reading cookie source data -- %s", e.getMessage());
            throw new SourceException(msg, e);
        }
    }


    @Override
    public void close() {
        try {
            bufReader.close();
        } catch (Exception e) {
            String msg = String.format("Unexpected error encountered when closing the cookie source -- %s", e.getMessage());
            throw new SourceException(msg, e);
        }
    }

}
