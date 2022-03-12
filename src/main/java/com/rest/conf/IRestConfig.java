package com.rest.conf;

import com.rest.readjson.Helper;
import org.javatuples.Pair;

import java.util.Optional;
import java.util.Properties;
import java.util.Set;

public interface IRestConfig {

    String VERSION = "RestAPIJSON version 1.0 (r:1) 2022/03/12";

    Optional<Pair<String,String>> getRenameRes();

    Set<String> listOfPlugins();

    String getJSONDir();

    Properties prop();

    Helper.ListPaths getJSonDirPaths();

}
