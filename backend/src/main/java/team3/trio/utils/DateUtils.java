package team3.trio.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class DateUtils {

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	public static Date toDate(String dateString) throws ParseException {
		return formatter.parse(dateString);
	} 
	public static String toString(Date date) {
		if (date == null) {return "";}
		return formatter.format(date);
	} 
}
