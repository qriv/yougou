package com.litesuits.android.inject;

/**
 * Created by taosj on 15/2/2.
 */

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
public @interface InjectView {
    int id() default 0;
}