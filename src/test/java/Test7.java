import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

// -c src/test/resources/cacenter/carest.properties -p 7999

public class Test7 extends PTestRestHelper {

    private static final String ipath="src/test/resources/cacenter/testperm/www.example.com.csr.pem";
    @Test
    public void test1() throws IOException {
        P("Test na wywołanie subcert");
        JSONArray a = testoka("/subcert", "subject=/C=PL/ST=Mazovia/L=Warsaw/O=MyHome/OU=MyRoom/CN=www.example.com");
    }
    @Test
    public void test2() throws IOException {
        P("Gen cert from csr");
        int res = makegetcallupload("/csrcert", null, ipath);
    }


}

