package com.rest.runjson.executors;

import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.executors.sql.PostgresJDBC;
import com.rest.runjson.executors.sql.SQLParam;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLExecutor implements IRunPlugin<IRestActionJSON> {

    private final static String URL = "url";
    private final static String USER = "user";
    private final static String PASSWORD = "password";

    @Override
    public void verifyProperties(IRestConfig conf) throws RestError {

        String url = Helper.getValue(conf.prop(), URL, true).get();
        String user = Helper.getValue(conf.prop(), USER, true).get();
        String password = Helper.getValue(conf.prop(), PASSWORD, true).get();
        try {
            PostgresJDBC.connect(url, user, password);
        } catch (SQLException throwables) {
            String errmess = "Cannot connect " + url;
            Helper.throwException(errmess, throwables);
        }
    }


    @Override
    public void executeJSON(IRestActionJSON j, IRestConfig conf, RunResult res, Map<String, ParamValue> values) throws RestError {
        int i = 1;
        List<SQLParam> sqlp = new ArrayList<SQLParam>();
        for (IRestActionJSON.IRestParam re : j.getParams()) sqlp.add(new SQLParam(i++, re));
        JSONArray a = null;
        try {
            a = PostgresJDBC.runquery(j.action(), sqlp, values);
        } catch (SQLException throwables) {
            String errmess = "Cannot execute " + j.action();
            Helper.throwException(errmess, throwables);
        }
        res.json = new JSONObject();
        res.json.put("res", a);
    }
}
