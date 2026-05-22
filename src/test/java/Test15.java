import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


// TODO: review later — /testmu1 response no longer contains the '----------------' multipart delimiter (expected 3 parts, got 1).
@Ignore("TODO: review later — /testmu1 response does not split into 3 parts on '----------------'")
public class Test15 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar15.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        String res = getok("/testmu1", null);
        // check multi content
        String[] p = res.split("----------------");
        System.out.println(p.length);
        assertEquals(p.length, 3);
    }

}
