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
package pheme.gathering.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

import atos.knowledgelab.capture.persistence.SolrManager;
import atos.knowledgelab.capture.util.CaptureConstants;
import pheme.gathering.util.CaptureFacadeFactory;
import atos.knowledgelab.capture.util.TweetUtils;
import twitter4j.IDs;
import twitter4j.OEmbed;
import twitter4j.OEmbedRequest;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.WebIntentTwitter;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;

public class TweetManager {
	
	private static TweetManager instance;
	private Twitter twitter;
	TwitterStreamFactory twitterStreamFactory;
	private final static Logger LOGGER = Logger.getLogger(TweetManager.class.getName());
//	private int twitterRate = 0;
//	private long windowsRateTimestamp;
//	private int currentRequests = 0;
//	private int minRate;
//	private int windowsRate;
//	private int windowsRateTime;
	private int apiLimit = 0; 
	private OAuth2Token bearerToken = null;
	private String streamMode = "";
	private Properties properties = new Properties();
	
	// TODO properly initialize this
	private CaptureFacadeIf cfi = CaptureFacadeFactory.getInstance();
	
	private SolrManager solr; 	
	
	public TweetManager () throws Exception {
		this.solr = SolrManager.getInstance();
				
		//Loading Twitter properties
		properties.load(this.getClass().getClassLoader().getResourceAsStream("twitter.properties"));
		String OauthCKey = properties.getProperty("oauth.consumerKey");
		String OauthCSec = properties.getProperty("oauth.consumerSecret");
		String OauthATok = properties.getProperty("oauth.accessToken");
		String OauthATSec = properties.getProperty("oauth.accessTokenSecret");
		String authMode = properties.getProperty("twitter.applicationonly");
		//minRate = Integer.parseInt(properties.getProperty("limits.minRate"));
		//windowsRate = Integer.parseInt(properties.getProperty("limits.windowsRate"));		
		//windowsRateTime = Integer.parseInt(properties.getProperty("limits.windowsRateTime"));
		streamMode = properties.getProperty("twitter.stream.mode");
		
		try {
			if (authMode.equalsIgnoreCase("true") == true) {
				apiLimit = Integer.parseInt(properties.getProperty("api.limit.applicationonly"));
			} else {
				apiLimit = Integer.parseInt(properties.getProperty("api.limit.applicationonly"));
			}
		} catch (NumberFormatException e) {
			//fallback to the lowest limit.
			apiLimit = 180;

			LOGGER.info(" ##### Twitter config file error!");
		} 
		
//		ConfigurationBuilder cb = new ConfigurationBuilder();
//        cb.setDebugEnabled(true).setOAuthConsumerKey(OauthCKey).setOAuthConsumerSecret(OauthCSec).setOAuthAccessToken(OauthATok).setOAuthAccessTokenSecret(OauthATSec);
//        cb.setJSONStoreEnabled(true);
//        
//        TwitterFactory tf = new TwitterFactory(cb.build());
//        twitter = tf.getInstance();
		
		//check the authentication mode
		//authentication only and per user.
		if (authMode.equalsIgnoreCase("true") == true) {
			//this is authentication only auth.
			ConfigurationBuilder cb = new ConfigurationBuilder();
	        cb.setApplicationOnlyAuthEnabled(true);
	        cb.setJSONStoreEnabled(true);
	        
			TwitterFactory tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();
	        twitter.setOAuthConsumer(OauthCKey, OauthCSec);
	        bearerToken = twitter.getOAuth2Token();

//	        RateLimitStatus apiLimitStat = twitter.getRateLimitStatus("search").get("/search/tweets");
//        	
//            System.out.println(apiLimitStat.getRemaining());
//            System.out.println(apiLimitStat.getSecondsUntilReset());
		} else {
			//this is per user authentication.
			ConfigurationBuilder cb = new ConfigurationBuilder();
	        cb.setDebugEnabled(true).setOAuthConsumerKey(OauthCKey).setOAuthConsumerSecret(OauthCSec).setOAuthAccessToken(OauthATok).setOAuthAccessTokenSecret(OauthATSec);
	        cb.setJSONStoreEnabled(true);
	        
	        TwitterFactory tf = new TwitterFactory(cb.build());
	        twitter = tf.getInstance();
		}
        
        ConfigurationBuilder cbs = new ConfigurationBuilder();
        cbs.setDebugEnabled(true)
        	.setOAuthConsumerKey(OauthCKey)
        	.setOAuthConsumerSecret(OauthCSec)
        	.setOAuthAccessToken(OauthATok)
        	.setOAuthAccessTokenSecret(OauthATSec);
        //cbs.setDebugEnabled(true);
        //nines cbs.setJSONStoreEnabled(true);
        cbs.setJSONStoreEnabled(true);
        
        twitterStreamFactory = new TwitterStreamFactory(cbs.build());
        //TwitterStream twitterStream = tsf.getInstance();
        
        //Init windowsRateTimestamp
        //windowsRateTimestamp = System.currentTimeMillis();
        
        //Init TwitterRate
        //twitterRate = windowsRate / windowsRateTime;
	}
	
	public static TweetManager getInstance() throws Exception {
		  if(instance == null) {
		     instance = new TweetManager();         
		  }
		  return instance;
		}
	
	public TwitterStreamFactory getTwitterStreamFactory() {
		return twitterStreamFactory;
	}


	@SuppressWarnings("unchecked")
	public TweetList searchTweets (QueryData keywords) throws Exception {
		
		TweetList tweetList = new TweetList();
        //Set<Tweet> tweetsSet = new HashSet<Tweet>();
        ArrayList<Tweet> tweetsSet = new ArrayList<Tweet>();
        
        //lastIDTweetLimit is a candidate for replacing the last Tweet field.
        //It will be the highest ID seen in the whole search query
        //for this datasource.
        long lastIDTweetLimit = 0;
        
        try {
            Query query = new Query(keywords.getQuery());
            
            //Support "lastTweetID" field, 
            //*** CHECKING WHETHER TO SEARCH FROM LASTID OR TO RETRIEVE ALL RESULTS ***
            if (keywords.isFromLastID() == true) {
            	// setting sinceId as lastId query value
            	query.setSinceId(keywords.getLastID());
            	lastIDTweetLimit = keywords.getLastID();
            } else {
            	query.setSinceId(0);
            }
            
           
            
            
            
            QueryResult result = null;
//            long startRateTime = System.currentTimeMillis();
            
            // variables for pagination use
            boolean hasNextPage = true;            
            long lastID = Long.MAX_VALUE;
            long maxID = 0;
            
            do {

            	//retrieve twitter api current limits for search queries            	
            	//add delay to ensure that api is not exhausted
            	queryBalancer(result);
            	
            	try {
            		// Esto se debe quedar asi para las siguientes veces. Luego la primera vez tiene que venir a cero pera que no lo ponga
            		if (keywords.getLastID() != 0) {
            			LOGGER.info("LAST ID: " + keywords.getLastID());
                        query.setSinceId(keywords.getLastID());		
                        
                    }
            		LOGGER.finest("Full query " + query.toString());
            		
            		int pageSize = 100;
            		if (keywords.isChronologicalOrder() && (keywords.getLastID() == 0)) {
            			LOGGER.info(" First time in a ordering query");
            			pageSize = 1;
            			hasNextPage = false;
            		}
            		// cambiamos el tamaño de la pagina
            		// result = twitter.search(query.count(100));
            		
            		//aqui se puede hacer query.setMaxId (si no es -1) para llegar a los tweets mas antiguos 
            		
	            	result = twitter.search(query.count(pageSize));
	                List<Status> tweets = result.getTweets();
	                
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
            	} catch (TwitterException te) {
                    LOGGER.log(Level.SEVERE, " ERROR searching for tweets: " + te.getMessage(), te);                    
                    
                    
                    if (te.getMessage().toLowerCase().contains("rate limit exceeded")) {
                    	LOGGER.info("Unexpected rate limit error.");
                    	 //well, sleep anyway
                        LOGGER.info("Sleeping for 150 seconds and retrying...");
                        Thread.sleep(150 * 1000);
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
                    		Thread.sleep(5 * 1000);
                    	} else {
                    		//this is a sink for all other unhandled Twitter exceptions.
                    		//Be careful, any unimportant exception will break the Query loop.
                    		LOGGER.log(Level.SEVERE, "Possibly unhandled exception. We are going to ignore it.", te);
                    		
                    		//throw new Exception("other error: " + te.getMessage());                    		
                    	}
                    	
                    }
                   
                }
            	
            	//write records to hbase % solr occasionally
            	//in order to avoid data loss for big result sets.
            	if ((tweetsSet.size() >= Integer.parseInt(properties.getProperty("max.tweet.batch"))) && !keywords.isChronologicalOrder()) {

            		tweetList.setTweets(tweetsSet);
            		
            		LOGGER.info("Performing intermediate write! Number of tweets: " + tweetList.getCount() + " for query " + keywords.getQuery());
            		//write
            		   
    				cfi.addTweetsToDataChannel(tweetList, keywords.getDcID(), keywords.getDsID(), keywords);
    				    				    			            		
            		//reset
            		//tweetsSet = new HashSet<Tweet>();
    				tweetsSet = new ArrayList<Tweet>();
            		tweetList = new TweetList();
            	}
                
            } while (hasNextPage == true);
            
            LOGGER.finest("The initial list is :" + tweetsSet);
            // chronologically sorted
            if (keywords.isChronologicalOrder()){
            	Collections.reverse((List<Tweet>) tweetsSet);
            }
            LOGGER.finest("The reverse list is :" + tweetsSet);
            
            tweetList.setTweets(tweetsSet);
            // setting lastId for next query
            keywords.setLastID(lastIDTweetLimit);
            keywords.setFromLastID(true);
            
        } catch (Exception te) {
        	LOGGER.log(Level.SEVERE, "This weird exception", te);
            LOGGER.severe("Other (unidentified) twitter exception while processing the following keywords: " + keywords);
            //LOGGER.severe(te.getMessage());
            
        }
        //return remaining tweets
        return tweetList;
	}
	
	/*
	 * queryBalancer is used for moderate the speed of twitter API invocations.
	 * In order not to exhaust the twiter API, it distributes the overall API quota
	 * over the given time period. For "/search/tweets" resource the current limit 
	 * is 180 queries per 15 min (in user-based auth) or 450 / 15 min (in application-based auth)
	 * This method check the current limit and spreads it into 5 buckets of 36 queries/3min
	 * or 90/3min. Whenever the bucket runs out it waits until the new bucket is available.
	 * 
	 */
	private void queryBalancer(QueryResult result) throws InterruptedException {
		try {
			//we try not to invoke getRateLimitStatus for every query, as it can also
			//contribute to limit rate excess. Instead we get the limit rate info from
			//the query result itself. We issue getRateLimitStatus only if query is null
			//(in case of the first invocation in the queue)
			//Careful: getRateLimitStatus can also throw exception when limit rate is exceeded.
			RateLimitStatus apiLimitStat;
			if (result == null) {
				apiLimitStat = twitter.getRateLimitStatus("search").get("/search/tweets");
			} else {
				apiLimitStat = result.getRateLimitStatus();
				
				//Sometime we enter here after making several search queries that return
				//no tweets. This way it is very easy to exceed rate limit, so we add
				//extra 5 seconds between such queries.
				
				if (result.getTweets() == null || result.getTweets().size() == 0) {
					Thread.sleep(5 * 1000);
				}
			}
			
			if (apiLimitStat == null) {
				LOGGER.info("This is rather weird situation. Twitter API doesn't return all quota information. We are going to ignore it.");
				
				
			} else {
				int remainingQueries = apiLimitStat.getRemaining();
				int secondsUntilReset = apiLimitStat.getSecondsUntilReset();
//				int batchMinDuration = 180; // minimum batch duration in seconds
//				int batchSize = apiLimit / 5; //number of queries per batch
				
				//old values were: 180s per batch, and 5 batches.
				//we need more agile values here, and we increase the number 
				//of batches to 15, 60s per batch, 30 api calls per batch.
				int batchMinDuration = 60; // minimum batch duration in seconds
				int batchSize = apiLimit / 15; //number of queries per batch
				
				LOGGER.info(" ## Query Balancer. Remaining queries: " + remainingQueries + ". Time to reset: " + secondsUntilReset);
				
				//separate 180 queries in 5 batches, 36 each
				//distribute batches evenly in time:
				//that is: 36 queries every 3 minutes (180 sec)
				//when we reach 36 queries in less than 180, sleep until the 3 min window is over.
				if (remainingQueries % batchSize == 0) {
					if (remainingQueries == 0) {
						LOGGER.info(" ### Twitter API exhausted. Sleeping for " + secondsUntilReset + " seconds...");
						//add 2 seconds more for safety margin.
						Thread.sleep((secondsUntilReset + 2) * 1000);
					} else {
						int batchNumber = remainingQueries / batchSize;
						int batchTimeLimit = batchMinDuration * batchNumber;
						
						if (secondsUntilReset - batchTimeLimit > 0) {
							LOGGER.info(" ### Query balancer: Batch " + batchNumber + " run out. Next batch in: " + (secondsUntilReset - batchTimeLimit) + " seconds.");
							LOGGER.info(" ### Query balancer: Twitter API total queries left: " + remainingQueries + ". Complete refresh in: " + secondsUntilReset + " seconds.");
							Thread.sleep((secondsUntilReset - batchTimeLimit) * 1000);
						} else {
							LOGGER.info(" ### Query balancer: Batch " + batchNumber + " run out. Next batch is ready to start.");
						}
					}
				}

			}
		} catch (TwitterException e)  {
			//we assume that the exception is due to the some api limit
			//that we are not aware of.
			//TODO check exception type.
			LOGGER.info(" ### Twitter Exception: " + e.getErrorMessage());
			LOGGER.info(" ### Message: " + e.getMessage());
			LOGGER.info(" ### Twitter API exhausted. Sleeping for 60 seconds...");
			Thread.sleep(60 * 1000);
		}
		
	}

	public String getTwitterStreamMode() {
		return streamMode;
	}
	
	//moved to TwitterStreamQuerySolver
//	public void streamTweets(QueryData streamQuery) throws Exception {
//		if (streamMode.equalsIgnoreCase("kafka") == true) {
//			streamTweetsKafka(streamQuery);
//		}
//		if (streamMode.equalsIgnoreCase("storm") == true) {
//			streamTweetsStorm(streamQuery);
//		}
//		if (streamMode.equalsIgnoreCase("dummy") == true) {
//			LOGGER.info(" ##### DUMMY STREAMING NOT IMPLEMENTED ... "); 
//
//		}
//	}
	
//	public void streamTweetsStorm(QueryData streamQuery) {
//		
//		LOGGER.info(" ##### STREAMING TWEETS... "); 
//		Config config = new Config();
//	
//		/*Map<String, Object> hbConf = new HashMap<String, Object>();
//        hbConf.put("hbase.rootdir", "xxxx");
//        config.put("hbase.conf", hbConf);*/
//        
//		
//		TwitterStream twitterStream = twitterStreamFactory.getInstance();
//
//        TwitterSpout spout = new TwitterSpout(twitterStream, streamQuery.getQuery());       
//        TwitterSolrBolt solrBolt = new TwitterSolrBolt("http:\\\\localhost:8080\\solr");
//                
//        SimpleHBaseMapper mapper = new SimpleHBaseMapper()
//                .withRowKeyField("tweetID")
//                .withColumnFields(new Fields("dcID"))
//                .withColumnFields(new Fields("dsID"))
//                .withColumnFields(new Fields("tweetID"))
//                .withColumnFields(new Fields("json"))
//                .withColumnFamily("tweet");
//
//        HBaseBolt hbaseBolt = new HBaseBolt("pheme_raw_tweet", mapper);
//
//
//        // TwitterSpout ==> solrBolt 
//        //              ==> HBaseBolt
//        
//        //Test
//        PrinterBolt pBolt = new PrinterBolt();
//        
//        LOGGER.info(" ##### BUILDING TOPOLOGY ... "); 
//        TopologyBuilder builder = new TopologyBuilder();
//
//        builder.setSpout(CaptureConstants.TWITTER_SPOUT, spout, 1);
//        //builder.setBolt(CaptureConstants.HBASE_BOLT, solrBolt, 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT);
//        //builder.setBolt(CaptureConstants.SOLR_BOLT, hbaseBolt, 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT);
//        builder.setBolt("printer_bolt", pBolt, 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT); 
//        
//        LocalCluster cluster = new LocalCluster();
//        LOGGER.info(" ##### SUBMITING TOPOLOGY ... ");
//        cluster.submitTopology("CaptureTopology", config, builder.createTopology());
//        try {
//			Thread.sleep(calculateSleepingTime(streamQuery.getCaptureEndDate()));
//		} catch (InterruptedException e) {
//			LOGGER.info(" ##### STREAM QUERY PROCESS ERROR WHILE STREAMING PROCESS WAS RUNNING: " + e.getMessage());
//		}
//        cluster.killTopology("CaptureTopology");
//        cluster.shutdown();
//        LOGGER.info(" ##### STREAM QUERY PROCESS HAS FINISHED ... ");
//        
//	}
	
	//moved to TwitterStreamWorker
//	public void streamTweetsKafka(QueryData streamQuery) throws Exception {
//		
//		LOGGER.info(" ##### STARTING PRODUCER & STREAMING TWEETS ... "); 
//
//		TwitterStream twitterStream = twitterStreamFactory.getInstance();
//		TwitterStreamProducer producer = new TwitterStreamProducer(twitterStream, streamQuery); 
//		producer.start();
//
//		try {
//				Thread.sleep(calculateSleepingTime(streamQuery.getCaptureEndDate()));
//		} catch (InterruptedException e) {
//				LOGGER.info(" ##### STREAM QUERY INTERRUPTED (POSSIBLY BY USER): " + e.getMessage());
//		}
//		twitterStream.shutdown();
//		producer.shutdown();
//
//        LOGGER.info(" ##### QUERY STREAM PROCESS HAS FINISHED... ");
//        
//	}
	
//	private int getRemainingTime(long startRateTime) {
//		int waitSeconds = 0;
//		int secondsToRate = (int) TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startRateTime);
//		if (secondsToRate < 60) {
//			waitSeconds = secondsToRate;
//		}
//		return waitSeconds;
//	}
//
//	private boolean limitReached() {		
//		return currentRequests == twitterRate;
//	}

	public TwitterUser getUserProfile(String userID) throws TwitterException {
		//TwitterUser userProf = new TwitterUser();
		String checkRateMsg = checkRateLimitStatus();
        boolean limitReached = checkRateMsg.trim()!="";
		
		User twitterUser = twitter.showUser(Integer.parseInt(userID));
		TwitterUser userProf = TweetUtils.getTwitterUserFromUser(twitterUser);
//		userProf.setId(""+twitterUser.getId());
//		userProf.setName(twitterUser.getName());
//		userProf.setScreenName(twitterUser.getScreenName());
//		userProf.setDescription(twitterUser.getDescription());
//		userProf.setLang(twitterUser.getLang());
//		if (twitterUser.getCreatedAt()!=null) {
//		  userProf.setCreatedAt(twitterUser.getCreatedAt().toString());
//		}
//		userProf.setStatusesCount(""+twitterUser.getStatusesCount());
//		userProf.setGeoEnabled(twitterUser.isGeoEnabled());
//		userProf.setFollowersCount(""+twitterUser.getFollowersCount());
//		userProf.setFriendsCount(""+twitterUser.getFriendsCount());
//		if (twitterUser.getURL()!=null) {
//		  userProf.setUrl(twitterUser.getURL().toString());
//		}
//		userProf.setListedCount(""+twitterUser.getListedCount());
//		userProf.setUtcOffset(""+twitterUser.getUtcOffset());
//			
		//Followers&Friends
        // TO-DO: develop this by means of Hadoop Job
        if (!limitReached) {
        	IDs ids = twitter.getFriendsIDs(Integer.parseInt(userID), -1);
        	if (ids!=null) {
          	  userProf.setUserFriends(formatUserIds(ids.getIDs()));
            }
            ids = twitter.getFollowersIDs(Integer.parseInt(userID), -1);
            if (ids!=null) {
           	  userProf.setUserFollowers(formatUserIds(ids.getIDs()));
			}  
        } 	
		return userProf;		
	}
	
	private String formatUserIds (long[] ids) {
		String idsStr = "";
		if (ids!=null) {
			for (int i=0; i < ids.length; i++) {
			  if (i==0) {
				  idsStr += ""+ids[i];
			  } else {
				  idsStr += ", " +ids[i];
			  }	
			}  
		}
		return idsStr;
	}
		
//	private synchronized void recalculateLimitRate () {
//
//		long timestamp = System.currentTimeMillis();
//		long minutesToWindowRate = TimeUnit.MINUTES.toMinutes(timestamp - windowsRateTimestamp);
//		if (minutesToWindowRate < windowsRateTime) {
//			twitterRate = (int) ((windowsRate - currentRequests) / (windowsRateTime - minutesToWindowRate));
//			
//			// Less than 30 req have been done during previous minutes
//			if (twitterRate > minRate) {
//				twitterRate = minRate;				
//			} else {
//				twitterRate = twitterRate - currentRequests;	
//			}
//			
//		} else {
//			windowsRateTimestamp = timestamp;
//			twitterRate = windowsRate / windowsRateTime;
//		}
//		currentRequests = 0;
//	}
	
	private String checkRateLimitStatus()  {
		String msg = "";
		try {
		Map<String,RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
		for (String endpoint : rateLimitStatus.keySet()) {
            RateLimitStatus status = rateLimitStatus.get(endpoint);
            //System.out.println(" Remaining: " + status.getRemaining()+"\n");
            if (status.getRemaining() <= 2) {
			int remainingTime = status.getSecondsUntilReset();
			if (remainingTime > 0) {
				msg = "Twitter request rate limit reached trying to get Followers and Friends. Waiting "+remainingTime/60+" minutes to request again.";
			}	
			
			/*try {
				Thread.sleep(remainingTime*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}*/
		}
		}
		} catch (TwitterException te) {
			System.err.println(te.getMessage());
			/*if (te.getStatusCode()==503) {
				try {
					Thread.sleep(120*1000);// wait 2 minutes
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}*/
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			
		}
		return msg;
	}
	
	private long calculateSleepingTime(String endCaptureDate) {
		long sleepTime = 0;
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
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

	private Twitter getTokenForOEmbed (){
		
		int apiLimit = 180;
		
		String oauth_consumerKey = "9TUnGW5dcfG8IrUbT0w98ARjk";
		String oauth_consumerSecret = "hZQwLLI3eQnY3N6hM1UQKvHFjPEXYowvUnPLnYdsR8L6Ah4HoM";
		String oauth_accessToken = "3356922897-zOBRHEe1HzcKSKn90hl5S9ZkFsCl4EnBfhzI0gZ";
		String oauth_accessTokenSecret = "KokMuZ2AbOOVIjCs0aBk3RwdRS7rBO6fIxsWcicf7n1xV";
		

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(oauth_consumerKey)
				.setOAuthConsumerSecret(oauth_consumerSecret)
				.setOAuthAccessToken(oauth_accessToken)
				.setOAuthAccessTokenSecret(oauth_accessTokenSecret);
		cb.setJSONStoreEnabled(true);


		TwitterFactory tf = new TwitterFactory(cb.build());
		return  tf.getInstance();

	}
	
	public Map<String, WebIntentTwitter> getWebIntentTimeline (Map<String, Integer> tweetIdMap) throws Exception{
		LOGGER.info("Retrieving batch of tweets..." + tweetIdMap.size());
		Twitter twitterForOEmbed = getTokenForOEmbed();
		Map<String, WebIntentTwitter> oEmbedTweets = new HashMap<String, WebIntentTwitter>();
		String url="";
		try {
			
			Iterator<String> idTweetIt = tweetIdMap.keySet().iterator();
			while (idTweetIt.hasNext()) {
				String idTweet = idTweetIt.next();
				//batchIDs.add(Long.parseLong((String) it.next()));
				LOGGER.info("Getting one tweet" + idTweet);
				OEmbedRequest or = new OEmbedRequest(Long.parseLong((String) idTweet), url);
				OEmbed a = twitterForOEmbed.getOEmbed(or);
				WebIntentTwitter oEmbedTweet = new WebIntentTwitter();
				oEmbedTweet.setId(idTweet);
				oEmbedTweet.setAuthorName(a.getAuthorName());
				oEmbedTweet.setAuthorURL(a.getAuthorURL());
				oEmbedTweet.setHtml(a.getHtml());
				oEmbedTweet.setTweetURL(a.getURL());
				LOGGER.info("Getting one tweet" + a.getURL());
				oEmbedTweet.setCriterionVolume(tweetIdMap.get(idTweet));
				oEmbedTweets.put(idTweet, oEmbedTweet);
				
			}
			LOGGER.info("Batch processed...");
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Batch fail", e);
		} finally {
			//batchIDs.clear();
		}
		return oEmbedTweets;
	}
	
	public Map<String, WebIntentTwitter> getMemberList (String owner, String listId) throws Exception{

		Twitter twitterForOEmbed = getTokenForOEmbed();
		Map<String, WebIntentTwitter> users = new HashMap<String, WebIntentTwitter>();
		try {
			
			ResponseList<User> twitterUsers = twitterForOEmbed.getUserListMembers(owner, listId, 5000, -1);
			Iterator<User> twitterUserIt = twitterUsers.iterator();
			while (twitterUserIt.hasNext()) {
				User twitterUser  = twitterUserIt.next();
				String idUser = String.valueOf(twitterUser.getId());
				WebIntentTwitter oEmbedUser = new WebIntentTwitter();
				oEmbedUser.setId(idUser);
				oEmbedUser.setAuthorName(twitterUser.getScreenName());
				oEmbedUser.setAuthorURL(twitterUser.getURL());
				oEmbedUser.setAuthorProfileImageURL(twitterUser.getProfileImageURLHttps());
				String widgetID = "659346295140827136";
				String userHTML = "<a class=\"twitter-timeline\" href=\"https://twitter.com/"+ twitterUser.getScreenName() + "\" data-widget-id=\"" +widgetID+ "\" data-screen-name=\"" +twitterUser.getScreenName()+ "\" width=\"200\" height=\"200\">Tweets para " + twitterUser.getScreenName() +".</a>";       
				LOGGER.info("El html para el user" + userHTML);
				oEmbedUser.setHtml(userHTML);
				users.put(idUser, oEmbedUser);
			}
			LOGGER.info("Batch processed...");
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Batch fail", e);
		} finally {
			//batchIDs.clear();
		}
		return users;
	}
	
	public Map<String, WebIntentTwitter> getWebIntentUser (Map<String, Integer> userIdMap) throws Exception{
		LOGGER.info("Retrieving batch of users..." + userIdMap.size());
		Twitter twitterForOEmbed = getTokenForOEmbed();
		Map<String, WebIntentTwitter> users = new HashMap<String, WebIntentTwitter>();
		try {
			
			List<Long> batchUserIds = new ArrayList<Long> ();
			Iterator<String> idUserIt = userIdMap.keySet().iterator();
			while (idUserIt.hasNext()) {
				String idUser = idUserIt.next();
				//batchIDs.add(Long.parseLong((String) it.next()));
				batchUserIds.add(Long.parseLong((String) idUser));
			}
			ResponseList<User> twitterUsers = twitterForOEmbed.lookupUsers(ArrayUtils.toPrimitive(batchUserIds.toArray(new Long[1])));
			Iterator<User> twitterUserIt = twitterUsers.iterator();
			while (twitterUserIt.hasNext()) {
				User twitterUser  = twitterUserIt.next();
				String idUser = String.valueOf(twitterUser.getId());
				WebIntentTwitter oEmbedUser = new WebIntentTwitter();
				oEmbedUser.setId(idUser);
				oEmbedUser.setAuthorName(twitterUser.getScreenName());
				oEmbedUser.setAuthorURL(twitterUser.getURL());
				oEmbedUser.setAuthorProfileImageURL(twitterUser.getProfileImageURLHttps());
				String widgetID = "659346295140827136";
				String userHTML = "<a class=\"twitter-timeline\" href=\"https://twitter.com/"+ twitterUser.getScreenName() + "\" data-widget-id=\"" +widgetID+ "\" data-screen-name=\"" +twitterUser.getScreenName()+ "\" width=\"200\" height=\"200\">Tweets para " + twitterUser.getScreenName() +".</a>";       
				LOGGER.info("El html para el user" + userHTML);
				oEmbedUser.setHtml(userHTML);
				oEmbedUser.setCriterionVolume(userIdMap.get(idUser));
				users.put(idUser, oEmbedUser);
			}
			LOGGER.info("Batch processed...");
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Batch fail", e);
		} finally {
			//batchIDs.clear();
		}
		return users;
	}
		
	// **********************************************************************************************************
	// ******************** Methods for twitter threads *************************************************
	// **********************************************************************************************************
		
	//private List<String> replyingids = new ArrayList<String>();
	//private Map<String,List<String>> structure = new HashMap<String,List<String>>();
	//private List<String> visibleids = new ArrayList<String>();
	//private TweetList tweetreplies = new TweetList();
	
	public Map<String, TweetThread> getTweetThread(List<String> tweetIdList) {
		
		LOGGER.info("Getting tweet thread for: "+ tweetIdList.toString());
		
		List<String> replyingids = new ArrayList<String>();
		Map<String,List<String>> structure = new HashMap<String,List<String>>();
		List<String> visibleids = new ArrayList<String>();
		TweetList tweetreplies = new TweetList();
		
		Map<String, TweetThread> tweetThread = new HashMap<String, TweetThread>();

		for (String tweetID : tweetIdList){
			TweetThread parcialTweetThread = generateTweetThread(tweetID,replyingids,structure,visibleids,tweetreplies);
			tweetThread.put(tweetID, parcialTweetThread);
		}
		
		LOGGER.info("Got tweet thread");

		return tweetThread;
	}
	
	public TweetThread generateTweetThread(String tweetid, List<String> replyingids, Map<String, List<String>> structure, List<String> visibleids, TweetList tweetreplies){
		
		// Collect source tweet
		ResponseList<Status> tweets = pythonretrievetweetlist(tweetid);
	     
	    for (Status tweet :tweets){
	    	collect_replying_tweets(tweet.getId()+"", tweet.getUser().getScreenName(),replyingids,structure,visibleids,tweetreplies);
		}
	      
	    //System.out.println("@@@@" + replyingids);
	    //System.out.println("@@@@" + structure);
	    //System.out.println("@@@@" + visibleids);
      
	    TweetThread tt = printTwitterThread(tweetid, tweetreplies, replyingids,structure,visibleids,tweetreplies);
	      
	    return tt;
		 
	}
	
	private void get_replying_ids (String tweetid, String username, int depth, List<String> replyingids, Map<String, List<String>> structure, List<String> visibleids, TweetList tweetreplies) {
		  int max_position = 0;
		  String content = "";
		  
		  do {
			  String url = "https://twitter.com/"+username+"/status/"+tweetid;
				
			  System.out.println("url: "+url);
			  
			  max_position = 0;
			  
			  String newcontent =""; 
			try {
				// read in data from URL
				URL url_get = new URL(url /*+ "\" -q --load-cookies=./cookies.txt -O -"*/);
				BufferedReader in = new BufferedReader(new InputStreamReader(url_get.openStream()));
				
				String inputLine;
			    while ((inputLine = in.readLine()) != null){
			        //System.out.println(inputLine);
			        newcontent = newcontent + inputLine;
			    }
			    in.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    
			//System.out.println("content: "+newcontent);
		        
			newcontent.replace("\\n", "\n").replace("\\u003c", "<").replace("\\u003e", ">").replace("\\/", "/").replace("\\\"", "\"");
	     
			String pattern = "|\"min_position\":([^\"]*)\"|U";
			// Create a Pattern object
			Pattern r = Pattern.compile(pattern);
			// Now create matcher object.
			Matcher m = r.matcher(content);
			if (m.find()) {
				max_position = m.start();
				//System.out.println("eeeey " + max_position);
			}
		    content += newcontent;
		    
		  } while (max_position != 0);

		  String pattern = "<a href=\"(/[^/]*/status/[0-9]*)\" class=\"tweet-timestamp js-permalink js-nav js-tooltip\"";
	      // Create a Pattern object
	      Pattern r = Pattern.compile(pattern);
	      // Now create matcher object.
	      Matcher m = r.matcher(content);
	      if (m.find( )) {
	    	 
	    	  while (m.find()){

				 String[] reptweettokens = m.group(1).split("/");
			     String repusername = reptweettokens[1];
			     String reptweetid = reptweettokens[(reptweettokens.length) - 1];
			     if (depth == 0 && !visibleids.contains(reptweetid)) {
			    	  visibleids.add(reptweetid);
			     }

			     if (!replyingids.contains(reptweetid)) {
			    	  List<String> replies = new ArrayList<String>();
			    	  if (structure.containsKey(tweetid)){
			    		  replies = structure.get(tweetid);
			    	  }
			    	  replies.add(reptweetid);
			    	  structure.put(tweetid, replies);
			    	  replyingids.add(reptweetid);
			    	  get_replying_ids(reptweetid, repusername, depth + 1, replyingids,structure,visibleids,tweetreplies);
			      }
	    	  }
	      }
	}
	
	private void collect_replying_tweets (String tweetid, String username, List<String> replyingids, Map<String, List<String>> structure, List<String> visibleids, TweetList tweetreplies) {
	
		  int replycount = 0;
		  structure.put(tweetid, new ArrayList<String>());
		  visibleids.add(tweetid);
		  replyingids.add(tweetid);
		 		  
		  get_replying_ids(tweetid, username, 0, replyingids,structure,visibleids,tweetreplies);
		  
		  //System.out.println("ææßæßæßæß   : "+ structure.toString());

		  String idsstr = "";
		  int idcount = 0;
		  int allcount = 0;
		  
		  // Set<Tweet> ts = new HashSet<Tweet>();
		  ArrayList<Tweet> ts = new ArrayList<Tweet>();
	    
	    	
		  for (String replyingid : replyingids) {
			  allcount++;
			  idsstr += replyingid + ",";
			  idcount++;
			  if (idcount == 100 || allcount == replyingids.size()) {
				  
				  ResponseList<Status> tweets = pythonretrievetweetlist(idsstr.substring(0, idsstr.length() - 1));
		     
				  for (Status tweet : tweets){
					  Tweet t = TweetUtils.getTweetFromStatus(tweet);
					  ts.add(t);
					  replycount++;
				  }
				  
				  idcount = 0;
				  idsstr = "";
			  }
		  }
		
		  tweetreplies.setTweets(ts);

		  System.out.println(tweetid + " - source tweet and " + replycount +" replies collected.\n");

	}
	
	private ResponseList<Status> pythonretrievetweetlist(String idsstr){
		
		String[] ids = idsstr.split(",");
		long[] idslong = new long[ids.length];
		int cont = 0;
		for (String id:ids){
			idslong[cont] = Long.parseLong(id);
			cont++;
		}
		
		ResponseList<Status> tweets = null;
		try {
			//tweets = twitter.lookup(idslong);
			tweets = getTokenForOEmbed().lookup(idslong);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return tweets;
	}
	
	private TweetThread printTwitterThread(String tweetHead, TweetList repliesList, List<String> replyingids, Map<String, List<String>> structure, List<String> visibleids, TweetList tweetreplies){
		
		int depth = 0;
		
		//Set<Tweet> repliesSet = repliesList.getTweets();
		ArrayList<Tweet> repliesSet = repliesList.getTweets();
		Map<String,Tweet> repliesMap = new HashMap<String,Tweet>();
		
		for (Tweet reply : repliesSet){
			repliesMap.put(reply.getId(), reply);
		}
		
		System.out.println(repliesMap.get(tweetHead).getRawJson());
		
		List<String> tweetChildren = structure.get(tweetHead);
		
		TweetThread tt = new TweetThread();
		
		List<TweetThread> parcial_tt = new ArrayList<TweetThread>();
		
		for (String tweetChildrenID : tweetChildren){
			parcial_tt.add(printTwitterThreadChildren(tweetChildrenID, depth+1, repliesMap, replyingids,structure,visibleids,tweetreplies));
		}
		
		tt.setTweet(repliesMap.get(tweetHead));
		tt.setTweetThread(parcial_tt);
		
		return tt;
	}
	
	private TweetThread printTwitterThreadChildren(String tweetid, int depth, Map<String,Tweet> repliesMap, List<String> replyingids, Map<String, List<String>> structure, List<String> visibleids, TweetList tweetreplies){
		
		String tab = "";
		for (int i=0; i<depth; i++) tab+="-";
		
		System.out.println(tab+repliesMap.get(tweetid).getRawJson());
		
		TweetThread tt = new TweetThread();
		
		List<TweetThread> parcial_tt = new ArrayList<TweetThread>();
		
		if (structure.containsKey(tweetid)){
			for (String sonID : structure.get(tweetid)){
				parcial_tt.add(printTwitterThreadChildren(sonID, depth+1, repliesMap,replyingids,structure,visibleids,tweetreplies));
			}
		}
		
		tt.setTweet(repliesMap.get(tweetid));
		tt.setTweetThread(parcial_tt);
				
		return tt;
	}
	
	public static void main(String[] args) throws Exception {
		TweetManager tm  = new TweetManager();
		long creditCardNumber = 1234_5678_9012_3456L;
		tm.getMemberList("ulibarrieva", "camaleon");
	}

}
