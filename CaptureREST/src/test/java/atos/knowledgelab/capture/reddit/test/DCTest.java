package atos.knowledgelab.capture.reddit.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.RedditDataSource;

public class DCTest {

	
	
	
	
	public static void main(String[] args) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
		DataChannel dc1 = new DataChannel();
		RedditDataSource rds = new RedditDataSource();
		rds.setSubreddits(Arrays.asList(new String[] {"subreddit1", "subreddit2"}));
		List<DataSource> listDS = new ArrayList<>();
		
		listDS.add(rds);
		
		
		dc1.setDataSources(listDS);
		
		String serialized = mapper.writeValueAsString(dc1);
		
		System.out.println(serialized);
		
		DataChannel dc2 = mapper.readValue(serialized, DataChannel.class);
		
		
		String serialized2 = mapper.writeValueAsString(dc2);
		
		System.out.println(serialized2);
		
		
		String serialized3 = "{\n" + 
				"  \"name\": \"Reddit datachannel\",\n" + 
				"  \"type\": \"search\",\n" + 
				"  \"description\": \"\",\n" + 
				"  \"startCaptureDate\": \"2017-01-27 12:48:09.000\",\n" + 
				"  \"endCaptureDate\": \"2017-02-02 17:50:09.000\",\n" + 
				"  \"status\": \"active\",\n" + 
				"  \"dataSources\": [\n" + 
				"    {\n" + 
				"      \"type\": \"reddit\",\n" + 
				"      \"subreddits\": [\n" + 
				"        \"worldnews\",\n" + 
				"        \"switzerland\"\n" + 
				"      ],\n" + 
				"      \"keywords\": \"trump\"\n" + 
				"    }\n" + 
				"  ]\n" + 
				"}\n" + 
				"";

		DataChannel dc3 = mapper.readValue(serialized3, DataChannel.class);
		
		
		String serialized4 = mapper.writeValueAsString(dc3);
		
		System.out.println(serialized4);
		
		//merge DS from dc 1 and dc3
		
		List<DataSource> dsList1 = dc3.getDataSources();
		List<DataSource> dsList2 = dc1.getDataSources();
		
		dsList1.addAll(dsList2);
		
		String serialized5 = mapper.writeValueAsString(dc3);
		
		System.out.println(serialized5);
		
	}

}
