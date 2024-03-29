
import com.rest.guice.rest.RegisterExecutors;

import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.RestRunJson;
import org.junit.Test;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


import static org.junit.Assert.assertEquals;

public class Test3 extends TestHelper {

    String runJSON(String f, Map<String, ParamValue> values) throws RestError {
        init("testpar3.properties");
        RegisterExecutors.registerExecutors(IRestActionJSON.PYTHON3);
        getrest();
        Path pp = getPath3(f);
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        RestRunJson.IReturnValue ires = run.executeJson(j, Optional.empty(), values, new HashMap<String, String>());
        return ires.StringValue();
    }

    @Test
    public void test2() throws RestError {
        Map<String, ParamValue> values = new HashMap<String, ParamValue>();
        String res = runJSON("testpython2.json", values);
        P(res);
        assertEquals("{}", res);
    }


}
