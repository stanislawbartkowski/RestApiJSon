package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.RestLogger;
import com.rest.runjson.IRunPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.rest.restservice.ParamValue;


abstract class AbstractShellExecutor implements IRunPlugin {

    private final String paramKey;
    private static File shellhome;

    @Inject
    private IRestConfig conf;

    AbstractShellExecutor(String paramKey) {
        this.paramKey = paramKey;
    }

    abstract List<String> createCmd(IRestActionJSON j);

    @Override
    public void verifyProperties() throws RestError {
        shellhome = new File(Helper.getValue(conf.prop(), paramKey, true).get());
    }

    @Override
    public void executeJSON(IRestActionJSON j, IRunPlugin.RunResult rres, Map<String, ParamValue> values) throws RestError {
        List<String> cmd = createCmd(j);
        StringBuffer output = new StringBuffer();
        Map<String, String> env = new HashMap<String, String>();
        for (Map.Entry<String, ParamValue> s : values.entrySet()) {
            Optional<IRestActionJSON.IRestParam> pa = j.getParams().stream().filter(e -> e.getName().equals(s.getKey())).findFirst();
            String vals = pa.isPresent() ? Helper.ParamValueToS(pa.get().getType(), s.getValue()) : s.getValue().getStringvalue();
            env.put(s.getKey(), vals);
        }
        if (j.setEnvir())
            conf.prop().forEach((key, val) -> {
                env.put("ENV_" + key, val.toString());
            });
        try {
            int exitcode = RunShellCommand.run(shellhome, env, output, cmd);
            if (exitcode != 0) {
                String errmess = cmd + " non zero exit value " + exitcode;
                Helper.throwSevere(errmess);
            }
            rres.res = output.toString();
        } catch (IOException | InterruptedException e) {
            Helper.throwException("Error during execution of " + cmd, e);
        }
    }
}