import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.guice.ModuleBuild;
import com.rest.guice.RestConfigFactory;
import com.rest.guice.rest.RegisterExecutors;
import com.rest.guice.rest.SetInjector;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

abstract public class TestHelper {

    private static final String jdir1 = "src/test/resources/jdir1";
    private static final String jdir2 = "src/test/resources/jdir2";
    private static final String jdir3 = "src/test/resources/jdir3";
    private static final String jdir4 = "src/test/resources/jdir4";
    private static final String jdir5 = "src/test/resources/jdir5";
    private static final String jdir6 = "src/test/resources/jdir6";
    private static final String jdirresou = "src/test/resources/jresoudir";
    protected static final String jdir17 = "src/test/resources/jdir17";

    private static final String testpar1 = "src/test/resources/testpar";

    private RestActionJSON rest;
    protected RestRunJson run;
    protected IRestConfig iconfig;
    protected Executors exec;

    protected void init(String p) throws RestError {
        SetInjector.setInjector();
        RestConfigFactory.setInstance(getPathPar(p), Optional.empty());
        run = ModuleBuild.getI().getInstance(RestRunJson.class);
        iconfig = ModuleBuild.getI().getInstance(IRestConfig.class);
        exec = ModuleBuild.getI().getInstance(Executors.class);
    }

    protected void getrest() {
        rest = ModuleBuild.getI().getInstance(RestActionJSON.class);
    }

    protected void registerEmpty() throws RestError {
        IRunPlugin i = new IRunPlugin() {
            @Override
            public void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError {

            }
        };
        for (String s : iconfig.listOfPlugins()) exec.registerExecutor(s, i);
        exec.registerExecutor(IRestActionJSON.SQL, i);
        rest = ModuleBuild.getI().getInstance(RestActionJSON.class);
    }

    protected void initnomore(String prop) throws RestError {
        init(prop);
        getrest();
        registerEmpty();
    }

    protected void initno() throws RestError {
        initnomore("testinit.properties");
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

    Path getPath5(String j) {
        return Paths.get(jdir5, j);
    }

    Path getPath6(String j) {
        return Paths.get(jdir6, j);
    }

    Path getPath17(String j) {
        return Paths.get(jdir17, j);
    }

    Path getPathPar(String j) {
        return Paths.get(testpar1, j);
    }

    Path getPathResource(String j) {
        return Paths.get(jdirresou, j);
    }

    JSONArray getA(String s) {
        JSONObject o = new JSONObject(s);
        return o.optJSONArray("res");
    }

    protected JSONObject getJ(String s) {
        return new JSONObject(s);
    }

    protected IRestActionJSON readJSONAction(Path path) throws RestError {
        return rest.readJSONAction(path);
    }

    String runJSON(String testpar, Path pp, Map<String, ParamValue> values) throws RestError {

        init(testpar);
        RegisterExecutors.registerExecutors(IRestActionJSON.SQL);
        getrest();
        P(pp.toString());
        IRestActionJSON j = readJSONAction(pp);
        RestRunJson.IReturnValue ires = run.executeJson(j, Optional.empty(), values);
        return ires.StringValue();
    }

}
