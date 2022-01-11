package net.sanjayts.quantcast.takehome.cookiestore.core;

import net.sanjayts.quantcast.takehome.cookiestore.exceptions.ParserException;
import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class CookieParserTest {

    private static final List<String> DEFAULT_HEADERS = List.of("cookie", "timestamp");

    private static final LocalDate cutoffDate = LocalDate.of(2018, 12, 8);

    @Test
    void givenNewParser_whenNoDataFoundInSource_thenAnExceptionShouldBeThrown() {
        var source = mock(CookieSource.class);
        doReturn(null).when(source).nextLine();
        var t = catchThrowable(() -> CookieParser.createFromAndValidate(source, DEFAULT_HEADERS, cutoffDate));
        assertThat(t).isInstanceOf(ParserException.class)
                .hasMessage("No header information found in the provided cookie source, please check");
    }

    @Test
    void givenNewParser_whenDataFound_thenCookieInfoStreamShouldBeReturned() {
        var source = mock(CookieSource.class);
        // Notice the +01:00, here we are also trying to test out our offset logic
        doReturn("cookie,timestamp", "AtY0laUfhglK3lC7,2018-12-09T14:19:00+01:00")
                .when(source).nextLine();
        var parser = CookieParser.createFromAndValidate(source, DEFAULT_HEADERS, cutoffDate);
        var infoStream = parser.cookieInfoStream();
        var cookieInfo = infoStream.findFirst().get();
        var dt = ZonedDateTime.of(LocalDateTime.of(2018, 12, 9, 13, 19), ZoneOffset.UTC);
        assertThat(cookieInfo).isEqualTo(new CookieInfo("AtY0laUfhglK3lC7", dt));
    }

    @Test
    void givenNewParser_whenSourceOnlyContainsHeader_thenEmptyStreamShouldBeReturned() {
        var source = mock(CookieSource.class);
        // Notice the +01:00, here we are also trying to test out our offset logic
        doReturn("cookie,timestamp", (Object) null).when(source).nextLine();
        var parser = CookieParser.createFromAndValidate(source, DEFAULT_HEADERS, cutoffDate);
        var cookies = parser.cookieInfoStream().toList();
        assertThat(cookies).isEmpty();
    }

    @Test
    void givenNewParser_whenSourceHeadersDontMatchParserHeaders_thenAnExceptionShouldBeThrown() {
        var source = mock(CookieSource.class);
        // Notice the +01:00, here we are also trying to test out our offset logic
        doReturn("biscuit,timestamp").when(source).nextLine();
        var t = catchThrowable(() -> CookieParser.createFromAndValidate(source, DEFAULT_HEADERS, cutoffDate));
        assertThat(t).isInstanceOf(ParserException.class)
                .hasMessage("Headers [biscuit, timestamp] found in the provided cookie source doesn't match the expected headers [cookie, timestamp]");
    }

    @Test
    void givenNewParser_whenSourceContainsMultipleBadInputs_thenCookieStreamShouldHaveMultipleInvalidEntries() {
        var source = mock(CookieSource.class);
        // Notice the +01:00, here we are also trying to test out our offset logic
        doReturn("cookie,timestamp", "", ",", " x , x", "abcd,",
                "c1,2018-12-09T00:00:00+00:00", "  ,    ", null).when(source).nextLine();
        var parser = CookieParser.createFromAndValidate(source, DEFAULT_HEADERS, cutoffDate);
        var cookies = parser.cookieInfoStream().toList();
        var zdt = ZonedDateTime.of(LocalDateTime.of(2018, 12, 9, 0, 0), ZoneOffset.UTC);
        assertThat(cookies).isEqualTo(List.of(new CookieInfo("c1", zdt)));
    }


}
