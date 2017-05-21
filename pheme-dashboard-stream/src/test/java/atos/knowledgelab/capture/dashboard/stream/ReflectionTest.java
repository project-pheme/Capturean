package atos.knowledgelab.capture.dashboard.stream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ReflectionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy'T'HH:mm:ssXXX");
		formatter.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
		
		ReflectionTest t1 = new ReflectionTest();
		t1.testMethod("hello world!");
		
		Date d = new Date();
		System.out.println(d);
		System.out.println(formatter.format(d));
		
		
		
		
		
		
		try {
			Class<?> type = Class.forName("atos.knowledgelab.capture.dashboard.stream.Test");
			Object obj = type.newInstance();
			Method[] methods = type.getDeclaredMethods();
			for (Method m : methods) {
				System.out.println(m.getName());
				
			}
			
			Method m = type.getMethod("testMethod", Object.class);
			m.setAccessible(true);
			m.invoke(obj, "hello");
			
//			Test t3 =  (Test) t2.cast(Test.class);
//			t3.testMethod("Uuuuu");
			
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	
	
	
	
	public void testMethod(Object o) {
		String s = (String) o;
		
		System.out.println(s);
		System.out.println("Hellooo!");
	}
}
