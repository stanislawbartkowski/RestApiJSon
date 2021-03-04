package com.rest.guice.rest;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.rest.guice.AbstractCommonModule;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.restservice.RestParams;
import com.rest.runjson.IRunPlugin;
import com.rest.runjson.executors.IBeforeExecutor;
import com.rest.service.RestService;

import java.util.Map;

public class CommonModule extends AbstractCommonModule {
    @Override
    protected void configure() {
        super.configure();
    }

    @Provides
    @Singleton
    @Named(IRestActionJSON.SQL)
    IBeforeExecutor getBeforeExecutor() {

        return new IBeforeExecutor() {
            @Override
            public void runBefore(IRestActionJSON j, IRunPlugin.RunResult res, Map<String, ParamValue> values) throws RestError {

            }
        };
    };

    @Provides
    @Singleton
    RestService.IEnhancer getRestServiceEnhancer() {
        return new RestService.IEnhancer() {

            @Override
            public void modifPars(IRestActionJSON irest, String[] path, RestParams par) throws RestError {

            }

            @Override
            public void modifValues(IRestActionJSON irest, Map<String, ParamValue> v) throws RestError {

            }
        };
    }

}
