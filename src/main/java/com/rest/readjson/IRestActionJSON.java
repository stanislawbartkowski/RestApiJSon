package com.rest.readjson;


import com.rest.restservice.PARAMTYPE;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IRestActionJSON {

    enum Method {
        PUT("PUT"),
        GET("GET"),
        POST("POST"),
        DELETE("DELETE");
        private final String m;

        Method(String m) {
            this.m = m;
        }
    }

    enum FORMAT {
        JSON("JSON"),
        TEXT("TEXT"),
        ZIP("ZIP"),
        JS("JS"),
        XML("XML"),
        MIXED("MIXED"),
        MIXEDBINARY("MIXEDBINARY");

        private final String m;

        FORMAT(String m) {
            this.m = m;
        }
    }

    enum OUTPUT {
        STDOUT("STDOUT"),
        TMPFILE("TMPFILE"),
        STRING("INTERNAL");

        private final String m;

        OUTPUT(String m) {
            this.m = m;
        }
    }

    String PYTHON3 = "PYTHON3";
    String SQL = "SQL";
    String SHELL = "SHELL";
    String RESOURCE = "RESOURCE";
    String RESOURCEDIR = "RESOURCEDIR";

    String YAMLEXT = "yaml";
    String JSONEXT = "json";

    interface IRestParam {
        String getName();

        PARAMTYPE getType();
    }

    String getName();

    Optional<String> getDesc();

    Method getMethod();

    String getProc();

    String action();

    List<String> actionL();

    FORMAT format();

    OUTPUT output();

    List<IRestParam> getParams();

    Path getJsonPath();

    boolean isUpload();

    Map<String, String> getAddPars();

    boolean setEnvir();

    boolean updateQuery();

}
