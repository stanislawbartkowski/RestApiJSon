package com.rest.runjson;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RestRunJson {

    public interface IReturnValue {
        byte[] ByteValue();

        String StringValue();
    }

    private final Map<String, IRunPlugin> imap = new HashMap<String, IRunPlugin>();

    final private IRestConfig rConfig;

    @Inject
    RestRunJson(IRestConfig rConfig) {

        this.rConfig = rConfig;
    }

    public void registerExecutor(String method, IRunPlugin i) throws RestError {
        i.verifyProperties();
        imap.put(method, i);
    }

    public IReturnValue executeJson(IRestActionJSON j, Optional<File> uploaded, Map<String, ParamValue> values) throws RestError {

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
        boolean zip = j.format() == IRestActionJSON.FORMAT.ZIP;
        if (zip && !tempfile) {
            String errmess = j.getJsonPath().toString() + " " + res.tempfile.toString() + " ZIP output requires temporary file setting";
            Helper.throwSevere(errmess);
        }
        if (tempfile) {
            res.tempfile = Helper.createTempFile(json);
            values.put(IRunPlugin.TMPFILE, new ParamValue(res.tempfile.toString()));
        }
        if (uploaded.isPresent()) {
            values.put(IRunPlugin.UPLOADEDFILE, new ParamValue(uploaded.get().toString()));
        }
        irun.executeJSON(j, res, values);

        if (zip) {
            try {
                byte[] zipbytes = Files.readAllBytes(res.tempfile.toPath());
                if (zipbytes.length == 0)
                    Helper.throwSevere(j.getJsonPath().toString() + " ZIP expected, empty file received");
                return new IReturnValue() {
                    @Override
                    public byte[] ByteValue() {
                        return zipbytes;
                    }

                    @Override
                    public String StringValue() {
                        return null;
                    }
                };
            } catch (IOException e) {
                Helper.throwException(j.getJsonPath().toString() + " Error while reading output result", e);
            }
        }

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
        return new IReturnValue() {
            @Override
            public byte[] ByteValue() {
                return null;
            }

            @Override
            public String StringValue() {
                return res.res;
            }
        };
    }

}

//    byte[] zip = Files.readAllBytes(tempfile.toPath());
//    produceByteResponse(v, Optional.of(zip), RestHelper.HTTPOK, Optional.empty());
