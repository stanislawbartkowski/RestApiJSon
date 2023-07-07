import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/testpar/restpar20.properties -p 7999


public class Test20 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {

        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
        }};
        String res = getok("/test1", null, props);
        System.out.println(res);
        String[] lines = res.split("\n");
        for (String l: lines) {
            System.out.println(l);
        }
        assertEquals("perseus",lines[1]);
        assertEquals("111111111",lines[2]);
    }

    @Test
    public void test2() throws IOException {
        String res = "{\"res\":{\"a\":1,\"b\":2,\"c\":3,\"authlabel\":\"admin\"}}";
        testok("/getres", "resource=list", res);

        //  now authorization label
        Map<String, String> props = new HashMap<String, String>() {{
            put("authlabel", "xxx");
        }};
        testok("/getres", "resource=list", "{}", props);

        Map<String, String> props1 = new HashMap<String, String>() {{
            put("authlabel", "admin");
        }};
        testok("/getres", "resource=list", res, props1);

    }

}
