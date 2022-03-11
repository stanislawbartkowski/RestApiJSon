import com.rest.readjson.RestError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;
import com.rest.runjson.ConvertRes;

import static org.junit.Assert.assertEquals;

public class Test12 extends PTestRestHelper {

    @Test
    public void test1() throws RestError, IOException {
        P("Test rename JSON");
        String js = "{\"res\": [{\"a\" : 1}]}";
        JSONObject o = new JSONObject(js);
        ConvertRes.rename(o, "res", "data");
        System.out.println(o.toString());
        JSONArray a = (JSONArray) o.get("data");
        System.out.println(a);
        assertEquals(a.length(), 1);
    }

    @Test
    public void test2() throws RestError, IOException {
        P("Test rename JSON but there is no res");
        String js = "{\"xxx\": [{\"a\" : 1}]}";
        JSONObject o = new JSONObject(js);
        ConvertRes.rename(o, "res", "data");
        System.out.println(o.toString());
        JSONArray a = (JSONArray) o.get("xxx");
        System.out.println(a);
        assertEquals(a.length(), 1);
    }

    @Test
    public void test3() throws RestError, IOException {
        P("Test rename JSON but res is not array");
        String js = "{\"res\": 99}";
        JSONObject o = new JSONObject(js);
        ConvertRes.rename(o, "res", "data");
        System.out.println(o.toString());
        int i = o.getInt("res");
        assertEquals(i, 99);
    }

}