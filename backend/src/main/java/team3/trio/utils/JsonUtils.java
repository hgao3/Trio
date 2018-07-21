package team3.trio.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonUtils {
	
	public static JsonObject toJsonObject(String nonJson) {
		return toJsonElement(nonJson).getAsJsonObject();
	} 
	public static JsonArray toJsonArray(String nonJson) {
		return toJsonElement(nonJson).getAsJsonArray();
	} 
	public static JsonElement toJsonElement(String nonJson) {
		JsonParser parser = new JsonParser();
		return parser.parse(nonJson);
	}
	public static final String ERROR_MISSING = " is missing from request json";
	public static Object findElementFromJson(JsonObject jo, String name, String returnType) throws Exception {
		if (jo != null && jo.has(name)) {
			if (returnType.equals("String")) {
				return jo.get(name).getAsString();
			} else if (returnType.equals("Long")) {
				return jo.get(name).getAsLong();
			} else if (returnType.equals("Boolean")) {
				return jo.get(name).getAsBoolean();
			}
		}
		throw new Exception(name + ERROR_MISSING);
	}
	
}
