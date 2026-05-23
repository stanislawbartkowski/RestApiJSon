import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
        assertEquals(a.length(), 4);
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(1.0));
        String res = runJSON("testsql1.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(a.length(), 1);
    }

    @Test
    public void test3() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(3));
        String res = runJSON("testsql2.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(a.length(), 2);
    }

    @Test
    public void test4() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(3));
        String res = runJSON("testsql4.json", values);
        P(res);
        JSONArray a = getA(res);
        JSONObject elem = (JSONObject) a.get(0);
        P(elem.toString());
        Object o = elem.get("num");
        // verify is BigDecimal
        assertTrue(o instanceof BigDecimal);
    }


}
