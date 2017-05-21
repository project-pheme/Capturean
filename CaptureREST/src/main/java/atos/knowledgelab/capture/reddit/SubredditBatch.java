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
package atos.knowledgelab.capture.reddit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import atos.knowledgelab.capture.bean.RedditPost;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.GenericSerialize;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;


public class SubredditBatch {
	
	boolean terminate = false;

	static Logger LOGGER = Logger
			.getLogger(SubredditBatch.class.getName());
	
	String dcID;
	String startTimestamp;
	String endTimestamp;
	String timestamp;
	
	StreamProducer<RedditPost> sp;
	ObjectMapper mapper = new ObjectMapper();
	
	public SubredditBatch(String dcID, String startCaptureDate, String endCaptureDate) throws CaptureException, ParseException{
		
		//init with actual time
		this.timestamp = String.valueOf(System.currentTimeMillis()/1000);	
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		Date date = format.parse(startCaptureDate);
		this.startTimestamp = String.valueOf(date.getTime()/1000);
		date = format.parse(endCaptureDate);
		this.endTimestamp = String.valueOf(date.getTime()/1000);
		
		//run only if actual time is before end time
		if (Long.parseLong(endTimestamp) < Long.parseLong(timestamp)){
			this.terminate = true;	
		}
		
		this.dcID = dcID;
		
		try {
			//Configuration of the producer
			StreamProducerConfig conf = new StreamProducerConfig();
			
			Properties kafkaProperties = new Properties();
			kafkaProperties.load(this.getClass().getClassLoader().getResourceAsStream("kafka-reddit.properties"));
	
			//Here it is important to specify the broker (kafka node or nodes)
			//more details here: http://kafka.apache.org/08/configuration.html
			conf.put("metadata.broker.list", kafkaProperties.getProperty("metadata.broker.list").toString());
			conf.put("zookeeper.connect", kafkaProperties.getProperty("zookeeper.connect").toString());
			conf.put("serializer.class", kafkaProperties.getProperty("serializer.class").toString());
			conf.put("request.required.acks", kafkaProperties.getProperty("request.required.acks").toString());
			conf.put("kafka.topic", kafkaProperties.getProperty("kafka.topic").toString());
			// props.put("partitioner.class", props.getProperty("partitioner.class"));
			
			ISerialize<RedditPost> serializer = new GenericSerialize<RedditPost>(RedditPost.class);
		
			sp = new StreamProducer<RedditPost>(conf, serializer);	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class CDN_Unable extends Exception {
	   public CDN_Unable(String msg){
	      super(msg);
	   }
	}
	
	private class Tuple{

		private JsonNode node;
		private Integer index;

		public Tuple(){

		}

		public Tuple(JsonNode node, Integer index){
			this.node = node;
			this.index = index;
		}

		public JsonNode getNode() {
			return node;
		}

		public void setNode(JsonNode node) {
			this.node = node;
		}

		public Integer getIndex() {
			return index;
		}

		public void setIndex(Integer index) {
			this.index = index;
		}

	}


	public void getPost(String url) throws CDN_Unable{

		if (!terminate) {
			
//		mapper.setSerializationInclusion(Include.NON_NULL);
//		mapper.enable(SerializationFeature.INDENT_OUTPUT);

			try {
							
				// array for  "kind": "more" nodes, that require additional API calls to retrieve
				// (those are those folded comments, under "load more comments (XX replies)" links)
				ArrayList<JsonNode> moreComments = new ArrayList<JsonNode>();
				ArrayList<String> moreChildren = new ArrayList<String>();
	
				// Build the JSON mapper and map the content of the reddit post to a JSON tree
	
				// Get the json string (source post + comments)
				HttpResponse<String> response = null;
				String[] responseString = null;
				int limit = Integer.MAX_VALUE;
				try {
					LOGGER.info(url);
					
					response = Unirest.get(url+".json?limit="+Integer.toString(limit)) // get all the comments ('limit' parameter = integer max value)
							.header("User-Agent", "Mozilla/5.0") // user-agent header required to avoid rejection from reddit servers
							.asString();
				} catch (UnirestException e) {e.printStackTrace();}
	
				// Split the response json string between source post and comments:
				LOGGER.info(response.getBody());
				
				//Check if there is an exception 
				//	=> <h2>Our CDN was unable to reach our servers</h2>
				// and throw a special exception
				if (response.getBody().contains("<h2>Our CDN was unable to reach our servers</h2>")) throw new CDN_Unable("<h2>Our CDN was unable to reach our servers</h2>");
				
				responseString = response.getBody() 	
						.substring(1, response.getBody().length()-1) // remove initial "[" and final "]"
						.split("\\}\\}, \\{\"kind\": \"Listing\", \"data\": \\{\"modhash\":"); // do the split using this separator: }}, {"kind": "Listing", "data": {"modhash": 
	
				String source = responseString[0]+"}}"; // complete the json string of the source post after split operation
	
				String comments = "{\"kind\": \"Listing\", \"data\": {\"modhash\":"+responseString[1]; // complete the json string of the replies after split operation
	
				//LOGGER.info(source);
				//LOGGER.info(comments);
	
				// Object mapper for source post
				ObjectMapper mapper = new ObjectMapper();
				//mapper.enable(SerializationFeature.INDENT_OUTPUT);
				com.fasterxml.jackson.databind.JsonNode sourceData = mapper.readTree(source).get("data").get("children").get(0).get("data");	
	
				String id = sourceData.get("id").textValue();
	
				boolean emptyStack;
				// Object mapper for comments
				com.fasterxml.jackson.databind.JsonNode roots = mapper.readTree(comments).get("data").get("children"); // .get(replyIndex).get("data") to get a children node. .replies on this node to check if it has replies
				int numberTrees = roots.size();
				Stack<Tuple> stackNodes = new Stack<Tuple>();
				Tuple treeTuple = null; //new Tuple(node,replyIndex);
				Object replies = null;
				JsonNode node = null;
				String kind = null;
				com.fasterxml.jackson.databind.JsonNode currentRoot = null;
				for (Integer indexRoot = 0; indexRoot < numberTrees; indexRoot++){ // loop for first-level replies
	
					if (terminate == true) {
						break;
					}
					
					currentRoot = roots.get(indexRoot);
					treeTuple = new Tuple(currentRoot,0);
					emptyStack = false;
	
					/*
					LOGGER.info("");
					LOGGER.info("");
					LOGGER.info("###############################");
					LOGGER.info("ROOT #" + indexRoot.toString()+": "+ currentRoot.get("data").get("body"));
					LOGGER.info("###############################");
					LOGGER.info("");
					 */
	
					// Deep-first-search (post-order) for each first-level reply
					while(!emptyStack && !terminate){ 
	
						node = treeTuple.getNode().get("data");
						kind = treeTuple.getNode().get("kind").textValue();
						replies = node.get("replies");
	
						// if the comment has no replies, or all the replies have been read:
						if (kind.equals("more") || replies instanceof TextNode || treeTuple.getIndex() == ((JsonNode) replies).get("data").get("children").size()){
	
							if (!kind.equals("more")){
	
								//here we prepare capture messages
								RedditPost post = new RedditPost();
								
								Long timestamp = node.get("created_utc").asLong();
								Date date = new Date(timestamp * 1000);
								SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
								
								dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
								String formatted = dateFormatter.format(date);
								
								post.setCreatedAt(formatted);
								
								post.setId(node.get("name").textValue());
								post.setSourceType("reddit");
								post.setText(node.get("body").textValue());
								post.setUserScreenName(node.get("author").textValue());
								post.setDcId(dcID);
								post.setSourceUrl(url);
								
								JsonNode copy = node.deepCopy();
								if (copy instanceof ObjectNode) {
							        ObjectNode object = (ObjectNode) copy;
							        object.remove("replies");
							        post.setRawJson(object);
							    }
		
								LOGGER.info("Mapper:");
								LOGGER.info(mapper.writeValueAsString(post));
	
								//Send RedditPost to kafka
								sp.send(post);
								
							} else {
								LOGGER.info(">> More:");
								
								JsonNode childrenArray = node.get("children");
								int aSize = childrenArray.size();
								for (JsonNode child : childrenArray) {
									LOGGER.info(child.asText());
									moreChildren.add(child.asText());
								}
							}
	
							if (!stackNodes.isEmpty()){
								treeTuple=stackNodes.pop(); // get and remove comment from stack
							}
							else{
								emptyStack=true;
							}
	
						}
	
						// if the comment has replies and there are still replies to read:
						else {
							stackNodes.push(new Tuple(treeTuple.getNode(),treeTuple.getIndex()+1)); // store the current node into the stack and the corresponding index indicating which reply to read once this reply have been read
							treeTuple = new Tuple(((JsonNode)replies).get("data").get("children").get(treeTuple.getIndex()),0); // get the ith children reply as indicated in the stack
						}
	
					}
	
				}
	
				//
				// here we have the root post
				//
				RedditPost post = new RedditPost();
				
				Long timestamp = sourceData.get("created_utc").asLong();
				Date date = new Date(timestamp * 1000);
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				
				dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String formatted = dateFormatter.format(date);
				
				post.setCreatedAt(formatted);
				
				post.setId(sourceData.get("name").textValue());
				post.setSourceType("reddit");
				
				post.setText(sourceData.get("selftext").textValue());
				//post.setTitle(sourceData.get("title").textValue());
				post.setUserScreenName(sourceData.get("author").textValue());
				post.setDcId(dcID);
				post.setSourceUrl(url);
				
				JsonNode copy = sourceData.deepCopy();
				if (copy instanceof ObjectNode) {
			        ObjectNode object = (ObjectNode) copy;
			        object.remove("replies");
			        post.setRawJson(object);
			    }
	
				LOGGER.info("Root:");
				LOGGER.info(mapper.writeValueAsString(post));
						
				//Send RedditPost to kafka
				sp.send(post);
				
				
				//TODO treat "more comments"
				if (!moreChildren.isEmpty()){
					LOGGER.info("Start more comments");
					for (String child : moreChildren){
						
						if (terminate == true) {
							break;
						}
						
						try {
							getPost(url+child+"/");
						}
						catch (CDN_Unable moe){
							//if exception => <h2>Our CDN was unable to reach our servers</h2>
							//repeat getPost
							LOGGER.info("CDN_Unable Exception: "+moe);
							getPost(url+child+"/");
						}
						catch (Exception e){
							//else
							//can be images, links, etc -> omit post
							//print error and continue without stop the pipeline
							LOGGER.info("Other Exception: "+e);
						}
					
					}
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		
		}

	}


	public void getSubReddits(List<String> subreddits, String query) throws CDN_Unable, InterruptedException{

		LOGGER.info("startTimestamp: " + startTimestamp);
		LOGGER.info("endTimestamp: " + endTimestamp);
		
		//wait until start time is reached or terminate
		while ((Long.parseLong(timestamp)<Long.parseLong(startTimestamp)) && !terminate){
			timestamp = String.valueOf(System.currentTimeMillis()/1000);
		}
		
		timestamp = startTimestamp;
		
		//repeat until entTimestamp is reached or terminate
		while ((Long.parseLong(timestamp)<Long.parseLong(endTimestamp)) && !terminate){
		
			String actual_timestamp = String.valueOf(System.currentTimeMillis()/1000);
			//if actual time is after end time, set end time in search limits
			if (Long.parseLong(actual_timestamp)>Long.parseLong(endTimestamp)){
				actual_timestamp = endTimestamp;
			}
			
			LOGGER.info("");
			LOGGER.info("#######################");
			LOGGER.info("timestamp: " + timestamp);
			LOGGER.info("actual_timestamp: " + actual_timestamp);
			LOGGER.info("#######################");
			LOGGER.info("");

			// for every subreddit:
			for (String subreddit : subreddits){
				
				if (terminate == true) {
					break;
				}
				
				LOGGER.info("");
				LOGGER.info("#######################");
				LOGGER.info("SUBREDDIT: " + subreddit);
				LOGGER.info("#######################");
				LOGGER.info("");
	
				// Transform query (keywords) to reddit format
				String q = getRedditQuery(query, timestamp, actual_timestamp);
				
				LOGGER.info("");
				LOGGER.info("#######################");
				LOGGER.info("query: " + query);
				LOGGER.info("reddit query: " + q);
				LOGGER.info("#######################");
				LOGGER.info("");
					
				// Query parameters:
				String after = "";
	
				// For every result page:
				HttpResponse<String> response=null;
				boolean end = false;
				int pageCounter = 0;
				while(!end && !terminate){
	
					pageCounter++;
	
					LOGGER.info("");
					LOGGER.info("RESULTS PAGE Nº " + pageCounter);
	
					// GET POST i:
					response = null;
					try {
						response = Unirest.get("https://www.reddit.com/r/"+subreddit+"/search?q="+q+"&syntax=cloudsearch&restrict_sr=on&sort=relevance&t=all&count=25&after="+after)
								.header("User-Agent", "Mozilla/5.0") // user-agent header required to avoid rejection from reddit servers
								.asString();
						
					} catch (UnirestException e) {e.printStackTrace();}
	
					// Parse json:
					Document doc = Jsoup.parse(response.getBody());
	
					// Get results
					Elements searchResults = doc.getElementsByClass("search-result-header");
					for (Element element : searchResults){
						if (terminate == true) {
							break;
						}
						
						String text = element.getElementsByTag("a").text();
						String url = element.getElementsByTag("a").attr("href").replace("?ref=search_posts", "");
						// ?ref=search_posts is automatically added to the href field of the search results
						// it must be removed, so that getPost can obtain the post in json format.
	
						
						try {
							getPost(url);
						}
						catch (CDN_Unable moe){
							//if exception => <h2>Our CDN was unable to reach our servers</h2>
							//repeat getPost
							LOGGER.info("CDN_Unable Exception: "+moe);
							getPost(url);
						}
						catch (Exception e){
							//else
							//can be images, links, etc -> omit post
							//print error and continue without stopping the pipeline
							LOGGER.info("Other Exception: "+e);
						}
	
						LOGGER.info("Text: " + text);
						LOGGER.info("URL: " + url);	
						LOGGER.info("");
					}
	
	
					// get after
					try {
						after = doc.getElementsByClass("nextprev").toString().split("after=")[1].split("\" rel=")[0];
						LOGGER.info("After: " + after);
						LOGGER.info("");
						LOGGER.info("");
					} catch (Exception e) {
						// if exception, end = true;
						end = true;
						after = null;
					}
				}
			}
			
			//set actual timestamp
			timestamp = actual_timestamp;
			
			//wait 15 seconds
			LOGGER.info("Waiting 15 seconds");
			Thread.sleep(10000);
			
		}
		
	}


	// Query:
	//example -> q=(and timestamp:1486383218..1486988020 (or (field title 'party') (field title 'beatles')))
	//q=(and%20timestamp:1487258768..1487259868%20(or%20(field%20title%20%27party%27)%20(field%20title%20%27beatles%27)))
	/*//first approach with "," separators
	private String getRedditQuery(String query, String start_timestamp, String end_timestamp){
		
		//init query with timestamps
		String q = "(and%20timestamp:"+start_timestamp+".."+end_timestamp;
		
		//if there are keywords
		if (query!=null && !query.equals("")){
		
			List<String> OR_keywords = new ArrayList<String>();
			
			//split different keywords
			String[] keywords = query.split(",");
			for (String keyword : keywords){
				//cleaning
				keyword = keyword.trim();
				//case 1: exact match -> "donald trump"
				if (keyword.startsWith("\"") && keyword.endsWith("\"")){
					//replace "s and spaces in URL format
					keyword = keyword.replaceAll("\"", "").replaceAll(" ", "%20");
					//add to OR_keywords
					OR_keywords.add("(field%20title%20%27"+keyword+"%27)");
				}
				//case 2: more than 1 word -> donald trump
				else if (keyword.contains(" ")){
					//split keywords
					String more_keywords = "(and%20";
					for (String internal_keyword : keyword.split(" ")){
						more_keywords = more_keywords +"(field%20title%20%27"+internal_keyword+"%27)%20";
					}
					more_keywords = more_keywords +")";
					//add to OR_keywords
					OR_keywords.add(more_keywords);
				}
				//case 3: one word -> trump
				else {
					//add to OR_keywords
					OR_keywords.add("(field%20title%20%27"+keyword+"%27)");
				}
			}
			
			//build OR_keywords
			if (!OR_keywords.isEmpty()){
				q = q + "%20(or%20";
				for (String OR_keyword : OR_keywords){
					q = q + OR_keyword + "%20";
				}
				q = q + ")";
			}
			
		}
		
		q = q + ")";
		
		return q;
	}
	*/
	
	//second approach -> all as AND
	private String getRedditQuery(String query, String start_timestamp, String end_timestamp){
		
		//init query with timestamps
		String q = "(and%20timestamp:"+start_timestamp+".."+end_timestamp;
		
		//if there are keywords
		if (query!=null && !query.equals("")){
			
			List<String> AND_keywords = new ArrayList<String>();
			
			//clean
			query = query.replaceAll("\"", "");
						
			//split different keywords
			String[] keywords = query.split(" ");
			for (String keyword : keywords){
				if (!keyword.equals("")){
					//cleaning
					keyword = keyword.trim();
					AND_keywords.add("(field%20title%20%27"+keyword+"%27)");
				}
			}
			
			//build AND_keywords
			if (!AND_keywords.isEmpty()){
				q = q + "%20";
				
				//force OR
				q = q + "(or%20";
				//end
				for (String AND_keyword : AND_keywords){
					q = q + AND_keyword + "%20";
				}
				//force OR
				q = q + ")";
				//end
				
			}
			
		}
		
		q = q + ")";
		
		return q;
	}

	public void terminate() {
		LOGGER.info("Setting Terminate = true");
		terminate = true;
	}

}