package com.rest.conf;

import com.rest.restservice.RestHelper;
import com.rest.restservice.RestLogger;
import com.rest.service.RestGet;
import com.sun.net.httpserver.HttpServer;

public class RegisterGet {

    private RegisterGet() {

    }

    private static final String GET="get";

    public static void  RegisterGetService(IRestConfig i, HttpServer server) {
        i.prop().forEach((key,value) -> {
            String k = key.toString();
            if (k.startsWith(GET)) {
                String v = value.toString();
                String elem[] = v.split(":");
                if (elem.length != 2) {
                    RestLogger.L.severe(String.format("%s %s - key value should contain 2 parts separated by :, found %d", k,v,elem.length));
                }
                else {
                    String dest = elem[0].strip();
                    String paths[] = elem[1].split(",");
                    for (String url : paths) RestHelper.registerService(server,new RestGet(url.strip(),dest));
                }
            }
        });

    }
}
