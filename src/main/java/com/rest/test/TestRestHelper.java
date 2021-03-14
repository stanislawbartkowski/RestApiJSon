package com.rest.test;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

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

    protected int makegetcallupload(String path, String query, String input) throws IOException {
        URL url = new URL("http://" + HOST + ":" + PORT + path + (query != null ? "?" + query : ""));
        con = (HttpURLConnection) url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("GET");
        BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream());
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(input));
        int i;
   	    while ((i = bis.read()) > 0) {
            bos.write(i);
        }
   	    bis.close();
   	    bos.close();

        return con.getResponseCode();
    }


    private String getString(InputStream i) {
        StringBuilder b = new StringBuilder();
        try (Scanner scanner = new Scanner(i)) {
            while (scanner.hasNextLine()) {
                if (b.length() != 0) b.append('\n');
                b.append(scanner.nextLine());
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