import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/testpar/restpar15.properties -p 7999

public class Test16 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        JSONArray a = testoka("/testdir", null);
        assertEquals(0,a.length());
    }

    @Test
    public void test2() throws IOException {
        JSONArray a = testoka("/testdir1", null);
        assertEquals(1,a.length());
    }

    @Test
    public void test3() throws IOException {
        JSONArray a = testoka("/testdir2", null);
        assertEquals(2,a.length());
    }

}
