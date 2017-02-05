package com.cisco.deviot.gateway.service.internal;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.cisco.deviot.gateway.common.ParamType;

@SuppressWarnings("serial")
public class ActionModel extends Callable implements Serializable, Comparable<ActionModel> {
	private String id;
	private String name;
	private String description;
	private boolean needPayload;
	
	private List<ParameterModel> parameters = Collections.emptyList();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean needPayload() {
		return needPayload;
	}
	public List<ParameterModel> getParameters() {
		return parameters;
	}
	public void setParameters(List<ParameterModel> parameters) {
		this.parameters = parameters;
		this.needPayload = parameters.stream().anyMatch(p -> p.getParamType() == ParamType.OBJECT);
	}	
	public Map<String, ParameterModel> getParametersMap() {
		Map<String, ParameterModel> ret = new LinkedHashMap<>();
		for(ParameterModel param : parameters) {
			ret.put(param.getName(), param);
		}
		return ret;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int compareTo(ActionModel o) {
		return name.compareTo(o.name);
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
