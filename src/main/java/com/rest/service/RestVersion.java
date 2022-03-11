package com.rest.service;

import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.readjson.RestActionJSON;
import com.rest.restservice.RestHelper;
import com.rest.restservice.RestParams;
import com.rest.restservice.RestStart;
import com.rest.runjson.RestRunJson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class RestVersion extends RestHelper.RestServiceHelper {

    public RestVersion() {
        super("restversion", false);
    }


    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {
        return new RestParams(RestHelper.GET, Optional.of(RestParams.CONTENT.JSON), true, new ArrayList<String>());
    }

    @Override
    public void servicehandle(RestHelper.IQueryInterface v) throws IOException, InterruptedException {
        String ver = String.format("{\"restver\": \"%s\", \"jsonapiver\": \"%s\"}", IRestConfig.VERSION, RestStart.VERSTRING);
        produceOKResponse(v, ver);
    }
}
