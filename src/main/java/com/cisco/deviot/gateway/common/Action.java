package com.cisco.deviot.gateway.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This defines an action can be called by devIoT server
 * 
 * @author haihxiao
 *
 */
@Target(ElementType.METHOD)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Action {
	/**
	 * The action name, default to the method name
	 */
	String name() default "";
	/**
	 * The action description
	 */
	String description() default "";
}
