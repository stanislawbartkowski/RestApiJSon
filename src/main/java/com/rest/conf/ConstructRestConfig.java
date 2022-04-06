package com.rest.conf;

import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.RestLogger;
import org.javatuples.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ConstructRestConfig {

    private static final String JDIR = "jdir";
    private static final String PLUGINS = "plugins";
    private static final String RENAME = "rename";
    private static final String MULTI = "multithread";
    private static final String MULTITRUE = "true";

    private static final Set<String> allowedPlugins = new HashSet<String>();

    static {
        allowedPlugins.add(IRestActionJSON.SHELL);
        allowedPlugins.add(IRestActionJSON.PYTHON3);
        allowedPlugins.add(IRestActionJSON.SQL);
        allowedPlugins.add(IRestActionJSON.RESOURCE);
    }

    static class RestConfig implements IRestConfig {

        private final Properties prop;
        private final Helper.ListPaths fparam;
        private final Set<String> listofPlugins;
        private final Optional<Pair<String, String>> rename;

        RestConfig(Properties prop, Set<String> listofPlugins, Optional<Pair<String, String>> rename) {
            this.prop = prop;
            // bad practice, method used in constructor
            this.fparam = new Helper.ListPaths(getJSONDir());
            this.listofPlugins = listofPlugins;
            this.rename = rename;
        }

        @Override
        public Optional<Pair<String, String>> getRenameRes() {
            return rename;
        }

        @Override
        public Set<String> listOfPlugins() {
            return listofPlugins;
        }

        @Override
        public String getJSONDir() {
            // not null
            return prop.getProperty(JDIR);
        }

        @Override
        public boolean isSingle() {
            return !prop.getProperty(MULTI, "false").equals(MULTITRUE);
        }

        @Override
        public Properties prop() {
            return prop;
        }

        @Override
        public Helper.ListPaths getJSonDirPaths() {
            return fparam;
        }
    }

    private Set<String> readListOfPlugins(Properties pro) throws RestError {
        Set<String> listofPlugins = new HashSet<String>();
        Optional<String> val = Helper.getValue(pro, PLUGINS, false);
        if (!val.isPresent()) {
            listofPlugins.add(IRestActionJSON.PYTHON3);
            listofPlugins.add(IRestActionJSON.SHELL);
            listofPlugins.add(IRestActionJSON.RESOURCE);
        } else {
            String[] vals = val.get().split(",");
            for (String s : vals) {
                s = s.trim(); // trail spaces
                if (!allowedPlugins.contains(s)) {
                    final StringBuffer mess = new StringBuffer();
                    mess.append(s + " unrecognized plugin name. Expected values: ");
                    allowedPlugins.stream().forEach(ss -> mess.append(" " + ss));
                    Helper.throwSevere(mess.toString());
                }
                listofPlugins.add(s);
            }
        }
        return listofPlugins;

    }

    private Optional<Pair<String, String>> getRename(String val) throws RestError {
        if (val == null) return Optional.empty();
        RestLogger.info(String.format("Reading parameter %s value: %s", RENAME, val));
        String a[] = val.split(":");
        if (a.length != 2) {
            String errmess = String.format("Parameter %s, value %s should contain exactly two values separated by :", RENAME, val);
            Helper.throwSevere(errmess);
        }
        return Optional.of(new Pair<String, String>(a[0], a[1]));
    }

    public IRestConfig create(Path p, Optional<List<String>> customplugins) throws RestError {

        if (customplugins.isPresent()) allowedPlugins.addAll(customplugins.get());
        FileInputStream f = null;
        Properties pro = new Properties();
        try {
            f = new FileInputStream(p.toFile());
            pro.load(f);
        } catch (IOException e) {
            Helper.throwException(p.toString(), e);
        }
        Helper.getValue(pro, JDIR, true);
        Optional<String> multi = Helper.getValue(pro,MULTI,false);
        if (multi.isPresent() && multi.get().equals("true")) RestLogger.info("Multi-thread enabled");
        else RestLogger.info("Single thread");

        return new RestConfig(pro, readListOfPlugins(pro), getRename(pro.getProperty(RENAME)));
    }
}
