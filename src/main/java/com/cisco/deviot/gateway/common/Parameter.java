package com.cisco.deviot.gateway.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * When annotates method parameter, it defines an argument of an action.
 * When annotates a getter method, it defines a readable property of the model.
 * When annotates a setter method, it defines a writable property of the model.
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Parameter {
	/**
	 * The name of the parameter, default to the method parameter name or method name
	 */
	String name() default "";
	/**
	 * The type of the parameter
	 */
	ParamType type() default ParamType.AUTO;
	/**
	 * The range of the parameter
	 */
	String[] range() default {};
	/**
	 * The current value of the parameter
	 */
	String value() default "";
	/**
	 * The description of the parameter
	 */
	String description() default "";
	/**
	 * The description of the parameter
	 */
	String unit() default "";
}
