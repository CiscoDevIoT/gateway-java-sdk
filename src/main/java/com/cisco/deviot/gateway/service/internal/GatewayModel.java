package com.cisco.deviot.gateway.service.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.deviot.gateway.common.ParamType;

public class GatewayModel {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public final String name;
	private Map<String, ThingModel> things = new HashMap<>();

	public GatewayModel(String name) {
		this.name = name;
	}
	
	public ThingModel addThing(String id, String name, Object thing) {
		ThingModel thingModel = ModelUtils.buildModel(id, name, thing);
		this.things.put(thingModel.getId(), thingModel);
		return thingModel;
	}
	
	public boolean containsThing(String id) {
		return this.things.containsKey(id);
	}
	
	public void removeThing(String id) {
		this.things.remove(id);
	}
	
	public Collection<ThingModel> getModels() {
		return this.things.values();
	}
	
	public Map<String, Object> getData() {
    	Map<String, Object> result = new HashMap<String, Object>();
		for(String name : things.keySet()) {
			Map<String, Object> data = getData(name);
			if(!data.isEmpty()) result.put(name, data);
		}
		return result;
	}
	
	public Map<String, Object> getData(String name) {
		ThingModel thingModel = this.things.get(name);
		if(thingModel == null) throw new IllegalArgumentException("thing is not found: " + name);
		Map<String, Object> ret = new HashMap<String, Object>();
		for(ParameterModel p : thingModel.getProperties()) {
			Object value = p.invoke();
			ret.put(p.getName(), value);
		}
		return ret;
	}
	
	public void triggerAction(String name, String action, Map<String, Object> params) {
		ThingModel thingModel = this.things.get(name);
		if(thingModel == null) throw new IllegalArgumentException("thing is not found: " + name);
		ActionModel act = thingModel.findAction(action);
		if(act == null) {
			log.warn("Action " + action + " is not defined in " + name);
			throw new IllegalArgumentException("action is not found: " + action);
		}
		List<ParameterModel> parameters = new ArrayList<ParameterModel>();
		Map<String, ParameterModel> paramMap = act.getParametersMap();
		for(String paramName : paramMap.keySet()) {
			ParameterModel param = paramMap.get(paramName);
			if(param.getParamType() != ParamType.OBJECT) {
				ParameterModel p = (ParameterModel)param.clone();
				Object val = params.remove(paramName);
				if(val != null) {
					p.setValue(param.getParamType().convert(val.toString(), p.getDataType()));
				}
				parameters.add(p);
			} else {
				ParameterModel p = (ParameterModel)param.clone();
				p.setValue(params);
				parameters.add(p);
			}
		}
		act.invoke(parameters);
	}
}
