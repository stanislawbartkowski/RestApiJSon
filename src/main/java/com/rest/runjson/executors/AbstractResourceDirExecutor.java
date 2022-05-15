package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.RestError;
import com.rest.runjson.IRunPlugin;

abstract class AbstractResourceDirExecutor implements IRunPlugin {

    @Inject
    protected IRestConfig conf;

    protected final static String actionDir = "dir";

    protected final static String resdir = "resdir";

    protected Helper.ListPaths rootdirlist;

    @Override
    public  void verifyProperties() throws RestError {
        String resourcerootdir = Helper.getValue(conf.prop(), resdir, true).get();
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


}
