import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


// -c src/test/resources/testpar/restpar19.properties -p 7999


public class Test19 extends PTestRestHelper {

    @Test
    public void test1() throws IOException {
        String res = getok("/links/hello.txt", null);
        System.out.println(res);
        assertEquals("Hello, I'm your file", res);
    }

    @Test
    public void test2() throws IOException {
        int res = makegetcall("/links/hello1.txt", null);
        System.out.println(res);
        assertEquals(400, res);
    }

    @Test
    public void test3() throws IOException {
        int res = makegetcall("/links/../hello.txt", null);
        System.out.println(res);
        assertEquals(403, res);
    }

    @Test
    public void test4() throws IOException {
        String res = getok("/hello.txt", null);
        System.out.println(res);
        assertEquals("Hello, I'm your file", res);
    }

    @Test
    public void test5() throws IOException {
        String res = getok("/hello2.txt", null);
        System.out.println(res);
        assertEquals("I'm hello2", res);
    }


}
