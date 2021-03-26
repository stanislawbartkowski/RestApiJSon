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

public class Test10 extends TestHelper {

    private String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        return super.runJSON("testparresourcerest.properties",getPathResource(f),values);
    }

    @Test
    public void test1() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("getdept.json", values);
        P(res);
        JSONArray a = getA(res);
        assertEquals(14,a.length());
    }

}
