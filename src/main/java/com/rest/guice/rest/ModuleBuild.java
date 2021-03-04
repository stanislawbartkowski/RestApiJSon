package com.rest.guice.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModuleBuild {

    private static Injector injector = Guice.createInjector(new CommonModule());

    public static Injector getI() {
        return injector;
    }
}
