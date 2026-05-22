import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class Test6 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restparamshell.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        P("Upload file");
        String i = "src/test/resources/jdir5/input.txt";
        int res = makegetcallupload("/testshell1", null, i);
        P("Result: " + res);
        Assert.assertEquals(res, 200);
        String s = getData();
        P(s);
        assertEquals(s, "Hello, I'm uploaded");

    }
    @Test
    public void test2() throws IOException {
        P("Upload file");
        String i = "src/test/resources/jdir5/inpututf8.txt";
        int res = makegetcallupload("/testshell1", null, i);
        P("Result: " + res);
        Assert.assertEquals(res, 200);
        String s = getData();
        P(s);
        assertEquals(s, "Zażółcona łączka");

    }

}

