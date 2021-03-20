package com.rest.conf;

import com.google.inject.Inject;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.runjson.IRunPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public IRunPlugin getExecutorProc(String proc, String path) throws RestError {
        IRunPlugin irun = imap.get(proc);
        if (irun == null) {
            String errmess = path + " " + proc + " not implemented";
            Helper.throwSevere(errmess);
        }
        return irun;
    }


    public IRunPlugin getExecutor(IRestActionJSON j) throws RestError {
        return getExecutorProc(j.getProc(), j.getJsonPath().toString());
    }

    public Set<String> listActions() {
        return imap.values().stream().map(e -> e.getActionParam()).collect(Collectors.toSet());
    }

    public Set<String> listProcs() {
        return imap.keySet();
    }

    public Set<String> listofAlwaysString() {
        final Set<String> a = new HashSet<String>();
        imap.entrySet().stream().forEach(e -> {
            if (e.getValue().alwaysString()) a.add(e.getKey());
        });
        return a;
    }


}
