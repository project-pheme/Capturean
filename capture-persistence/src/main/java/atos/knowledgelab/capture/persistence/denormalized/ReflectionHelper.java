/*******************************************************************************
 * 	Copyright (C) 2017  ATOS Spain S.A.
 *
 * 	This file is part of the Capturean software.
 *
 * 	This program is dual licensed under the terms of GNU Affero General
 * 	Public License and proprietary for commercial usage.
 *
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as
 * 	published by the Free Software Foundation, either version 3 of the
 * 	License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 	You can be released from the requirements of the license by purchasing
 * 	a commercial license or negotiating an agreement with Atos Spain S.A.
 * 	Buying such a license is mandatory as soon as you develop commercial
 * 	activities involving the Capturean software without disclosing the source 
 * 	code of your own applications. 
 *
 * 	
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.persistence.denormalized;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.hbase.util.Bytes;

import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.Tweet;

public class ReflectionHelper {

	public static void printAnnotatedMethodsAll(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Method[] methods = clazz.getMethods();
		
		Object value;
		System.out.println("\nClass annotations:");
		for (Annotation annotation : clazz.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			System.out.println("Annotation: " + type);
			for (Method m : type.getDeclaredMethods()) {
				try {
					value = m.invoke(annotation, (Object[]) null);
					System.out.println(m + " -> " + value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		System.out.println("\nMethod annotations:");
		for (Method method : methods) {

			try {
				
				for (Annotation annotation : method.getAnnotations()) {
					Class<? extends Annotation> type = annotation.annotationType();
					
					System.out.println(type.getDeclaredMethod("name", null));
					
					for (Method m : type.getDeclaredMethods()) {
						value = m.invoke(annotation, (Object[]) null);
						System.out.println(method + " :: " + m + " -> " + value);
					}
				}
				
				//Annotation a = method.getAnnotation(annotationClass);
				//value = method.invoke(a, (Object[]) null);
				
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	public static Map<byte[], byte[]> findAndExecuteAnnotatedMethodsForClass(
			Class<?> lookedUpClass, 
			Class<? extends Annotation> annotationClass,
			String annotationMethodName,
			Object obj) throws InstantiationException {
		
		Map<byte[], byte[]> result = new HashMap<>();
		
		Method[] methods = lookedUpClass.getMethods();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		System.out.println("\nClass annotations:");
		for (Annotation annotation : lookedUpClass.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			System.out.println("Annotation: " + type);
			for (Method m : type.getDeclaredMethods()) {
				try {
					value = m.invoke(annotation, (Object[]) null);
					System.out.println(m + " -> " + value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		System.out.println("\nMethod annotations:");
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				annotatedMethods.add(method);
				
				try {
					
					for (Annotation annotation : method.getAnnotations()) {
						Class<? extends Annotation> type = annotation.annotationType();
						for (Method m : type.getDeclaredMethods()) {
							if (m.getName().toString().equalsIgnoreCase(annotationMethodName)) {
								//Classm.getReturnType();
								Class<?> rType = method.getReturnType();
								value = m.invoke(annotation, (Object[]) null);
								System.out.println(method + " :: " + m + " -> " + value);
								
								Object response = method.invoke(obj, (Object[]) null);
								rType.cast(response);
								System.out.println(" >> " + rType.cast(response));

								//Be careful here. We assume that all fields are mappable either
								//to String, to Date, to Integer or to Double. 
								//In case a new field type is added,
								//it will probably be ignored!
								//TODO use generic types <T>
								if (rType.cast(response) instanceof String) {
									result.put(Bytes.toBytes((String)value), Bytes.toBytes((String)rType.cast(response)));
								}
								if (rType.cast(response) instanceof Date) {
									Date d = (Date) rType.cast(response);
									SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
									result.put(Bytes.toBytes((String)value), Bytes.toBytes(dt.format(d)));
								}
								if (rType.cast(response) instanceof Integer) {
									Integer i = (Integer) rType.cast(response);									
									result.put(Bytes.toBytes((String)value), Bytes.toBytes(i));
								}
								if (rType.cast(response) instanceof Double) {
									Double d = (Double) rType.cast(response);									
									result.put(Bytes.toBytes((String)value), Bytes.toBytes(d));
								}
							}
						}
					}
					
					//Annotation a = method.getAnnotation(annotationClass);
					//value = method.invoke(a, (Object[]) null);
					
				} catch (IllegalAccessException e) {
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
		}
		return result;
	}

	public static Object populateFieldsWithAnnotatedMethodsForClass(
			Map<String, byte[]> fields,
			Class<?> lookedUpClass, 
			Class<? extends Annotation> annotationClass,
			String annotationMethodName,
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Method[] methods = lookedUpClass.getMethods();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				annotatedMethods.add(method);
				
				try {
					
					for (Annotation annotation : method.getAnnotations()) {
						Class<? extends Annotation> type = annotation.annotationType();
						for (Method m : type.getDeclaredMethods()) {
							if (m.getName().toString().equalsIgnoreCase(annotationMethodName)) {
								//Classm.getReturnType();
								Class<?> rType = method.getReturnType();
								value = m.invoke(annotation, (Object[]) null);
								System.out.println(method + " :: " + m + " -> " + value);
								
								
								
								
//								Object response = method.invoke(obj, (Object[]) null);
//								rType.cast(response);
//								System.out.println(" >> " + rType.cast(response));

								//Be careful here. We assume that all fields are mappable either
								//to String, to Date, to Integer or to Double. 
								//In case a new field type is added,
								//it will probably be ignored!
								//TODO use generic types <T>
								System.out.println(lookedUpClass);
								//String typeString = method.getReturnType().getTypeName();
								Class<?> returnTypeClass = method.getReturnType();
								
								//System.out.println(typeString);
								//Object o = rType.newInstance();
								//Object o = rType.getConstructor(parameterTypes);

						
								Field field = null;
								try {
									field = lookedUpClass.getDeclaredField(value.toString());
									
									
									
									field.setAccessible(true);
									
									if (returnTypeClass.isAssignableFrom(String.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											field.set(obj, new String(b));
										}
										
									}
//									if (typeString.equalsIgnoreCase("java.lang.String")) {
//										byte[] b = fields.get((String)value);
//										if (b != null) {
//											field.set(obj, new String(b));
//										}
//										
//									}
									if (returnTypeClass.isAssignableFrom(Integer.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											field.set(obj, new Integer(new String(b)));
										}
										
									}
//									if (typeString.equalsIgnoreCase("java.lang.Integer")) {
//										byte[] b = fields.get((String)value);
//										if (b != null) {
//											field.set(obj, new Integer(new String(b)));
//										}
//										
//									}
									if (returnTypeClass.isAssignableFrom(Double.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											field.set(obj, new Double(new String(b)));
										}
										
									}
//									if (typeString.equalsIgnoreCase("java.lang.Double")) {
//										byte[] b = fields.get((String)value);
//										if (b != null) {
//											field.set(obj, new Double(new String(b)));
//										}
//										
//									}
									if (returnTypeClass.isAssignableFrom(Date.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											
											SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
											Date d = dt.parse(new String(b));
											
											field.set(obj, (Date) d);
										}
										
									}
//									if (typeString.equalsIgnoreCase("java.util.Date")) {
//										byte[] b = fields.get((String)value);
//										if (b != null) {
//											
//											SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//											Date d = dt.parse(new String(b));
//											
//											field.set(obj, (Date) d);
//										}
//										
//									}
									
								} catch (NoSuchFieldException e) {
									
									if (field != null && field.getDeclaringClass() == lookedUpClass) {
										System.out.println("Error! Field " + value.toString() + " couldn't be read!");
										
									} else {
										System.out.println(value.toString() + " is a superclass method. Skipping.");
									}
								}
								
							}
						}
					}
					
					//Annotation a = method.getAnnotation(annotationClass);
					//value = method.invoke(a, (Object[]) null);
					
				} catch (IllegalAccessException e) {
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
		}
		
		return null;
	}
	
	
	public static Object populateFieldsGenericForClass(
			Map<String, byte[]> fields,
			Class<?> lookedUpClass, 
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Method[] methods = lookedUpClass.getMethods();
		Field[] classFields = lookedUpClass.getDeclaredFields();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Field field : classFields) {
			System.out.println("Field: " + field.getName());
//			if (method.isAnnotationPresent(annotationClass)) {
//				annotatedMethods.add(method);
				
			System.out.println(lookedUpClass);
			//String typeString1 = field.getType().getTypeName();
			Class<?> fieldTypeClass = field.getType();
			//System.out.println(typeString1);
			
			try {
				//field2 = lookedUpClass.getDeclaredField(value.toString());
				
				
				field.setAccessible(true);
				Object o = field.get(obj);
				System.out.println(o);
				
				
				//if (typeString1.equalsIgnoreCase("java.lang.String")) {
				if (fieldTypeClass.isAssignableFrom(String.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						field.set(obj, new String(b));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.lang.Integer")) {ç
				if (fieldTypeClass.isAssignableFrom(Integer.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						field.set(obj, new Integer(new String(b)));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.lang.Double")) {
				if (fieldTypeClass.isAssignableFrom(Double.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						field.set(obj, new Double(new String(b)));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.util.Date")) {
				if (fieldTypeClass.isAssignableFrom(Date.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						
						SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
						Date d = dt.parse(new String(b));
						
						field.set(obj, (Date) d);
					}
					
				}
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				
				
			//}
		}
		
		return null;
	}
	
	public static Map<String, Object> getFieldsGenericForClass(
			Class<?> lookedUpClass, 
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] methods = lookedUpClass.getMethods();
		Field[] classFields = lookedUpClass.getDeclaredFields();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Field field : classFields) {
			System.out.println("Field: " + field.getName());
//			if (method.isAnnotationPresent(annotationClass)) {
//				annotatedMethods.add(method);
				
			System.out.println(lookedUpClass);
			//String typeString1 = field.getType().getTypeName();
			Class<?> fieldTypeClass = field.getType();
			System.out.println(fieldTypeClass);
			//System.out.println(typeString1);
			
			try {
				//field2 = lookedUpClass.getDeclaredField(value.toString());
				
				
				field.setAccessible(true);
				Object o = field.get(obj);
				System.out.println(o);
				
				map.put(field.getName(), o);
				
				
				
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
				
				
			//}
		}
		
		return map;
	}
	
	
	
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
		// TODO Auto-generated method stub

//		for (Annotation annotation : DataChannel.class.getAnnotations()) {
//			Class<? extends Annotation> type = annotation.annotationType();
//			System.out.println("Values of " + type.getName());
//
//			for (Method method : type.getDeclaredMethods()) {
//				Object value = method.invoke(annotation, (Object[]) null);
//				System.out.println(" " + method.getName() + ": " + value);
//			}
//		}

		//printAnnotatedMethodsAll(Tweet.class, javax.xml.bind.annotation.XmlElement.class);
		System.out.println("=====================================");

		Tweet t = new Tweet();
		t.setCreatedAt(new Date());
		t.setUserScreenName("user1");
		
		System.out.println(t.getCreatedAt());
		try {
			findAndExecuteAnnotatedMethodsForClass(Tweet.class, javax.xml.bind.annotation.XmlElement.class, "name", t);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=====================================");

		Map<String, byte[]> fields = new HashMap<String, byte[]>();
		fields.put("text", Bytes.toBytes("bla bla bla"));
		
		Date d = new Date();
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		fields.put("createdAt", Bytes.toBytes(dt.format(d)));
		fields.put("userScreenName", Bytes.toBytes("nickname"));
		
		
		Tweet t1 = new Tweet();
		try {
			populateFieldsWithAnnotatedMethodsForClass(fields, Tweet.class, javax.xml.bind.annotation.XmlElement.class, "name", t1);
			System.out.println(t1.getText());
			System.out.println(t1.getUserScreenName());
			System.out.println(t1.getCreatedAt());
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		System.out.println("=====================================");
		
		DataPool dp1 = new DataPool();
		dp1.setPoolID("poolid");
		dp1.setName("dp name");
		dp1.setDescription("dp desc");
		dp1.setKeywords(Arrays.asList(new String[] {"ssss", "ssss"}));
		
		fields = new HashMap<String, byte[]>();
		fields.put("name", Bytes.toBytes("bla bla bla"));
		
		
		try {
			Map<String, Object> map1 = getFieldsGenericForClass(DataPool.class, dp1);
			MapUtils.debugPrint(System.out, "Result:", map1);
			System.out.println(dp1.getName());
			
			populateFieldsGenericForClass(fields, DataPool.class, dp1);
			
			System.out.println(dp1.getName());
			
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
