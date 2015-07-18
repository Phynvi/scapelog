package com.scapelog.client.loader.analyser.impl.detours;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Detour {
    TargetType type() default TargetType.STATIC;
    String target() default "";
}