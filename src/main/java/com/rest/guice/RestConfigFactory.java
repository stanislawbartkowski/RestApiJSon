package com.rest.guice;

import com.google.inject.Provider;
import com.rest.conf.IRestConfig;
import com.rest.conf.ConstructRestConfig;
import com.rest.guice.rest.ModuleBuild;
import com.rest.readjson.RestError;

import java.nio.file.Path;
import java.util.Optional;

public class RestConfigFactory implements Provider<IRestConfig> {

    private static IRestConfig instance;

    public static void setInstance(Path p) throws RestError {
        instance = ModuleBuild.getI().getInstance(ConstructRestConfig.class).create(p, Optional.empty());
    }

    @Override
    public IRestConfig get() {
        return instance;
    }
}
