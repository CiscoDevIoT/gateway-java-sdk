package com.cisco.deviot.gateway.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.deviot.gateway.common.Connector;
import com.cisco.deviot.gateway.service.internal.GatewayModel;
import com.cisco.deviot.gateway.util.JsonUtils;
import com.cisco.deviot.gateway.util.StringUtils;

public class MqttConnector implements Connector {
	private static final Logger log = LoggerFactory.getLogger(MqttConnector.class);
	
	private abstract class MqttClientSupport implements MqttCallback {
		protected MqttClient client;
		protected MqttConnectOptions connOpts;
		protected String[] topics;
		
		public MqttClientSupport(String serverUrl, String topic) {
			try {
				this.client = new MqttClient(serverUrl, "DevIoT-" + System.currentTimeMillis(), new MemoryPersistence());
			} catch (MqttException e) {
				throw new IllegalArgumentException(e);
			}
			this.connOpts = new MqttConnectOptions();
			this.connOpts.setCleanSession(true);
			this.client.setCallback(this);
		}
		
		@Override
		public void connectionLost(Throwable t) {
			log.error("{} lost connection to {} - {}", client.getServerURI(), t.getMessage(), t);
			tryReconnect(client, connOpts);
		}
		
		@Override
		public void deliveryComplete(IMqttDeliveryToken token) {
		}
		
		public void connect() {
			tryReconnect(client, connOpts);
		}
		
		public void publish(String topic, MqttMessage message) throws MqttException, MqttPersistenceException {
			client.publish(topic, message);
		}

		public void disconnect() {
			close(client);
		}
	}
	
	private MqttClientSupport client;
	private GatewayModel gateway;
	private String host;
	private int port;
	private String dataTopic;
	private String actionTopic;
	private String mqttServer;
	
	public MqttConnector(GatewayModel gateway, String account, String mqttServer) {
		this.gateway = gateway;
		String ns = account.replace("@", "-");
		if(StringUtils.isEmpty(ns)) ns = "-";
		this.dataTopic = String.format("/deviot/%s/%s/data", ns, gateway.name);
		this.actionTopic = String.format("/deviot/%s/%s/action", ns, gateway.name);
		try {
			URL u = new URL(mqttServer.replaceAll("mqtts", "https").replaceAll("mqtt", "http"));
			this.host = u.getHost();
			this.port = u.getPort() < 1 ? 1883 : u.getPort();
			this.mqttServer = mqttServer.replaceAll("mqtts", "ssl").replaceAll("mqtt", "tcp");
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public void publish(Map<String, Object> data) {
        try {
            MqttMessage message = new MqttMessage(JsonUtils.toJson(data).getBytes());
			client.publish(dataTopic, message);
	        log.debug("Published message to " + dataTopic + ": " + data);
		} catch (MqttPersistenceException e) {
			log.error("Failed to deliver message to " + dataTopic + ": " + e.getMessage(), e);
		} catch (MqttException e) {
			log.error("Failed to deliver message to " + dataTopic + ": " + e.getMessage(), e);
		}
	}

	public void start() {
		client = new MqttClientSupport(mqttServer, actionTopic) {
			@SuppressWarnings("unchecked")
			public void messageArrived(String topic, MqttMessage message) throws Exception {
				String body = new String(message.getPayload());
				Map<String, Object> data = (Map<String, Object>)JsonUtils.parseJson(body, HashMap.class);
	        	String id = (String)data.remove("id");
	        	if(id == null) id = (String)data.remove("name");
	        	String action = (String)data.remove("action");
	        	if(id != null || action != null) {
		        	gateway.triggerAction(id, action, data);
	        	} else {
	    			log.warn("id/action not available in message " + data);
	        	}
			}
		};
		client.connect();
	}

	public void stop() {
		client.disconnect();
	}

	@Override
	public String getHost() {
		return host;
	}

	@Override
	public int getPort() {
		return port;
	}

	@Override
	public String getData() {
		return dataTopic;
	}

	@Override
	public String getAction() {
		return actionTopic;
	}
	
	private static void close(MqttClient client) {
		if(client != null) {
	        try {
	            client.close();
	            log.info("disconnected from mqtt server {}", client.getServerURI());
	        } catch(MqttException me) {
	        }
		}
	}
	
	private static void delay(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}
	
	private void tryReconnect(MqttClient client, MqttConnectOptions connOpts) {
		long time = 500;
		while(client != null) {
			time = time * 2;
	        try {
	            client.connect(connOpts);
    			client.subscribe(actionTopic);
				log.info("connected to mqtt server: {}/{}", client.getServerURI(), actionTopic);
	            return;
	        } catch(MqttException e) {
				log.error("{} failed to connect to {}/{} - {}, reconnecting in {} ms...", client.getServerURI(), actionTopic, e.getMessage(), time);
				delay(time);
	        }		
		}
	}
}
