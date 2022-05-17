package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rest.readjson.IRestActionJSON;
import com.rest.runjson.IRunPlugin;

import java.util.ArrayList;
import java.util.List;

public class Python3Executor extends AbstractShellExecutor {

    private final static String PYTHONHOME = "pythonhome";

    public Python3Executor() {
        super(PYTHONHOME);
    }

    @Override
    List<String> createCmd(IRestActionJSON j) {

        List<String> l = new ArrayList<String>();
        l.add("python3");
        l.addAll(j.actionL());
        return l;
    }
}
