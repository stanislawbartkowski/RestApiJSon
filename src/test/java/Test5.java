import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/testpar/restparam.properties -p 7999

public class Test5 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        P("test not existing");
        test400("/noexisting");
    }

    @Test
    public void test2() throws IOException {
        P("Test na wywołanie sql");
        JSONArray a = testoka("/testsql", null);
        assertEquals(4,a.length());
    }

    @Test
    public void test3() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2", "id=1");
        assertEquals(1,a.length());
    }

    @Test
    public void test4() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2y", "id=1");
        assertEquals(1,a.length());
    }


}
