package com.rest.conf;

import com.rest.readjson.Helper;

import java.util.Properties;
import java.util.Set;

public interface IRestConfig {

    String VERSION = "RestAPIJSON version 1.1.0 (r:0) 2026/05/24";

    Set<String> listOfPlugins();

    String getJSONDir();

    boolean isSingle();

    Properties prop();

    Helper.ListPaths getJSonDirPaths();

    String getAllowedReqs();

    String getAuthUrl();

    String getAuthRealm();

    String getAuthClientId();

    default boolean isSecured() {
        return getAuthUrl() != null;
    }
}
