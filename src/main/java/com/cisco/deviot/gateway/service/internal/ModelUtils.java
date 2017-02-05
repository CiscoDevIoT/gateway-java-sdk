package com.cisco.deviot.gateway.service.internal;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cisco.deviot.gateway.common.Action;
import com.cisco.deviot.gateway.common.Model;
import com.cisco.deviot.gateway.common.ParamType;
import com.cisco.deviot.gateway.common.Parameter;
import com.cisco.deviot.gateway.util.StringUtils;

class ModelUtils {
	public static ThingModel buildModel(String id, String name, Object modelObject) {
		ThingModel thingModel = new ThingModel();
		thingModel.setId(id);
		thingModel.setName(name);
		
		if(modelObject.getClass().isAnnotationPresent(Model.class)) {
			Model modelAnno = modelObject.getClass().getAnnotation(Model.class);
			thingModel.setKind(modelAnno.kind());
		} else {
			thingModel.setKind(modelObject.getClass().getSimpleName().toLowerCase());
		}

		List<ActionModel> actions = new ArrayList<ActionModel>();
		List<ParameterModel> properties = new ArrayList<ParameterModel>();
		List<ParameterModel> settings = new ArrayList<ParameterModel>();
		Collection<Method> methods = getMethods(modelObject.getClass());
		for(Method method : methods) {
			if(isAction(method)) {
				Action actAnno = method.getAnnotation(Action.class);
				ActionModel action = new ActionModel();
				action.setId(method.getName().toLowerCase());
				action.setMethod(method);
				action.setTarget(modelObject);
				action.setName(getOrElse(actAnno.name(), method.getName()));
				action.setDescription(getOrElse(actAnno.description(), action.getName()));
				List<ParameterModel> parameters = Arrays.stream(method.getParameters()).map(new Function<java.lang.reflect.Parameter, ParameterModel>() {
					@Override
					public ParameterModel apply(java.lang.reflect.Parameter t) {
						ParameterModel ret = new ParameterModel();
						Parameter paramAnno = t.getAnnotation(Parameter.class);
						if(paramAnno != null) {
							ret.setName(getOrElse(paramAnno.name(), t.getName()));
							ret.setDescription(getOrElse(paramAnno.description(), ret.getName()));
							if(paramAnno.type() == ParamType.AUTO) {
								ret.setParamType(ParamType.from(t.getType()));
							} else {
								ret.setParamType(paramAnno.type());
							}
							ret.setValue(paramAnno.type().convert(paramAnno.value(), t.getType()));
							if(t.getType().isEnum()) {
								ret.setRange(t.getType().getEnumConstants());
							} else {
								ret.setRange(paramAnno.type().convert(paramAnno.range()));
							}
						} else {
							ret.setName(t.getName());
							ret.setParamType(ParamType.from(t.getType()));
							ret.setValue(null);
							if(t.getType().isEnum()) {
								ret.setRange(t.getType().getEnumConstants());
							}
						}
						ret.setId(ret.getName().toLowerCase());
						ret.setDataType(t.getType());
						return ret;
					}
				}).collect(Collectors.toList());
				if(parameters.size() > 0) action.setParameters(parameters);
				actions.add(action);
			} else if(isGetter(method)) {
				Parameter paramAnno = method.getAnnotation(Parameter.class);
				String methodName = method.getName();
				if(methodName.startsWith("get")) methodName = methodName.substring(3);
				else methodName = methodName.substring(2);
				ParameterModel prop = new ParameterModel();
				prop.setName(getOrElse(paramAnno.name(), StringUtils.decapitalize(methodName)));
				prop.setDescription(getOrElse(paramAnno.description(), prop.getName()));
				prop.setUnit(getOrElse(paramAnno.unit(), ""));
				if(paramAnno.type() == ParamType.AUTO) {
					prop.setParamType(ParamType.from(method.getReturnType()));
				} else {
					prop.setParamType(paramAnno.type());
				}
				prop.setMethod(method);
				prop.setTarget(modelObject);
				prop.setRange(paramAnno.type().convert(paramAnno.range()));
				prop.setDataType(method.getReturnType());
				properties.add(prop);
			} else if(isSetter(method)) {
				Parameter paramAnno = method.getAnnotation(Parameter.class);
				ParameterModel prop = new ParameterModel();
				prop.setName(getOrElse(paramAnno.name(), method.getName().substring(3)));
				prop.setDescription(getOrElse(paramAnno.description(), method.getName()));
				if(paramAnno.type() == ParamType.AUTO) {
					prop.setParamType(ParamType.from(method.getParameterTypes()[0]));
				} else {
					prop.setParamType(paramAnno.type());
				}
				prop.setMethod(method);
				prop.setTarget(modelObject);
				prop.setRange(paramAnno.type().convert(paramAnno.range()));
				prop.setDataType(method.getParameterTypes()[0]);
				settings.add(prop);
			}
		}
		Collections.sort(actions);
		Collections.sort(properties);
		Collections.sort(settings);
		thingModel.setActions(actions);
		thingModel.setProperties(properties);
		return thingModel;
	}
	
	private static boolean isAction(Method method) {
		return method.isAnnotationPresent(Action.class);
	}
	
	private static boolean isGetter(Method method) {
		if(!method.isAnnotationPresent(Parameter.class)) return false;
		if(method.getParameterCount() != 0 || method.getReturnType() == Void.class) return false;
		if(method.getName().startsWith("get")) return true;
		boolean isBoolean = method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class;
		return isBoolean && method.getName().startsWith("is");
	}
	
	private static Collection<Method> getMethods(Class<?> clazz) {
		Map<String, Method> methods = new LinkedHashMap<String, Method>();
		while(!clazz.equals(Object.class) && clazz != null) {
			for(Method m : clazz.getDeclaredMethods()) {
				if(!methods.containsKey(m.getName())) {
					methods.put(m.getName(), m);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return methods.values();
	}
	
	private static boolean isSetter(Method method) {
		if(!method.isAnnotationPresent(Parameter.class)) return false;
		if(method.getParameterCount() != 1 && method.getReturnType() != Void.class) return false;
		return method.getName().startsWith("set");
	}
	
	private static String getOrElse(String name, String def) {
		if(name == null || name.length() == 0) return def;
		return name;
	}
}
