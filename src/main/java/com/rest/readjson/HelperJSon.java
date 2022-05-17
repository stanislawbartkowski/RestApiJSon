package com.rest.readjson;

import com.rest.restservice.RestLogger;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class HelperJSon {

    private HelperJSon() {
    }

    private static final String REPLACE = "replace#";
    private static final String REPLACEA = "replace-#";
    private static final String DEFROOT = "res";

    private static JSONArray tranformArray(Helper.ListPaths files, JSONArray a) throws RestError {
        JSONArray arr = new JSONArray();
        AtomicReference<Optional<RestError>> err = new AtomicReference<>(Optional.empty());
        a.forEach(e -> {
            if (e instanceof JSONObject) {
                JSONObject oo = (JSONObject) e;
                Optional<String> s = oo.keySet().stream().filter(ee -> ee.startsWith(REPLACEA)).findAny();
                if (s.isEmpty()) {
                    try {
                        arr.put(transformO(files, oo));
                    } catch (RestError ex) {
                        RestLogger.L.severe("Error while transforming JSObject");
                        err.set(Optional.of(ex));
                    }
                    return;
                }
                String key = s.get().trim();
                if (oo.length() != 1) {
                    String errmess = String.format("%s should contain only one element, found %d", key, oo.length());
                    RestLogger.L.severe(errmess);
                    err.set(Optional.of(new RestError(errmess)));
                    return;
                }
                String val = oo.getString(key).trim();
                String root = (key.equals(REPLACEA)) ? DEFROOT : oo.getString(key.substring(REPLACEA.length()));
                try {
                    JSONObject resa = readJS(files, val);
                    JSONArray aa = resa.getJSONArray(root);
                    arr.putAll(aa);
                } catch (RestError ex) {
                    RestLogger.L.severe(String.format("Error while transforming JSArray replacement %s - %s", key, val));
                    err.set(Optional.of(ex));
                }
                return;
            }
            arr.put(e);
        });
        if (!err.get().isEmpty()) {
            throw err.get().get();
        }
        return arr;
    }


    public static JSONObject transformO(Helper.ListPaths files, JSONObject oo) throws RestError {
        List<JSONObject> rlist = new ArrayList<JSONObject>();
        AtomicReference<Optional<RestError>> err = new AtomicReference<>(Optional.empty());
        oo.keySet().forEach(s -> {
                    if (s.startsWith(REPLACE)) {
                        try {
                            rlist.add(readJS(files, oo.getString(s)));
                        } catch (RestError e) {
                            RestLogger.L.log(Level.SEVERE, String.format("Found replacement but the file does not exist %s %s", s, oo.getString(s)));
                            err.set(Optional.of(e));
                        }
                    }
                }
        ); // for Each

        if (!err.get().isEmpty()) {
            throw err.get().get();
        }

        JSONObject o = new JSONObject();
        // concatenate
        for (JSONObject jo : rlist) {
            jo.keySet().forEach(s -> {
                o.put(s, jo.get(s));
            });
        }
        oo.keySet().forEach(s -> {
            if (s.startsWith(REPLACEA)) {
                String errmess = String.format("%s - only allowed for JSArray", s);
                RestLogger.L.log(Level.SEVERE, errmess);
                err.set(Optional.of(new RestError(errmess)));
            }
            if (!s.startsWith(REPLACE)) {
                Object obj = oo.get(s);
                if (obj instanceof JSONObject) {
                    try {
                        obj = transformO(files, (JSONObject) obj);
                    } catch (RestError e) {
                        RestLogger.L.log(Level.SEVERE, String.format("Error while transforming nested object %s", s));
                        err.set(Optional.of(e));
                    }
                }
                if (obj instanceof JSONArray) {
                    try {
                        obj = tranformArray(files, (JSONArray) obj);
                    } catch (RestError e) {
                        RestLogger.L.log(Level.SEVERE, String.format("Error while transforming array %s", s));
                        err.set(Optional.of(e));
                    }
                }

                o.put(s, obj);
            }
        });
        if (!err.get().isEmpty()) {
            throw err.get().get();
        }
        return o;
    }


    public static Pair<JSONObject, Path> readJSP(Helper.ListPaths files, String resfile, Optional<String> opt) throws RestError {
        Optional<Path> resourceF = files.getPath(resfile, opt);
        String s = Helper.readTextFile(resourceF.get());
        JSONObject o = new JSONObject(s);
        return Pair.with(transformO(files, o), resourceF.get());
    }

    public static JSONObject readJS(Helper.ListPaths files, String resfile) throws RestError {
        Pair<JSONObject, Path> pa = readJSP(files, resfile, Optional.empty());
        return pa.getValue0();
    }
}
