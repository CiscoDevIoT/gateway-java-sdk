package com.cisco.deviot.gateway.service.internal;

import java.io.Serializable;

import com.cisco.deviot.gateway.common.ParamType;

@SuppressWarnings("serial")
public class ParameterModel extends Callable implements Serializable, Cloneable, Comparable<ParameterModel> {
	private String id;
	private String name;
	private int type;
	private Object value;
	private Object[] range;
	private String description;
	private String unit;
	private transient ParamType paramType;
	private transient Class<?> dataType;

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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public void setParamType(ParamType type) {
		this.type = type.value();
		this.paramType = type;
	}
	public ParamType getParamType() {
		return paramType;
	}
	public Class<?> getDataType() {
		return dataType;
	}
	public void setDataType(Class<?> dataType) {
		this.dataType = dataType;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Object[] getRange() {
		return range;
	}
	public void setRange(Object[] range) {
		this.range = range != null && range.length != 0 ? range : null;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
    @Override  
    public Object clone() {  
        try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			ParameterModel p = new ParameterModel();
			p.setId(getId());
			p.setName(getName());
			p.setParamType(getParamType());
			p.setValue(getValue());
			p.setRange(getRange());
			p.setMethod(getMethod());
			p.setTarget(getTarget());
			return p;
		}  
    } 
	@Override
	public String toString() {
		return name;
	}
	@Override
	public int compareTo(ParameterModel o) {
		return name.compareTo(o.name);
	}
}
