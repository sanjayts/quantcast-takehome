package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;

import java.time.LocalDate;
import java.util.*;


/**
 * The data store responsible for storing all the parsed cookie information. This class also provides us with the
 * capability of querying for the most frequent cookie for a given day and other queries which we might think of.
 */
@NoArgsConstructor
@Slf4j
public class CookieDataStore {

    /*
    The choice of data structures for this particular implementation is specifically chosen for the purpose of fastest
    retrieval for the most frequent cookie. The presence of a priority queue (or a heap) allows us to quickly pick the
    most frequent cookie O(logN) while at the same time taking up memory linear to the total cookie count. The drawback
    here being each insert into the heap taking up O(logN) time which becomes a total of O(NlogN) insert for the
    entire cookie population as opposed to O(N) for a pure map based approach.

    This is different from the naive hash map approach which would need to traverse all the cookies for a given date
    which in the worst case of a single date and only-once-present cookie will degrade into a linear scan.

    That's not to say this implementation is ideal for all use-cases (what about top K? What about cases wherein a
    small number of unique cookies occur numerous times?). It's all about trade-offs!
     */

    private final Map<LocalDate, PriorityQueue<CookieEntry>> datedCookies = new HashMap<>();

    private final Map<LocalDate, Map<String, Integer>> cookieNameCnt = new HashMap<>();

    private final Comparator<CookieEntry> cookieComparator = Comparator.comparingInt(CookieEntry::hitCount).reversed();

    /**
     * Retrieves the most active cookies for a given date
     *
     * @param date The date for which the cookies should be returned
     * @return The set of most active cookies; empty set if no eligible cookies exist.
     */
    public Set<String> mostActiveFor(LocalDate date) {
        log.debug("Most active cookie requested for date {}", date);
        var pq = datedCookies.get(date);
        if (pq == null || pq.isEmpty()) {
            return Collections.emptySet();
        }

        var cookies = new HashSet<String>();
        var maxCnt = pq.peek().hitCount();
        while (!pq.isEmpty()) {
            var ce= pq.poll();
            if (ce.hitCount() != maxCnt) {
                break;
            }
            cookies.add(ce.cookieName());
        }
        // Since this is a one-shot program, we don't maintain a list of popped entries. If this store was to be queried
        // back to back, multiple times, we want to make sure all the stuff we pop off gets inserted back unless we want
        // to have a query-state maintaining store.
        return cookies;
    }

    /**
     * Adds a cookie to the cookie data store.
     *
     * @param info The cookie info to be added.
     */
    public void addCookie(CookieInfo info) {
        log.debug("Start adding {} to the cookie store", info);
        var localDate = info.getTimestamp().toLocalDate();
        var cookieName = info.getName();

        if (!datedCookies.containsKey(localDate)) {
            datedCookies.put(localDate, new PriorityQueue<>(cookieComparator));
        }
        if (!cookieNameCnt.containsKey(localDate)) {
            cookieNameCnt.put(localDate, new HashMap<>());
        }

        var newCnt = cookieNameCnt.get(localDate).compute(cookieName, (k, v) -> v == null ? 1 : v + 1);
        var pq = datedCookies.get(localDate);
        var ce = new CookieEntry(cookieName, newCnt);
        pq.add(ce);
    }

}

/** Our internal record which maintains a point-in-time hit-count information for a given cookie */
record CookieEntry(String cookieName, int hitCount) {}
