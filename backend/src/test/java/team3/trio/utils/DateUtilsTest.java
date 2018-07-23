package team3.trio.utils;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class DateUtilsTest {

	@Test
	public void test() throws Exception {
		
		String isoDate = "01/20/2018";
		Date date = DateUtils.toDateFromISO(isoDate);
		Assert.assertTrue(isoDate.equals(DateUtils.toIsoString(date)));

		
		
	}

}
