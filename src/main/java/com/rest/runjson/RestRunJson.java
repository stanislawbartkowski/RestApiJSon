package com.rest.runjson;

import com.google.inject.Inject;
import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public class RestRunJson {

    public interface IReturnValue {
        byte[] ByteValue();

        String StringValue();
    }

    final private IRestConfig rConfig;

    final private Executors executors;

    @Inject
    RestRunJson(IRestConfig rConfig, Executors executors) {

        this.rConfig = rConfig;
        this.executors = executors;
    }

    public IReturnValue executeJson(IRestActionJSON j, Optional<File> uploaded, Map<String, ParamValue> values) throws RestError {

        IRunPlugin irun = executors.getExecutor(j);
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

        if (json) {
            if (res.json == null) {
                if (res.res == null) {
                    String errmess = j.getJsonPath().toString() + " expected JSON result";
                    Helper.throwSevere(errmess);
                }
            } else res.res = res.json.toString();
        }
        if (res.res == null) {
            String errmess = j.getJsonPath().toString() + " expected " + j.format().toString() + " result";
            Helper.throwSevere(errmess);
        }
        VerifyResult.verifyResult(res.res, j.format());
        // clear the result
        if (tempfile) res.tempfile.delete();
        // convert all to string
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