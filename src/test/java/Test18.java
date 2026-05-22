import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


public class Test18 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar18.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        int res = makegetcall("/testput", null);
        P("Method not allowed expected");
        assertEquals(res, 405);
    }

    @Test
    public void test2() throws IOException {
        int res = makegetcall("/testput", null);
        P("Method not allowed expected");
        assertEquals(res, 405);
    }

}
