import com.rest.conf.IRestConfig;
import com.rest.guice.rest.ModuleBuild;
import com.rest.guice.RestConfigFactory;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.runjson.RestRunJson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

abstract public class TestHelper {

    private static final String jdir1 = "src/test/resources/jdir1";
    private static final String jdir2 = "src/test/resources/jdir2";
    private static final String jdir3 = "src/test/resources/jdir3";
    private static final String jdir4 = "src/test/resources/jdir4";

    private static final String testpar1="src/test/resources/testpar";

    private RestActionJSON rest;
    protected RestRunJson run;

    protected void init(String p) throws RestError {
        RestConfigFactory.setInstance(getPathPar(p));
        rest = ModuleBuild.getI().getInstance(RestActionJSON.class);
        run = ModuleBuild.getI().getInstance(RestRunJson.class);
    }

    protected void initno() throws RestError {
        RestConfigFactory.setInstance(getPathPar("testinit.properties"));
        rest = ModuleBuild.getI().getInstance(RestActionJSON.class);
        run = ModuleBuild.getI().getInstance(RestRunJson.class);
    }



    void P(String s) {
        System.out.println(s);
    }

    Path getPath1(String j) {
        return Paths.get(jdir1, j);
    }

    Path getPath2(String j) {
        return Paths.get(jdir2, j);
    }

    Path getPath3(String j) {
        return Paths.get(jdir3, j);
    }

    Path getPath4(String j) {
        return Paths.get(jdir4, j);
    }

    Path getPathPar(String j) {
        return Paths.get(testpar1, j);
    }

    JSONArray getA(String s) {
        JSONObject o = new JSONObject(s);
        return o.optJSONArray("res");
    }

    protected IRestActionJSON readJSONAction(Path path) throws RestError {
        return rest.readJSONAction(path, "GET");
    }
}
