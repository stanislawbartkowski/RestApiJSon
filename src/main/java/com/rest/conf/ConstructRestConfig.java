package com.rest.conf;

import com.rest.readjson.Helper;
import com.rest.readjson.RestError;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

public class ConstructRestConfig {

    private static final String JDIR = "jdir";

    static class RestConfig implements IRestConfig {

        private final Properties prop;

        RestConfig(Properties  prop) {
            this.prop = prop;
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
    }


    public static IRestConfig create(Path p) throws RestError {

        FileInputStream f = null;
        Properties pro = new Properties();
        try {
            f = new FileInputStream(p.toFile());
            pro.load(f);
        } catch (IOException e) {
            Helper.throwException(p.toString(),e);
        }
        Helper.getValue(pro,JDIR,true);

        return new RestConfig(pro);

    }
}
