package net.sanjayts.quantcast.takehome.cookiestore.core;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Set;

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
public class Runner {

    public Set<String> run(CookieParser parser, CookieDataStore store, LocalDate targetDate) {
        parser.cookieInfoStream().forEach(ci -> {
           store.addCookie(ci);
        });
        return store.mostActiveFor(targetDate);
    }

}
