package net.sanjayts.quantcast.takehome.cookiestore;

import lombok.extern.slf4j.Slf4j;
import net.sanjayts.quantcast.takehome.cookiestore.core.CookieDataStore;
import net.sanjayts.quantcast.takehome.cookiestore.core.CookieParser;
import net.sanjayts.quantcast.takehome.cookiestore.core.CookieSource;
import net.sanjayts.quantcast.takehome.cookiestore.core.Runner;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Command(name = "cookie-store", mixinStandardHelpOptions = true, version = "1.0.0",
					description = "Parse cookie log file and retrieve most frequent cookies for a given day")
public class Main implements Callable<Integer> {

	@Option(names = {"-f"}, description = "The log file path", required = true)
	private File logFile;

	@Option(names = {"-d"}, description = "Date for which we want to see the most active cookie(s)", required = true)
	private LocalDate targetDate;

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Main()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() {
		try {
			log.debug("Successfully parsed the command line arguments -- file={}, target date={}", logFile.toURI(), targetDate);
			var bufReader = readerFromLogfile();
			var cookieSource = new CookieSource(bufReader);

			// Since the timestamps in source file are sorted in desc format, the target date becomes the cutoff date.
			// So for e.g. if the target date is 2020-01-15, then any dates less than 2020-01-15 00:00:00.000 should be
			// skipped. This means that 2020-01-15 01:00:00 will still be considered which is what we expect.
			var parser = CookieParser.createFromAndValidate(cookieSource, List.of("cookie", "timestamp"), targetDate);
			var dataStore = new CookieDataStore();
			var mostActiveCookies = new Runner().run(parser, dataStore, targetDate);

			// If no matching cookies found, nothing gets printed on STDOUT. Is this user-friendly enough?
			// Maybe a not found message with a non-zero return code to ensure we stay CLI friendly? Worth a thought...
			mostActiveCookies.forEach(log::info);

			return 0;
		} catch (Exception e) {
			log.error("{}", e.getMessage());
			return 1;
		}
	}

	private BufferedReader readerFromLogfile() throws Exception {
		var fileURI = logFile.toURI().toString();
		if (!logFile.exists()) {
			throw new IllegalArgumentException("The provided log file %s doesn't exist.".formatted(fileURI));
		}
		if (logFile.isDirectory()) {
			throw new IllegalArgumentException("The provided log file %s is actually a directory.".formatted(fileURI));
		}
		if (!logFile.canRead()) {
			throw new IllegalArgumentException(("The provided log file %s is not accessible, please check file perms " +
					"and try again.").formatted(fileURI));
		}
		return Files.newBufferedReader(logFile.toPath());
	}

}
