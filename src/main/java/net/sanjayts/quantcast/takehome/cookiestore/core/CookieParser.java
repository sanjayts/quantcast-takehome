package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sanjayts.quantcast.takehome.cookiestore.exceptions.ParserException;
import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;


/**
 * The class responsible for "parsing" cookie information out of the log file and creating a domain object out of it. This
 * class deals with validating the source headers and skipping entries/lines which are malformed.
 */
@Slf4j
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieParser {

    private final CookieSource source;

    private final List<String> headers;

    private LocalDate cutoffDate;

    /**
     * Given a cookie source, the file headers and cutoff date, create a new parser. The headers will be used to validate
     * that the source data format conforms to our assumptions.
     */
    public static CookieParser createFromAndValidate(CookieSource source, List<String> headers, LocalDate cutoffDate) {
        var parser = new CookieParser(source, headers, cutoffDate);
        parser.validateHeaders();
        return parser;
    }

    private void validateHeaders() {
        var line = source.nextLine();
        if (line == null) {
            throw new ParserException("No header information found in the provided cookie source, please check");
        }
        var parsedHeaders = Arrays.stream(line.split(",")).map(String::strip).toList();
        if (!headers.equals(parsedHeaders)) {
            var msg = String.format("Headers %s found in the provided cookie source doesn't match the expected headers %s",
                    parsedHeaders, headers);
            throw new ParserException(msg);
        }
    }

    /**
     * Streams all cookie objects after reading them and parsing them from the underlying source. This method takes
     * care of skipping all unnecessary entries and stopping the parser for reading entries after the cutoff date. The
     * terminating condition for the stream is when the underlying source runs out of data to hand over.
     */
    public Stream<CookieInfo> cookieInfoStream() {
        var filterPred = getFilterPred();
        var twPred = getTakeWhilePred();
        return Stream.generate(() -> parseInfo(source.nextLine())).filter(filterPred).takeWhile(twPred);
        // The terminating condition of our stream -- when we encounter a `null`,
        // we know we have hit EOF and should now terminate the stream. We also terminate when the parsed date is
        // <= the target date.
    }

    private Predicate<CookieInfo> getTakeWhilePred() {
        return (ci -> {
            if (ci == null) {
                log.debug("No more data found in the source so terminate our stream");
                return false;
            } else if (ci.getTimestamp().toLocalDate().isBefore(cutoffDate)) {
               log.debug("Early exit from our parsing loop since we have gone below the cutoff date {} with cookie {}",
                       cutoffDate, ci);
               return false;
           } else {
                return true;
            }
        });
    }

    private Predicate<CookieInfo> getFilterPred() {
        return (ci -> ci == null || ci.isValid());
    }

    private CookieInfo parseInfo(String line) {
        if (line == null) {
            return null;
        }

        var parts = line.split(",");
        if (parts.length != 2) {
            return CookieInfo.createInvalid(line);
        }

        if (parts[0].isBlank()) {
            return CookieInfo.createInvalid(line);
        }

        try {
            return new CookieInfo(parts[0], parseDate(parts[1]));
        } catch (Exception e) {
            log.debug("Failed to parse the date in line {}, creating an invalid cookie", line);
            return CookieInfo.createInvalid(line);
        }
    }

    private ZonedDateTime parseDate(String dateString) {
        // Parse the timestamp in the log file as an offset datetime and then normalize it to UTC timezone so that
        // CookieInfo always deals with UTC as opposed to different timezones.
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME).withZoneSameInstant(ZoneOffset.UTC);
    }

}
