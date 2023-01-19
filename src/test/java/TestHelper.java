import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.guice.ModuleBuild;
import com.rest.guice.RestConfigFactory;
import com.rest.guice.rest.RegisterExecutors;
import com.rest.guice.rest.SetInjector;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

abstract public class TestHelper {

    protected static final String jdir1 = "src/test/resources/jdir1";
    protected static final String jdir2 = "src/test/resources/jdir2";
    protected static final String jdir3 = "src/test/resources/jdir3";
    protected static final String jdir4 = "src/test/resources/jdir4";
    protected static final String jdir5 = "src/test/resources/jdir5";
    protected static final String jdir6 = "src/test/resources/jdir6";
    protected static final String jdirresou = "src/test/resources/jresoudir";
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

    private Pair<String, String> dePath(Path p) {
        String file = p.getFileName().toString();
        String dir = p.subpath(0, p.getNameCount() - 1).toString();
        return Pair.with(dir, file);

    }

    protected IRestActionJSON readJSONAction(String dir, String filename) throws RestError {
        Helper.ListPaths f = new Helper.ListPaths(dir);
        return rest.readJSONAction(f, filename, Optional.empty());
    }

    protected IRestActionJSON readJSONAction(Path p) throws RestError {
        Pair<String, String> pa = dePath(p);
        return readJSONAction(pa.getValue0(), pa.getValue1());
    }


    String runJSON(String testpar, Path p, Map<String, ParamValue> values) throws RestError {

        init(testpar);
        RegisterExecutors.registerExecutors(IRestActionJSON.SQL);
        getrest();
        P(p.toString());
        IRestActionJSON j = readJSONAction(p);
        RestRunJson.IReturnValue ires = run.executeJson(j, Optional.empty(), values, new HashMap<String,String>());
        return ires.StringValue();
    }

}
