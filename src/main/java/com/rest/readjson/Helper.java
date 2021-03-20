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

        public Optional<Path> getPath(String s, boolean force) throws RestError {

            for (File f : files) {
                File ff = new File(f, s);
                if (ff.exists() && ff.isFile()) return Optional.of(Paths.get(ff.toURI()));
            }
            if (force) {
                final StringBuffer bu = new StringBuffer();
                Arrays.stream(files).forEach(f -> {
                    bu.append(f.getAbsolutePath());
                    bu.append(' ');
                });
                String errmess = "File does not exist: " + bu.toString() + " " + s;
                Helper.throwSevere(errmess);
            }
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
            return s;
        } catch (IOException e) {
            throwException("Error while reading " + path.toString(), e);
        }
        return null;
    }

    public static Optional<String> getValue(Properties p, String key, boolean force) throws RestError {
        String pkey = key;
        String val = p.getProperty(pkey);
        if (val == null) {
            if (force) {
                String errmess = pkey + " not defined in property file";
                RestLogger.L.severe(errmess);
                throw new RestError(errmess);
            }
            RestLogger.info(pkey + "no value found but not mandatory");
            return Optional.empty();
        }
        RestLogger.info(pkey + " value read:" + val);
        return Optional.of(val);
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
        }
        return null;
    }


}
