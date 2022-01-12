package net.sanjayts.quantcast.takehome.cookiestore.core;

import net.sanjayts.quantcast.takehome.cookiestore.model.CookieInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CookieDataStoreTest {

    private static final List<CookieInfo> SAMPLE_COOKIES = List.of(
            new CookieInfo("zxcvzxcvzxcvv", zDtTimeOf(2021, 12, 31, 59)),

            new CookieInfo("asdf3sdfasdf", zDtTimeOf(2022, 1, 1, 1)),
            new CookieInfo("asdf3sdfasdf", zDtTimeOf(2022, 1, 1, 2)),
            new CookieInfo("asdf3sdfasdf", zDtTimeOf(2022, 1, 1, 3)),
            new CookieInfo("csdafg3423ds", zDtTimeOf(2022, 1, 1, 4)),
            new CookieInfo("csdafg3423ds", zDtTimeOf(2022, 1, 1, 5)),
            new CookieInfo("qwdafg3423ds", zDtTimeOf(2022, 1, 1, 6)),
            new CookieInfo("qwdafg3423ds", zDtTimeOf(2022, 1, 1, 7)),
            new CookieInfo("tydafg3423ds", zDtTimeOf(2022, 1, 1, 8)),
            new CookieInfo("yudafg3423ds", zDtTimeOf(2022, 1, 1, 9)),

            new CookieInfo("poiuwerwerwer", zDtTimeOf(2022, 2, 2, 1)),
            new CookieInfo("poiuwerwerwer", zDtTimeOf(2022, 2, 2, 2)),
            new CookieInfo("fhfg342534fff", zDtTimeOf(2022, 2, 2, 3)),
            new CookieInfo("zxcvzxcvzxcvv", zDtTimeOf(2022, 2, 2, 4)),
            new CookieInfo("zxcvzxcvzxcvv", zDtTimeOf(2022, 2, 2, 5))
    );

    @Test
    void givenEmptyDataStore_whenCookiesAdded_thenAllOperationsShouldBeSuccessful() {
        var store = new CookieDataStore();
        SAMPLE_COOKIES.forEach(store::addCookie);
    }

    @Test
    void givenPopulatedDataStore_whenMostFrequentRequested_thenTheMostFrequentCookieShouldBeReturned() {
        var store = new CookieDataStore();
        SAMPLE_COOKIES.forEach(store::addCookie);
        var mostActiveCookie = store.mostActiveFor(LocalDate.of(2022, 1, 1));
        assertThat(mostActiveCookie).isEqualTo(Set.of("asdf3sdfasdf"));
    }

    @Test
    void givenPopulatedDataStore_whenWeHaveMultipleMostActiveCookies_thenAllMostActiveCookiesShouldBeReturned() {
        var store = new CookieDataStore();
        SAMPLE_COOKIES.forEach(store::addCookie);
        var mostActiveCookie = store.mostActiveFor(LocalDate.of(2022, 2, 2));
        assertThat(mostActiveCookie).isEqualTo(Set.of("zxcvzxcvzxcvv", "poiuwerwerwer"));
    }

    @Test
    void givenPopulatedDataStore_whenNoCookiesForGivenDate_thenEmptySetShouldBeReturned() {
        var store = new CookieDataStore();
        SAMPLE_COOKIES.forEach(store::addCookie);
        var mostActiveCookie = store.mostActiveFor(LocalDate.of(2022, 12, 12));
        assertThat(mostActiveCookie).isEmpty();
    }

    private static ZonedDateTime zDtTimeOf(int year, int month, int day, int min) {
        return ZonedDateTime.of(LocalDateTime.of(year, month, day, 0, min), ZoneOffset.UTC);
    }

}
