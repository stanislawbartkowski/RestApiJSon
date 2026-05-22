import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;


public class Test23 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar23.properties", 7999);
    }

    @Test
    public void test1() throws IOException {

        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
        }};
        int res = makegetcall("/test1", null, props);
        System.out.println(res);
        assertEquals(res, 400);
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
            put("Authorization", "Bearer xxxxxxxx");
        }};
        int res = makegetcall("/test1", null, props);
        System.out.println(res);
        assertEquals(res, 400);
    }

    @Test
    public void test3() throws IOException {
        String token = SecurityHelper.authenticateTestUser();
        Map<String, String> props = new HashMap<String, String>() {{
            put("uuid", "111111111");
            put("user", "perseus");
            put("Authorization", "Bearer " + token);
        }};
        int res = makegetcall("/test1", null, props);
        System.out.println(res);
        assertEquals(res, 200);

        // jeszcze raz to samo, tokenw cachu
        res = makegetcall("/test1", null, props);
        System.out.println(res);
        assertEquals(res, 200);

    }
    }
