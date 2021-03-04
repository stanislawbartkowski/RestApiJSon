package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rest.readjson.IRestActionJSON;
import com.rest.runjson.IRunPlugin;

public class Python3Executor extends AbstractShellExecutor {

    private final static String PYTHONHOME="pythonhome";

    public Python3Executor() {
        super(PYTHONHOME);
    }

    @Override
    String createCmd(IRestActionJSON j) {
        return "python3 " + j.action();
    }
}
