import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


public class Test19 extends PTestRestHelper {

    @BeforeClass
    public void startServer() throws Exception {
        TestServer.start("src/test/resources/testpar/restpar19.properties", 7999);
    }

    @Test
    public void test1() throws IOException {
        String res = getok("/links/hello.txt", null);
        System.out.println(res);
        assertEquals(res, "Hello, I'm your file");
    }

    @Test
    public void test2() throws IOException {
        int res = makegetcall("/links/hello1.txt", null);
        System.out.println(res);
        assertEquals(res, 400);
    }

    @Test
    public void test3() throws IOException {
        int res = makegetcall("/links/../hello.txt", null);
        System.out.println(res);
        assertEquals(res, 403);
    }

    @Test
    public void test4() throws IOException {
        String res = getok("/hello.txt", null);
        System.out.println(res);
        assertEquals(res, "Hello, I'm your file");
    }

    @Test
    public void test5() throws IOException {
        String res = getok("/hello2.txt", null);
        System.out.println(res);
        assertEquals(res, "I'm hello2");
    }


}
