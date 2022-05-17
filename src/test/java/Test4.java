import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.json.JSONArray;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class Test4 extends TestHelper {

    private String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        return super.runJSON("testpar4.properties", getPath4(f), values);
    }

    @Test
    public void test1() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testsql.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(4, a.length());
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(1.0));
        String res = runJSON("testsql1.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(1, a.length());
    }

    @Test
    public void test3() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(3));
        String res = runJSON("testsql2.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(2, a.length());
    }

}
