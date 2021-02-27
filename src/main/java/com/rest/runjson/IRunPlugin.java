package com.rest.runjson;

import com.rest.conf.IRestConfig;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import org.json.JSONObject;
import com.rest.restservice.ParamValue;

import java.io.File;
import java.util.Map;

public interface IRunPlugin {

    String TMPFILE = "TMPFILE";

    class RunResult {
        public File tempfile;
        public String res;
        public JSONObject json;
    }

    default void verifyProperties(IRestConfig conf) throws RestError {
    }

    void executeJSON(IRestActionJSON j, IRestConfig conf, RunResult res, Map<String,ParamValue> values) throws RestError;
}
