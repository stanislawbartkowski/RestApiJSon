package com.rest.runjson.executors;

import com.rest.readjson.IRestActionJSON;

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
