package com.cisco.deviot.gateway.service.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Callable {
	protected transient final Logger log = LoggerFactory.getLogger(getClass());
	protected transient Method method;
	protected transient Object target;

	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		method.setAccessible(true);
		this.method = method;
	}
	public Object getTarget() {
		return target;
	}
	public void setTarget(Object target) {
		this.target = target;
	}
	public Object invoke() {
		return invoke(Collections.emptyList());
	}
	public Object invoke(ParameterModel param) {
		return invoke(Collections.singletonList(param));
	}
	public Object invoke(List<ParameterModel> parameters) {
		List<Object> args = parameters.stream().map(p -> p.getValue()).collect(Collectors.toList());
		try {
			return method.invoke(target, args.toArray(new Object[args.size()]));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			log.error("Failed to call method " + method.getName() +"("+ args + "): " + e.getMessage(), e);
			return null;
		}
	}
}
