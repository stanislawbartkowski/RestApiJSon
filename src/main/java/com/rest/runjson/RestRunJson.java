package com.rest.runjson;

import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.restservice.RestLogger;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class RestRunJson {

    static private final Map<Integer, IRunPlugin> imap = new HashMap<Integer, IRunPlugin>();

    static private IRestConfig rConfig;

    public static void setRestConfig(IRestConfig r) {
        rConfig = r;
    }

    public static void registerExecutor(int action, IRunPlugin i) throws RestError {
        i.verifyProperties(rConfig);
        imap.put(action, i);
    }

    public static String executeJson(IRestActionJSON j, Map<String, ParamValue> values) throws RestError {

        IRunPlugin irun = imap.get(j.getProc());
        if (irun == null) {
            String errmess = j.getJsonPath().toString() + " " + j.getProc() + " not implemented";
            Helper.throwSevere(errmess);
        }
        IRunPlugin.RunResult res = new IRunPlugin.RunResult();
        res.tempfile = null;
        res.res = null;
        res.json = null;
        boolean tempfile = j.output() == IRestActionJSON.OUTPUT.TMPFILE;
        boolean json = j.format() == IRestActionJSON.FORMAT.JSON;
        if (tempfile) {
            try {
                res.tempfile = File.createTempFile("rst", json ? ".json" : ".txt");
                values.put(IRunPlugin.TMPFILE, new ParamValue(res.tempfile.toString()));
            } catch (IOException e) {
                throw new RestError(e.getMessage());
            }
        }
        irun.executeJSON(j, rConfig, res, values);
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
