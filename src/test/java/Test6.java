import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

// -c src/test/resources/testpar/restparamshell.properties -p 7999

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class Test6 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        P("Upload file");
        String i = "src/test/resources/jdir5/input.txt";
        int res = makegetcallupload("/testshell1",null,i);
        P("Result: " + res);
        Assert.assertEquals(200, res);
        String s = getData();
        P(s);
        assertEquals("Hello, I'm uploaded",s);

    }

}

