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

    private static final String AUTHLABEL = "authlabel";
    private static final String AUTHNOLABEL = "authnolabel";

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
                String root = (key.equals(REPLACEA)) ? DEFROOT : key.substring(REPLACEA.length());
                try {
                    JSONObject resa = readJS(files, val, Optional.empty());
                    JSONArray aa = resa.getJSONArray(root);
                    arr.putAll(aa);
                } catch (RestError ex) {
                    RestLogger.L.severe(String.format("Error while transforming JSArray replacement %s - %s", key, val));
                    err.set(Optional.of(ex));
                } catch (org.json.JSONException je) {
                    String mess = String.format("Error while transforming JSArray replacement %s - %s", key, val);
                    RestLogger.L.log(Level.SEVERE, mess, je);
                    err.set(Optional.of(new RestError(je.getMessage())));

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
                    rlist.add(readJS(files, oo.getString(s), Optional.empty()));
                } catch (RestError e) {
                    RestLogger.L.log(Level.SEVERE, String.format("Found replacement but the file does not exist %s %s", s, oo.getString(s)));
                    err.set(Optional.of(e));
                }
            }
        }); // for Each

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


    public static Pair<JSONObject, Path> readJSP(Helper.ListPaths files, String resfile, Optional<String> opt, Optional<String> authlabel) throws RestError {
        Optional<Path> resourceF = files.getPath(resfile, opt);
        String s = Helper.readTextFile(resourceF.get());
        JSONObject o = new JSONObject(s);
        o = transformO(files, o);
        if (authlabel.isPresent()) o = HelperJSon.transformSec(o, authlabel.get());
        return Pair.with(o, resourceF.get());
    }

    public static JSONObject readJS(Helper.ListPaths files, String resfile, Optional<String> authlabel) throws RestError {
        Pair<JSONObject, Path> pa = readJSP(files, resfile, Optional.empty(), authlabel);
        return pa.getValue0();
    }

    // ==========================
    // security label
    // ==========================

    private static boolean isAuthorized(Object oauthLabel, String authlabel) {
        if (authlabel.equals("")) return true;
        String oL = oauthLabel.toString();
        return oL.equals(authlabel);
    }

    private static boolean removeO(JSONObject o, String authlabel) {
        if (o.has(AUTHLABEL)) {
            Object oo = o.get(AUTHLABEL);
            if (oo instanceof String) {
                return !isAuthorized(oo, authlabel);
            }
        }
        if (o.has(AUTHNOLABEL)) {
            // opposite to AUTHLABEL
            Object oo = o.get(AUTHNOLABEL);
            if (oo instanceof String) {
                return isAuthorized(oo, authlabel);
            }
        }
        return false;
    }

    private static JSONArray transformSecA(JSONArray a, String authlabel) {
        JSONArray arr = new JSONArray();
        a.forEach(e -> {

            Object addE = e;
            if (e instanceof JSONObject) {
                JSONObject oo = (JSONObject) e;
                if (removeO(oo, authlabel)) addE = null;
                else {
                    addE = transformSec(oo, authlabel);
                }
            }
            if (e instanceof JSONArray) {
                JSONArray aa = (JSONArray) e;
                addE = transformSecA(aa, authlabel);
            }
            if (addE != null) {
                arr.put(addE);
            }
        });
        return arr;
    }

    private static JSONObject transformSec(JSONObject oo, String authlabel) {
        JSONObject o = new JSONObject();
        oo.keySet().forEach(s -> {
            Object obj = oo.get(s);
            if (obj instanceof JSONObject) {
                if (removeO((JSONObject) obj, authlabel)) obj = null;
                else obj = transformSec((JSONObject) obj, authlabel);
            }
            if (obj instanceof JSONArray) {
                obj = transformSecA((JSONArray) obj, authlabel);
            }
            if (obj != null) o.put(s, obj);
        });
        return o;
    }

    public static JSONObject transformSecurity(JSONObject o, String authlabel) {
        // empty - admin security
        return transformSec(o, authlabel);
    }
}
