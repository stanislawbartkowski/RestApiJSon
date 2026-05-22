import com.rest.guice.rest.RegisterExecutors;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.RestRunJson;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.testng.Assert.*;

public class Test2 extends TestHelper {

    String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        init("testpar2.properties");
        RegisterExecutors.registerExecutors(IRestActionJSON.SHELL);
        getrest();
        Path pp = getPath2(f);
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        RestRunJson.IReturnValue ires = run.executeJson(j, Optional.empty(), values, new HashMap<String, String>());
        if (ires.fileValue().isPresent()) {
            return Helper.readTextFile(ires.fileValue().get().toPath());
        }
        return ires.StringValue();
    }

    @Test
    public void test1() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell1.json", values);
        P(res);
        assertEquals(res, "hello");
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        RestError thrown = expectThrows(
                RestError.class,
                () -> runJSON("testshell2.json", values));
        P(thrown.getMessage());
    }

    @Test
    public void test3() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell3.json", values);
        P(res);
        assertEquals(res, "{}");
    }

    @Test
    public void test4() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell4.json", values);
        P(res);
        assertTrue(res.contains("homeshell"));
    }

    @Test
    public void test5() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell5.json", values);
        P(res);
        assertEquals(res, "Hello\n");
    }

    @Test
    public void test6() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell6.json", values);
        P(res);
        assertEquals(res, "{}\n");
    }

    @Test
    public void test7() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("PARAMS", new ParamValue("hello"));
        String res = runJSON("testshell7.json", values);
        P(res);
        assertEquals(res, "{\"params\":\"hello\"}\n");
    }

}
