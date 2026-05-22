import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

public class Test5 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restparam.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        P("test not existing");
        test400("/noexisting");
    }

    @Ignore("Requires PostgreSQL")
    @Test
    public void test2() throws IOException {
        P("Test na wywołanie sql");
        JSONArray a = testoka("/testsql", null);
        assertEquals(a.length(), 4);
    }

    @Ignore("Requires PostgreSQL")
    @Test
    public void test3() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2", "id=1");
        assertEquals(a.length(), 1);
    }

    @Ignore("Requires PostgreSQL")
    @Test
    public void test4() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2y", "id=1");
        assertEquals(a.length(), 1);
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
        assertEquals(a.length(), 0);

        // insert value
        a = testoka("/testsql6", "name=" + v);
        assertNull(a);
        // select again
        a = testoka("/testsql7", "name=" + v);
        System.out.println(a);
        assertEquals(a.length(), 1);

    }

    @Ignore("Requires PostgreSQL")
    @Test
    public void test5() throws IOException {
        testsqlvalue("Hello");
    }

    @Ignore("Requires PostgreSQL")
    @Test
    public void test6() throws IOException {
        testsqlvalue("200/111");
    }


}
