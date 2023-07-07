package com.rest.runjson.executors;

import com.rest.readjson.Helper;
import com.rest.readjson.HelperJSon;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.ParamValue;
import com.rest.restservice.RestParams;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetResourceExecutor extends AbstractResourceDirExecutor {

    private final static String resurceP = "resource";

    private static Map<IRestActionJSON.FORMAT, String> extMap = new HashMap<IRestActionJSON.FORMAT, String>();

    static {
        extMap.put(IRestActionJSON.FORMAT.JSON, IRestActionJSON.JSONEXT);
        extMap.put(IRestActionJSON.FORMAT.TEXT, "txt");
        extMap.put(IRestActionJSON.FORMAT.JS, "js");
        extMap.put(IRestActionJSON.FORMAT.XML, "xml");
        extMap.put(IRestActionJSON.FORMAT.ZIP, "zip");
    }

    @Override
    public void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError {
        par.addParam(resurceP, PARAMTYPE.STRING);
    }

    @Override
    public void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError {
        String dir = j.action();
        ParamValue resourceP = values.get(resurceP);
        if (resourceP == null) Helper.throwSevere(resurceP + " not specified in the list of values");
        String resource = resourceP.getStringvalue();
        String ext = extMap.get(j.format());
        if (ext == null) {
            Helper.throwSevere(j.format() + " is not expected as resource");
        }
        boolean isjson = j.format() == IRestActionJSON.FORMAT.JSON;
        String fileName = isjson ? resource : resource + '.' + ext;
        String resourcepath = new File(dir, fileName).getPath();
        // do not force for json
        if (!isjson) {
            Optional<Path> resourceF = rootdirlist.getPath(resourcepath, Optional.empty());
            res.res = Helper.readTextFile(resourceF.get());
        } else res.res = HelperJSon.readJS(rootdirlist, resourcepath, Helper.authLabel(values)).toString();
    }
}
