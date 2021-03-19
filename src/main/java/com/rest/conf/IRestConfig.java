package com.rest.conf;

import com.rest.main.RestMainHelper;
import com.rest.readjson.Helper;

import java.util.List;
import java.util.Properties;
import java.util.Set;

public interface IRestConfig {

    Set<String> listOfPlugins();

    String getJSONDir();

    Properties prop();

    Helper.ListPaths getJSonDirPaths();

}
