package com.cisco.deviot.gateway.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.deviot.gateway.common.Connector;
import com.cisco.deviot.gateway.common.ServiceMode;
import com.cisco.deviot.gateway.service.internal.GatewayModel;
import com.cisco.deviot.gateway.util.HttpUtils;

public class Gateway {
	private final Logger log = LoggerFactory.getLogger(Gateway.class);

	private static final String API_URL = "/api/v1/gateways";
	
	private GatewayModel gateway;
	private String account;
	private String deviotServer;
	private Connector connector;
	private boolean started;

	public Gateway(String name, String deviotServer, String mqttServer) {
		this(name, deviotServer, mqttServer, "");
	}

	public Gateway(String name, String deviotServer, String mqttServer, String account) {
		this.gateway = new GatewayModel(name.replaceAll("[@/\\.]", ""));
		this.account = account;
		this.deviotServer = deviotServer;
		this.connector = new MqttConnector(this.gateway, account, mqttServer);
	}

	public void sendData() {
		this.connector.publish(getData());
	}
	
	public void sendData(Map<String, Object> data) {
		this.connector.publish(data);
	}
	
	public Map<String, Object> getData() {
		return this.gateway.getData();
	}

	public Gateway register(String id, String name, Object thing) {
		if(gateway.containsThing(id)) {
			log.warn("thing {} already registered", id);
		} else {
			this.gateway.addThing(id, name, thing);
			log.info("thing {}[{}] registered", id, name);
		}
		return this;
	}
	
	public Gateway unregister(String id) {
		if(gateway.containsThing(id)) {
			log.info("thing {} unregistered", id);
			gateway.removeThing(id);
		} else {
			log.warn("thing {} not registered", id);
		}
		return this;
	}
	
	public void start() {
		if(started) {
			log.warn("Gateway servie {} started already", gateway.name);
		} else {
			started = true;
			new Thread(new Runnable() {
				@Override
				public void run() {
					registerSelf();
				}
			}).start();
			connector.start();
			log.info("Gateway servie {} started", gateway.name);
		}
	}
	
	private void registerSelf() {
		Boolean failed = null;
		Map<String, Object> gatewayModel = new HashMap<>();
		gatewayModel.put("name", gateway.name);
		gatewayModel.put("mode", ServiceMode.MQTT.ordinal());
    	gatewayModel.put("owner", account);
    	gatewayModel.put("host", connector.getHost());
    	gatewayModel.put("port", connector.getPort());
    	gatewayModel.put("data", connector.getData());
    	gatewayModel.put("action", connector.getAction());
    	gatewayModel.put("sensors", gateway.getModels());
		while(started) {
	    	try {
				HttpUtils.postJson(deviotServer + API_URL, gatewayModel, Collections.emptyMap());
				if((failed == null || failed)) log.info("Gateway service {} registered to {}", gateway.name, deviotServer);
				failed = false;
			} catch (IOException e) {
				if(failed == null || !failed) log.error("Gateway service {} registered to {} failed: {}", gateway.name, deviotServer, e.getMessage());
				failed = true;
			}
			try {
				Thread.sleep(100000);
			} catch (InterruptedException e) {
				break;
			}
		}
		started = false;
	}
	
	public void stop() {
		if(started) {
			try {
				HttpUtils.delete(deviotServer + API_URL + "/" + gateway.name, Collections.emptyMap());
				log.info("Gateway service {} deregistered from {}", gateway.name, deviotServer);
			} catch (IOException e) {
			}
			started = false;
			connector.stop();
			log.info("Gateway service {} stopped", gateway.name);
		} else {
			log.warn("Gateway service {} already stopped", gateway.name);
		}
	}
}
