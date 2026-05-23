import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

// Load test: generates a ~100 MB JSON document and fetches it through the
// RESOURCE/JSON endpoint, asserting the payload round-trips intact.
public class Test24 extends PTestRestHelper {

    private static final long TARGET_BYTES = 100L * 1024 * 1024;
    private static final Path BIG_FILE = Paths.get("target/loadtest/big/big.json");

    @BeforeClass
    public void setup() throws Exception {
        generateBigJson();
        TestServer.start("src/test/resources/testpar/restparbig.properties", 7999);
    }

    private void generateBigJson() throws IOException {
        if (Files.exists(BIG_FILE) && Files.size(BIG_FILE) >= TARGET_BYTES) return;
        Files.createDirectories(BIG_FILE.getParent());
        String filler = "abcdefghijklmnopqrstuvwxyz0123456789".repeat(5); // 180 ASCII chars
        try (BufferedWriter w = Files.newBufferedWriter(BIG_FILE, StandardCharsets.UTF_8)) {
            w.write("{\"res\":[");
            long written = 8;
            long i = 0;
            while (written < TARGET_BYTES - 200) {
                if (i > 0) {
                    w.write(',');
                    written++;
                }
                String row = "{\"i\":" + i + ",\"s\":\"" + filler + "\"}";
                w.write(row);
                written += row.length();
                i++;
            }
            w.write("]}");
        }
    }

    // JSON format streams the file by default — no parse, low memory.
    @Test
    public void test1() throws IOException {
        runLoad("/big", "JSON (stream)");
    }

    // parse: true falls back to the legacy parse-and-restringify path.
    // Heavy: a 100 MB parse needs ~500 MB heap.
    @Test
    public void test2() throws IOException {
        runLoad("/bigparse", "JSON (parse)");
    }

    private void runLoad(String path, String label) throws IOException {
        long t0 = System.currentTimeMillis();
        int code = makegetcall(path, "resource=big.json");
        assertEquals(code, 200);
        long chars = 0;
        StringBuilder head = new StringBuilder(8);
        try (BufferedReader r = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buf = new char[8192];
            int n;
            while ((n = r.read(buf)) >= 0) {
                chars += n;
                if (head.length() < 8) head.append(buf, 0, Math.min(n, 8 - head.length()));
            }
        }
        long elapsed = System.currentTimeMillis() - t0;
        P(label + " payload chars: " + chars + ", elapsed ms: " + elapsed + ", head: " + head);
        assertTrue(chars >= TARGET_BYTES / 2, "payload too small: " + chars);
        assertTrue(head.toString().startsWith("{\"res\""), "unexpected payload start: " + head);
    }
}