package com.rest.guice;

import com.google.inject.Provider;
import com.rest.conf.IRestConfig;
import com.rest.conf.ConstructRestConfig;
import com.rest.readjson.RestError;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class RestConfigFactory implements Provider<IRestConfig> {

    private static IRestConfig instance;

    public static void setInstance(Path p,Optional<List<String>> customplugins) throws RestError {
        instance = ModuleBuild.getI().getInstance(ConstructRestConfig.class).create(p, customplugins);
    }

    @Override
    public IRestConfig get() {
        return instance;
    }
}
