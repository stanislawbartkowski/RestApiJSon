import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertEquals(4, a.length());
    }

    @Test
    public void test3() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2", "id=1");
        assertEquals(1, a.length());
    }

    @Test
    public void test4() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2y", "id=1");
        assertEquals(1, a.length());
    }

    private void testsqlvalue(String v) throws IOException {
        P("Delete from testm");
        JSONArray a = testoka("/testsql5", null);
        System.out.println(a);
        // null table expected
        assertNull(a);
        // check if empty
        a = testoka("/testsql8", null);
        System.out.println(a);
        assertEquals(0, a.length());

        // insert value
        a = testoka("/testsql6", "name=" + v);
        assertNull(a);
        // select again
        a = testoka("/testsql7", "name=" + v);
        System.out.println(a);
        assertEquals(1, a.length());

    }

    @Test
    public void test5() throws IOException {
        testsqlvalue("Hello");
    }

    @Test
    public void test6() throws IOException {
        testsqlvalue("200/111");
    }


}
