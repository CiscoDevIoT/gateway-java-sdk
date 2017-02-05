package com.cisco.deviot.gateway.sample;

import java.util.Random;

import com.cisco.deviot.gateway.common.Action;
import com.cisco.deviot.gateway.common.Parameter;

public class Mock {
	private int value;
	
	@Parameter
	public int getValue() {
		this.value = new Random().nextInt(100);
		return value;
	}
	
	@Action(name="beep")
	public void beep(@Parameter(name="duration", value="10") int duration) {
		System.out.println("Bee " + duration + " ping...");
	}
}
