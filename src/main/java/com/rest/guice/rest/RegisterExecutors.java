package com.rest.guice.rest;

import com.google.inject.Key;
import com.google.inject.name.Names;
import com.rest.conf.Executors;
import com.rest.guice.ModuleBuild;
import com.rest.readjson.RestError;
import com.rest.runjson.IRunPlugin;

public class RegisterExecutors {

    public static void registerExecutors(String proc) throws RestError {
        Executors rest = ModuleBuild.getI().getInstance(Executors.class);
        rest.registerExecutor(proc, ModuleBuild.getI().getInstance(Key.get(IRunPlugin.class, Names.named(proc))));
    }

}
