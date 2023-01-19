package com.rest.runjson.executors.sql;

import com.rest.restservice.ParamValue;
import com.rest.restservice.RestLogger;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.JDBCType;

public class JDBC {

    private static Connection jconn;

    private static final int validtimeout = 5;

    private static String url;
    private static String user;
    private static String password;

    public static void setConnData(String url, String user, String password) {
        JDBC.url = url;
        JDBC.user = user;
        JDBC.password = password;
    }

    public static void connect() throws SQLException {
        RestLogger.L.info("Connecting to " + url + " user " + user);
        jconn = DriverManager.getConnection(url, user, password);
        RestLogger.L.info("Connected");
    }

    public static Connection getConnection() throws SQLException {
        if (! jconn.isValid(validtimeout)) {
            String mess = String.format("Connection is invalid after waiting for %d sec. Reconnecting ",validtimeout);
            RestLogger.L.info(mess);
            connect();
        }
        return jconn;
    }

    private static JSONObject createRow(ResultSet res) throws SQLException {
        JSONObject j = new JSONObject();
        ResultSetMetaData meta = res.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            JDBCType type = JDBCType.valueOf(meta.getColumnType(i));
            String colname = meta.getColumnName(i).toLowerCase();
            switch (type) {
                case INTEGER:
                    int in = res.getInt(i);
                    j.put(colname, in);
                    break;
                case DECIMAL:
                    BigDecimal d = res.getBigDecimal(i);
                    j.put(colname, d);
                    break;
                case BOOLEAN:
                case BIT:
                    boolean bl = res.getBoolean(i);
                    j.put(colname, bl);
                    break;
                default:
                    String val = res.getString(i);
                    j.put(colname, val);
                    break;
            } //switch
            if (res.wasNull()) j.put(colname, JSONObject.NULL);

        } // for

        return j;
    }

    public static JSONArray runquery(String q, List<SQLParam> plist, Map<String, ParamValue> values, boolean updatequery) throws SQLException {

        RestLogger.L.info(q);
        PreparedStatement prep = getConnection().prepareStatement(q);
        for (SQLParam p : plist) {
            ParamValue v = values.get(p.getParam().getName());
            switch (p.getParam().getType()) {
                case BOOLEAN:
                    RestLogger.info(String.format("%d boolean %s", p.getPos(), v.isLogTrue() ? "true" : "false"));
                    prep.setBoolean(p.getPos(), v.isLogTrue());
                    break;
                case DATE:
                    RestLogger.info(String.format("%d date %s", p.getPos(), v.getDatevalue().toString()));
                    prep.setDate(p.getPos(), v.getDatevalue());
                    break;
                case DOUBLE:
                    RestLogger.info(String.format("%d double %f", p.getPos(), v.getDoublevalue()));
                    prep.setDouble(p.getPos(), v.getDoublevalue());
                    break;
                case INT:
                    RestLogger.info(String.format("%d int %d", p.getPos(), v.getIntvalue()));
                    prep.setInt(p.getPos(), v.getIntvalue());
                    break;
                default:
                    RestLogger.info(String.format("%d string %s", p.getPos(), v.getStringvalue()));
                    prep.setString(p.getPos(), v.getStringvalue());
                    break;
            }
        }
        JSONArray ja = new JSONArray();
        if (updatequery) prep.executeUpdate();
        else {
            ResultSet res = prep.executeQuery();

            while (res.next()) {
                JSONObject row = createRow(res);
                ja.put(row);
            }
        }

        return ja;
    }

}
