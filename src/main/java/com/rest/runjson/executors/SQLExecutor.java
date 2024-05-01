package com.rest.runjson.executors;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.executors.sql.JDBC;
import com.rest.runjson.executors.sql.SQLParam;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLExecutor implements IRunPlugin {

    private final static String URL = "url";
    private final static String USER = "user";
    private final static String PASSWORD = "password";

    @Inject
    private IRestConfig conf;
    private final IBeforeExecutor iBefore;

    @Inject
    public SQLExecutor(@Named(IRestActionJSON.SQL) IBeforeExecutor iBefore) {
        this.iBefore = iBefore;
    }

    @Override
    public boolean alwaysString() {
        return true;
    }

    @Override
    public void verifyProperties() throws RestError {

        String url = Helper.getValue(conf.prop(), URL, true).get();
        String user = Helper.getValue(conf.prop(), USER, true).get();
        String password = Helper.getValuePassword(conf.prop(), PASSWORD, true).get();
        try {
            JDBC.setConnData(url, user, password);
            JDBC.connect();
        } catch (SQLException throwables) {
            String errmess = "Cannot connect " + url;
            Helper.throwException(errmess, throwables);
        }
    }


    @Override
    public void executeJSON(IRestActionJSON j, RunResult res, Map<String, ParamValue> values) throws RestError {
        iBefore.runBefore(j, res, values);
        int i = 1;
        List<SQLParam> sqlp = new ArrayList<SQLParam>();
        for (IRestActionJSON.IRestParam re : j.getParams()) sqlp.add(new SQLParam(i++, re));
        JSONArray a = null;
        try {
            try  (BufferedWriter writer = new BufferedWriter(new FileWriter(res.tempfile))) {
                a =JDBC.runquery(j.action(),sqlp,values,j.updateQuery(), writer);
            }
        } catch (SQLException | IOException throwables) {
            String errmess = "Cannot execute " + j.action();
            Helper.throwException(errmess, throwables);
        }
        //res.json = new JSONObject();
        // add res only for query statements
        //if (!j.updateQuery()) res.json.put("res", a);
    }
}
