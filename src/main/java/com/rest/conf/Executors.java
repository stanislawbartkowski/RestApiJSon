package com.rest.conf;

import com.google.inject.Inject;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.runjson.IRunPlugin;

import java.util.HashMap;
import java.util.Map;

public class Executors {
    private final Map<String, IRunPlugin> imap = new HashMap<String, IRunPlugin>();

    final private IRestConfig rConfig;

    @Inject
    Executors(IRestConfig rConfig) {

        this.rConfig = rConfig;
    }

    public void registerExecutor(String method, IRunPlugin i) throws RestError {
        i.verifyProperties();
        imap.put(method, i);
    }

    public IRunPlugin getExecutor(IRestActionJSON j) throws RestError {
        IRunPlugin irun = imap.get(j.getProc());
        if (irun == null) {
            String errmess = j.getJsonPath().toString() + " " + j.getProc() + " not implemented";
            Helper.throwSevere(errmess);
        }
        return irun;
    }

}
