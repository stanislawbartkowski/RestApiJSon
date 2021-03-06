package com.rest.runjson;

import com.rest.conf.IRestConfig;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestParams;
import com.rest.service.RestService;
import org.json.JSONObject;
import com.rest.restservice.ParamValue;

import java.io.File;
import java.util.Map;

public interface IRunPlugin  {

    String TMPFILE = "TMPFILE";
    String UPLOADEDFILE = "UPLOADEDFILE";
    String PARAMACTION = "action";

    class RunResult {
        public File tempfile;
        public String res;
        public JSONObject json;
    }

    default void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError {
    }

    default String getActionParam() {
        return PARAMACTION;
    }

    default void verifyProperties() throws RestError { }

    default boolean alwaysString() { return false; }

    void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError;
}
