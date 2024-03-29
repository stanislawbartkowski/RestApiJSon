package com.rest.runjson;

import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestParams;
import org.json.JSONObject;
import com.rest.restservice.ParamValue;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public interface IRunPlugin {

    String TMPFILE = "TMPFILE";
    String CONTENTTEMP = "CONTENTFILE";
    String UPLOADEDFILE = "UPLOADEDFILE";
    String PARAMACTION = "action";

    String REQPARAM = "REQUEST_";

    String REQAUTHLABEL = REQPARAM + "authlabel";

    class RunResult {
        public File tempfile;
        public String res;
        public JSONObject json;
        public File contenfile;
        public String content;
        public byte[] bytecontent;

        public Optional<File> fileContent = Optional.empty();
    }

    default void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError {
    }

    default String getActionParam() {
        return PARAMACTION;
    }

    default void verifyProperties() throws RestError {
    }

    default boolean alwaysString() {
        return false;
    }

    void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError;
}
