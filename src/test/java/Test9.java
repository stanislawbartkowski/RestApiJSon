import org.json.JSONArray;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

public class Test9 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/testparresource.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        testok("/test2", "resource=list", "{}");
    }

    @Test
    public void test2() throws IOException {
        testok("/test3", "resource=listt", "Hello");
    }

    @Test
    public void test3() throws IOException {
        testok("/test7", "resource=testpol", "żółć");
    }

}
