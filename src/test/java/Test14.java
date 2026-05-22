import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


public class Test14 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar14.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        testok("/getres", "resource=list", "{}");
    }

    @Test
    public void test2() throws IOException {
        testok("/getres", "resource=wsp/data", "{\"a\":10}");
    }

    @Test
    public void test3() throws IOException {
        P("Test na wywołanie python");
        testok("/testpython1", null, "Hello");
    }

    @Test
    public void test4() throws IOException {
        P("Test na wywołanie python");
        testok("/wsp/testpython1", null, "Hello");
    }

    @Test
    public void test5() throws IOException {
        P("Test na wywołanie python");
        testok("/wsp/testpython2", "PARAMS=aaaa", "{'response' : 'aaaa'}");
    }

    @Test
    public void test6() throws IOException {
        P("Test na wywołanie python i parametr zawierajacy / ");
        testok("/wsp/testpython2", "PARAMS=A/B", "{'response' : 'A/B'}");
    }

}
