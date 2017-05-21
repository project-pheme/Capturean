package atos.knowledgelab.capture.dashboard.stream;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateParsing {

	public static void main(String[] args) throws ParseException {
		// TODO Auto-generated method stub
		
		String incomingDate = "Tue Jul 26 16:18:55 +0000 2016";
		
		Object date = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US).parse(incomingDate);
		
		TimeZone tz = TimeZone.getTimeZone("UTC");
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
		isoFormat.setTimeZone(tz);
		String isoDate = isoFormat.format(date);
		
		System.out.println(isoDate);
	}

}
