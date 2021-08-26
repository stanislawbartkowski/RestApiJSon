import com.rest.readjson.RestError;
import org.json.JSONArray;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// -c src/test/resources/testpar/restpar11.properties -p 7999
// CREATE TABLE RESTTEST (ID INT, NAME VARCHAR(100));

public class Test11 extends PTestRestHelper {

    @Test
    public void test1() throws RestError, IOException {
        P("Test na wywołanie python");
        JSONArray a = testoka("/getdata", null);
        a.forEach( System.out::println);
        assertEquals(1,a.length());
    }

    private void checktable(boolean empty) throws RestError, IOException {
        JSONArray a = testoka("/jdbcdata", "id=11");
        a.forEach( System.out::println);
        System.out.println("" + a.length());
        if (empty) assertTrue(a.length() == 0);
        else assertTrue(a.length() > 0);
    }

    private static final String addpath="src/test/resources/jdata11/adddata.json";
    private static final String changepath="src/test/resources/jdata11/changedata.json";

    @Test
    public void test2() throws RestError, IOException {
        P("Test na wywołanie post data python");
        int res = makegetcallupload("/jdbcdata", null, addpath);
        System.out.println(res);
        assertEquals(200,res);
        String s = getData();
        P(s);
        checktable(false);
    }

    @Test
    public void test3() throws RestError, IOException {
        P("Test na wywołanie put change");
        int res = makegetcalluploadmeth("/jdbcdata", "PUT",null, changepath);
        System.out.println(res);
        assertEquals(200,res);
        String s = getData();
        P(s);
        checktable(false);
    }

    @Test
    public void test4() throws RestError, IOException {
        P("metoda delete");
        int res = makecall("/jdbcdata", "id=11","DELETE");
        System.out.println(res);
        assertEquals(200,res);
        String s = getData();
        P(s);
        checktable(true);
    }
}