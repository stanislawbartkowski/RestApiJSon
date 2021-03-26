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

public class PostgresJDBC {

    private static Connection conn;

    public static void connect(String url, String user, String password) throws SQLException {
        RestLogger.L.info("Connecting to " + url + " user " + user);
        conn = DriverManager.getConnection(url, user, password);
        RestLogger.L.info("Connected");
    }

    public static Connection getConnection() {
        return conn;
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

    public static JSONArray runquery(String q, List<SQLParam> plist, Map<String, ParamValue> values) throws SQLException {

        RestLogger.L.info(q);
        PreparedStatement prep = conn.prepareStatement(q);
        for (SQLParam p : plist) {
            ParamValue v = values.get(p.getParam().getName());
            switch (p.getParam().getType()) {
                case BOOLEAN:
                    prep.setBoolean(p.getPos(), v.isLogTrue());
                    break;
                case DATE:
                    prep.setDate(p.getPos(), v.getDatevalue());
                    break;
                case DOUBLE:
                    prep.setDouble(p.getPos(), v.getDoublevalue());
                    break;
                case INT:
                    prep.setInt(p.getPos(), v.getIntvalue());
                    break;
                default:
                    prep.setString(p.getPos(), v.getStringvalue());
                    break;
            }
        }
        ResultSet res = prep.executeQuery();

        JSONArray ja = new JSONArray();
        while (res.next()) {
            JSONObject row = createRow(res);
            ja.put(row);
        }

        return ja;
    }

}
