package com.rest.runjson.executors;

import com.rest.readjson.IRestActionJSON;

import java.util.List;

public class ShellExecutor extends AbstractShellExecutor {

    private final static String SHELLHOME="shellhome";

    public ShellExecutor() {
        super(SHELLHOME);
    }

    @Override
    List<String> createCmd(IRestActionJSON j) {
        return j.actionL();
    }
}
