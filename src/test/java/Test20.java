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
}
