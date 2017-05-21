package atos.knowledgelab.capture.dashboard.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.importer.beans.DashboardDocument;
import atos.knowledgelab.capture.stream.config.ConsumerClientConfig;

public class APICaller {

	String endpoint;
	String token;
	String url;
	String auth;
	
	HttpClient client = HttpClientBuilder.create().build();
	ObjectMapper mapper = new ObjectMapper();
	private static Logger LOGGER = Logger.getLogger(APICaller.class.getName());
	
	private boolean debugOnly = false;

	public APICaller(String token, String url) {

		
		// this.endpoint = endpoint;
		this.token = token;
		this.url = url;

		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		if (debugOnly == true) {
			LOGGER.warning("The streaming component is running in DEBUG_ONLY mode! No message will be sent to the API.");			
		}

	}
	
	public APICaller() throws IOException {
		//read API details from config file
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("stream.properties"));
				
		this.token = props.getProperty("target.token");
		this.url = props.getProperty("target.uri");
		this.auth  = props.getProperty("target.auth.key");
		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		if (debugOnly == true) {
			LOGGER.warning("The streaming component is running in DEBUG_ONLY mode! No message will be sent to the API.");			
		}

	}

	public void sendDocument(DashboardDocument ddoc) throws IOException {
		//in debug only mode we don't actually send anything, but only print the object
		if (debugOnly == true) {
		
			sendHTTPPOSTDummy(ddoc);
			
		} else {
			try {
				int returnCode = sendHTTPPOST(ddoc);
				
				if (returnCode == 401) {
					LOGGER.info("Getting new security token and resending document...");
					this.token = getToken();
					returnCode = sendHTTPPOST(ddoc);
					LOGGER.info("Resent doc with status HTTP " + returnCode);
				}
				
				
			} catch(Exception e) {
				LOGGER.log(Level.SEVERE, "Unexpected error on sending data to Modul API", e);
			}
		}
		

	}
	
	private int sendHTTPPOSTDummy(DashboardDocument ddoc) throws UnsupportedOperationException, IOException {
		LOGGER.info("Dummy Sending: " + mapper.writeValueAsString(ddoc));
		return 0;
	}
	
	private int sendHTTPPOST(DashboardDocument ddoc) throws UnsupportedOperationException, IOException {
		HttpPost httppost = new HttpPost(this.url);
		
		StringEntity params = new StringEntity(mapper.writeValueAsString(ddoc));
		httppost.addHeader("content-type", "application/json");
		httppost.addHeader("Authorization", "Bearer " + this.token);
		httppost.setEntity(params);
		
		int returnHTTPCode = 0;
		
		try {
			LOGGER.info("Sending: " + mapper.writeValueAsString(ddoc));
			
			
			HttpResponse response = client.execute(httppost);
			LOGGER.info(response.getStatusLine().toString());
			
			returnHTTPCode = response.getStatusLine().getStatusCode();
			
			if (returnHTTPCode == 401) {
				LOGGER.info("Got HTTP 401 code.");
										
			} else {
				
				BufferedReader rd = new BufferedReader(
				        new InputStreamReader(response.getEntity().getContent()));

				StringBuffer result = new StringBuffer();
				String line = "";
				while ((line = rd.readLine()) != null) {
					result.append(line);
				}
				
				//System.out.println(result);				
				LOGGER.info(result.toString());
			}
			
		} finally {
			httppost.releaseConnection();
		}
		return returnHTTPCode;

	}
	
	public synchronized String getToken() throws IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost("https://api.weblyzard.com/0.1/token");
		
		String token = "";
		
		//StringEntity params = new StringEntity(mapper.writeValueAsString(ddoc));
		//httppost.addHeader("content-type", "application/json");
		httppost.addHeader("Authorization", "Basic " + this.auth);
		try {
			//System.out.println("Sending: " + mapper.writeValueAsString(ddoc));
			
			
			HttpResponse response = client.execute(httppost);
			
			LOGGER.info("Getting token: " + response.getStatusLine().toString());
			
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			LOGGER.info("Got token " + result);
			token = result.toString();
			
		} finally {
			httppost.releaseConnection();
		}
		
		return token;
		
	}
}
