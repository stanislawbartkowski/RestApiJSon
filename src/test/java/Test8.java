import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class Test8 extends TestHelper {

    @Before
    public void beforeEachTestMethod() throws IOException, RestError {
        init("testparresource.properties");
        RestMainHelper.registerExecutors(iconfig);
        getrest();
    }

    @Test
    public void test1() throws RestError {
        P("Hello");
        Path p = getPath6("test1.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals("super", j.action());
    }

    private RestRunJson.IReturnValue runResource(String json, String res) throws RestError {
        Path p = getPath6(json);
        IRestActionJSON j = readJSONAction(p);
        assertEquals("resoudir", j.action());
        IRunPlugin irun = exec.getExecutor(j);
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("resource", new ParamValue(res));
        return run.executeJson(j, Optional.empty(), values);
    }

    @Test
    public void test2() throws RestError {
        RestRunJson.IReturnValue ires = runResource("test2.json", "list");
        assertEquals("{}", ires.StringValue());
    }

    @Test
    public void test3() throws RestError {
        RestError thrown = assertThrows(
                RestError.class,
                () -> runResource("test4.json", "codeerr"));
        P(thrown.getMessage());
    }

    @Test
    public void test4() throws RestError {
        RestRunJson.IReturnValue ires = runResource("test4.json", "code");
        P(ires.StringValue());
        assertEquals("var j = \"hello\";", ires.StringValue());
    }

    @Test
    public void test5() throws RestError {
        RestRunJson.IReturnValue ires = runResource("test4.json", "codeo");
        P(ires.StringValue());
    }

    @Test
    public void test6() throws RestError {
        RestRunJson.IReturnValue ires = runResource("test5.json", "cust");
        P(ires.StringValue());
    }

    private void runJson(String res) throws RestError {
        RestRunJson.IReturnValue ires = runResource("test6.json", res);
        P(ires.StringValue());
        JSONObject o = new JSONObject(ires.StringValue());
        String s = o.getString("hello");
        assertEquals("Hello", s);
        JSONArray a = o.optJSONArray("pars");
        assertEquals(2, a.length());
    }


    @Test
    public void test7() throws RestError {
        runJson("listdef");
    }

    @Test
    public void test8() throws RestError {
        runJson("listdefyaml");
    }


}
