import com.rest.readjson.Helper;
import com.rest.readjson.HelperJSon;
import com.rest.readjson.RestError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;


public class Test21 extends TestHelper {

    @BeforeMethod
    public void beforeEachTestMethod() throws IOException, RestError {
        initno();
    }

    @Test
    public void test1() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir21);
        JSONObject o = readJS(files, "testdefa");
        System.out.println(o);
        JSONObject oo = HelperJSon.transformSecurity(o, "aaa");
        System.out.println(oo);
        // authlabel does not work for root object
        assertEquals(oo.length(), 2);
    }

    @Test
    public void test2() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir21);
        JSONObject o = readJS(files, "testsec1");
        System.out.println(o);
        JSONObject oo = HelperJSon.transformSecurity(o, "aaa");
        System.out.println(oo);
        // remove 1
        assertEquals(oo.length(), 1);

        JSONObject o1 = HelperJSon.transformSecurity(o, "admin");
        System.out.println(o1);
        // sec label comply
        assertEquals(o1.length(), 2);
    }

    @Test
    public void test3() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir21);
        JSONObject o = readJS(files, "testsec2");
        System.out.println(o);
        JSONObject oo = HelperJSon.transformSecurity(o, "aaa");
        System.out.println(oo);
        JSONArray a = oo.getJSONArray("res");
        assertEquals(a.length(), 1);
    }

    @Test
    public void test4() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir21);
        JSONObject o = readJS(files, "testsec3");
        System.out.println(o);

        JSONObject o1 = HelperJSon.transformSecurity(o, "");
        System.out.println(o1);
        JSONArray a1 = o1.getJSONArray("res");
        assertEquals(a1.length(), 1);

        JSONObject o2 = HelperJSon.transformSecurity(o, "admin");
        System.out.println(o2);
        JSONArray a2 = o2.getJSONArray("res");
        assertEquals(a2.length(), 1);

        JSONObject o3 = HelperJSon.transformSecurity(o, "xxxxx");
        System.out.println(o3);
        JSONArray a3 = o3.getJSONArray("res");
        assertEquals(a3.length(), 2);

    }

    @Test
    public void test5() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir21);
        JSONObject o = readJS(files, "testsec4");
        System.out.println(o);

        JSONObject o1 = HelperJSon.transformSecurity(o, "xxxxxx");
        System.out.println(o1);
        JSONArray a1 = o1.getJSONArray("res");
        JSONObject oo1 = (JSONObject) a1.get(1);
        System.out.println(oo1);

        JSONArray aa1 = oo1.getJSONArray("a");
        System.out.println(aa1);
        assertEquals(aa1.length(), 1);
    }


}
