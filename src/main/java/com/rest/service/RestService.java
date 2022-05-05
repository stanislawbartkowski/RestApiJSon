package com.rest.service;

import com.google.inject.Inject;
import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

public class RestService extends RestHelper.RestServiceHelper {

    public interface IEnhancer {
        void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError;

        void modifValues(IRestActionJSON irest, Map<String, ParamValue> v) throws RestError;
    }

    private final IRestConfig iconfig;
    private final boolean corsallowed = true;
    private IRestActionJSON irest = null;

    private final List<String> httpMethods = Arrays.asList(RestHelper.GET, RestHelper.PUT, RestHelper.POST, RestHelper.DELETE);

    private final IEnhancer in;
    private final RestRunJson run;
    private final RestActionJSON restJSON;
    final private Executors exec;

    final private static Map<IRestActionJSON.FORMAT, RestParams.CONTENT> mapf = new HashMap<IRestActionJSON.FORMAT, RestParams.CONTENT>();

    static {
        mapf.put(IRestActionJSON.FORMAT.JS, RestParams.CONTENT.JS);
        mapf.put(IRestActionJSON.FORMAT.JSON, RestParams.CONTENT.JSON);
        mapf.put(IRestActionJSON.FORMAT.TEXT, RestParams.CONTENT.TEXT);
        mapf.put(IRestActionJSON.FORMAT.ZIP, RestParams.CONTENT.ZIP);
        mapf.put(IRestActionJSON.FORMAT.XML, RestParams.CONTENT.XML);
        mapf.put(IRestActionJSON.FORMAT.MIXED, RestParams.CONTENT.MIXED);
    }

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
        String name = Arrays.stream(path).reduce(null, (s, e) -> s == null ? e : s + "/" + e);
        String meth = httpExchange.getRequestMethod();
        RestLogger.info("Rest method " + path + " HTTP method " + meth);
        // OPTIONS - info only
        if (RestHelper.OPTIONS.equals(meth))
            return new RestParams(meth, Optional.empty(), corsallowed, httpMethods, Optional.empty(), false);
        try {
            // .json or yaml
            // firstly looks for _method then default
            Optional<Path> p = iconfig.getJSonDirPaths().getPath(name + "-" + meth.toLowerCase(), Optional.of(name));

            irest = restJSON.readJSONAction(p.get());

            RestParams.CONTENT fo = mapf.get(irest.format());
            Optional<RestParams.CONTENT> out = fo == null ? Optional.empty() : Optional.of(fo);

            RestParams par = new RestParams(irest.getMethod().toString(), out, corsallowed, httpMethods, Optional.empty(), irest.isUpload());
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
    public void servicehandle(RestHelper.IQueryInterface v) throws IOException {
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
            if (ires.secondPart() != null) {
                produce2PartResponse(v, Optional.of(ires.StringValue()), Optional.of(ires.secondPart()), RestHelper.HTTPOK, Optional.empty());
                return;
            }
            if (ires.ByteValue() != null) {
                produceByteResponse(v, Optional.of(ires.ByteValue()), RestHelper.HTTPOK, Optional.empty());
                return;
            }
            if (ires.StringValue().equals("")) produceNODATAResponse(v);
            else produceOKResponse(v, ires.StringValue());
        } catch (RestError restError) {
            throw new IOException(restError.getMessage());
        }
    }

}
