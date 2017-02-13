package com.cisco.deviot.gateway.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that an annotated class is a "Model", the model will be 
 * registered to devIoT as a thing or a service
 * 
 * @author haihxiao
 */
@Target(ElementType.TYPE)
@Retention(value=RetentionPolicy.RUNTIME)
public @interface Model {
	/**
	 * This define the model type
	 */
	String kind() default "";
}
