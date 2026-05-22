import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


// -c src/test/resources/testpar/restpar15.properties -p 7999

public class Test15 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        String res = getok("/testmu1", null);
        // check multi content
        String[] p = res.split("----------------");
        System.out.println(p.length);
        assertEquals(p.length, 3);
    }

}
