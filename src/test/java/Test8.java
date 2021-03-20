import com.google.inject.Inject;
import com.rest.guice.rest.ModuleBuild;
import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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
        assertEquals("super",j.action());
    }

    @Test
    public void test2() throws RestError {
        P("Hello");
        Path p = getPath6("test2.json");
        IRestActionJSON j = readJSONAction(p);
        assertEquals("resdir",j.action());
        IRunPlugin irun = exec.getExecutor(j);
        IRunPlugin.RunResult ires = new IRunPlugin.RunResult();
        Map<String,ParamValue> values = new HashMap<String, ParamValue>();
        values.put("resource",new ParamValue("list"));
        irun.executeJSON(j,ires, values);
        assertEquals("{}",ires.res);
    }

}
