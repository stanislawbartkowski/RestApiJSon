package com.rest.guice.rest;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.rest.readjson.RestError;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;

public class RegisterExecutors {

    public static void registerExecutors(String proc) throws RestError {
        RestRunJson rest = ModuleBuild.getI().getInstance(RestRunJson.class);
        rest.registerExecutor(proc,ModuleBuild.getI().getInstance(Key.get(IRunPlugin.class, Names.named(proc))));
    }

}
