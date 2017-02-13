package com.cisco.deviot.gateway.util;

public class StringUtils {
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public static String decapitalize(String str) {
		if(isEmpty(str)) return str;
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
}
