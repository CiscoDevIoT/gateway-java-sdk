package com.cisco.deviot.gateway.sample;

import java.net.InetAddress;

import com.cisco.deviot.gateway.service.Gateway;

public class Main {
	public static void main(String[] args) throws Exception {
		Gateway gw = new Gateway("js_" + InetAddress.getLocalHost().getHostName().toLowerCase(), "http://127.0.0.1:9000", "mqtt://127.0.0.1:18883");
		gw.register("mock_01", "JavaMock 01", new Mock());
		gw.start();
		while(true) {
			Thread.sleep(1000);
			gw.sendData();
		}
	}
}
