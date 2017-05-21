package atos.knowledgelab.capture.stream.translator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ssXXX");
		formatter.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		
		Date d = new Date();
		System.out.println(d);
		System.out.println(formatter.format(d));
	}

}
