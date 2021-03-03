package com.rest.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

abstract public class TestRestHelper {

    //    private final String HOST = "localhost";
//   private final int PORT = 9080;
//    private final String HOST = "pers";

    private final String HOST;
    private final int PORT;
    protected HttpURLConnection con;

    protected TestRestHelper(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    protected void P(String s) {
        System.out.println(s);
    }

    protected int makegetcall(String path, String query) throws IOException {
        URL url = new URL("http://" + HOST + ":" + PORT + path + (query != null ? "?" + query : ""));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        return con.getResponseCode();
    }

    private String getString(InputStream i) {
        StringBuilder b = new StringBuilder();
        try (Scanner scanner = new Scanner(i)) {
            while (scanner.hasNextLine()) {
                b.append(scanner.nextLine() + "\n");
            }
        }
        return b.toString();
    }

    protected String getData() throws IOException {
        return getString(con.getInputStream());
    }

    protected String getErrData() throws IOException {
        return getString(con.getErrorStream());
    }

    protected String callok(String meth, String query) throws IOException {
        int res = makegetcall(meth, query);
        P("Result: " + res);
        Assert.assertEquals(200, res);
        return getData();
    }

    protected JSONArray getA(String s) {
        JSONObject o = new JSONObject(s);
        return o.optJSONArray("res");
    }

    protected void test400(String meth, String query) throws IOException {
        int res = makegetcall(meth, query);
        P("Result: " + res);
        Assert.assertEquals(400, res);
        String da = getErrData();
        P(da);
    }

    protected void test400(String meth) throws IOException {
        test400(meth,null);
    }

    protected void testok(String meth, String query, String validdata) throws IOException {
        int res = makegetcall(meth, query);
        P("Result: " + res);
        Assert.assertEquals(200, res);
        String da = getData();
        P(da);
        Assert.assertEquals(validdata, da.trim());
    }

    protected JSONArray testoka(String meth, String query) throws IOException {
        int res = makegetcall(meth, query);
        P("Result: " + res);
        Assert.assertEquals(200, res);
        String da = getData();
        P(da);
        return getA(da);
    }



}