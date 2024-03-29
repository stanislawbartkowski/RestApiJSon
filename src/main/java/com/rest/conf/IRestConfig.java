package com.rest.conf;

import com.rest.readjson.Helper;
import org.javatuples.Pair;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public interface IRestConfig {

    String VERSION = "RestAPIJSON version 1.6 (r:0) 2024/03/29";

    Optional<Pair<String, String>> getRenameRes();

    Set<String> listOfPlugins();

    String getJSONDir();

    boolean isSingle();

    Properties prop();

    Helper.ListPaths getJSonDirPaths();

    String getAllowedReqs();

}
