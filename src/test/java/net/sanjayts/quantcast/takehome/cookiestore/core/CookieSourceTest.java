package net.sanjayts.quantcast.takehome.cookiestore.core;

import net.sanjayts.quantcast.takehome.cookiestore.exceptions.SourceException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class CookieSourceTest {

    private final String SAMPLE_DATA = "cookie,timestamp\nAtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00";

    @Test
    void givenNewSource_whenNextLineCalled_thenFirstLineShouldBeReturned() {
        var cookieSource = new CookieSource(fromData(SAMPLE_DATA));
        assertThat(cookieSource.nextLine()).isEqualTo("cookie,timestamp");
    }

    @Test
    void givenNewSource_whenNextLineCalledTwice_thenBothLinesShouldBeReturned() {
        var cookieSource = new CookieSource(fromData(SAMPLE_DATA));
        assertThat(cookieSource.nextLine()).isEqualTo("cookie,timestamp");
        assertThat(cookieSource.nextLine()).isEqualTo("AtY0laUfhglK3lC7,2018-12-09T14:19:00+00:00");
    }

    @Test
    void givenNewSourceWithNullData_whenNextLineCalled_thenAnExceptionIsThrown() throws Exception {
        var mockReader = Mockito.mock(BufferedReader.class);
        doThrow(new IOException("EOF")).when(mockReader).readLine();
        var cookieSource = new CookieSource(mockReader);
        Throwable e = catchThrowable(cookieSource::nextLine);
        assertThat(e).isInstanceOf(SourceException.class)
                .hasMessage("Unexpected error encountered when reading cookie source data -- EOF")
                .hasCauseInstanceOf(IOException.class);
    }

    @Test
    void givenNewSource_whenNextLineCalledAndNoDataExists_thenNullShouldBeReturned() {
        var cookieSource = new CookieSource(fromData("cookie,timestamp"));
        assertThat(cookieSource.nextLine()).isEqualTo("cookie,timestamp");
        assertThat(cookieSource.nextLine()).isNull();
    }

    @Test
    void givenNewSource_whenSourceCreatedWithTryWithResource_thenCloseShouldBeAutomaticallyCalled() throws Exception {
        var mockReader = Mockito.mock(BufferedReader.class);
        try (var cookieSource = new CookieSource(mockReader)) {
            assertThat(cookieSource.nextLine()).isNull();
        }
        verify(mockReader).close();
    }

    @Test
    void givenNewSource_whenCloseFails_thenAnExceptionShouldBeThrown() throws Exception {
        var mockReader = Mockito.mock(BufferedReader.class);
        doThrow(new IOException("stream closed")).when(mockReader).close();
        Throwable t = catchThrowable(() -> {
            try (var cookieSource = new CookieSource(mockReader)) {
                assertThat(cookieSource.nextLine()).isNull();
            }
        });
        verify(mockReader).close();
        assertThat(t).isInstanceOf(SourceException.class)
                .hasMessage("Unexpected error encountered when closing the cookie source -- stream closed");
    }

    private static BufferedReader fromData(String data) {
        return new BufferedReader(new StringReader(data));
    }

}
