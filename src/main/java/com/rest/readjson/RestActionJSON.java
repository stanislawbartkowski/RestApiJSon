package com.rest.readjson;

import java.nio.file.Path;
import java.util.*;

import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestLogger;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.rest.readjson.Helper.readTextFile;

public class RestActionJSON {

    private static final String PARAMPARNAME = "name";
    private static final String PARAMPARTYPE = "type";
    private static final String PARAMDESCRITPION = "description";
    private static final String PARAMMETHOD = "method";
    private static final String PARAMPROC = "proc";
    private static final String PARAMPARS = "pars";
    private static final String PARAMFORMAT = "format";
    private static final String PARAMOUTPUT = "output";
    private static final String PARAMACTION = "action";

    private static final String SQLPROC = "SQL";
    private static final String PYTHON3PROC = "PYTHON3";
    private static final String SHELLPROC = "SHELL";

    private static final Set<String> allowedKeys = new HashSet<String>();
    private static final Set<String> allowedParKeys = new HashSet<String>();

    private static Map<String, PARAMTYPE> tmap = new HashMap<String, PARAMTYPE>();
    private static Map<String, Integer> procmap = new HashMap<String, Integer>();

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
        private final int proc;
        private final String action;
        private final List<IRestParam> plist;
        private final Path jsonPath;
        private final FORMAT format;
        private final OUTPUT output;

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
        public int getProc() {
            return proc;
        }

        @Override
        public String action() {
            return action;
        }

        @Override
        public FORMAT format() {
            return this.format;
        }

        @Override
        public OUTPUT output() {
            if (proc == IRestActionJSON.SQL) return OUTPUT.STRING;
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


        RestAction(String name, Optional<String> desc, Method method, int proc, String action, List<IRestParam> plist, FORMAT format, OUTPUT output, Path jsonPath) {
            this.name = name;
            this.desc = desc;
            this.method = method;
            this.proc = proc;
            this.plist = plist;
            this.action = action;
            this.format = format;
            this.output = output;
            this.jsonPath = jsonPath;
        }
    }

    static {
        tmap.put("string", PARAMTYPE.STRING);
        tmap.put("date", PARAMTYPE.DATE);
        tmap.put("number", PARAMTYPE.DOUBLE);
        tmap.put("log", PARAMTYPE.BOOLEAN);
        tmap.put("int", PARAMTYPE.INT);

        procmap.put(PYTHON3PROC, IRestActionJSON.PYTHON3);
        procmap.put(SQLPROC, IRestActionJSON.SQL);
        procmap.put(SHELLPROC, IRestActionJSON.SHELL);

        allowedKeys.add(PARAMPARS);
        allowedKeys.add(PARAMFORMAT);
        allowedKeys.add(PARAMOUTPUT);
        allowedKeys.add(PARAMMETHOD);
        allowedKeys.add(PARAMPROC);
        allowedKeys.add(PARAMDESCRITPION);
        allowedKeys.add(PARAMACTION);

        allowedParKeys.add(PARAMPARNAME);
        allowedParKeys.add(PARAMPARTYPE);
    }


    public static IRestActionJSON.IRestParam constructIP(String name, PARAMTYPE type) {
        return new RestAction.RestParam(name, type);
    }


    private static void verifyAttributes(JSONObject json, Set<String> atts, Path p) throws RestError {
        for (String s : json.keySet()) {
            if (!atts.contains(s)) {
                String mess = p.toString() + " incorrect attribute " + s + ".";
                String keys = "";
                Iterator<String> i = atts.iterator();
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

    private static String replaceVariables(String s) throws RestError {
        return s;
    }

    private static String getPar(JSONObject json, String key, Optional<String> defa) throws RestError {
        Object o = json.opt(key);
        if (o == null) {
            if (defa == null) Helper.throwSevere("Parameter " + key + " is not defined");
            if (!defa.isPresent())
                Helper.throwSevere("Parameter " + key + " is not defined and no default value provided");
            return defa.get();
        }
        return replaceVariables(o.toString());
    }

    private static void throwmaperror(String stype, Set<String> se) throws RestError {

        String mess = stype + " value is incorrrect. Expected :";
        for (String s : se) mess = mess + " " + s;
        Helper.throwSevere(mess);
    }

    private static IRestActionJSON.IRestParam readPar(JSONObject json, Path p) throws RestError {
        verifyAttributes(json, allowedParKeys, p);
        String name = getPar(json, PARAMPARNAME, null);
        String stype = getPar(json, PARAMPARTYPE, Optional.of("string"));
        PARAMTYPE typ = tmap.get(stype);
        if (typ == null) throwmaperror(stype, tmap.keySet());
        return constructIP(name, typ);
    }

    public static IRestActionJSON readJSONAction(Path p) throws RestError {
        String jsonstring = null;
        jsonstring = readTextFile(p);
        JSONObject json = new JSONObject(jsonstring);
        verifyAttributes(json, allowedKeys, p);
        RestLogger.info(json.toString());
        String name = getName(p);
        Optional<String> descr = getJSon(json, PARAMDESCRITPION);
        // metoda
        IRestActionJSON.Method m = getJSONAttr(IRestActionJSON.Method.class, json, IRestActionJSON.Method.GET, PARAMMETHOD);
        String sproc = getPar(json, PARAMPROC, Optional.of(PYTHON3PROC));
        Integer proc = procmap.get(sproc);
        if (proc == null) throwmaperror(sproc, procmap.keySet());

        String action = getPar(json, PARAMACTION, Optional.empty());

        List<IRestActionJSON.IRestParam> plist = new ArrayList<IRestActionJSON.IRestParam>();
        JSONArray a = json.optJSONArray(PARAMPARS);
        if (a != null) {
            for (int i = 0; i < a.length(); i++)
                plist.add(readPar(a.optJSONObject(i), p));
        }

        IRestActionJSON.OUTPUT output = getJSONAttr(IRestActionJSON.OUTPUT.class, json, IRestActionJSON.OUTPUT.TMPFILE, PARAMOUTPUT);
        IRestActionJSON.FORMAT format = getJSONAttr(IRestActionJSON.FORMAT.class, json, IRestActionJSON.FORMAT.JSON, PARAMFORMAT);

        return new RestAction(name, descr, m, proc, action, plist, format, output, p);
    }

}