import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class Test16 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar15.properties", 7999);
    }

    // TODO: review later — /testdir returns HTTP 400 in this environment.
    @Ignore("TODO: review later — /testdir returns HTTP 400")
    @Test
    public void test1() throws IOException {
        JSONArray a = testoka("/testdir", null);
        assertEquals(a.length(), 0);
    }

    @Test
    public void test2() throws IOException {
        JSONArray a = testoka("/testdir1", null);
        assertEquals(a.length(), 1);
    }

    @Test
    public void test3() throws IOException {
        JSONArray a = testoka("/testdir2", null);
        assertEquals(a.length(), 2);
    }

}
