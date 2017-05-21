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

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.TweetList;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAPI {

	//move this to properties file
	//number of microbatches:
	private int microBatchCount = 9;
	int apiLimit = 450;	
	int batchMinDuration = 15 * 60 / microBatchCount; // minimum batch duration in seconds
	int batchSize = apiLimit / microBatchCount; //number of queries per batch
	
	//current limits (Twitter API rate stats)
	Integer remainingQueries = 0;
	Integer secondsUntilReset = 0;
	

	
	//twitter low level api and configuration 
	private Twitter twitter;
	private TwitterStreamFactory twitterStreamFactory;
	private final static Logger LOGGER = Logger.getLogger(TwitterAPI.class.getName());
	
	private String apiName = "API"; 
	
	private OAuth2Token bearerToken = null;
	private String streamMode = "";
	private Properties properties = new Properties();

	//properties of currently used API (from rate limit)
	//number of seconds until the next pool of queries is available
	private Integer timeToNextBatch = 0;
	
	
	public TwitterAPI(String OauthCKey, String OauthCSec, String OauthATok, String OauthATSec, String authMode, int apiLimit, String streamMode) throws Exception {
		this.apiLimit = apiLimit;
		
		
		if (authMode.equalsIgnoreCase("true") == true) {
			//this is authentication only auth.
			ConfigurationBuilder cb = new ConfigurationBuilder();
	        cb.setApplicationOnlyAuthEnabled(true);
	        cb.setJSONStoreEnabled(true);
	        
			TwitterFactory tf = new TwitterFactory(cb.build());
			twitter = tf.getInstance();
	        twitter.setOAuthConsumer(OauthCKey, OauthCSec);
	        bearerToken = twitter.getOAuth2Token();

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

        
        //start counter to update secondsUntilNextBatch timer
        Timer timerUpdate = new Timer();

        //the time will kick in every 1 second and decrease
        //the timeToNextBatch counter. This way we ensure that we always
        //have correct waiting time before choosing between APIs 
        timerUpdate.scheduleAtFixedRate(new TimerTask() {
			@Override
			public synchronized void run() {
				if (timeToNextBatch  > 0) {
					timeToNextBatch -= 1;
					
					System.out.println(getApiName() + " => Time to next batch: " + timeToNextBatch);
				}
			}
			
		}, 1000, 1000);
	}
	
	
	public QueryResult searchTweets(Query query, int pageSize) throws Exception {
		
		TweetList tweetList = new TweetList();
		QueryResult result = getTwitter().search(query.count(pageSize));
		

        return result;
	}
	

	
	/*
	 * queryBalancer is used for moderate the speed of twitter API invocations.
	 * In order not to exhaust the twiter API, it distributes the overall API quota
	 * over the given time period. For "/search/tweets" resource the current limit 
	 * is 180 queries per 15 min (in user-based auth) or 450 / 15 min (in application-based auth)
	 * This method check the current limit and spreads it into 5 buckets of 36 queries/3min
	 * or 90/3min. Whenever the bucket runs out it waits until the new bucket is available.
	 * 
	 * As of 25/01/2017 we no longer wait here, but rather in TwitterQueryManager
	 * Here we only update variables such as secondsUntilNextBatch.
	 */
	public void queryBalancer(QueryResult result) throws InterruptedException {
		try {
			//we try not to invoke getRateLimitStatus for every query, as it can also
			//contribute to limit rate excess. Instead we get the limit rate info from
			//the query result itself. We issue getRateLimitStatus only if query is null
			//(in case of the first invocation in the queue)
			//Careful: getRateLimitStatus can also throw exception when limit rate is exceeded.
			RateLimitStatus apiLimitStat;
			if (result == null) {
				LOGGER.info(apiName + ": Get rate limit quota");
				apiLimitStat = twitter.getRateLimitStatus("search").get("/search/tweets");
			} else {
				apiLimitStat = result.getRateLimitStatus();
				
				//Sometime we enter here after making several search queries that return
				//no tweets. This way it is very easy to exceed rate limit, so we add
				//extra 5 seconds between such queries.
				
//				if (result.getTweets() == null || result.getTweets().size() == 0) {
//					Thread.sleep(5 * 1000);
//				}
			}
			
			if (apiLimitStat == null) {
				LOGGER.info(apiName + ": This is rather weird situation. Twitter API doesn't return all quota information. We are going to ignore it.");
				
				
			} else {
				int remainingQueries = apiLimitStat.getRemaining();
				this.remainingQueries = remainingQueries;
				
				int secondsUntilReset = apiLimitStat.getSecondsUntilReset();
//				int batchMinDuration = 180; // minimum batch duration in seconds
//				int batchSize = apiLimit / 5; //number of queries per batch
				
				//old values were: 180s per batch, and 5 batches.
				//we need more agile values here, and we increase the number 
				//of batches to 15, 60s per batch, 30 api calls per batch.

				
				LOGGER.info(apiName + ": Query Balancer. Remaining queries: " + remainingQueries + ". Time to reset: " + secondsUntilReset);
				
				//separate 180 queries in 5 batches, 36 each
				//distribute batches evenly in time:
				//that is: 36 queries every 3 minutes (180 sec)
				//when we reach 36 queries in less than 180, sleep until the 3 min window is over.
				if (remainingQueries % batchSize == 0) {
					if (remainingQueries == 0) {
						LOGGER.info(apiName + ":  Twitter API exhausted. Sleeping for " + secondsUntilReset + " seconds...");
						
						//store this in a global variable to allow checking which API has available pool of queries 
						this.timeToNextBatch = secondsUntilReset;
						
						//add 2 seconds more for safety margin.
//						Thread.sleep((secondsUntilReset + 2) * 1000);
					} else {
						int batchNumber = remainingQueries / batchSize;
						int batchTimeLimit = batchMinDuration * batchNumber;
						
						if (secondsUntilReset - batchTimeLimit > 0) {
							LOGGER.info(apiName + ": Query balancer: Batch " + batchNumber + " run out. Next batch in: " + (secondsUntilReset - batchTimeLimit) + " seconds.");
							LOGGER.info(apiName + ": Query balancer: Twitter API total queries left: " + remainingQueries + ". Complete refresh in: " + secondsUntilReset + " seconds.");
							this.timeToNextBatch = secondsUntilReset - batchTimeLimit;
//							Thread.sleep((secondsUntilReset - batchTimeLimit) * 1000);
						} else {
							LOGGER.info(apiName + ": Query balancer: Batch " + batchNumber + " run out. Next batch is ready to start.");
							this.timeToNextBatch = 0;
						}
					}
				}

			}
		} catch (TwitterException e)  {
			//we assume that the exception is due to the some api limit
			//that we are not aware of.
			//TODO check exception type.
			LOGGER.info(apiName + " ### Twitter Exception: " + e.getErrorMessage());
			LOGGER.info(apiName + " ### Message: " + e.getMessage());
			LOGGER.info(apiName + " ### Twitter API exhausted. Sleeping for 60 seconds...");
			//Thread.sleep(60 * 1000);
			this.timeToNextBatch = 60;
		}
		
	}


	public Twitter getTwitter() {
		return twitter;
	}


	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}


	public Integer getTimeToNextBatch() {
		return timeToNextBatch;
	}


	public void setTimeToNextBatch(Integer timeToNextBatch) {
		this.timeToNextBatch = timeToNextBatch;
	}


	public String getApiName() {
		return apiName;
	}


	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	
}
