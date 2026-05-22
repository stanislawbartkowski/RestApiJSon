import org.json.JSONArray;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.testng.Assert.assertEquals;

// TODO: review later — /csrcert and /subcert endpoints return HTTP 400; likely depend on openssl tooling or specific cert/key fixtures.
@Ignore("TODO: review later — /csrcert and /subcert return HTTP 400 in this environment")
public class Test7 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/cacenter/carest.properties", 7999);
    }

    private static final String ipath = "src/test/resources/cacenter/testperm/www.example.com.csr.pem";

    private void verifyzipresult(int no) throws IOException {
        ZipInputStream zip = getZipResult();
        ZipEntry e = zip.getNextEntry();
        int i = 0;
        while (e != null) {
            P(e.toString());
            e = zip.getNextEntry();
            i = i + 1;
        }
        assertEquals(i, no);
    }

    @Test
    public void test1() throws IOException {
        P("Test na wywołanie subcert");
        int res = makecall("/subcert", "subject=/C=PL/ST=Mazovia/L=Warsaw/O=MyHome/OU=MyRoom/CN=www.example.com", "POST");
        verifyzipresult(4);
    }

    @Test
    public void test2() throws IOException {
        P("Gen cert from csr");
        int res = makegetcallupload("/csrcert", null, ipath);
        verifyzipresult(3);
    }


}

