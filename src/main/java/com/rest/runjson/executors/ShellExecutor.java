package com.rest.runjson.executors;

import com.rest.readjson.IRestActionJSON;

public class ShellExecutor extends AbstractShellExecutor {

    private final static String SHELLHOME="shellhome";

    public ShellExecutor() {
        super(SHELLHOME);
    }

    @Override
    String createCmd(IRestActionJSON j) {
        return j.action();
    }
}
