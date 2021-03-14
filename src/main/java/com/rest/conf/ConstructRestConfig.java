package com.rest.conf;

import com.rest.conf.IRestConfig;
import com.rest.readjson.Helper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class ConstructRestConfig {

    private static final String JDIR = "jdir";
    private static final String PLUGINS = "plugins";

    private static final Set<String> allowedPlugins = new HashSet<String>();

    static {
        allowedPlugins.add(IRestActionJSON.SHELL);
        allowedPlugins.add(IRestActionJSON.PYTHON3);
        allowedPlugins.add(IRestActionJSON.SQL);
    }

    static class RestConfig implements IRestConfig {

        private final Properties prop;
        private final Helper.ListPaths fparam;
        private final Set<String> listofPlugins;

        RestConfig(Properties prop, Set<String> listofPlugins) {
            this.prop = prop;
            // bad practice, method used in constructor
            this.fparam = new Helper.ListPaths(getJSONDir());
            this.listofPlugins = listofPlugins;
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
        } else {
            String[] vals = val.get().split(",");
            for (String s : vals) {
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


    public IRestConfig create(Path p) throws RestError {

        FileInputStream f = null;
        Properties pro = new Properties();
        try {
            f = new FileInputStream(p.toFile());
            pro.load(f);
        } catch (IOException e) {
            Helper.throwException(p.toString(), e);
        }
        Helper.getValue(pro, JDIR, true);

        return new RestConfig(pro, readListOfPlugins(pro));

    }
}
