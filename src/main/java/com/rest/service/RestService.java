package com.rest.service;

import com.google.inject.Inject;
import com.rest.auth.IVerifyToken;
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
import java.util.*;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.util.UriEncoder;

public class RestService extends RestHelper.RestServiceHelper {

    public interface IEnhancer {
        void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError;

        void modifValues(IRestActionJSON irest, Map<String, ParamValue> v) throws RestError;
    }

    private final IRestConfig iconfig;
    private final boolean corsallowed = true;
    private IRestActionJSON irest = null;
    private Map<String, String> reqparams;

    private final List<String> httpMethods = Arrays.asList(RestHelper.GET, RestHelper.PUT, RestHelper.POST, RestHelper.DELETE);

    private final IEnhancer in;
    private final RestRunJson run;
    private final RestActionJSON restJSON;
    final private Executors exec;
    final private IVerifyToken verifyToken;

    final private static Map<IRestActionJSON.FORMAT, RestParams.CONTENT> mapf = new HashMap<IRestActionJSON.FORMAT, RestParams.CONTENT>();

    static {
        mapf.put(IRestActionJSON.FORMAT.JS, RestParams.CONTENT.JS);
        mapf.put(IRestActionJSON.FORMAT.JSON, RestParams.CONTENT.JSON);
        mapf.put(IRestActionJSON.FORMAT.TEXT, RestParams.CONTENT.TEXT);
        mapf.put(IRestActionJSON.FORMAT.ZIP, RestParams.CONTENT.ZIP);
        mapf.put(IRestActionJSON.FORMAT.XML, RestParams.CONTENT.XML);
        mapf.put(IRestActionJSON.FORMAT.MIXED, RestParams.CONTENT.MIXED);
        mapf.put(IRestActionJSON.FORMAT.MIXEDBINARY, RestParams.CONTENT.MIXED);
    }

    @Inject
    public RestService(IRestConfig iconfig, IEnhancer in, RestActionJSON restJSON, RestRunJson run, Executors exec, IVerifyToken verifyToken) {
        super("");
        this.in = in;
        this.iconfig = iconfig;
        this.restJSON = restJSON;
        this.run = run;
        this.exec = exec;
        this.verifyToken = verifyToken;
    }

    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {

        if (!verifyToken.verifyToken(httpExchange.getRequestHeaders())) {
            String mess = "Not authorized";
            throw new IOException(mess);
        }
        String[] path = getPath(httpExchange);
        String name = Arrays.stream(path).reduce(null, (s, e) -> s == null ? e : s + "/" + e);
        String meth = httpExchange.getRequestMethod();
        RestLogger.info(String.format("Rest method: %s HTTP method: %s ", name, meth));
        // OPTIONS - info only
        Optional<String> headersAllowed = iconfig.getAllowedReqs() == null ? Optional.empty() : Optional.of(iconfig.getAllowedReqs());
        if (RestHelper.OPTIONS.equals(meth))
            return new RestParams(meth, Optional.empty(), corsallowed, httpMethods, headersAllowed, false);
        try {
            irest = restJSON.readJSONAction(iconfig.getJSonDirPaths(), name + "-" + meth.toLowerCase(), Optional.of(name));

            RestParams.CONTENT fo = mapf.get(irest.format());
            Optional<RestParams.CONTENT> out = fo == null ? Optional.empty() : Optional.of(fo);

            RestParams par = new RestParams(irest.getMethod().toString(), out, corsallowed, httpMethods, headersAllowed, irest.isUpload());
            irest.getParams().stream().forEach(s -> {
                par.addParam(s.getName(), s.getType());
            });
            IRunPlugin irun = exec.getExecutor(irest);
            irun.modifPars(irest, path, par);
            if (in != null) {
                in.modifPars(irest, path, par);
            }
            // request params
            reqparams = new HashMap<String, String>();
            if (iconfig.getAllowedReqs() != null) {
                Set<String> s = Arrays.stream(iconfig.getAllowedReqs().split(",")).collect(Collectors.toSet());
                for (String key : httpExchange.getRequestHeaders().keySet()) {
                    String k = key.toLowerCase();
                    if (s.contains(k)) {
                        List<String> li = httpExchange.getRequestHeaders().get(key);
                        if (!li.isEmpty()) {
                            String reqparam = UriEncoder.decode(li.get(0));
                            reqparams.put(k, reqparam);
                        }
                    }
                }

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
            long start = System.currentTimeMillis();
            RestRunJson.IReturnValue ires = run.executeJson(irest, tempupload, v.getValues(), reqparams);
            long finish = System.currentTimeMillis();
            RestLogger.info(String.format("Execution time %.3g sec", (float) (finish - start) / 1000));

            if (tempupload.isPresent()) tempupload.get().delete();
            if ((ires.secondPart() != null) || (ires.secondBytePart() != null)) {
                String res = ires.fileValue().isPresent() ? Helper.readTextFile(ires.fileValue().get().toPath()) : ires.StringValue();
                if (ires.fileValue().isPresent()) ires.fileValue().get().delete();
                if (ires.secondPart() != null)
                    produce2PartResponse(v, Optional.of(res), Optional.of(ires.secondPart()), RestHelper.HTTPOK, Optional.empty());
                if (ires.secondBytePart() != null)
                    produce2PartByteResponse(v, Optional.of(res), Optional.of(ires.secondBytePart()), RestHelper.HTTPOK, Optional.empty());
                return;
            }
            if (ires.fileValue().isPresent()) {
                produceResponseFromFile(v, ires.fileValue().get(), true, RestHelper.HTTPOK, Optional.empty());
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
