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
package pheme.gathering.scheduler.twitter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.util.DataChannelState;
import atos.knowledgelab.capture.util.TweetUtils;
import pheme.gathering.search.TweetManager;
import pheme.gathering.util.CaptureFacadeFactory;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterQueryManager implements Observer {

	//
	private static TwitterQueryManager instance;

	
	ArrayList<TwitterAPI> apiList = new ArrayList<TwitterAPI>();
	private LinkedBlockingQueue<QueryData> queryList = new LinkedBlockingQueue<QueryData>();
	
	//temporary query queue data (temporary parameters, but also intermediate tweets)
	HashMap<String, TemporaryQueryData> temporalQueryData = new HashMap<>();
	
	
	//
	private Properties properties = new Properties();
	private final static Logger LOGGER = Logger.getLogger(TwitterQueryManager.class.getName());
	private int apiLimit = 0;
	
	//access to facades (for data storage and query retrieval)
	private CaptureFacadeIf cfi;

	
	public TwitterQueryManager() throws Exception {
		//get facade object
		this.cfi = CaptureFacadeFactory.getInstance();

		
		//Loading Twitter properties
		properties.load(this.getClass().getClassLoader().getResourceAsStream("twitter-scheduler.properties"));
		
		String apiCountParam = properties.getProperty("api.count");
		try {
			int apiCount = Integer.parseInt(apiCountParam);
			
			for (int i=1; i<=apiCount; i++) {
				String prefix = "api." + i + ".";
				
				String OauthCKey = properties.getProperty(prefix + "oauth.consumerKey");
				String OauthCSec = properties.getProperty(prefix + "oauth.consumerSecret");
				String OauthATok = properties.getProperty(prefix + "oauth.accessToken");
				String OauthATSec = properties.getProperty(prefix + "oauth.accessTokenSecret");
				String authMode = properties.getProperty(prefix + "twitter.applicationonly");

				try {
					if (authMode.equalsIgnoreCase("true") == true) {
						apiLimit = Integer.parseInt(properties.getProperty(prefix + "api.limit.applicationonly"));
					} else {
						apiLimit = Integer.parseInt(properties.getProperty(prefix + "api.limit.applicationonly"));
					}
				} catch (NumberFormatException e) {
					//fallback to the lowest limit.
					apiLimit = 180;

					LOGGER.info(" ##### Twitter config file error!");
				} 
				
				String streamMode = "kafka";

				TwitterAPI api = new TwitterAPI(OauthCKey, OauthCSec, OauthATok, OauthATSec, authMode, apiLimit, streamMode); 
				LOGGER.info("Registering Twitter API " + i);
				api.setApiName("API " + i);
				apiList.add(api);
				
			}
			

		} catch (NumberFormatException e) {
			LOGGER.severe("api.count should be a number!");
			e.printStackTrace();
		} catch (Exception e) {
			LOGGER.severe("Can't initialize Twitter API with given parameters!");
			e.printStackTrace();
		}
		
		//register APIs, with the observer pattern
		
		

		
		
	}

	public static synchronized TwitterQueryManager getInstance() throws Exception {
		if(instance == null) {
			instance = new TwitterQueryManager();         
		}
		return instance;
	}
	
	public synchronized void notifyNewQueries() {
		this.notifyAll();
	}
	
	public void start() {
		Timer timerQueryUpdate = new Timer();

		/*
		 * We start three threads here:
		 * 
		 * 1) the "queryListUpdate" thread:
		 * 	  one that will periodically update internal query list
		 *    with active queries (active = those that should be running
		 *    in the moment of checking)
		 * 2) the query search loop:
		 *    the main "worker" thread that loops over available queries
		 *    and performs twitter search & store tweets
		 * 3) the notification thread:
		 *    the thread that watches and react to new queries added to the system
		 *    (if new query is added that should be running, the thread will
		 *    call queryListUpdate and add it to the query list.
		 * 
		 */
		
		
		//schedule query update process every 60 seconds 
		//(do it in 3 seconds, to give some time for capture to warm)
		timerQueryUpdate.scheduleAtFixedRate(new TimerTask() {
			@Override
			public synchronized void run() {
				queryListUpdate();
				System.out.println("Scheduled periodic update!");
				instance.notifyNewQueries();
				
				
			}
			
		}, 2000, 15 * 1000);

		Timer searchLoop = new Timer();

		//schedule the main loop
		searchLoop.schedule(new TimerTask() {
			@Override
			public synchronized void run() {
				querySearchLoop();

				
			}
			
		}, 4000);
		
		//wake up on new query added from REST services
		Thread notifyingThread = new Thread(new Runnable() {

			@Override
			public synchronized void run() {
				while (true) {
					System.out.println(" T >> waiting");
					cfi.getQueryQueue().waitForNewQueries();
					System.out.println(" T >> notify"); 
					queryListUpdate();
					instance.notifyNewQueries();
				}
				
			}
			
		});
		
		notifyingThread.start();
		
	}
	
	
	/**
	 * 
	 * This is the main loop for iterating active queries and invoking search API.
	 * This method handles the "queryList" object and distributes search queries over available APIs ("apiList").
	 * The loop runs until all search queries are finished (e.g. all tweets has been retrieved).
	 * 
	 * 
	 */
	protected synchronized void querySearchLoop() {
		//first use the existing notification mechanism to avoid unecessary polling
		//in case there is no active query
		//cfi.getQueryQueue().getActiveQuery();
		
		while (true) {
			
			try {
				System.out.println("Iterate over queries");
				System.out.println("count: " + queryList.size());
				//cfi.getQueryQueue().getActiveQuery();
				
				
				LinkedHashMap<String, QueryData> queriesMap = cfi.getActiveQueriesMapByDSType("twitter");
				Set<Entry<String, QueryData>> queries = queriesMap.entrySet();

				//iterate over all queries & apis
				int i = 0;
				TwitterAPI api;
				
				Iterator<QueryData> iterator = queryList.iterator();
				
				
				int count = 0;
				while(iterator.hasNext()) {
					//debug
					count++;
					System.out.println("Iteration " + count);
					QueryData query = iterator.next();

					//choose api (in round robin fashion)
					
	        		
	        		//choose most suitable API (the one that has the least time to wait)
	        		api = selectAPI();
	        		
					//ensure proper rate limit 
					//check if we need to wait for API
	        		if (api.getTimeToNextBatch() > 0) {
	        			System.out.println("Waiting for " + api.getTimeToNextBatch() + " seconds...");
		        		Thread.sleep(api.getTimeToNextBatch() * 1000);
	        			
	        		}
	        		
	        		
	        		//TODO: we shouldn't block on search, but rather here
	        		//otherwise we will never choose a proper API
	        		//we should: read all rate limit on search, store it in the TwitterAPI object, make it available
	        		//here choose API based on that data
	        		//if we should block, then do it here (we can use a API's method for that)
	        		//we will wait the least amount of time (always choose API where we wait the least amount of time)
					
					//load current query data
					TemporaryQueryData qData = temporalQueryData.get(query.getDsID());
					if (qData == null) {
						query.setState(DataChannelState.RUNNING);
						qData = new TemporaryQueryData(query);
						qData.setHistoricalLimit(query.getHistoricalLimit());
						
					}
						
					
					//do the search & store data if needed
					//TODO do it in an asynchronous mode?
					qData = singleQuerySearch(qData, api);
					
					//perform (intermediate) store
					storeTweets(qData);
					
					//update query in solr (in store tweets)
					

					//update datachannel/datasource state
					updateDataChannelsState();

					
					//check status of the query retrieval remove if finished 
					if (qData.hasNextPage == false) {
						System.out.println("Remove");
						iterator.remove();
						temporalQueryData.remove(query.getDsID());
					} else {
						//keep temporary query queue params
						temporalQueryData.put(query.getDsID(), qData);
						
					}
					
				}
				
				// 
				if (queryList.size() > 0) {
					
				} else {
					try {
						System.out.println("No more queries to process. Waiting for periodic update.");
						wait();
						System.out.println("Finished waiting :)");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			} catch (Exception e) {
				LOGGER.severe(" #TwitterQueryManager: ERROR capturing data from Twitter "
						+ ". Exception in the querySearchLoop: "
						+ e.getMessage());
				e.printStackTrace();
			}
			

			
		}
		
		
		
	}


	private TwitterAPI selectAPI() {
		
		// select api that has the least waiting time for the query pool 
		TwitterAPI selectedAPI = null;
		Integer timeToReset = null;
		for (TwitterAPI api : apiList) {
			Integer ttr = api.getTimeToNextBatch();
			if (timeToReset == null || ttr < timeToReset) {
				timeToReset = ttr;
				selectedAPI = api;
			}
		}
		
		System.out.println("Selected " + selectedAPI.getApiName() + " with TTR: " + timeToReset);

		return selectedAPI;
	}

	private void updateDataChannelsState() {
		// TODO Auto-generated method stub
		
	}

	

	/**
	 * 
	 * Query list update
	 * 
	 */
	protected void queryListUpdate() {
		// TODO Auto-generated method stub
		try {
			
			LinkedHashMap<String, QueryData> queriesMap = cfi.getActiveQueriesMapByDSType("twitter");
			Set<Entry<String, QueryData>> queries = queriesMap.entrySet();
			
			ArrayList<String> activeDataSources = new ArrayList<String>();
//			ArrayList<String> queueDataSources = new ArrayList<String>();
//			
//			for (QueryData q : queryList) {
//				queueDataSources.add(q.getDsID());
//			}
			
			for (Entry<String, QueryData> q : queries) {
				activeDataSources.add(q.getKey());
				
				//debug:
				ObjectMapper om = new ObjectMapper();
				om.setSerializationInclusion(Include.NON_NULL);
				String s = q.getKey() + " \t " + om.writeValueAsString(q.getValue());
				System.out.println("Debug: " + s);
				
			}
			
			
			// if the queue is empty, we simply repopulate it with all queries
			if (queryList.isEmpty() == true) {
				for (Entry<String, QueryData> q : queries) {
					queryList.add(q.getValue());
				}
			} else {
				// in case we have still some queries, we only add the ones that are not present
				// we check that by comparing DataSource ID.
				//Iterator<QueryData> iter = queryList.iterator();
				HashMap<String, QueryData> queriesToAdd = new HashMap<>();
				queriesToAdd.putAll(queriesMap);
				
//				for () {
//					
//				}
//				
//				for (Entry<String, QueryData> q : queries) {
//					if (queryList.contains(q.getValue())) {
//						System.out.println("DS in the queue, skip");
//					} else {
//						System.out.println("ADD DS");
//						queryList.add(q.getValue());
//					}
//				}
				
				for (QueryData q : queryList) {
					if (activeDataSources.contains(q.getDsID())) {
						System.out.println("DS in the queue, skip");
						queriesToAdd.remove(q.getDsID());
					} else {
//						System.out.println("ADD DS");
//						queryList.add(q);
					}
				}
				
				for (Entry<String, QueryData> q : queriesToAdd.entrySet()) {
					queryList.add(q.getValue());
				}
			}
			
			System.out.println("list size is now: " + queryList.size());
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * 
	 * Perform search with all given query parameters
	 * 
	 * 
	 */
	protected TemporaryQueryData singleQuerySearch(TemporaryQueryData tempQueryData, TwitterAPI twitterApi) {
		try {
			LOGGER.info("Enter search query");
			//restore our temporal variables
			ArrayList<Tweet> tweetsSet = tempQueryData.getTweetsSet();
            //lastIDTweetLimit is a candidate for replacing the last Tweet field.
            //It will be the highest ID seen in the whole search query
            //for this datasource.
			long lastIDTweetLimit = tempQueryData.getLastIDTweetLimit();

			Query query = tempQueryData.getQuery();
			System.out.println(query.toString());
            
            QueryResult result = tempQueryData.getResult();
            
            long totalTweetCount = tempQueryData.getTotalTweetCount();
            long historicalDataLimit = tempQueryData.getHistoricalLimit();
            
            // variables for pagination use
            boolean hasNextPage = tempQueryData.isHasNextPage();      
            long lastID = tempQueryData.getLastID();
            long maxID = tempQueryData.getMaxID();
			QueryData keywords = tempQueryData.getQueryData();
            //end of restoring temporal variables
			
			System.out.println("- Before:");
			System.out.println("- lastID: \t\t" + lastID);
			System.out.println("- lastIDTweetLimit: \t\t" + lastIDTweetLimit);
			System.out.println("- maxID: " + maxID);
			System.out.println("- historicalDataLimit: " + historicalDataLimit);
			System.out.println("- totalTweetCount: " + totalTweetCount);

        	//retrieve twitter api current limits for search queries            	
        	//and store 

        	
        	try {
        		//first we try with historical data limit
        		if (keywords.isChronologicalOrder() == false && historicalDataLimit > 0 && totalTweetCount >= historicalDataLimit) {
        			LOGGER.info("Historical data limit for DC " + keywords.getDsID() + " reached. Limit is: " + historicalDataLimit);

        			//previous behaviour:
        			//hasNextPage = false;
        			
        			//here we change the state of the DC to chronological order
        			//the default behaviour is that the limited historical channel becomes a "chronological order" channel
        			keywords.setChronologicalOrder(true);
        			
        		} else {
        			
            		// Esto se debe quedar asi para las siguientes veces.
            		if (keywords.getLastID() != 0) {
            			LOGGER.info("LAST ID: " + keywords.getLastID());
                        query.setSinceId(keywords.getLastID());		
                        
                    }
            		LOGGER.finest("Full query " + query.toString());
            		
            		int pageSize = 100;
            		if (keywords.isChronologicalOrder() && (keywords.getLastID() == 0)) {
            			LOGGER.info(" First time in a ordered query (stream simulation)");
            			pageSize = 1;
            			hasNextPage = false;
            		}
            		// cambiamos el tamaño de la pagina
            		// result = twitter.search(query.count(100));
            		
            		//aqui se puede hacer query.setMaxId (si no es -1) para llegar a los tweets mas antiguos 

            		
                	//result = twitterApi.getTwitter().search(query.count(pageSize));
            		result = twitterApi.searchTweets(query, pageSize);
                    List<Status> tweets = result.getTweets();
                    
                    //update query balancer metrics
        			twitterApi.queryBalancer(result);

                    //increment out tweet counter:
                    totalTweetCount += tweets.size();
                    //update rate counters
                    //TODO
                    
                    if (tweets.size() == 0) {
                    	LOGGER.info("Nothing more. The last Tweet ID seen is: " + lastIDTweetLimit);
                    	
                    	hasNextPage = false;
    				} else {
    					//sacar el id del primer tweet, para luego
    	            	//no tener que llegar al fondo, pero solo hasta este tweet.
    	            	if (tweets != null && tweets.get(0) != null) {
    	            		if (lastIDTweetLimit < tweets.get(0).getId()) {
    		            		lastIDTweetLimit =  tweets.get(0).getId();
    	            		}
    	            	}
    	            	
    					for (Status tweet : tweets) {
    						//show acquired tweets
    	                	LOGGER.finest("@" + tweet.getId() + " - " + tweet.getCreatedAt() /*+ " ## Source: " + tweet.getSource()*/);
    	                	
    	                	
    						Tweet phemeTweet = TweetUtils.getTweetFromStatus(tweet);
    	                    
    	                    tweetsSet.add(phemeTweet);
    	                    
    	                    //getting the lowest ID (oldest tweet) from the list. Para que en la siguiente pagina busque solo por debajo de este.
    	                    if (tweet.getId() < lastID) {
    							lastID = tweet.getId();
    						}
    	                    
    	                    query.setMaxId(lastID - 1);
    	                    
    	                }
    				}
        		}
        		
        		
            	//store temporal variables back
            	tempQueryData.setQueryData(keywords);
            	tempQueryData.setMaxID(maxID);
            	tempQueryData.setLastID(lastID);
            	tempQueryData.setHasNextPage(hasNextPage);
            	tempQueryData.setResult(result);
            	tempQueryData.setQuery(query);
            	tempQueryData.setLastIDTweetLimit(lastIDTweetLimit);
            	tempQueryData.setTweetsSet(tweetsSet);
            	tempQueryData.setTotalTweetCount(totalTweetCount);
            	
    			System.out.println("- After:");
    			System.out.println("- lastID" + lastID);
    			System.out.println("- lastIDTweetLimit" + lastIDTweetLimit);
    			System.out.println("- maxID" + maxID);
    			System.out.println("- historicalDataLimit: " + historicalDataLimit);
    			System.out.println("- totalTweetCount: " + totalTweetCount);
            	
        		
        	} catch (TwitterException te) {
                LOGGER.log(Level.SEVERE, " ERROR searching for tweets: " + te.getMessage(), te);                    
                
                
                if (te.getMessage().toLowerCase().contains("rate limit exceeded")) {
                	LOGGER.info("Unexpected rate limit error.");
                	 //well, sleep anyway
                    LOGGER.info("Sleeping for 30 seconds and retrying...");
                    //Thread.sleep(30 * 1000);
                    twitterApi.setTimeToNextBatch(30);
                } else {
                    //occasionally we get a very strange error from twitter API,
                    //which is not related with RateExcceed. If we don't handle it
                    //properly, and Throw exception here, our search loop will break,
                    //and the current ActiveQuery will finish and we will start 
                    //new query from the beginning.
                	//This error manifests itself with the follwing exception code:
                	//506c3b98-105d1087 63e3f388-fb44fc2f 63e3f388-fb44fc20
                	if (te.getErrorMessage() == null) {
                		//this is some strange/unidentified exception, and we don't mind skipping it
                		LOGGER.info("The exception above looks strange. Retrying previous query in 5 sec.");
                		twitterApi.setTimeToNextBatch(10);
                	} else {
                		//this is a sink for all other unhandled Twitter exceptions.
                		//Be careful, any unimportant exception will break the Query loop.
                		LOGGER.log(Level.SEVERE, "Possibly unhandled exception. We are going to ignore it and repeat last query.", te);
                		
                		//throw new Exception("other error: " + te.getMessage());                    		
                	}
                	
                }
               
            }
        	

			
		} catch (Exception te) {
        	LOGGER.log(Level.SEVERE, "This weird exception", te);
            LOGGER.severe("Other (unidentified) twitter exception while processing the following keywords: " + tempQueryData.getQueryData().getQuery());
            //LOGGER.severe(te.getMessage());
            
        }
		
		
		
		return tempQueryData;
		
	}
	

	/**
	 * 
	 * This is a way to store tweets retrieved through the search API.
	 * We do it this way in order to avoid having 2 different entry points 
	 * to the "addTweetsToDataChannel" method. 
	 * 
	 * 
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
	
	private void storeTweets(TemporaryQueryData tempQueryData) throws Exception {
		//write records to hbase % solr occasionally
    	//in order to avoid data loss for big result sets.
		
		//debug
		System.out.println("Tweetset Size: " + tempQueryData.getTweetsSet().size());
		
		ArrayList<Tweet> tweetsSet = tempQueryData.getTweetsSet();
		QueryData keywords = tempQueryData.getQueryData();
		TweetList tweetList = new TweetList();
		
		
		//PHEME workaround:
		//the idea is to provide ordered tweets even in historical mode.
		//we set the limit for historical & ordered tweets to 10.000 tweets
		boolean orderHistoricalTweets = false;
		Integer maxLimitOrderedHistorical = 10000;
		Integer maxBatchSize = Integer.parseInt(properties.getProperty("max.tweet.batch"));
		
		if (tempQueryData.getHistoricalLimit() > 0 
				&& keywords.isChronologicalOrder() == false) {
			
			orderHistoricalTweets = true;
			maxBatchSize = Math.max(maxBatchSize, maxLimitOrderedHistorical);
		}
		
		
		
		//intermediate write (when acumulated more than max.tweet.batch number)
    	if ((tweetsSet.size() >= maxBatchSize) 
    			&& !keywords.isChronologicalOrder()) {

    		//This is workaround for Pheme: IT IS ONE-TIME ONLY THING
    		// feel free to remove this in the futurue
    		if (orderHistoricalTweets == true) {
    			//the idea is that when Phene dashboard requests 1000 historical tweets
    			//we can provide them in a proper order
    			Collections.reverse((List<Tweet>) tweetsSet);
    		}
    		
    		
    		tweetList.setTweets(tweetsSet);
    		
    		LOGGER.info("Performing intermediate write! Number of tweets: " + tweetList.getCount() + " for query " + keywords.getQuery());
    		//write
    		   
			cfi.addTweetsToDataChannel(tweetList, keywords.getDcID(), keywords.getDsID(), keywords);
			    				    			            		
    		//reset
    		//tweetsSet = new HashSet<Tweet>();
			tweetsSet = new ArrayList<Tweet>();
    		tweetList = new TweetList();
    		
    		tempQueryData.setTweetsSet(tweetsSet);

    	}
    	
    	//final write (when query is finished, that is: it reached the end)
    	if (tempQueryData.hasNextPage == false) {
    		if (keywords.isChronologicalOrder()) {
            	Collections.reverse((List<Tweet>) tweetsSet);
            }
            LOGGER.finest("The reverse list is :" + tweetsSet);
            
            tweetList.setTweets(tweetsSet);
            // setting lastId for next query
            keywords.setLastID(tempQueryData.getLastIDTweetLimit());
            keywords.setFromLastID(true);
            
            //TODO fix finished state for ordered DS
            //keywords.setState(DataChannelState.FINISHED);
            if (keywords.isChronologicalOrder() == false) {
            	keywords.setState(DataChannelState.FINISHED);
            }
            
            
            //store tweets
			cfi.addTweetsToDataChannel(tweetList, keywords.getDcID(), keywords.getDsID(), keywords);

            //update
            cfi.updateDataSource(tempQueryData.getQueryData());
            String queryId = tempQueryData.getQueryData().getDsID();
			//
//			QueryData upToDateQuery = cfi.getQueryQueue().getQueryFromQueueById(queryId);
//
//			// ***** SETTING LASTTWEETID !!! *****
//			// Ojo, aqui, si el datachannel ha sido modificado (conretamente el lastTweetId) en medio de la query, cuando acabe la query pondra 
//			// el lastTweetId que sale de la searchTweet y por tanto machacara el que se acabe de meter en el datachannel.
//			// Este lastTweetId esta modificado solo para cuando se reinicia un datachannel sorted, pero no pasad nada, por qne la siguinete query se resetea.
//			upToDateQuery.setLastID(tempQueryData.getQueryData().getLastID());
			
//			if (upToDateQuery != null) {
//
//				// Remove Query from Queue
//				cfi.getQueryQueue().deleteQueryToQueue(queryId);
//
//				if (cfi.isQueryActive(queryId)) {
//					// Put query in the Queue
//					cfi.getQueryQueue().addQueryToQueue(queryId, upToDateQuery);
//				}
//			}
    	}
		
    	
    	//update tweet counter (After everything)
    	keywords.setTotalTweetCount(tempQueryData.getTotalTweetCount());
    			
    			
	}
	
	
//	private void oldQueryBalancer(QueryResult result, TwitterAPI api) throws InterruptedException {
//		try {
//			//we try not to invoke getRateLimitStatus for every query, as it can also
//			//contribute to limit rate excess. Instead we get the limit rate info from
//			//the query result itself. We issue getRateLimitStatus only if query is null
//			//(in case of the first invocation in the queue)
//			//Careful: getRateLimitStatus can also throw exception when limit rate is exceeded.
//			RateLimitStatus apiLimitStat;
//			if (result == null) {
//				apiLimitStat = api.getTwitter().getRateLimitStatus("search").get("/search/tweets");
//			} else {
//				apiLimitStat = result.getRateLimitStatus();
//				
//				//Sometime we enter here after making several search queries that return
//				//no tweets. This way it is very easy to exceed rate limit, so we add
//				//extra 5 seconds between such queries.
//				
//				if (result.getTweets() == null || result.getTweets().size() == 0) {
//					Thread.sleep(5 * 1000);
//				}
//			}
//			
//			if (apiLimitStat == null) {
//				LOGGER.info("This is rather weird situation. Twitter API doesn't return all quota information. We are going to ignore it.");
//				
//				
//			} else {
//				int remainingQueries = apiLimitStat.getRemaining();
//				int secondsUntilReset = apiLimitStat.getSecondsUntilReset();
////				int batchMinDuration = 180; // minimum batch duration in seconds
////				int batchSize = apiLimit / 5; //number of queries per batch
//				
//				//old values were: 180s per batch, and 5 batches.
//				//we need more agile values here, and we increase the number 
//				//of batches to 15, 60s per batch, 30 api calls per batch.
//				int batchMinDuration = 60; // minimum batch duration in seconds
//				int batchSize = apiLimit / 15; //number of queries per batch
//				
//				LOGGER.info(" ## Query Balancer. Remaining queries: " + remainingQueries + ". Time to reset: " + secondsUntilReset);
//				
//				//separate 180 queries in 5 batches, 36 each
//				//distribute batches evenly in time:
//				//that is: 36 queries every 3 minutes (180 sec)
//				//when we reach 36 queries in less than 180, sleep until the 3 min window is over.
//				if (remainingQueries % batchSize == 0) {
//					if (remainingQueries == 0) {
//						LOGGER.info(" ### Twitter API exhausted. Sleeping for " + secondsUntilReset + " seconds...");
//						//add 2 seconds more for safety margin.
//						Thread.sleep((secondsUntilReset + 2) * 1000);
//					} else {
//						int batchNumber = remainingQueries / batchSize;
//						int batchTimeLimit = batchMinDuration * batchNumber;
//						
//						if (secondsUntilReset - batchTimeLimit > 0) {
//							LOGGER.info(" ### Query balancer: Batch " + batchNumber + " run out. Next batch in: " + (secondsUntilReset - batchTimeLimit) + " seconds.");
//							LOGGER.info(" ### Query balancer: Twitter API total queries left: " + remainingQueries + ". Complete refresh in: " + secondsUntilReset + " seconds.");
//							Thread.sleep((secondsUntilReset - batchTimeLimit) * 1000);
//						} else {
//							LOGGER.info(" ### Query balancer: Batch " + batchNumber + " run out. Next batch is ready to start.");
//						}
//					}
//				}
//
//			}
//		} catch (TwitterException e)  {
//			//we assume that the exception is due to the some api limit
//			//that we are not aware of.
//			//TODO check exception type.
//			LOGGER.info(" ### Twitter Exception: " + e.getErrorMessage());
//			LOGGER.info(" ### Message: " + e.getMessage());
//			LOGGER.info(" ### Twitter API exhausted. Sleeping for 60 seconds...");
//			Thread.sleep(60 * 1000);
//		}
//		
//	}
	
}
