package net.sanjayts.quantcast.takehome.cookiestore.core;

import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class RunnerTest {

    @Test
    void givenNewRunner_whenRunInvoked_thenEndToEndFlowShouldBeExecute() {
        var parser = mock(CookieParser.class);
        var cookies = Stream.of(
                new CookieInfo("asdf1adsf", zDtTimeOf(2020, 1, 1, 1)),
                new CookieInfo("asdf1adsf", zDtTimeOf(2020, 1, 1, 2)),
                new CookieInfo("zxzf1adsf", zDtTimeOf(2020, 1, 1, 3))
        );
        doReturn(cookies).when(parser).cookieInfoStream();
        var frequentCookies = new Runner().run(parser, new CookieDataStore(), LocalDate.of(2020, 1, 1));
        assertThat(frequentCookies).isEqualTo(Set.of("asdf1adsf"));
    }

    private static ZonedDateTime zDtTimeOf(int year, int month, int day, int min) {
        return ZonedDateTime.of(LocalDateTime.of(year, month, day, 0, min), ZoneOffset.UTC);
    }

}