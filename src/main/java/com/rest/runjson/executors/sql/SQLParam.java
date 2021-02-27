package com.rest.runjson.executors.sql;

import com.rest.readjson.IRestActionJSON;

public class SQLParam {

    private final int pos;
    private final IRestActionJSON.IRestParam param;

    public SQLParam(int pos, IRestActionJSON.IRestParam param) {
        this.pos = pos;
        this.param = param;
    }

    public IRestActionJSON.IRestParam getParam() {
        return param;
    }

    public int getPos() {
        return pos;
    }
}
