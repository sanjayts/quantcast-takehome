package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.NoArgsConstructor;
import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;

import java.time.LocalDate;
import java.util.*;


/**
 * The data store responsible for storing all the parsed cookie information. This class also provides us with the
 * capability of querying for the most frequent cookie for a given day and other queries which we might think of.
 */
@NoArgsConstructor
public class CookieDataStore {

    private final Map<LocalDate, Map<String, Integer>> datedCookies = new HashMap<>();

    /**
     * Retrieves the most active cookies for a given date
     *
     * @param date The date for which the cookies should be returned
     * @return The set of most active cookies; empty set if no eligible cookies exist.
     */
    public Set<String> mostActiveFor(LocalDate date) {
        var cookieToFreq = datedCookies.get(date);
        if (cookieToFreq == null) {
            return Collections.emptySet();
        }
        var cookies = new HashSet<String>();
        var maxFreq = Integer.MIN_VALUE;
        for (var e : cookieToFreq.entrySet()) {
            if (e.getValue() >= maxFreq) {
                maxFreq = e.getValue();
                cookies.add(e.getKey());
            }
        }
        return cookies;
    }

    /**
     * Adds a cookie to the cookie data store.
     *
     * @param info The cookie info to be added.
     */
    public void addCookie(CookieInfo info) {
        var localDate = info.getTimestamp().toLocalDate();
        if (!datedCookies.containsKey(localDate)) {
            datedCookies.put(localDate, new HashMap<>());
        }
        datedCookies.get(localDate).compute(info.getName(), (k, v) -> v == null ? 1 : v + 1);
    }

}

record CookieEntry(String cookieName, int dayCount) {}