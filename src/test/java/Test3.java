
import com.rest.guice.rest.RegisterExecutors;

import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static com.rest.readjson.RestActionJSON.readJSONAction;
import static org.junit.Assert.assertEquals;

public class Test3 extends TestHelper {

    @Test
    public void test1() throws RestError {
        Path p = getPath3("testpython1.json");
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        assertEquals(IRestActionJSON.PYTHON3, j.getProc());
    }

    String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        Path p = getPathPar("testpar3.properties");
        P(p.toString());
        constructIC(p);
        RegisterExecutors.registerExecutors(IRestActionJSON.PYTHON3);
        Path pp = getPath3(f);
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        return R().executeJson(j, values);
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testpython2.json", values);
        P(res);
        assertEquals("{}",res);
    }


}
