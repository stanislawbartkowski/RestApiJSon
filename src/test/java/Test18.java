import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


// -c src/test/resources/testpar/restpar18.properties -p 7999


public class Test18 extends PTestRestHelper {

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
