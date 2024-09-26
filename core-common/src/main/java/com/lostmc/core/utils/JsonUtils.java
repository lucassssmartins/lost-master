package com.lostmc.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.lostmc.core.Commons;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonUtils {

	public static <T> T jsonToObject(JsonElement json, Class<T> clas) {
		return Commons.getGson().fromJson(json, clas);
	}
	
	public static <T> T jsonToObject(String json, Class<T> clas) {
		return Commons.getGson().fromJson(json, clas);
	}
	
	public static JsonElement jsonTree(Object src) {
		return Commons.getGson().toJsonTree(src);
	}

	public static String elementToString(JsonElement element) {
		if (element.isJsonPrimitive()) {
			JsonPrimitive primitive = element.getAsJsonPrimitive();
			if (primitive.isString()) {
				return primitive.getAsString();
			}
		}
		return Commons.getGson().toJson(element);
	}

	public static <T> T mapToObject(Map<String, String> map, Class<T> clazz) {
		JsonObject obj = new JsonObject();
		for (Entry<String, String> entry : map.entrySet()) {
			try {
				obj.add(entry.getKey(), Commons.getJsonParser().parse(entry.getValue()));
			} catch (Exception e) {
				obj.addProperty(entry.getKey(), entry.getValue());
			}
		}
		return Commons.getGson().fromJson(obj, clazz);
	}

	public static Map<String, String> objectToMap(Object src) {
		Map<String, String> map = new HashMap<>();
		JsonObject obj = (JsonObject) Commons.getGson().toJsonTree(src);
		for (Entry<String, JsonElement> entry : obj.entrySet()) {
			map.put(entry.getKey(), Commons.getGson().toJson(entry.getValue()));
		}
		return map;
	}
}
