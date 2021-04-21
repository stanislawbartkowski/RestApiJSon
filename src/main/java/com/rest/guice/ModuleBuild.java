package com.rest.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ModuleBuild {

    private static Injector injector = null;

    public static void setInjector(Injector pinjector) {
        injector = pinjector;
    }

    public static Injector getI() {
        return injector;
    }
}
