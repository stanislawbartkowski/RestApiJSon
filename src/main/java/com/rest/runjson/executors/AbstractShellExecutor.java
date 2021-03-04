package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.RestLogger;
import com.rest.runjson.IRunPlugin;

import java.io.*;
import java.util.Map;
import java.util.Optional;

import com.rest.restservice.ParamValue;


abstract class AbstractShellExecutor implements IRunPlugin {

    private final String paramKey;
    private static File shellhome;

    @Inject private IRestConfig conf;

    AbstractShellExecutor(String paramKey) {
        this.paramKey = paramKey;
    }

    abstract String createCmd(IRestActionJSON j);

    @Override
    public void verifyProperties() throws RestError {
        shellhome = new File(Helper.getValue(conf.prop(),paramKey,true).get());
    }

    private static String readOutput(InputStream i) throws IOException {
        StringBuilder output = new StringBuilder();
        BufferedReader buf = new BufferedReader(new InputStreamReader(i));
        String line;
        while ((line = buf.readLine()) != null) {
            if (output.length() != 0) output.append('\n');
            output.append(line);
        }
        return output.toString();
    }

    @Override
    public void executeJSON(IRestActionJSON j, IRunPlugin.RunResult rres, Map<String, ParamValue> values) throws RestError {

        beforeExecute(j,rres,values);

        String cmd = createCmd(j);
        Runtime run = Runtime.getRuntime();
        try {
            String[] env = new String[values.size()];
            int i = 0;
            for (Map.Entry<String,ParamValue> s : values.entrySet()) {
                Optional<IRestActionJSON.IRestParam> pa = j.getParams().stream().filter(e -> e.getName().equals(s.getKey())).findFirst();
                String vals = pa.isPresent() ? Helper.ParamValueToS(pa.get().getType(),s.getValue()) : s.getValue().getStringvalue();
                env[i] = s.getKey() + "=" + vals;
                i++;
            }
            Process pr = run.exec(cmd,env,shellhome);
            pr.waitFor();
            if (pr.exitValue() != 0) {
                String res = readOutput(pr.getErrorStream());
                RestLogger.info(res);
                String errmess = cmd + " non zero exit value " + pr.exitValue();
                Helper.throwSevere(errmess);
            }
            rres.res = readOutput(pr.getInputStream());
        } catch (IOException e) {
            Helper.throwException("Error during execution of " + cmd, e);
        } catch (InterruptedException e) {
            Helper.throwException("Error during execution of " + cmd, e);
        }
    }

}
