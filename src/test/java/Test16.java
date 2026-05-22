import org.json.JSONArray;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

// -c src/test/resources/testpar/restpar15.properties -p 7999

public class Test16 extends PTestRestHelper {

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
