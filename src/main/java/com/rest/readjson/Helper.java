package com.rest.readjson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestLogger;
import com.rest.restservice.ParamValue;

public class Helper {

    public static class ListPaths {

        private final File[] files;

        public ListPaths(String s) {
            String[] ss = s.split(",");
            files = new File[ss.length];
            for (int i = 0; i < ss.length; i++)
                files[i] = new File(ss[i]);
        }

        private Optional<Path> getIsFile(File f, String s, Optional<String> ext) {
            File ff = ext.isPresent() ? new File(f, s + "." + ext.get()) : new File(f, s);
            if (ff.exists() && ff.isFile()) return Optional.of(Paths.get(ff.toURI()));
            return Optional.empty();
        }

        private Optional<Path> getFile(String s) {
            for (File f : files) {
                Optional<Path> p = getIsFile(f, s, Optional.empty());
                if (p.isPresent()) return p;
                p = getIsFile(f, s, Optional.of(IRestActionJSON.JSONEXT));
                if (p.isPresent()) return p;
                p = getIsFile(f, s, Optional.of(IRestActionJSON.YAMLEXT));
                if (p.isPresent()) return p;
            }
            return Optional.empty();
        }

        public Optional<Path> getPath(String s, Optional<String> alternatives) throws RestError {

            Optional<Path> p = getFile(s);
            if (p.isPresent()) return p;
            if (alternatives.isPresent())
                p = getFile(alternatives.get());
            if (p.isPresent()) return p;

            // prepare error message
            final StringBuffer bu = new StringBuffer();
            Arrays.stream(files).forEach(f -> {
                bu.append(f.getAbsolutePath());
                bu.append(' ');
            });
            String errmess = "Directory: " + bu.toString() + " File does not exist:" + s + (alternatives.isPresent() ? " or " + alternatives.get() : "") + " Expected extension " + IRestActionJSON.JSONEXT + " or " + IRestActionJSON.YAMLEXT;
            Helper.throwSevere(errmess);
            return Optional.empty();
        }

        public String getErrPath(String s) {
            String errmess = null;

            for (File f : files)
                if (errmess == null) errmess = "Directory: " + f.toString();
                else errmess = errmess + "," + f.toString();

            return errmess + " File: " + s;
        }

    }

    public static void throwSevere(String mess) throws RestError {
        RestLogger.L.severe(mess);
        throw new RestError(mess);
    }

    public static void throwException(String errmess, Exception e) throws RestError {
        RestLogger.L.log(Level.SEVERE, errmess, e);
        throw new RestError(errmess);
    }

    public static String readTextFile(Path path) throws RestError {
        try {
            String s = new String(Files.readAllBytes(path));
            if (path.toString().endsWith(IRestActionJSON.YAMLEXT)) return Helper.convertYamlToJson(s);
            else return s;
        } catch (IOException e) {
            throwException("Error while reading " + path.toString(), e);
        }
        return null;
    }

    private static Optional<String> getValueS(Properties p, String key, boolean force, Optional<String> mask) throws RestError {
        String pkey = key;
        String val = p.getProperty(pkey);
        if (val == null) {
            if (force) {
                String errmess = pkey + " not defined in property file but manadatory";
                RestLogger.L.severe(errmess);
                throw new RestError(errmess);
            }
            RestLogger.info(pkey + " no value found but not mandatory");
            return Optional.empty();
        }
        RestLogger.info(pkey + " value read:" + (mask.isPresent() ? mask.get() : val));
        return Optional.of(val);
    }

    public static Optional<String> getValue(Properties p, String key, boolean force) throws RestError {
        return getValueS(p, key, force, Optional.empty());
    }

    public static Optional<String> getValuePassword(Properties p, String key, boolean force) throws RestError {
        return getValueS(p, key, force, Optional.of("XXXXXXXX"));
    }

    public static String ParamValueToS(PARAMTYPE t, ParamValue v) {
        switch (t) {
            case DOUBLE: {
                Double d = v.getDoublevalue();
                return d.toString();
            }
            case DATE:
                Date da = v.getDatevalue();
                return da.toString();
            case BOOLEAN:
                return Boolean.toString(v.isLogTrue());
            case INT:
                return Integer.toString(v.getIntvalue());
            default:
                return v.getStringvalue();
        }
    }

    public static File createTempFile(boolean json) throws RestError {
        try {
            return File.createTempFile("rst", json ? ".json" : ".txt");
        } catch (IOException e) {
            String errmess = "Error while creating temporary file";
            Helper.throwException(errmess, e);
            return null;
        }
    }

    private static String convertYamlToJson(String yaml) throws RestError {
        try {
            ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
            Object obj = yamlReader.readValue(yaml, Object.class);
            ObjectMapper jsonWriter = new ObjectMapper();
            return jsonWriter.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (IOException ex) {
            String errmess = "Error while converting YAML to JSON";
            Helper.throwException(errmess, ex);
            return null;
        }
    }
}
