package com.rest.guice;

import com.google.inject.*;
import com.google.inject.name.Names;
import com.rest.conf.ConstructRestConfig;
import com.rest.conf.Executors;
import com.rest.conf.IRestConfig;
import com.rest.main.RestMainHelper;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestActionJSON;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.RestRunJson;
import com.rest.runjson.executors.GetResourceExecutor;
import com.rest.runjson.executors.Python3Executor;
import com.rest.runjson.executors.SQLExecutor;
import com.rest.runjson.executors.ShellExecutor;

public abstract class AbstractCommonModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConstructRestConfig.class).in(Singleton.class);
        bind(IRestConfig.class).toProvider(RestConfigFactory.class).in(Singleton.class);
        bind(Executors.class).in(Scopes.SINGLETON);

        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.SQL))
                .to(SQLExecutor.class).in(Scopes.SINGLETON);
        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.PYTHON3))
                .to(Python3Executor.class).in(Scopes.SINGLETON);
        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.SHELL))
                .to(ShellExecutor.class).in(Scopes.SINGLETON);
        bind(IRunPlugin.class).annotatedWith(Names.named(IRestActionJSON.RESOURCE))
                .to(GetResourceExecutor.class).in(Scopes.SINGLETON);
//        bind(RestActionJSON.class).in(Singleton.class);
        bind(RestRunJson.class).in(Scopes.SINGLETON);

//        bind(RestService.class).in(Singleton.class);
        bind(RestMainHelper.class).in(Singleton.class);
    }


}
