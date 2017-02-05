package com.cisco.deviot.gateway.common;

public class Color {
	public final int red;
	public final int green;
	public final int blue;
	
	public static Color of(String val) {
		return new Color(val);
	}
	
	private Color(String colorVal) {
		if(colorVal.startsWith("#")) colorVal = colorVal.substring(1);
		while(colorVal.length() < 6) colorVal = "0" + colorVal; // padding with 0
		String[] rgb = colorVal.toUpperCase().split("(?<=\\G.{2})");
		red = Integer.parseInt(rgb[0], 16);
		green = Integer.parseInt(rgb[1], 16);
		blue = Integer.parseInt(rgb[2], 16);
	}
}
