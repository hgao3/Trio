package team3.trio.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class DateUtils {

	private static SimpleDateFormat formatter = new SimpleDateFormat("MMMMM dd, yyyy");
	private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	
	
	public static Date toDateFromDB(String dateString) throws ParseException {
		return DateUtils.toDate(dateString, formatter);
	}
	
	public static Date toDateFromISO(String dateString) throws ParseException {
		return DateUtils.toDate(dateString, formatter2);
	}
	
	public static String toDBString(Date date) {
		return DateUtils.toString(date, formatter);
	}
	
	public static String toIsoString(Date date) {
		return DateUtils.toString(date, formatter2);
	}
	
	public static Date toDate(String dateString, SimpleDateFormat formatter) throws ParseException {
		return formatter.parse(dateString);
	} 
	public static String toString(Date date, SimpleDateFormat formatter) {
		if (date == null) {return "";}
		return formatter.format(date);
	} 
	
	
}
