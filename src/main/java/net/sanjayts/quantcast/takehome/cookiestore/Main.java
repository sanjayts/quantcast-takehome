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
			// Since the timestamps in source file are sorted in desc format, the cutoff date becomes the day before
			// the target date i.e. if target date is 2021-01-01, we should stop parsing the source once we encounter
			// any date time <= 2020-12-31
			var cutoffDate = targetDate.plusDays(-1);
			var parser = CookieParser.createFromAndValidate(cookieSource, List.of("cookie,timestamp"), cutoffDate);
			var dataStore = new CookieDataStore();
			new Runner().run(parser, dataStore, targetDate);
			return 0;
		} catch (Exception e) {
			log.error("{}", e.getMessage());
			return -1;
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
