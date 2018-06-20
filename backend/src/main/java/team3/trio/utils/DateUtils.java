package team3.trio.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class DateUtils {

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static Date toDate(String dateString) throws ParseException {
		return formatter.parse(dateString);
	} 
	public static String toString(Date date) {
		return formatter.format(date);
	} 
}
