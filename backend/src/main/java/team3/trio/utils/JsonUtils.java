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
}
