package com.rest.guice.rest;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rest.guice.ModuleBuild;

public class SetInjector {

    public static void setInjector() {
        Injector i = Guice.createInjector(new CommonModule());
        ModuleBuild.setInjector(i);
    }
}
