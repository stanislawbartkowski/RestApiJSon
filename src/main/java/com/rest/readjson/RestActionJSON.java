package com.rest.readjson;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.google.inject.Inject;
import com.rest.conf.Executors;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestLogger;
import com.rest.runjson.IRunPlugin;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.rest.readjson.Helper.readTextFile;

public class RestActionJSON {

    public interface IRestActionEnhancer {

        Set<String> addKeys();

        void verify(IRestActionJSON i) throws RestError;

        String replace(String param) throws RestError;

        Set<String> addMap();

        Optional<String> defaultProc();
    }


    private static final String PARAMPARNAME = "name";
    private static final String PARAMPARTYPE = "type";
    private static final String PARAMDESCRITPION = "description";
    private static final String PARAMMETHOD = "method";
    private static final String PARAMPROC = "proc";
    private static final String PARAMPARS = "pars";
    private static final String PARAMFORMAT = "format";
    private static final String PARAMOUTPUT = "output";
    private static final String PARAMUPLOAD = "upload";

    private static final String PYTHON3PROC = "PYTHON3";

    private static String defaultProc = PYTHON3PROC;

    private static final Set<String> allowedKeys = new HashSet<String>();
    private static final Set<String> allowedParKeys = new HashSet<String>();

    private static final Set<String> additionalKeys = new HashSet<String>();

    private static final Set<String> alwaysString = new HashSet<String>();

    @Inject
    private final IRestActionEnhancer iEnhancer;

    @Inject
    private final Executors exec;


    private static Map<String, PARAMTYPE> tmap = new HashMap<String, PARAMTYPE>();
    private static Set<String> procmap = new HashSet<String>();

    private static class RestAction implements IRestActionJSON {

        private static class RestParam implements IRestParam {

            private final String name;
            private final PARAMTYPE type;

            private RestParam(String name, PARAMTYPE type) {
                this.name = name;
                this.type = type;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public PARAMTYPE getType() {
                return type;
            }
        }

        private final String name;
        private final Optional<String> desc;
        private final Method method;
        private final String proc;
        private final List<String> actionL;
        private final String action;
        private final List<IRestParam> plist;
        private final Path jsonPath;
        private final FORMAT format;
        private final OUTPUT output;
        private final Map<String, String> addPars;
        private final boolean upload;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Optional<String> getDesc() {
            return desc;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public String getProc() {
            return proc;
        }

        @Override
        public String action() {
            return action;
        }

        @Override
        public List<String> actionL() {
            return actionL;
        }

        @Override
        public FORMAT format() {
            return this.format;
        }

        @Override
        public OUTPUT output() {
            if (alwaysString.contains(proc)) return OUTPUT.STRING;
            return this.output;
        }

        @Override
        public List<IRestParam> getParams() {
            return plist;
        }

        @Override
        public Path getJsonPath() {
            return jsonPath;
        }

        @Override
        public boolean isUpload() {
            return upload;
        }

        @Override
        public Map<String, String> getAddPars() {
            return addPars;
        }


        RestAction(String name, Optional<String> desc, Method method, String proc, String action, List<IRestParam> plist, FORMAT format, OUTPUT output, Path jsonPath, Map<String, String> addPars, List<String> actionL, boolean upload) {
            this.name = name;
            this.desc = desc;
            this.method = method;
            this.proc = proc;
            this.plist = plist;
            this.action = action;
            this.format = format;
            this.output = output;
            this.jsonPath = jsonPath;
            this.addPars = addPars;
            this.actionL = actionL;
            this.upload = upload;
        }
    }

    static {
        tmap.put("string", PARAMTYPE.STRING);
        tmap.put("date", PARAMTYPE.DATE);
        tmap.put("number", PARAMTYPE.DOUBLE);
        tmap.put("log", PARAMTYPE.BOOLEAN);
        tmap.put("int", PARAMTYPE.INT);

//        procmap.add(IRestActionJSON.PYTHON3);
//        procmap.add(IRestActionJSON.SQL);
//        procmap.add(IRestActionJSON.SHELL);
//        procmap.add(IRestActionJSON.RESOURCE);

        allowedKeys.add(PARAMPARS);
        allowedKeys.add(PARAMFORMAT);
        allowedKeys.add(PARAMOUTPUT);
        allowedKeys.add(PARAMMETHOD);
        allowedKeys.add(PARAMPROC);
        allowedKeys.add(PARAMDESCRITPION);
        allowedKeys.add(PARAMUPLOAD);
        allowedKeys.add(IRunPlugin.PARAMACTION);

        allowedParKeys.add(PARAMPARNAME);
        allowedParKeys.add(PARAMPARTYPE);
    }

    @Inject
    public RestActionJSON(IRestActionEnhancer iEnhancer,Executors exec) {
        this.iEnhancer = iEnhancer;
        this.exec = exec;
        additionalKeys.addAll(iEnhancer.addKeys());
        procmap.addAll(iEnhancer.addMap());
        if (iEnhancer.defaultProc().isPresent()) defaultProc = iEnhancer.defaultProc().get();
        allowedKeys.addAll(exec.listActions());
        procmap.addAll(exec.listProcs());
        alwaysString.addAll(exec.listofAlwaysString());
    }

    public static IRestActionJSON.IRestParam constructIP(String name, PARAMTYPE type) {
        return new RestAction.RestParam(name, type);
    }

    private static void verifyAttributes(JSONObject json, Set<String> atts, Set<String> addatt, Path p) throws RestError {
        for (String s : json.keySet()) {
            if (!atts.contains(s) && !addatt.contains(s)) {
                String mess = p.toString() + " incorrect attribute " + s + ".";
                String keys = "";
                Iterator<String> i = atts.iterator();
                while (i.hasNext()) {
                    keys = keys + " " + i.next();
                }
                i = addatt.iterator();
                while (i.hasNext()) {
                    keys = keys + " " + i.next();
                }
                Helper.throwSevere(mess + " Expected values:" + keys);
            }
        }
    }

    private static String getName(Path path) {
        String s = path.toFile().getName();
        if (s.indexOf(".") < 0) return s;
        return s.substring(0, s.indexOf("."));
    }

    private static Optional<String> getJSon(JSONObject json, String key) {
        Object o = json.opt(key);
        if (o == null) return Optional.empty();
        else return Optional.of(o.toString());
    }

    private static <E extends Enum<E>> E toEnum(Class<E> cl, String s, String key) throws RestError {
        try {
            return E.valueOf(cl, s);
        } catch (IllegalArgumentException e) {
            String mess = s + " incorrect value for " + key + ". Expected values :";
            Iterator<E> i = EnumSet.allOf(cl).iterator();
            while (i.hasNext()) {
                E elem = i.next();
                mess = mess + " " + elem.toString();
            }
            Helper.throwSevere(mess);
        }
        return null;
    }

    private static <E extends Enum<E>> E getJSONAttr(Class<E> cl, JSONObject json, E defa, String key) throws RestError {
        E m = defa;
        Optional<String> method = getJSon(json, key);
        if (method.isPresent()) m = toEnum(cl, method.get(), key);
        return m;
    }

    private String preplaceVariable(String s) throws RestError {
        return iEnhancer.replace(s);
    }

    private class StringList {
        String res;
        List<String> resl = new ArrayList<String>();
    }

    private Object getParO(JSONObject json, String key, Optional<? extends Object> defa) throws RestError {
        Object o = json.opt(key);
        if (o == null) {
            if (defa == null) Helper.throwSevere("Parameter " + key + " is not defined");
            if (!defa.isPresent())
                Helper.throwSevere("Parameter " + key + " is not defined and no default value provided");
            return defa.get();
        }
        return o;
    }

    private String getPar(JSONObject json, String key, Optional<String> defa) throws RestError {
        Object o = getParO(json, key, defa);
        if (o instanceof String) return preplaceVariable(o.toString());
        return o.toString();
    }

    private StringList getParL(JSONObject json, String key, Optional<String> defa) throws RestError {
        Object o = getParO(json, key, defa);
        StringList res = new StringList();
        if (o instanceof String) {
            res.res = preplaceVariable(json.getString(key));
            res.resl.add(res.res);

        } else {
            res.res = o.toString();
            if (o instanceof JSONArray) {
                JSONArray a = (JSONArray) o;
                a.forEach(ele -> res.resl.add(ele.toString()));
            }
        }
        return res;
    }


    private boolean getParB(JSONObject json, String key, Optional<Boolean> defa) throws RestError {
        Object o = getParO(json, key, defa);
        if (!(o instanceof Boolean)) Helper.throwSevere(key + " boolean expected, found " + o.toString());
        Boolean b = (Boolean)o;
        return b.booleanValue();
    }

    private static void throwmaperror(String stype, Set<String> se) throws RestError {

        String mess = stype + " value is incorrect. Expected :";
        for (String s : se) mess = mess + " " + s;
        Helper.throwSevere(mess);
    }

    private IRestActionJSON.IRestParam readPar(JSONObject json, Path p) throws RestError {
        verifyAttributes(json, allowedParKeys, new HashSet<String>(), p);
        String name = getPar(json, PARAMPARNAME, null);
        String stype = getPar(json, PARAMPARTYPE, Optional.of("string"));
        PARAMTYPE typ = tmap.get(stype);
        if (typ == null) throwmaperror(stype, tmap.keySet());
        return constructIP(name, typ);
    }

    private Path getFile(Path p, String method) {
        String filename = p.toString() + "-" + method;
        File f = new File(filename);
        if (f.exists()) return Paths.get(filename);
        else return p;
    }

    public IRestActionJSON readJSONAction(Path pin, String method) throws RestError {
        String jsonstring = null;
        Path p = getFile(pin,method);
        jsonstring = readTextFile(p);
        JSONObject json = new JSONObject(jsonstring);
        verifyAttributes(json, allowedKeys, additionalKeys, p);
        RestLogger.info(json.toString());
        String name = getName(p);
        Optional<String> descr = getJSon(json, PARAMDESCRITPION);
        // metoda
        IRestActionJSON.Method m = getJSONAttr(IRestActionJSON.Method.class, json, IRestActionJSON.Method.GET, PARAMMETHOD);
        String proc = getPar(json, PARAMPROC, Optional.of(defaultProc));
        if (!procmap.contains(proc)) throwmaperror(proc, procmap);
        IRunPlugin iplug = exec.getExecutorProc(proc,p.toString());

        StringList action = getParL(json, iplug.getActionParam(), Optional.empty());
        boolean upload = getParB(json, PARAMUPLOAD, Optional.of(false));

        List<IRestActionJSON.IRestParam> plist = new ArrayList<IRestActionJSON.IRestParam>();
        JSONArray a = json.optJSONArray(PARAMPARS);
        if (a != null) {
            for (int i = 0; i < a.length(); i++)
                plist.add(readPar(a.optJSONObject(i), p));
        }

        IRestActionJSON.OUTPUT output = getJSONAttr(IRestActionJSON.OUTPUT.class, json, IRestActionJSON.OUTPUT.TMPFILE, PARAMOUTPUT);
        IRestActionJSON.FORMAT format = getJSONAttr(IRestActionJSON.FORMAT.class, json, IRestActionJSON.FORMAT.JSON, PARAMFORMAT);

        Map<String, String> addPars = new HashMap<String, String>();

        json.keySet().stream().forEach(e -> {
            String key = e;
            if (additionalKeys.contains(key)) {
                Object o = json.get(key);
                if (o != null && (o instanceof String || o instanceof Boolean)) {
                    try {
                        addPars.put(key, getPar(json, key, Optional.empty()));
                    } catch (RestError restError) {
                        // do nothing here
                        restError.printStackTrace();
                    }
                }
            }
        });

        IRestActionJSON ires = new RestAction(name, descr, m, proc, action.res, plist, format, output, p, addPars, action.resl, upload);

        iEnhancer.verify(ires);
        return ires;
    }

}