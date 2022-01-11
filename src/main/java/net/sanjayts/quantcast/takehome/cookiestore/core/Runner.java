package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

/**
 * The runner code responsible for orchestrating the entire program flow. This class receives the front-loaded
 * program dependencies (core services) need to execute the logic.
 *
 * This is a very common manual dependency injection approach wherein all the layers at or below runner are unaware
 * of the actual implementation of the classes used in case we desire it so (this could have been easily done by making
 * cookie source, parser etc. adhere to some interface). This benefits us multi-fold -- it allows us to easily test our
 * application logic by passing in precise mocks and eases swapping out implementations.
 */
@Slf4j
// todo test cases
public class Runner {

    public void run(CookieParser parser, CookieDataStore store, LocalDate targetDate) {
        parser.cookieInfoStream().forEach(ci -> {
           log.debug("Cookie streamed from parser -- {}", ci);
           store.addCookie(ci);
        });
        // If no matching cookies found, nothing gets printed on STDOUT. Is this user-friendly enough?
        // Maybe a not found message with a non-zero return code to ensure we stay CLI friendly? Worth a thought...
        store.mostActiveFor(targetDate).forEach(log::info);
    }

}
