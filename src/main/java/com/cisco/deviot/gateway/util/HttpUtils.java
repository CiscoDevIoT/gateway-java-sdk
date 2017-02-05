package com.cisco.deviot.gateway.util;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

/**
 * Authors: Hai-Hua Xiao (haihxiao@cisco.com)
 * Date: 15/10/3
 **/
public class HttpUtils {
    public static final String CONTENT_TYPE_JSON = "application/json; encoding=utf-8";

    public static String exec(String url, String method, String body, Map<String, String> headers) throws IOException {
        HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();
        if(headers != null) {
        	for(Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
        	}
        }
        conn.setRequestMethod(method);
        if(body != null && body.length() != 0) {
            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            out.write(body);
            out.flush();
            out.close();
        }
        return IOUtils.toString(conn.getInputStream(), Charset.forName("UTF-8"));
    }
    
    public static String get(String url) throws IOException {
        return get(url, Collections.emptyMap());
    }

    public static String get(String url, Map<String, String> headers) throws IOException {
        return exec(url, "GET", null, Collections.emptyMap());
    }

    public static String delete(String url) throws IOException {
        return delete(url, Collections.emptyMap());
    }

    public static String delete(String url, Map<String, String> headers) throws IOException {
        return exec(url, "DELETE", null, Collections.emptyMap());
    }

    public static String postJson(String url, String content, Map<String, String> headers) throws IOException {
		Map<String, String> nheaders = new HashMap<String, String>(headers);
    	nheaders.put("Content-Type", CONTENT_TYPE_JSON);
        return post(url, content, nheaders);
    }

    public static String postJson(String url, Map<?, ?> content, Map<String, String> headers) throws IOException {
        return postJson(url, JsonUtils.toJson(content), headers);
    }

    public static String putJson(String url, String content, Map<String, String> headers) throws IOException {
    	Map<String, String> nheaders = new HashMap<String, String>(headers);
    	nheaders.put("Content-Type", CONTENT_TYPE_JSON);
        return put(url, content, nheaders);
    }

    public static String putJson(String url, Map<?, ?> content, Map<String, String> headers) throws IOException {
        return putJson(url, JsonUtils.toJson(content), headers);
    }

    public static String post(String url, String content, Map<String, String> headers) throws IOException {
        return exec(url, "POST", content, headers);
    }

    public static String put(String url, String content, Map<String, String> headers) throws IOException {
        return exec(url, "PUT", content, headers);
    }
}
