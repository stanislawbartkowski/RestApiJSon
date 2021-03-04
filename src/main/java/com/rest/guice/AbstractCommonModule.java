package com.rest.guice;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.rest.conf.ConstructRestConfig;
import com.rest.conf.IRestConfig;
import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import com.rest.runjson.executors.Python3Executor;
import com.rest.runjson.executors.SQLExecutor;
import com.rest.runjson.executors.ShellExecutor;
import com.rest.service.RestService;

public abstract class AbstractCommonModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ConstructRestConfig.class).in(Singleton.class);
        bind(IRestConfig.class).toProvider(RestConfigFactory.class).in(Singleton.class);

        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.SQL))
                .to(SQLExecutor.class).in(Scopes.SINGLETON);
        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.PYTHON3))
                .to(Python3Executor.class).in(Scopes.SINGLETON);
        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.SHELL))
                .to(ShellExecutor.class).in(Scopes.SINGLETON);
        bind(RestRunJson.class).in(Singleton.class);
        bind(RestService.class).in(Singleton.class);
        bind(RestMainHelper.class).in(Singleton.class);
//        requestStaticInjection(RegisterExecutors.class);
    }


}
