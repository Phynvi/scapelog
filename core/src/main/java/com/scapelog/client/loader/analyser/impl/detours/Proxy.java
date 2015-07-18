package com.scapelog.client.loader.analyser.impl.detours;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Proxy {
    /**
     * The unqualified getName of the class to proxy.
     *
     * @return The class getName
     */
    String value();
}