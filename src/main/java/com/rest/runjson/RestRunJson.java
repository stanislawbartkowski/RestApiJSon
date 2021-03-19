package com.rest.runjson;

import com.google.inject.Inject;
import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

public class RestRunJson {

    final private IRestConfig rConfig;
    final private Executors exec;

    @Inject
    RestRunJson(IRestConfig rConfig, Executors executors) {

        this.rConfig = rConfig;
        this.exec = executors;
    }

    public String executeJson(IRestActionJSON j, Map<String, ParamValue> values) throws RestError {

        IRunPlugin irun = exec.getExecutor(j);
        IRunPlugin.RunResult res = new IRunPlugin.RunResult();
        res.tempfile = null;
        res.res = null;
        res.json = null;
        boolean tempfile = j.output() == IRestActionJSON.OUTPUT.TMPFILE;
        boolean json = j.format() == IRestActionJSON.FORMAT.JSON;
        if (tempfile) {
            res.tempfile = Helper.createTempFile(json);
            values.put(IRunPlugin.TMPFILE, new ParamValue(res.tempfile.toString()));
        }
        irun.executeJSON(j, res, values);
        if (tempfile) {
            res.res = Helper.readTextFile(res.tempfile.toPath());
            if (res.res.equals("")) {
                String errmess = j.getJsonPath().toString() + " " + res.tempfile.toString() + " Expected result in temporary file but the file is empty";
                Helper.throwSevere(errmess);
            }
        }
        if (res.json == null && json) {
            if (res.res == null) {
                String errmess = j.getJsonPath().toString() + " expected JSON result";
                Helper.throwSevere(errmess);
            }
            // verify JSON
            try {
                JSONTokener tokener = new JSONTokener(res.res);
                res.json = new JSONObject(tokener);
            } catch (org.json.JSONException e) {
                Helper.throwException(res.res, e);
            }
        }
        if (!json && res.res == null) {
            String errmess = j.getJsonPath().toString() + " expected TEXT result";
            Helper.throwSevere(errmess);
        }
        // clear the result
        if (tempfile) res.tempfile.delete();
        // convert all to string
        if (res.res == null) res.res = res.json.toString();
        return res.res;
    }

}
