package com.cisco.deviot.gateway.common;

import java.util.HashMap;
import java.util.Map;

import com.cisco.deviot.gateway.util.StringUtils;


/**
 * Supported parameter type
 * 
 * @author haihxiao
 *
 */
public enum ParamType {
	NUMBER(0),
	STRING(1),
	BOOL(2),
	COLOR(3),
	BINARY(4),
	OBJECT(99),
	AUTO(-1);
	
	private final int value;
	
	ParamType(int val) {
		this.value = val;
	}
	
	public int value() {
		return value;
	}
		
	private static Map<Class<?>, ParamType> mapping = new HashMap<>();
	static {
		mapping.put(byte.class, ParamType.NUMBER);
		mapping.put(char.class, ParamType.NUMBER);
		mapping.put(short.class, ParamType.NUMBER);
		mapping.put(int.class, ParamType.NUMBER);
		mapping.put(float.class, ParamType.NUMBER);
		mapping.put(double.class, ParamType.NUMBER);
		mapping.put(Byte.class, ParamType.NUMBER);
		mapping.put(Character.class, ParamType.NUMBER);
		mapping.put(Short.class, ParamType.NUMBER);
		mapping.put(Integer.class, ParamType.NUMBER);
		mapping.put(Float.class, ParamType.NUMBER);
		mapping.put(Double.class, ParamType.NUMBER);
		mapping.put(boolean.class, ParamType.BOOL);
		mapping.put(Boolean.class, ParamType.BOOL);
		mapping.put(String.class, ParamType.STRING);
		mapping.put(Map.class, ParamType.OBJECT);
	}
	
	public static ParamType from(Class<?> type) {
		ParamType ret = mapping.get(type);
		if(ret == null) ret = ParamType.STRING;
		return ret;
	}
	
	public Object[] convert(String[] obj) {
		Object[] ret = new Object[obj.length];
		for(int i=0;i<ret.length;i++) {
			ret[i] = convert(obj[i]);
		}
		return ret;
	}
	
	public Object convert(String obj) {
		if(this == ParamType.NUMBER) {
			return StringUtils.isEmpty(obj) ? 0 : Double.valueOf(obj);
		} else if(this == ParamType.BOOL) {
			return StringUtils.isEmpty(obj) ? false : Boolean.parseBoolean(obj);
		}
		return obj;
	}
	
	public Object convert(String obj, Class<?> expectedType) {
		if(expectedType == int.class || expectedType == Integer.class) {
			return StringUtils.isEmpty(obj) ? 0 : Double.valueOf(obj).intValue();
		} else if(expectedType == long.class || expectedType == Long.class) {
			return StringUtils.isEmpty(obj) ? 0L : Double.valueOf(obj).longValue();
		} else if(expectedType == float.class || expectedType == Float.class) {
			return StringUtils.isEmpty(obj) ? 0 : Double.valueOf(obj).floatValue();
		} else if(expectedType == double.class || expectedType == Double.class) {
			return StringUtils.isEmpty(obj) ? 0 : Double.valueOf(obj);
		} else if(expectedType == byte.class || expectedType == Byte.class) {
			return StringUtils.isEmpty(obj) ? (byte)0 : Double.valueOf(obj).byteValue();
		} else if(expectedType == short.class || expectedType == Short.class) {
			return StringUtils.isEmpty(obj) ? (short)0 : Double.valueOf(obj).shortValue();
		} else if(expectedType == boolean.class || expectedType == Boolean.class) {
			return StringUtils.isEmpty(obj) ? false : Boolean.parseBoolean(obj);
		} else if(expectedType.isEnum()) {
			Object[] ecs = expectedType.getEnumConstants();
			for(Object ec : ecs) {
				if(ec.toString().equalsIgnoreCase(obj)) return ec;
			}
			try {
				int index = Integer.parseInt(obj);
				if(index > 0 && index < ecs.length) return ecs[index];
			} catch(NumberFormatException nfe) {}
		}
		return obj;
	}
}
