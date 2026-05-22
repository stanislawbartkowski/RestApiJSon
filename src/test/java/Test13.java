import org.json.JSONArray;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

// -c src/test/resources/testpar/restparamrename.properties -p 7999

@Ignore("Requires PostgreSQL")
public class Test13 extends PTestRestHelper {

    @Test
    public void test2() throws IOException {
        P("Test na wywołanie sql");
        JSONArray a = testoka("/testsql", null, "data");
        assertEquals(a.length(), 4);
    }

    @Test
    public void test3() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2", "id=1", "data");
        assertEquals(a.length(), 1);
    }

    @Test
    public void test4() throws IOException {
        P("Test na wywołanie sql z parametrem");
        JSONArray a = testoka("/testsql2y", "id=1", "data");
        assertEquals(a.length(), 1);
    }


}
