package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.ParamValue;
import com.rest.restservice.RestParams;
import com.rest.runjson.IRunPlugin;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetResourceExecutor implements IRunPlugin {

    private final static String resurceP = "resource";
    private final static String actionDir = "dir";
    private final IRestConfig iconfig;
    private final static String resdir = "resdir";

    private Helper.ListPaths rootdirlist;

    private static Map<IRestActionJSON.FORMAT, String> extMap = new HashMap<IRestActionJSON.FORMAT, String>();
    static {
        extMap.put(IRestActionJSON.FORMAT.JSON,"json");
        extMap.put(IRestActionJSON.FORMAT.TEXT,"txt");
        extMap.put(IRestActionJSON.FORMAT.JS,"js");
        extMap.put(IRestActionJSON.FORMAT.XML,"xml");
        extMap.put(IRestActionJSON.FORMAT.ZIP,"zip");
    }

    @Inject
    public GetResourceExecutor(IRestConfig iconfig) {
        this.iconfig = iconfig;
    }

    @Override
    public void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError {
        par.addParam(resurceP, PARAMTYPE.STRING);
    }

    @Override
    public  void verifyProperties() throws RestError {
        String resourcerootdir = Helper.getValue(iconfig.prop(), resdir, true).get();
        rootdirlist = new Helper.ListPaths(resourcerootdir);
    }


    @Override
    public String getActionParam() {
        return actionDir;
    }

    @Override
    public boolean alwaysString() {
        return true;
    }

    @Override
    public void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError {
        String dir = j.action();
//        String path = Helper.getValue(iconfig.prop(), dir, true).get();
        ParamValue resourceP = values.get(resurceP);
        if (resourceP == null) Helper.throwSevere(resurceP + " not specified in the list of values");
        String resource = resourceP.getStringvalue();
        boolean json = j.format() == IRestActionJSON.FORMAT.JSON;
        String ext = extMap.get(j.format());
        if (ext == null) {
            Helper.throwSevere(j.format() + " is not expected as resource");
        }
        String resourcepath = new File(dir,resource + '.' + ext).getPath();
        Optional<Path> resourceF = rootdirlist.getPath(resourcepath, true);
        res.res = Helper.readTextFile(resourceF.get());
    }
}
