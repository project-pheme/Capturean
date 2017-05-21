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
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.Stack;

import java.util.TimeZone;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.RedditPost;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.GenericSerialize;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.pusher.client.Pusher;
import com.pusher.client.channel.ChannelEventListener;

public class SubredditStream {

	static Logger LOGGER = Logger
			.getLogger(SubredditStream.class.getName());
	
	// ATTRIBUTES AND PRIVATE CLASSES
	private Pusher pusher;
	private static Integer num_threads = 0;

	private Queue<Thread> threadsQueue = new LinkedList<Thread>();

	private boolean first = true;
	private Long startTime = null;
	private boolean timeThresholdReached = false;

	StreamProducer<RedditPost> sp;

	boolean terminate = false;

	private static class Tuple{

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

	public static class RunnableOnEvent implements Runnable {

		String arg0;
		String arg1;
		String arg2;
		String dcID;
		Pusher pusher;
		StreamProducer<RedditPost> sp;
		enum State {REPLIES, BODY, BODYOK};
		enum BodyAction {KEEP, UP};

		public RunnableOnEvent(String arg0, String arg1, String arg2, String dcID, Pusher pusher,StreamProducer<RedditPost> sp){
			this.arg0 = arg0;
			this.arg1 = arg1;
			this.arg2 = arg2;
			this.dcID = dcID;
			this.pusher = pusher;
			this.sp = sp;
		}

		public void run() {

			synchronized (num_threads){num_threads++;} // Thread gets its turn

			try{
				// Build the JSON mapper and map the content of the reddit post to a JSON tree
				ObjectMapper mapper = new ObjectMapper();
				mapper.enable(SerializationFeature.INDENT_OUTPUT);
				JsonNode tree = mapper.readTree(arg2); 

				// Get the metadata fields
				String url = tree.get("url").textValue();
				String post = tree.get("title").textValue();
				String id = tree.get("id").textValue();
				
				//LOGGER.info("");
				//LOGGER.info("#### THREAD #" + num_threads.toString()+":");
				//LOGGER.info("POST: " + post);
				//LOGGER.info("URL: " + url);
				//LOGGER.info("");
				 
				// Now we have the URL obtained by pusher and required to send requests:

				// Get the json string (source post + comments)
				HttpResponse<String> response = null;
				String[] responseString = null;
				int limit = Integer.MAX_VALUE;
				try {
					synchronized (pusher){try {Thread.sleep(50000);} catch (InterruptedException e1) {e1.printStackTrace();}} // wait a few seconds for new comments before sending the request
					//url = "https://www.reddit.com/r/AskReddit/comments/411l9a/youve_just_arrived_in_2016_from_20_years_ago_what/"; // for testing purposes
					response = Unirest.get(url+".json?limit="+Integer.toString(limit)) // get all the comments ('limit' parameter = integer max value)
							.header("User-Agent", "Mozilla/5.0") // user-agent header required to avoid rejection from reddit servers
							.asString();
				} catch (UnirestException e) {e.printStackTrace();}

				// Split the response json string between source post and comments:

				LOGGER.info(response.getBody());

				responseString = response.getBody() 	
						.substring(1, response.getBody().length()-1) // remove initial "[" and final "]"
						.split("\\}\\}, \\{\"kind\": \"Listing\", \"data\": \\{\"modhash\":"); // do the split using this separator: }}, {"kind": "Listing", "data": {"modhash": 

				String source = responseString[0]+"}}"; // complete the json string of the source post after split operation

				String comments = "{\"kind\": \"Listing\", \"data\": {\"modhash\":"+responseString[1]; // complete the json string of the replies after split operation

				//LOGGER.info(source);
				//LOGGER.info(comments);

				JsonNode sourceData = mapper.readTree(source).get("data").get("children").get(0).get("data");
				
				boolean emptyStack;
				JsonNode roots = mapper.readTree(comments).get("data").get("children"); // .get(replyIndex).get("data") to get a children node. .replies on this node to check if it has replies
				int numberTrees = roots.size();
				Stack<Tuple> stackNodes = new Stack<Tuple>();
				Tuple treeTuple = null; //new Tuple(node,replyIndex);
				Object replies = null;
				JsonNode node = null;
				String kind = null;
				JsonNode currentRoot = null;
				for (Integer indexRoot = 0; indexRoot < numberTrees; indexRoot++){ // loop for first-level replies

					currentRoot = roots.get(indexRoot);
					treeTuple = new Tuple(currentRoot,0);
					emptyStack = false;

					LOGGER.info("");
					LOGGER.info("");
					LOGGER.info("###############################");
					LOGGER.info("ROOT #" + indexRoot.toString()+": "+ currentRoot.get("data").get("body"));
					LOGGER.info("###############################");
					LOGGER.info("");

					// Deep-first-search (post-order) for each first-level reply
					while(!emptyStack){ 

						node = treeTuple.getNode().get("data");
						kind = treeTuple.getNode().get("kind").textValue();
						replies = node.get("replies");
						//LOGGER.info(replies.toString());
						// if the comment has no replies, or all the replies have been read:

						if (kind.equals("more") || replies instanceof TextNode || treeTuple.getIndex() == ((JsonNode) replies).get("data").get("children").size()){

							if (!kind.equals("more")){

								//here we prepare capture messages
								RedditPost singlePost = new RedditPost();
								
								Long timestamp = node.get("created_utc").asLong();
								Date date = new Date(timestamp * 1000);
								SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
								
								dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
								String formatted = dateFormatter.format(date);
								
								singlePost.setCreatedAt(formatted);
								
								singlePost.setId(node.get("name").textValue());
								singlePost.setSourceType("reddit");
								singlePost.setText(node.get("body").textValue());
								singlePost.setUserScreenName(node.get("author").textValue());
								singlePost.setDcId(dcID);
								singlePost.setSourceUrl(url);
								
								JsonNode copy = node.deepCopy();
								if (copy instanceof ObjectNode) {
							        ObjectNode object = (ObjectNode) copy;
							        object.remove("replies");
							        singlePost.setRawJson(object);
							    }
								
								LOGGER.info("Mapper:");
								LOGGER.info(mapper.writeValueAsString(singlePost));

								//Send RedditPost to kafka
								sp.send(singlePost);
							}

							if (!stackNodes.isEmpty()){
								treeTuple=stackNodes.pop(); // get and remove comment from stack
							}
							else{
								emptyStack=true;
							}

						}

						// if the comment has replies and there are still replies to read:
						else{

							stackNodes.push(new Tuple(treeTuple.getNode(),treeTuple.getIndex()+1)); // store the current node into the stack and the corresponding index indicating which reply to read once this reply have been read
							treeTuple = new Tuple(((JsonNode)replies).get("data").get("children").get(treeTuple.getIndex()),0); // get the ith children reply as indicated in the stack
						}

					}

				}
				
				//
				// here we have the root post
				//
				RedditPost singlePost = new RedditPost();
				
				Long timestamp = sourceData.get("created_utc").asLong();
				Date date = new Date(timestamp * 1000);
				SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				
				dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String formatted = dateFormatter.format(date);
				
				singlePost.setCreatedAt(formatted);
				
				singlePost.setId(sourceData.get("name").textValue());
				singlePost.setSourceType("reddit");
				
				singlePost.setText(sourceData.get("selftext").textValue());
				//post.setTitle(sourceData.get("title").textValue());
				singlePost.setUserScreenName(sourceData.get("author").textValue());
				singlePost.setDcId(dcID);
				singlePost.setSourceUrl(url);
				
				JsonNode copy = sourceData.deepCopy();
				if (copy instanceof ObjectNode) {
			        ObjectNode object = (ObjectNode) copy;
			        object.remove("replies");
			        singlePost.setRawJson(object);
			    }
				
				LOGGER.info("Root:");
				LOGGER.info(mapper.writeValueAsString(singlePost));
				
				//Send RedditPost to kafka
				sp.send(singlePost);
				
			}catch (Exception e) {
				e.printStackTrace();
				LOGGER.severe("Exception connecting to a new post: exiting event handler.");
				return;
			}

			synchronized (num_threads){num_threads--;} // Allow a new post in the queue to be listened to
		}


	}


	// CONSTRUCTOR
	public SubredditStream(final String dcID, String subreddit) {

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
		
		String eventName = "new-listing";
		
		ChannelEventListener callback = new ChannelEventListener() {	

			// Method which describes what to do in case there is a new post on the subreddit
			public void onEvent(String arg0, String arg1, String arg2) {

				Thread th = (new Thread(new RunnableOnEvent(arg0, arg1, arg2, dcID, pusher, sp)));
				threadsQueue.add(th);
				LOGGER.info("New thread " + th + " added to the queue" + " - " + arg0);
				
				// Two conditions to start reading:
				
				int capacityThreshold = 5; // capacity condition: Queue size > 20.
				int timeThreshold = 200000; // time condition: 200000 miliseconds past after the capacity condition is true for the first time.

				// First threshold: don't read a post till there are 20 posts in the queue
				if (threadsQueue.size() > capacityThreshold){

					// Second threshold: don't read a post till it is 200000 miliseconds past after the 10th post was read
					if (timeThresholdReached){
						Thread thHead = threadsQueue.remove();
						thHead.start();
						LOGGER.info("Thread "+ thHead +"started");
					}

					// if the first was already read and its 20000 miliseconds past, set timeThresholdReached = true, so it starts polling posts when the next post arrives.
					else if (startTime!=null && System.currentTimeMillis() - startTime > timeThreshold){
						LOGGER.info("Time threshold reached: " + (System.currentTimeMillis() - startTime) + " milis");
						timeThresholdReached = true;
					}

					// if this is the first post received, start timer.
					else if (first){
						first = false;
						startTime = System.currentTimeMillis();
						LOGGER.info("First post obtained after reaching capacity threshold ("+ capacityThreshold +"). Waiting for a window of " + timeThreshold + " miliseconds to start polling posts from the queue");
					}
				}
			}


			public void onSubscriptionSucceeded(String arg0) {
				LOGGER.info("Pusher connected. Socket Id is: " + arg0);
				LOGGER.info("");
			}

		};

		pusher = new Pusher("50ed18dd967b455393ed");	
		pusher.subscribe(subreddit, callback, eventName);
	}


	// METHODS
	public void listenComments(String startDate, String endDate) throws InterruptedException{
		
		//wait until start date
		long startWait = calculateSleepingTime(startDate);
		LOGGER.info("startWait: " + startWait);
		Thread.sleep(startWait);
		
		pusher.connect();
		
		//finish thread when end date is reached
		//Thread.sleep(calculateSleepingTime(endDate));

		//finish thread when end date is reached or terminate
		long endWait = calculateSleepingTime(endDate);
		LOGGER.info("endWait: " + endWait);
		while(calculateSleepingTime(endDate)>0 && !terminate){}
	}
	
	public void terminate() {
		LOGGER.info("Setting Terminate = true");
		terminate = true;
		pusher.disconnect();
	}
	
	private long calculateSleepingTime(String endCaptureDate) {
		long sleepTime = 0;
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		Date date = null;
		try {
			date = format.parse(endCaptureDate);

			if (date.after(new Date(timestamp))) {
				sleepTime = (date.getTime() - timestamp);
			}
		} catch (ParseException ex) {

		}
				
		return sleepTime;
	}


}
