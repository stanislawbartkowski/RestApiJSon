package com.rest.service;

import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.readjson.RestActionJSON;
import com.rest.restservice.RestHelper;
import com.rest.restservice.RestParams;
import com.rest.restservice.RestStart;
import com.rest.runjson.RestRunJson;
import com.sun.net.httpserver.HttpExchange;
import org.javatuples.Pair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class RestVersion extends RestHelper.RestServiceHelper {

    public RestVersion() {
        super("restversion", false);
    }


    private static List<Pair<String, String>> vert = new ArrayList<>();

    public static void addVer(String key, String ver) {
        vert.add(Pair.with(key, ver));
    }

    static {
        addVer("restver", RestStart.VERSTRING);
        addVer("jsonapiver", IRestConfig.VERSION);
    }


    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {
        return new RestParams(RestHelper.GET, Optional.of(RestParams.CONTENT.JSON), true, new ArrayList<String>());
    }

    @Override
    public void servicehandle(RestHelper.IQueryInterface v) throws IOException, InterruptedException {
        JSONObject o = new JSONObject();
        for (Pair<String, String> ve : vert) {
            o.put(ve.getValue0(), ve.getValue1());

        }
        produceOKResponse(v, o.toString());
    }
}
