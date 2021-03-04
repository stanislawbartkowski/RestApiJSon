package com.rest.guice.rest;

import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.rest.guice.AbstractCommonModule;
import com.rest.readjson.IRestActionJSON;
import com.rest.readjson.RestError;
import com.rest.restservice.ParamValue;
import com.rest.restservice.RestParams;
import com.rest.service.RestService;

import java.util.Map;

public class CommonModule extends AbstractCommonModule {
    @Override
    protected void configure() {
        super.configure();
    }

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
