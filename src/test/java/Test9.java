
// -c src/test/resources/testpar/testparresource.properties -p 7999


import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

public class Test9 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        testok("/test2", "resource=list", "{}");
    }

    @Test
    public void test2() throws IOException {
        testok("/test3", "resource=listt", "Hello");
    }


}
