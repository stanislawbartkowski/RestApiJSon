package com.rest.runjson.executors;

import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.runjson.IRunPlugin;

import java.util.Map;

public interface IBeforeExecutor {

    default void runBefore(IRestActionJSON j, IRunPlugin.RunResult res, Map<String, ParamValue> values) throws RestError  {

    }
}
