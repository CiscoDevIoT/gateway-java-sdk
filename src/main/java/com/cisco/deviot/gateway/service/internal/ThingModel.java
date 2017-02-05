package com.cisco.deviot.gateway.service.internal;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ThingModel implements Serializable {
	private String id;
	private String name;
	private String kind;
	
	private List<ParameterModel> properties;
	private List<ActionModel> actions;

	public ThingModel() {
	}
	
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
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public List<ActionModel> getActions() {
		return actions;
	}
	public void setActions(List<ActionModel> actions) {
		this.actions = actions;
	}
	public List<ParameterModel> getProperties() {
		return properties;
	}
	public void setProperties(List<ParameterModel> properties) {
		this.properties = properties;
	}
	public ActionModel findAction(String actionName) {
		for(ActionModel act: getActions()) {
			if(act.getName().equals(actionName)) {
				return act;
			}
		}
		return null;
	}
}
