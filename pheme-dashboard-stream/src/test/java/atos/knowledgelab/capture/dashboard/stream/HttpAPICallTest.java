package atos.knowledgelab.capture.dashboard.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class HttpAPICallTest {
	
	public static void main(String[] args) throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost httppost = new HttpPost("https://api.weblyzard.com/0.1/token");
		
		//StringEntity params = new StringEntity(mapper.writeValueAsString(ddoc));
		//httppost.addHeader("content-type", "application/json");
		httppost.addHeader("Authorization", "Basic YXBpQHBoZW1lLndlYmx5emFyZC5jb206cmp6UWsxMUZEWmhNVQ==");
		try {
			//System.out.println("Sending: " + mapper.writeValueAsString(ddoc));
			
			
			HttpResponse response = client.execute(httppost);
			System.out.println(response.getStatusLine().toString());
			
			
			BufferedReader rd = new BufferedReader(
			        new InputStreamReader(response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			
			System.out.println(result);
			
		} finally {
			httppost.releaseConnection();
		}
	}

}
