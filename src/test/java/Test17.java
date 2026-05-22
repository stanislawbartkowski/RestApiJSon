import com.rest.readjson.Helper;
import com.rest.readjson.HelperJSon;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static org.testng.Assert.*;

public class Test17 extends TestHelper {


    @Test
    public void test1() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testdefa.yaml", Optional.empty());
        System.out.println(o);
        String v = o.getString("action");
        System.out.println(v);
        assertEquals(v, "Custom action");
    }

    @Test
    public void test2() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar.yaml", Optional.empty());
        System.out.println(o);
        String v = o.getString("Hello");
        assertEquals(v, "Wow");
        assertEquals(o.length(), 1);
    }

    @Test
    public void test3() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar1.yaml", Optional.empty());
        System.out.println(o);
        String v = o.getString("C");
        assertEquals(v, "newC");
        assertEquals(o.length(), 3);
    }

    @Test
    public void test4() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar2.yaml", Optional.empty());
        System.out.println(o);
        assertEquals(o.length(), 3);
        JSONObject rr = o.getJSONObject("B");
        System.out.println(rr);
        assertEquals(rr.length(), 2);
    }

    @Test
    public void test5() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = expectThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar3.yaml", Optional.empty())
        );
        P(thrown.getMessage());
    }

    @Test
    public void test6() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar4.yaml", Optional.empty());
        System.out.println(o);
        JSONArray a = o.optJSONArray("B");
        assertEquals(a.length(), 2);
        JSONObject e2 = a.getJSONObject(1);
        System.out.println(e2);
        assertEquals(e2.length(), 2);
    }

    @Test
    public void test7() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = expectThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar5.yaml", Optional.empty())
        );
        P(thrown.getMessage());
    }

    @Test
    public void test8() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = expectThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar6.yaml", Optional.empty())
        );
        P(thrown.getMessage());
    }

    @Test
    public void test9() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar7.yaml", Optional.empty());
        assertEquals(o.length(), 2);
        JSONArray a = o.optJSONArray("B");
        assertEquals(a.length(), 5);
    }

    private void runtest(String res) throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar8.yaml", Optional.empty());
        JSONArray a = o.optJSONArray("B");
        assertEquals(a.length(), 2);
        JSONObject o1 = a.getJSONObject(1);
    }

    @Test
    public void test10() throws RestError {
        runtest("testpar8.yaml");
    }

    @Test
    public void test11() throws RestError {
        runtest("testpar9.yaml");
    }

    // TODO: review later — step3.yaml is missing from src/test/resources/jdir17.
    @Ignore("TODO: review later — step3.yaml is missing from jdir17")
    @Test
    public void test12() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "step3.yaml", Optional.empty());
        System.out.println(o);
        JSONArray a = o.optJSONArray("fields");
        System.out.println(a.length());
        assertEquals(a.length(), 8);
    }

    // TODO: review later — step31.yaml is missing from src/test/resources/jdir17.
    @Ignore("TODO: review later — step31.yaml is missing from jdir17")
    @Test
    public void test13() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "step31.yaml", Optional.empty());
        System.out.println(o);
        JSONArray a = o.optJSONArray("fields");
        assertNotNull(a);
        System.out.println(a.length());
        assertEquals(a.length(), 8);
        assertNotNull(o.optJSONObject("formprops"));
        assertNotNull(o.getJSONObject("cardprops"));
    }

    // TODO: review later — step32.yaml is missing from src/test/resources/jdir17.
    @Ignore("TODO: review later — step32.yaml is missing from jdir17")
    @Test
    public void test14() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "step32.yaml", Optional.empty());
        System.out.println(o);
        JSONArray a = o.optJSONArray("fields");
        assertNotNull(a);
        System.out.println(a.length());
        assertEquals(a.length(), 8);
    }

}