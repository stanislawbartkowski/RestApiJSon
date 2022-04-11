import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/testpar/restpar14.properties -p 7999



public class Test14 extends PTestRestHelper {

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
        testok("/testpython1", null,"Hello");
    }

    @Test
    public void test4() throws IOException {
        P("Test na wywołanie python");
        testok("/wsp/testpython1", null,"Hello");
    }


}
