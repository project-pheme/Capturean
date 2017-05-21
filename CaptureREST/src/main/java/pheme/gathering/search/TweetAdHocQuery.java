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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import pheme.gathering.resources.TweetSearchMode;
import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterObjectFactory;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.OAuth2Token;
import twitter4j.conf.ConfigurationBuilder;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.persistence.SolrManager;
import atos.knowledgelab.capture.util.TweetUtils;

public class TweetAdHocQuery {

	
	private Properties properties = new Properties();
	private int apiLimit = 0; 
	private final static Logger LOGGER = Logger.getLogger(TweetAdHocQuery.class.getName());
	private Twitter twitter;
	private OAuth2Token bearerToken = null;

	
	public TweetAdHocQuery() throws IOException, TwitterException {
		
		//Loading Twitter properties
		properties.load(this.getClass().getClassLoader().getResourceAsStream("twitter.properties"));
		String OauthCKey = properties.getProperty("adhoc.search.consumerKey");
		String OauthCSec = properties.getProperty("adhoc.search.consumerSecret");
		String OauthATok = properties.getProperty("adhoc.search.accessToken");
		String OauthATSec = properties.getProperty("adhoc.search.accessTokenSecret");
		String authMode = properties.getProperty("twitter.applicationonly");
		//minRate = Integer.parseInt(properties.getProperty("limits.minRate"));
		//windowsRate = Integer.parseInt(properties.getProperty("limits.windowsRate"));		
		//windowsRateTime = Integer.parseInt(properties.getProperty("limits.windowsRateTime"));
		
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
        
        //TwitterStream twitterStream = tsf.getInstance();
        
        //Init windowsRateTimestamp
        //windowsRateTimestamp = System.currentTimeMillis();
        
        //Init TwitterRate
        //twitterRate = windowsRate / windowsRateTime;
	}
	
	/**
	 * Modes: 
	 * 	- historical: 1-2 queries
	 *  - current: 1 query for last ID, 2 queries for current tweets (param: time)
	 * 
	 * 
	 * @param query
	 * @param mode
	 * @return
	 * @throws TwitterException 
	 */
	public List<Tweet> adHocSearch(QueryData queryData, TweetSearchMode mode, int maxResults, int seconds) throws TwitterException {
		ArrayList<Tweet> tweetsList = new ArrayList<Tweet>();
		
		String keywords = queryData.getQuery();
		
		Query query = new Query(keywords);
		query.setCount(maxResults);
		
		if (mode == TweetSearchMode.MIXED) {
			query.setResultType(ResultType.mixed);			
		}
		if (mode == TweetSearchMode.LIVE) {
			query.setResultType(ResultType.recent);			
		}
		if (mode == TweetSearchMode.MOST_POPULAR) {
			query.setResultType(ResultType.popular);			
		}
		if (query.getResultType() == null) {
			query.setResultType(ResultType.recent);
		}
		
		
		QueryResult result;
		
		long lastID = Long.MAX_VALUE;
		int maxPages = 3;
		int pageCount = 0;
		
        do {
        	//System.out.println(query);
        	
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            System.out.println("Fetching " + tweets.size() + " tweets...");
            
            for (Status tweet : tweets) {
				//show acquired tweets
            	//LOGGER.info("@" + tweet.getId() + " - " + tweet.getCreatedAt() /*+ " ## Source: " + tweet.getSource()*/);
            	
            	
				Tweet phemeTweet = TweetUtils.getTweetFromStatus(tweet);
            	
//				Tweet phemeTweet = new Tweet();
//            	phemeTweet.setText(tweet.getText());
//            	phemeTweet.setUserScreenName(tweet.getUser().getScreenName());
//            	phemeTweet.setId("" + tweet.getId());
//            	phemeTweet.setText(tweet.getText());
//            	phemeTweet.setRawJson(TwitterObjectFactory.getRawJSON(tweet));
            	
				tweetsList.add(phemeTweet);
                
				if (tweet.getId() < lastID) {
					lastID = tweet.getId();
				}
                
                query.setMaxId(lastID - 1);
            }
            
            LOGGER.info("Ad-hoc query. Number of tweets: " + tweetsList.size());
            LOGGER.info("Ad-hoc query. Remaining queries: " + result.getRateLimitStatus().getRemaining());
            
        	pageCount++;
            
            if (tweetsList.size() >= maxResults) {
            	LOGGER.info("Ad-hoc query. Done!");
            	break;
            }
            
            
            
        } while ((query = result.nextQuery()) != null);
		
		return tweetsList;
		
	}
	
	
	public static void main(String[] args) {
		
		
		
		
		
		
		
		
		
		try {
			TweetAdHocQuery tahq = new TweetAdHocQuery();
			QueryData qd = new QueryData();
			qd.setQuery("iran cash");
			
			List<Tweet> list = tahq.adHocSearch(qd, TweetSearchMode.MIXED, 100, 10);
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
