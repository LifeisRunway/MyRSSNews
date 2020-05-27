package com.imra.mynews.di.common;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Date: 26.05.2020
 * Time: 22:44
 *
 * @author IMRA027
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Json {
}