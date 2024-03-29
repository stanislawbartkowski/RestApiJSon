package com.rest.service;

import com.rest.restservice.RestHelper;
import com.rest.restservice.RestLogger;
import com.rest.restservice.RestParams;
import com.sun.net.httpserver.HttpExchange;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class RestGet extends RestHelper.RestServiceHelper {

    private final String destdir;

    private final String url;

    public RestGet(String url, String destdir) {
        super(url);
        this.destdir = destdir;
        this.url = url;
    }

    @Override
    public RestParams getParams(HttpExchange httpExchange) throws IOException {
        return new RestParams(RestHelper.GET, Optional.empty(), true, new ArrayList<String>());
    }

    private boolean checkpath(RestHelper.IQueryInterface v, File f) throws IOException {
        String s = f.getCanonicalPath();
        File fdest = new File(destdir, url);
        String adest = fdest.getCanonicalPath();
        if (s.startsWith(adest)) return true;
        String errmess = String.format("%s - required path expands outside allowed scope", f.toString());
        RestLogger.info(errmess);
        produceResponse(v, Optional.of(errmess), HttpURLConnection.HTTP_FORBIDDEN);
        return false;
    }

    @Override
    public void servicehandle(RestHelper.IQueryInterface v) throws IOException, InterruptedException {
        String[] path = getPath(v.getT());
        String name = Arrays.stream(path).reduce(null, (s, e) -> s == null ? e : s + "/" + e);
        File f = new File(destdir, name);
        if (!checkpath(v, f)) return;
        RestLogger.info(String.format("GET %s", name));
        byte b[] = Files.readAllBytes(f.toPath());
        produceByteResponse(v, Optional.of(b), RestHelper.HTTPOK, Optional.empty());
    }
}
