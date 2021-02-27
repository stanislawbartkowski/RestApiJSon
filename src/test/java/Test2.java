import com.rest.conf.ConstructRestConfig;
import com.rest.conf.IRestConfig;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import com.rest.runjson.executors.ShellExecutor;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.rest.readjson.RestActionJSON.readJSONAction;
import static org.junit.Assert.*;

public class Test2 extends TestHelper {

    @Test
    public void test1() throws RestError {
        Path p = getPath2("testshell.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(IRestActionJSON.SHELL, j.getProc());
    }

    @Test
    public void test2() throws RestError {
        Path p = getPathPar("testpar1.properties");
        P(p.toString());
        RestError thrown = assertThrows(
                RestError.class,
                () -> ConstructRestConfig.create(p));
        P(thrown.getMessage());
    }

    String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        Path p = getPathPar("testpar2.properties");
        P(p.toString());
        IRestConfig ic = ConstructRestConfig.create(p);
        RestRunJson.setRestConfig(ic);
        RestRunJson.registerExecutor(IRestActionJSON.SHELL, new ShellExecutor());
        Path pp = getPath2(f);
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        return RestRunJson.executeJson(j, values);
    }

    @Test
    public void test3() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell1.json", values);
        P(res);
        assertEquals("hello", res);
    }

    @Test
    public void test4() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        RestError thrown = assertThrows(
                RestError.class,
                () -> runJSON("testshell2.json", values));
        P(thrown.getMessage());
    }

    @Test
    public void test5() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell3.json", values);
        P(res);
        assertEquals("{}", res);
    }

    @Test
    public void test6() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell4.json", values);
        P(res);
        assertTrue(res.contains("homeshell"));
    }

    @Test
    public void test7() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell5.json", values);
        P(res);
        assertEquals("Hello\n",res);
    }

    @Test
    public void test8() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testshell6.json", values);
        P(res);
        assertEquals("{}\n",res);
    }

    @Test
    public void test9() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        values.put("PARAMS",new ParamValue("hello"));
        String res = runJSON("testshell7.json", values);
        P(res);
        assertEquals("{\"params\":\"hello\"}\n",res);
    }



}
