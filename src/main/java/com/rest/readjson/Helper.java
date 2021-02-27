package com.rest.readjson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

import com.rest.restservice.PARAMTYPE;
import com.rest.restservice.RestLogger;
import com.rest.restservice.ParamValue;

public class Helper {

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
            throwException("Error while reding " + path.toString(), e);
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

}
