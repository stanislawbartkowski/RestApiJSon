package com.rest.conf;

import com.rest.main.RestMainHelper;
import com.rest.readjson.Helper;

import java.util.Properties;

public interface IRestConfig {

    String getJSONDir();

    Properties prop();

    Helper.ListPaths getJSonDirPaths();

}
