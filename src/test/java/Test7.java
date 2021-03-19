import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.Assert.assertEquals;

// -c src/test/resources/cacenter/carest.properties -p 7999

public class Test7 extends PTestRestHelper {

    private static final String ipath="src/test/resources/cacenter/testperm/www.example.com.csr.pem";

    private void verifyzipresult(int no) throws IOException {
        ZipInputStream zip = getZipResult();
        ZipEntry e = zip.getNextEntry();
        int i = 0;
        while (e != null) {
            P(e.toString());
            e = zip.getNextEntry();
            i = i + 1;
        }
        assertEquals(no,i);
    }

    @Test
    public void test1() throws IOException {
        P("Test na wywołanie subcert");
        int res = makecall("/subcert", "subject=/C=PL/ST=Mazovia/L=Warsaw/O=MyHome/OU=MyRoom/CN=www.example.com","POST");
        verifyzipresult(4);
    }

    @Test
    public void test2() throws IOException {
        P("Gen cert from csr");
        int res = makegetcallupload("/csrcert", null, ipath);
        verifyzipresult(3);
    }


}

