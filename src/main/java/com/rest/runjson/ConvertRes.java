package com.rest.runjson;

import org.json.JSONArray;
import org.json.JSONObject;


public class ConvertRes {

    private ConvertRes() {
    }

    public static void rename(JSONObject j, String from, String to) {
        if (!j.has(from)) return;
        // rename only if it is the array
        JSONArray a = j.optJSONArray(from);
        if (a == null) return;
        j.put(to, a);
        j.remove(from);
    }
}
