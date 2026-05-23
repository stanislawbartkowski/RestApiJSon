import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class Test16 extends PTestRestHelper {

    /**
     *
     * @throws Exception
     */
    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar15.properties", 7999);
    }

    // Checks that GET /testdir returns an empty JSON array.
    // res15/print is an empty directory
    @Test
    public void test1() throws IOException {
        JSONArray a = testoka("/testdir", null);
        assertEquals(a.length(), 0);
    }

    // Checks that GET /testdir1 returns a single-entry JSON array.
    // res15/print1 contains one file
    @Test
    public void test2() throws IOException {
        JSONArray a = testoka("/testdir1", null);
        assertEquals(a.length(), 1);
    }

    // Checks that GET /testdir2 returns a two-entry JSON array.
    // res15/print2 contains two listable files
    @Test
    public void test3() throws IOException {
        JSONArray a = testoka("/testdir2", null);
        assertEquals(a.length(), 2);
    }

    // Checks that GET /testdir3 returns HTTP 400.
    // testdir3.yaml points to res15/nonexisting, which does not exist on disk.
    @Test
    public void test4() throws IOException {
        test400("/testdir3");
    }

}
