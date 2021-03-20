package com.rest.service;

import com.google.inject.Inject;
import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.*;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestService extends RestHelper.RestServiceHelper {

    public interface IEnhancer {
        void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError;

        void modifValues(IRestActionJSON irest, Map<String, ParamValue> v) throws RestError;
    }

    private final IRestConfig iconfig;
    private final boolean corsallowed = true;
    private IRestActionJSON irest = null;

    private final IEnhancer in;
    private final RestRunJson run;
    private final RestActionJSON restJSON;
    final private Executors exec;

    @Inject
    public RestService(IRestConfig iconfig, IEnhancer in, RestActionJSON restJSON, RestRunJson run, Executors exec) {
        super("", false);
        this.in = in;
        this.iconfig = iconfig;
        this.restJSON = restJSON;
        this.run = run;
        this.exec = exec;
    }

    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {
        String[] path = getPath(httpExchange);
        String name = path[0];
        RestLogger.info("Rest method " + name);
        try {
            // .json
            String fname = name + ".json";
            Optional<Path> p = iconfig.getJSonDirPaths().getPath(fname,true);

            String meth = httpExchange.getRequestMethod();
            irest = restJSON.readJSONAction(p.get(), meth);

            List<String> aMethods = new ArrayList<>();
            aMethods.add(irest.getMethod().toString());
            Optional<RestParams.CONTENT> out = Optional.empty();
            switch (irest.format()) {
                case JSON:
                    out = Optional.of(RestParams.CONTENT.JSON);
                    break;
                case TEXT:
                    out = Optional.of(RestParams.CONTENT.TEXT);
                    break;
                case ZIP:
                    out = Optional.of(RestParams.CONTENT.ZIP);
                    break;
            }
            RestParams par = new RestParams(irest.getMethod().toString(), out, corsallowed, aMethods, Optional.empty(), irest.isUpload());
            irest.getParams().stream().forEach(s -> {
                par.addParam(s.getName(), s.getType());
            });
            IRunPlugin irun = exec.getExecutor(irest);
            irun.modifPars(irest, path, par);
            if (in != null) {
                in.modifPars(irest, path, par);
            }
            return par;
        } catch (RestError restError) {
            throw new IOException(restError.getMessage());
        }
    }

    @Override
    public synchronized void servicehandle(RestHelper.IQueryInterface v) throws IOException {
        try {
            if (in != null) {
                in.modifValues(irest, v.getValues());
            }
            Optional<File> tempupload = Optional.empty();
            if (irest.isUpload()) {
                File temp = File.createTempFile("upload", null);
                tempupload = Optional.of(temp);
                try (FileOutputStream fos = new FileOutputStream(temp)) {
                    fos.write(v.getRequestData().array());
                }
            }
            RestRunJson.IReturnValue ires = run.executeJson(irest, tempupload, v.getValues());
            if (tempupload.isPresent()) tempupload.get().delete();
            if (ires.ByteValue() != null)
                produceByteResponse(v, Optional.of(ires.ByteValue()), RestHelper.HTTPOK, Optional.empty());
            else if (ires.StringValue().equals("")) produceNODATAResponse(v);
            else produceOKResponse(v, ires.StringValue());
        } catch (RestError restError) {
            throw new IOException(restError.getMessage());
        }
    }

}
