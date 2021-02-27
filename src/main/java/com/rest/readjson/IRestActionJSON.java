package com.rest.readjson;


import com.rest.restservice.PARAMTYPE;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface IRestActionJSON {

    enum Method {
        PUT("PUT"),
        GET("GET");
        private final String m;

        Method(String m) {
            this.m = m;
        }
    }

    enum FORMAT {
        JSON("JSON"),
        TEXT("TEXT");

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

    int PYTHON3 = 0;
    int SQL = 1;
    int SHELL = 2;

    interface IRestParam {
        String getName();

        PARAMTYPE getType();
    }

    String getName();

    Optional<String> getDesc();

    Method getMethod();

    int getProc();

    String action();

    FORMAT format();

    OUTPUT output();

    List<IRestParam> getParams();

    Path getJsonPath();

}
