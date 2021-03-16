import com.rest.guice.rest.RegisterExecutors;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.RestRunJson;
import org.json.JSONArray;
import org.junit.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class Test4 extends TestHelper {

    String runJSON(String f, Map<String, ParamValue> values) throws RestError {

        init("testpar4.properties");
        RegisterExecutors.registerExecutors(IRestActionJSON.SQL);
        Path pp = getPath4(f);
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        RestRunJson.IReturnValue ires = run.executeJson(j, Optional.empty(),values);
        return ires.StringValue();
    }

    @Test
    public void test1() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testsql.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(4,a.length());
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(1.0));
        String res = runJSON("testsql1.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(1,a.length());
    }

    @Test
    public void test3() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("id", new ParamValue(3));
        String res = runJSON("testsql2.json", values);
        P(res);
        JSONArray a = getA(res);
        P(a.toString());
        assertEquals(2,a.length());
    }

}
