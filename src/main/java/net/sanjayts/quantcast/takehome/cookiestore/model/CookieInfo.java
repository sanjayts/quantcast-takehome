package net.sanjayts.quantcast.takehome.cookiestore.model;

import lombok.Data;

import java.time.ZonedDateTime;

/**
 * The class responsible for storing all the information related to a cookie. This currently includes only two fields
 * but can be extended to add more. Before consuming this object, we should verify whether this cookie is valid by
 * checking its `isValid` method.
 */
@Data
public class CookieInfo {

    private String name;

    /** The UTC timestamp parsed from the log file */
    private ZonedDateTime timestamp;

    private String rawCookieData;

    public CookieInfo(String name, ZonedDateTime timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }

    private CookieInfo(String rawCookieData) {
        this.rawCookieData = rawCookieData;
    }

    /**
     * Create an invalid cookie with reference to the problematic input data for future troubleshooting.
     */
    public static CookieInfo createInvalid(String data) {
        return new CookieInfo(data);
    }

    public boolean isValid() {
        return rawCookieData == null;
    }

}
