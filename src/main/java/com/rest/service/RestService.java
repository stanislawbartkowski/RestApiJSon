package com.rest.service;

import com.rest.conf.IRestConfig;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.*;
import com.rest.runjson.RestRunJson;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestService extends RestHelper.RestServiceHelper {

    private final IRestConfig iconfig;
    private final boolean corsallowed = true;
    private IRestActionJSON irest = null;

    public RestService(IRestConfig iconfig) {
        super("", false);
        this.iconfig = iconfig;
    }

    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {
        String[] path = getPath(httpExchange);
        String name = path[0];
        RestLogger.info("Rest method " + name);
        // dodaj rozszerzenie .json
        Path p = new File(iconfig.getJSONDir(), name + ".json").toPath();
        try {
            irest = RestActionJSON.readJSONAction(p);
        } catch (RestError restError) {
            throw new IOException(restError.getMessage());
        }
        List<String> aMethods = new ArrayList<>();
        aMethods.add(RestHelper.GET);
        aMethods.add(RestHelper.PUT);
        RestParams par = new RestParams(irest.getMethod().toString(),
                (irest.format() == IRestActionJSON.FORMAT.JSON) ? Optional.of(RestParams.CONTENT.JSON) : irest.format() == IRestActionJSON.FORMAT.TEXT ? Optional.of(RestParams.CONTENT.TEXT) : Optional.empty(),
                corsallowed, aMethods);
        irest.getParams().stream().forEach(s -> {
            par.addParam(s.getName(), s.getType());
        });
        return par;
    }

    @Override
    public synchronized void servicehandle(RestHelper.IQueryInterface v) throws IOException {
        try {
            String res = RestRunJson.executeJson(irest, v.getValues());
            if (res.equals("")) produceNODATAResponse(v);
            else produceOKResponse(v, res);
        } catch (RestError restError) {
            throw new IOException(restError.getMessage());
        }
    }

}
