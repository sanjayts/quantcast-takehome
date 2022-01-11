package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
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

// TODO javadocs
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CookieParser {

    private final CookieSource source;

    private final List<String> headers;

    private LocalDate cutoffDate;

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

    public Stream<CookieInfo> cookieInfoStream() {
        var cutoffPred = pred();
        return Stream.generate(() -> {
            final String line = source.nextLine();
            return parseInfo(line);
        }).filter(cutoffPred).takeWhile(Objects::nonNull);
        // The terminating condition of our stream -- when we encounter a `null`,
        // we know we have hit EOF and should now terminate the stream. We also terminate when the parsed date is
        // <= the target date.
    }

    private Predicate<CookieInfo> pred() {
        return (ci -> ci == null || (ci.isValid() && ci.getTimestamp().toLocalDate().isAfter(cutoffDate)));
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
            return CookieInfo.createInvalid(line);
        }
    }

    private ZonedDateTime parseDate(String dateString) {
        // Parse the timestamp in the log file as an offset datetime and then normalize it to UTC timezone so that
        // CookieInfo always deals with UTC as opposed to different timezones.
        return ZonedDateTime.parse(dateString, DateTimeFormatter.ISO_OFFSET_DATE_TIME).withZoneSameInstant(ZoneOffset.UTC);
    }

}
