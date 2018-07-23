package team3.trio.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonObject;

public class DateUtils {

	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static SimpleDateFormat formatter3 = new SimpleDateFormat("MMMMM dd, yyyy");
	private static SimpleDateFormat formatter4 = new SimpleDateFormat("MM/dd/yyyy");
	
	
	public static Date toDateFromDashboard(String dateString) throws ParseException {
		return DateUtils.toDate(dateString, formatter);
	}
	
	public static String toDashboardString(Date date) {
		return DateUtils.toString(date, formatter);
	}
	
	public static Date toDateFromDB(String dateString) throws ParseException {
		return DateUtils.toDate(dateString, formatter3);
	}
	
	public static Date toDateFromISO(String dateString) throws ParseException {
		return DateUtils.toDate(dateString, formatter4);
	}
	
	public static String toDBString(Date date) {
		return DateUtils.toString(date, formatter3);
	}
	
	public static String toIsoString(Date date) {
		return DateUtils.toString(date, formatter4);
	}
	
	public static Date toDate(String dateString, SimpleDateFormat formatter) throws ParseException {
		return formatter.parse(dateString);
	} 
	public static String toString(Date date, SimpleDateFormat formatter) {
		if (date == null) {return "";}
		return formatter.format(date);
	} 
	
	
}
