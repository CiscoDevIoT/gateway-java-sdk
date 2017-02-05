package com.cisco.deviot.gateway.util;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JsonUtils {
    private static final Gson gson;
    static {
    	GsonBuilder builder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
    	builder.registerTypeAdapter(Class.class, new JsonDeserializer<Class<?>>() {
			@Override
			public Class<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				try {
					return Class.forName(json.getAsJsonPrimitive() .getAsString());
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
    	});
    	gson = builder.create();
    }

    public static <T> T parseJson(String text, Class<T> targetClass) {
        return gson.fromJson(text, targetClass);
    }

    @SuppressWarnings("unchecked")
	public static <T> T parseObject(Object object, Class<T> targetClass) throws RuntimeException {
        if(object == null) {
            if(targetClass.isArray()) {
                return (T)Array.newInstance(targetClass.getComponentType(), 0);
            }
            return null;
        }
        return parseJson(toJson(object), targetClass);
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
}

