package com.cisco.deviot.gateway.common;

import java.util.Map;

public interface Connector {
	public void publish(Map<String, Object> data);
	public void start();
	public void stop();
	public String getHost();
	public int getPort();
	public String getData();
	public String getAction();
}
