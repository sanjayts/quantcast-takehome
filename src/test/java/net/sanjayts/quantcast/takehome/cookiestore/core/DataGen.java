package net.sanjayts.quantcast.takehome.cookiestore.core;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.*;

/**
 * Test data generator for ease of load/functional testing.
 */
public class DataGen {

    private static final String[] NAMES = new String[] {
            "AtY0laUfhglK3lC7", "BtY0laUfhglK3lC7", "CtY0laUfhglK3lC7", "DtY0laUfhglK3lC7", "EtY0laUfhglK3lC7",
            "FtY0laUfhglK3lC7", "GtY0laUfhglK3lC7", "HtY0laUfhglK3lC7", "JtY0laUfhglK3lC7", "KtY0laUfhglK3lC7",
            "LtY0laUfhglK3lC7", "MtY0laUfhglK3lC7", "NtY0laUfhglK3lC7", "OtY0laUfhglK3lC7", "PtY0laUfhglK3lC7",
            "QtY0laUfhglK3lC7", "RtY0laUfhglK3lC7", "StY0laUfhglK3lC7", "TtY0laUfhglK3lC7", "UtY0laUfhglK3lC7",
    };

    private static final String[] TIMESTAMPS = new String[] {
            "2018-01-01T14:19:00+00:00", "2018-02-02T14:19:00+00:00", "2018-03-03T14:19:00+00:00", "2018-04-04T14:19:00+00:00",
            "2018-05-05T14:19:00+00:00", "2018-06-06T14:19:00+00:00", "2018-07-07T14:19:00+00:00", "2018-08-08T14:19:00+00:00",
            "2018-09-09T14:19:00+00:00", "2018-10-10T14:19:00+00:00", "2018-11-11T14:19:00+00:00", "2018-12-12T14:19:00+00:00"
    };

    public static void main(String[] args) throws Exception {
        var out = Files.newBufferedWriter(Path.of("test-data/load-test.txt"), StandardCharsets.UTF_8);
        new DataGen().generateData(out, 1_000_000);
    }

    void generateData(Writer out, int recordCnt) throws Exception {
        var noOfNames = NAMES.length;
        var noOfTs = TIMESTAMPS.length;
        var rand = new SecureRandom();
        try (out) {
            var data = new ArrayList<Cookie>();
            for (int i = 0; i < recordCnt; ++i) {
                var rName = NAMES[rand.nextInt(noOfNames)];
                var rTs = TIMESTAMPS[rand.nextInt(noOfTs)];
                data.add(new Cookie(rName, rTs));
            }

            data.sort(Comparator.comparing(Cookie::timestamp).reversed());

            for (var c : data) {
                out.write(String.format("%s,%s\n", c.name(), c.timestamp()));
            }
        }
    }

}

record Cookie(String name, String timestamp) {}