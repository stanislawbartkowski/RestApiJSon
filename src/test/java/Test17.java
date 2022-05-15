import com.rest.readjson.Helper;
import com.rest.readjson.HelperJSon;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class Test17 extends TestHelper {


    @Test
    public void test1() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testdefa.yaml");
        System.out.println(o);
        String v = o.getString("action");
        System.out.println(v);
        assertEquals("Custom action", v);
    }

    @Test
    public void test2() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar.yaml");
        System.out.println(o);
        String v = o.getString("Hello");
        assertEquals("Wow", v);
        assertEquals(1, o.length());
    }

    @Test
    public void test3() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar1.yaml");
        System.out.println(o);
        String v = o.getString("C");
        assertEquals("newC", v);
        assertEquals(3, o.length());
    }

    @Test
    public void test4() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar2.yaml");
        System.out.println(o);
        assertEquals(3, o.length());
        JSONObject rr = o.getJSONObject("B");
        System.out.println(rr);
        assertEquals(2, rr.length());
    }

    @Test
    public void test5() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = assertThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar3.yaml")
        );
        P(thrown.getMessage());
    }

    @Test
    public void test6() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar4.yaml");
        System.out.println(o);
        JSONArray a = o.optJSONArray("B");
        assertEquals(2, a.length());
        JSONObject e2 = a.getJSONObject(1);
        System.out.println(e2);
        assertEquals(2, e2.length());
    }

    @Test
    public void test7() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = assertThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar5.yaml")
        );
        P(thrown.getMessage());
    }

    @Test
    public void test8() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        RestError thrown = assertThrows(
                RestError.class,
                () -> HelperJSon.readJS(files, "testpar6.yaml")
        );
        P(thrown.getMessage());
    }

    @Test
    public void test9() throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar7.yaml");
        assertEquals(2, o.length());
        JSONArray a = o.optJSONArray("B");
        assertEquals(5, a.length());
    }

    private void runtest(String res) throws RestError {
        Helper.ListPaths files = new Helper.ListPaths(jdir17);
        JSONObject o = HelperJSon.readJS(files, "testpar8.yaml");
        JSONArray a = o.optJSONArray("B");
        assertEquals(2,a.length());
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

}