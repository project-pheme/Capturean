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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;

import atos.knowledgelab.capture.util.CaptureConstants;
import twitter4j.IDs;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterUser;

public class TweetManagerDummy {

	private static TweetManagerDummy instance;
	private Twitter twitter;
	TwitterStream twitterStream;
	private final static Logger LOGGER = Logger.getLogger(TweetManagerDummy.class.getName());
	private int twitterRate = 0;
	private long windowsRateTimestamp;
	private int currentRequests = 0;
	private int minRate;
	private int windowsRate;
	private int windowsRateTime;

	//dummy twitter config:
	//how many tweets per single search
	private int tweetCount = 50;
	//add some delay to each search (in milliseconds)
	private int searchDelay = 1 * 1000;
	//how many requests per search (min-max)
	private int minReqNum = 5;
	private int maxReqNum = 15;
	//total tqitter queries
	private int totalQueriesCount = 0;
	private long startTime = System.currentTimeMillis();
	
	
	public TweetManagerDummy() throws Exception {
		LOGGER.info(" ### USING DUMMY TWITTER API - writing " + tweetCount + " PER SEARCH QUERY ###");
		
		Properties properties = new Properties();

		// Loading Twitter properties
		properties.load(this.getClass().getClassLoader().getResourceAsStream("twitter.properties"));
		String OauthCKey = properties.getProperty("oauth.consumerKey");
		String OauthCSec = properties.getProperty("oauth.consumerSecret");
		String OauthATok = properties.getProperty("oauth.accessToken");
		String OauthATSec = properties.getProperty("oauth.accessTokenSecret");
		minRate = Integer.parseInt(properties.getProperty("limits.minRate"));
		windowsRate = Integer.parseInt(properties.getProperty("limits.windowsRate"));
		windowsRateTime = Integer.parseInt(properties.getProperty("limits.windowsRateTime"));

		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(OauthCKey).setOAuthConsumerSecret(OauthCSec)
				.setOAuthAccessToken(OauthATok).setOAuthAccessTokenSecret(OauthATSec);
		cb.setJSONStoreEnabled(true);

		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();

		ConfigurationBuilder cbs = new ConfigurationBuilder();
		cbs.setDebugEnabled(true).setOAuthConsumerKey(OauthCKey).setOAuthConsumerSecret(OauthCSec)
				.setOAuthAccessToken(OauthATok).setOAuthAccessTokenSecret(OauthATSec);
		cbs.setDebugEnabled(true);

		TwitterStreamFactory tsf = new TwitterStreamFactory(cbs.build());
		twitterStream = tsf.getInstance();

		// Init windowsRateTimestamp
		windowsRateTimestamp = System.currentTimeMillis();

		// Init TwitterRate
		twitterRate = windowsRate / windowsRateTime;
	}

	public static TweetManagerDummy getInstance() throws Exception {
		if (instance == null) {
			instance = new TweetManagerDummy();
		}
		return instance;
	}
	
	private String generateText() {
		StringBuilder sb = new StringBuilder(); 
		for (int i=0; i<10; i++) {
			sb.append(RandomStringUtils.random(6, new char[]{'a', 'b', 'c','d', 'e', 'f', 'g', 'o', 'u', 'i', 'm', 'n', 's'}));
			sb.append(" ");			
		}
		return sb.toString();
	}

	public TweetList searchTweets(String keywords) throws Exception {
		
		TweetList tweetList = new TweetList();
        //Set<Tweet> tweetsSet = new HashSet<Tweet>();
        ArrayList<Tweet> tweetsSet = new ArrayList<Tweet>();
        try {
            //Query query = new Query(keywords);
            //QueryResult result;
            long startRateTime = System.currentTimeMillis();
            
            //dummy simulation:
            //perform from 5-15 requests
            Random rand = new Random();
            int requests =  rand.nextInt(maxReqNum - minReqNum) + minRate;
            int requestCounter = 0;
            do {
            	requestCounter++;
            	LOGGER.info(" ##### QUERY " + currentRequests );
            	LOGGER.info(" ##### TWITTERRATE: " + twitterRate + " / REQUEST: " + currentRequests );
            	if (limitReached()) {
            		recalculateLimitRate();
            		int remainingTime = getRemainingTime(startRateTime);
            		if (remainingTime > 0) {
            			LOGGER.info(" ## TWITTER API LIMIT REACHED. SLEEP FOR " + remainingTime + " MS...");
                		LOGGER.info(" %%%%% Total queries: " + totalQueriesCount + "  in  " + ((System.currentTimeMillis() - startTime)/1000/60) + " min");
            			Thread.sleep(remainingTime);
            		}
            	}
            	
            	//result = twitter.search(query.count(100));
                //List<Status> tweets = result.getTweets();
            	totalQueriesCount++;
            	if (totalQueriesCount % 50 == 0) {
            		LOGGER.info(" %%%%% Total queries: " + totalQueriesCount + "  in  " + ((System.currentTimeMillis() - startTime)/1000/60) + " min");
            	}
                
        		for (int i=0; i < tweetCount; i++) {
        			
        		    int randomNum = rand.nextInt((9999999 - 1000000)) + 1000000;
        		    
        			Tweet phemeTweet = new Tweet();
        			phemeTweet.setId("" + randomNum);
        			
        			phemeTweet.setUserID("" + rand.nextInt((9999999 - 1000000)));
        			phemeTweet.setUserScreenName("laurenargyle__" + rand.nextInt(10));
        			phemeTweet.setUserDescription("Bands// Netflix// Pizza");
        			// New schema
        			//phemeTweet.setUserFollowers("" + rand.nextInt(50));
        			//phemeTweet.setUserFollowes("" + rand.nextInt(50));
        			phemeTweet.setUserFollowers(rand.nextInt(50));
        			phemeTweet.setUserFollowes(rand.nextInt(50));
        			
        			//phemeTweet.setText("@laurenargyle__ - RT @HellaNovelli_: im rly excited for ptv and sws tour even though they havent released dates or anything");
        			phemeTweet.setText(this.generateText());
        			phemeTweet.setSource("Source: <a href=\"http://www.twitter.com\" rel=\"nofollow\">Twitter for Windows Phone</a>");
        			
        			// New schema
         			// phemeTweet.setRetweetCount("1");
        			// phemeTweet.setFavouriteCount("2");
        			phemeTweet.setRetweetCount(1);
        			phemeTweet.setFavouriteCount(2);
        			phemeTweet.setInReplyToId("-1");

        			phemeTweet.setCreatedAt(new Date());
        			phemeTweet.setRawJson("{\"retweeted_status\":{\"contributors\":null,\"text\":\"im rly excited for ptv and sws tour even though they havent released dates or anything\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":497335994002456576,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":1,\"created_at\":\"Thu Aug 07 10:58:35 +0000 2014\",\"in_reply_to_user_id\":null,\"favorite_count\":1,\"id_str\":\"497335994002456576\",\"place\":null,\"user\":{\"location\":\"sick sad world\",\"default_profile\":false,\"profile_background_tile\":true,\"statuses_count\":10491,\"lang\":\"en\",\"profile_link_color\":\"000000\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/410185461/1406448819\",\"id\":410185461,\"following\":false,\"protected\":false,\"favourites_count\":4204,\"profile_text_color\":\"000000\",\"description\":\"addicted to bands, youtubers, cheesy tv shows and cereal\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\" emma \",\"profile_background_color\":\"FFFFFF\",\"created_at\":\"Fri Nov 11 19:14:42 +0000 2011\",\"is_translation_enabled\":false,\"default_profile_image\":false,\"followers_count\":784,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/495624581919830016/QReOxJHk_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/483255328608223235/LyEDI-S9.jpeg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/483255328608223235/LyEDI-S9.jpeg\",\"follow_request_sent\":false,\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"expanded_url\":\"http://Instagram.com/em_naomi\",\"indices\":[0,22],\"display_url\":\"Instagram.com/em_naomi\",\"url\":\"http://t.co/5kvJj4RYdr\"}]}},\"url\":\"http://t.co/5kvJj4RYdr\",\"utc_offset\":7200,\"time_zone\":\"Amsterdam\",\"notifications\":false,\"profile_use_background_image\":true,\"friends_count\":616,\"profile_sidebar_fill_color\":\"FFFFFF\",\"screen_name\":\"HellaNovelli_\",\"id_str\":\"410185461\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/495624581919830016/QReOxJHk_normal.jpeg\",\"listed_count\":1,\"is_translator\":false},\"coordinates\":null,\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"}},\"contributors\":null,\"text\":\"RT @HellaNovelli_: im rly excited for ptv and sws tour even though they havent released dates or anything\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[{\"id\":410185461,\"name\":\" emma\",\"indices\":[3,17],\"screen_name\":\"HellaNovelli_\",\"id_str\":\"410185461\"}]},\"in_reply_to_status_id_str\":null,\"id\":497336781986365441,\"source\":\"<a href=\\\"http://www.twitter.com\\\" rel=\\\"nofollow\\\">Twitter for Windows Phone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":1,\"created_at\":\"Thu Aug 07 11:01:43 +0000 2014\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"497336781986365441\",\"place\":null,\"user\":{\"location\":\"Nottinghamshire\",\"default_profile\":true,\"profile_background_tile\":false,\"statuses_count\":617,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/387863842/1404315215\",\"id\":387863842,\"following\":false,\"protected\":false,\"favourites_count\":187,\"profile_text_color\":\"333333\",\"description\":\"Bands// Netflix// Pizza\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"lauren\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Sun Oct 09 20:06:04 +0000 2011\",\"is_translation_enabled\":false,\"default_profile_image\":false,\"followers_count\":172,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/496032753855774720/3wJnuQWX_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":false,\"entities\":{\"description\":{\"urls\":[]}},\"url\":null,\"utc_offset\":null,\"time_zone\":null,\"notifications\":false,\"profile_use_background_image\":true,\"friends_count\":254,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"laurenargyle__\",\"id_str\":\"387863842\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/496032753855774720/3wJnuQWX_normal.png\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null,\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"}}");

        			phemeTweet.setHashTags("hastag1, hashtag2");
        			tweetsSet.add(phemeTweet);
        		}
        		
                
                currentRequests++;
                
            } while (requestCounter <= requests);
            
            tweetList.setTweets(tweetsSet);
            
        } catch (Exception te) {
            LOGGER.severe(" ERROR searching for tweets: " + te.getMessage());            
        }
        
        return tweetList;
	}
	
	public TweetList searchTweets2(String keywords) throws Exception {
		
		LOGGER.info(" ### Creating a batch of " +tweetCount+ " tweets.");
			
		Thread.sleep(searchDelay);
		
		TweetList tweetList = new TweetList();
		//Set<Tweet> tweetsSet = new HashSet<Tweet>();
		ArrayList<Tweet> tweetsSet = new ArrayList<Tweet>();
		
		Random rand = new Random();
		for (int i=0; i < tweetCount; i++) {
			
		    int randomNum = rand.nextInt((9999999 - 1000000)) + 1000000;
		    
			Tweet phemeTweet = new Tweet();
			phemeTweet.setId("" + randomNum);
			
			phemeTweet.setUserID("" + rand.nextInt((9999999 - 1000000)));
			phemeTweet.setUserScreenName("laurenargyle__" + rand.nextInt(10));
			phemeTweet.setUserDescription("Bands// Netflix// Pizza");
			// New schema
			// phemeTweet.setUserFollowers("" + rand.nextInt(50));
			// phemeTweet.setUserFollowes("" + rand.nextInt(50));
			phemeTweet.setUserFollowers(rand.nextInt(50));
			phemeTweet.setUserFollowes(rand.nextInt(50));
			
			//phemeTweet.setText("@laurenargyle__ - RT @HellaNovelli_: im rly excited for ptv and sws tour even though they havent released dates or anything");
			phemeTweet.setText(this.generateText());
			phemeTweet.setSource("Source: <a href=\"http://www.twitter.com\" rel=\"nofollow\">Twitter for Windows Phone</a>");
			// New schema
			// phemeTweet.setRetweetCount("1");
			// phemeTweet.setFavouriteCount("2");
			phemeTweet.setRetweetCount(1);
			phemeTweet.setFavouriteCount(2);
			phemeTweet.setInReplyToId("-1");

			phemeTweet.setCreatedAt(new Date());
			phemeTweet.setRawJson("{\"retweeted_status\":{\"contributors\":null,\"text\":\"im rly excited for ptv and sws tour even though they havent released dates or anything\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[]},\"in_reply_to_status_id_str\":null,\"id\":497335994002456576,\"source\":\"<a href=\\\"http://twitter.com/download/iphone\\\" rel=\\\"nofollow\\\">Twitter for iPhone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":1,\"created_at\":\"Thu Aug 07 10:58:35 +0000 2014\",\"in_reply_to_user_id\":null,\"favorite_count\":1,\"id_str\":\"497335994002456576\",\"place\":null,\"user\":{\"location\":\"sick sad world\",\"default_profile\":false,\"profile_background_tile\":true,\"statuses_count\":10491,\"lang\":\"en\",\"profile_link_color\":\"000000\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/410185461/1406448819\",\"id\":410185461,\"following\":false,\"protected\":false,\"favourites_count\":4204,\"profile_text_color\":\"000000\",\"description\":\"addicted to bands, youtubers, cheesy tv shows and cereal\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"FFFFFF\",\"name\":\" emma \",\"profile_background_color\":\"FFFFFF\",\"created_at\":\"Fri Nov 11 19:14:42 +0000 2011\",\"is_translation_enabled\":false,\"default_profile_image\":false,\"followers_count\":784,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/495624581919830016/QReOxJHk_normal.jpeg\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://pbs.twimg.com/profile_background_images/483255328608223235/LyEDI-S9.jpeg\",\"profile_background_image_url_https\":\"https://pbs.twimg.com/profile_background_images/483255328608223235/LyEDI-S9.jpeg\",\"follow_request_sent\":false,\"entities\":{\"description\":{\"urls\":[]},\"url\":{\"urls\":[{\"expanded_url\":\"http://Instagram.com/em_naomi\",\"indices\":[0,22],\"display_url\":\"Instagram.com/em_naomi\",\"url\":\"http://t.co/5kvJj4RYdr\"}]}},\"url\":\"http://t.co/5kvJj4RYdr\",\"utc_offset\":7200,\"time_zone\":\"Amsterdam\",\"notifications\":false,\"profile_use_background_image\":true,\"friends_count\":616,\"profile_sidebar_fill_color\":\"FFFFFF\",\"screen_name\":\"HellaNovelli_\",\"id_str\":\"410185461\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/495624581919830016/QReOxJHk_normal.jpeg\",\"listed_count\":1,\"is_translator\":false},\"coordinates\":null,\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"}},\"contributors\":null,\"text\":\"RT @HellaNovelli_: im rly excited for ptv and sws tour even though they havent released dates or anything\",\"geo\":null,\"retweeted\":false,\"in_reply_to_screen_name\":null,\"truncated\":false,\"lang\":\"en\",\"entities\":{\"symbols\":[],\"urls\":[],\"hashtags\":[],\"user_mentions\":[{\"id\":410185461,\"name\":\" emma\",\"indices\":[3,17],\"screen_name\":\"HellaNovelli_\",\"id_str\":\"410185461\"}]},\"in_reply_to_status_id_str\":null,\"id\":497336781986365441,\"source\":\"<a href=\\\"http://www.twitter.com\\\" rel=\\\"nofollow\\\">Twitter for Windows Phone<\\/a>\",\"in_reply_to_user_id_str\":null,\"favorited\":false,\"in_reply_to_status_id\":null,\"retweet_count\":1,\"created_at\":\"Thu Aug 07 11:01:43 +0000 2014\",\"in_reply_to_user_id\":null,\"favorite_count\":0,\"id_str\":\"497336781986365441\",\"place\":null,\"user\":{\"location\":\"Nottinghamshire\",\"default_profile\":true,\"profile_background_tile\":false,\"statuses_count\":617,\"lang\":\"en\",\"profile_link_color\":\"0084B4\",\"profile_banner_url\":\"https://pbs.twimg.com/profile_banners/387863842/1404315215\",\"id\":387863842,\"following\":false,\"protected\":false,\"favourites_count\":187,\"profile_text_color\":\"333333\",\"description\":\"Bands// Netflix// Pizza\",\"verified\":false,\"contributors_enabled\":false,\"profile_sidebar_border_color\":\"C0DEED\",\"name\":\"lauren\",\"profile_background_color\":\"C0DEED\",\"created_at\":\"Sun Oct 09 20:06:04 +0000 2011\",\"is_translation_enabled\":false,\"default_profile_image\":false,\"followers_count\":172,\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/496032753855774720/3wJnuQWX_normal.png\",\"geo_enabled\":false,\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"follow_request_sent\":false,\"entities\":{\"description\":{\"urls\":[]}},\"url\":null,\"utc_offset\":null,\"time_zone\":null,\"notifications\":false,\"profile_use_background_image\":true,\"friends_count\":254,\"profile_sidebar_fill_color\":\"DDEEF6\",\"screen_name\":\"laurenargyle__\",\"id_str\":\"387863842\",\"profile_image_url\":\"http://pbs.twimg.com/profile_images/496032753855774720/3wJnuQWX_normal.png\",\"listed_count\":0,\"is_translator\":false},\"coordinates\":null,\"metadata\":{\"result_type\":\"recent\",\"iso_language_code\":\"en\"}}");

			phemeTweet.setHashTags("hastag1, hashtag2");
			tweetsSet.add(phemeTweet);
		}
		
		tweetList.setTweets(tweetsSet);
		

		return tweetList;
	}

//	public void streamTweets(QueryData streamQuery) {
//
//		LOGGER.info(" ##### STREAMING TWEETS ... ");
//		Config config = new Config();
//
//		/*
//		 * Map<String, Object> hbConf = new HashMap<String, Object>();
//		 * hbConf.put("hbase.rootdir", "xxxx"); config.put("hbase.conf",
//		 * hbConf);
//		 */
//
//		TwitterSpout spout = new TwitterSpout(twitterStream, streamQuery.getQuery());
//		TwitterSolrBolt solrBolt = new TwitterSolrBolt("http:\\\\localhost:8080\\solr");
//
//		SimpleHBaseMapper mapper = new SimpleHBaseMapper().withRowKeyField("tweetID")
//				.withColumnFields(new Fields("dcID")).withColumnFields(new Fields("dsID"))
//				.withColumnFields(new Fields("tweetID")).withColumnFields(new Fields("json")).withColumnFamily("tweet");
//
//		HBaseBolt hbaseBolt = new HBaseBolt("pheme_raw_tweet", mapper);
//
//		// TwitterSpout ==> solrBolt
//		// ==> HBaseBolt
//
//		// Test
//		PrinterBolt pBolt = new PrinterBolt();
//
//		LOGGER.info(" ##### BUILDING TOPOLOGY ... ");
//		TopologyBuilder builder = new TopologyBuilder();
//
//		builder.setSpout(CaptureConstants.TWITTER_SPOUT, spout, 1);
//		// builder.setBolt(CaptureConstants.HBASE_BOLT, solrBolt,
//		// 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT);
//		// builder.setBolt(CaptureConstants.SOLR_BOLT, hbaseBolt,
//		// 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT);
//		builder.setBolt("printer_bolt", pBolt, 1).shuffleGrouping(CaptureConstants.TWITTER_SPOUT);
//
//		LocalCluster cluster = new LocalCluster();
//		LOGGER.info(" ##### SUBMITING TOPOLOGY ... ");
//		cluster.submitTopology("CaptureTopology", config, builder.createTopology());
//		try {
//			Thread.sleep(calculateSleepingTime(streamQuery.getCaptureEndDate()));
//		} catch (InterruptedException e) {
//			LOGGER.info(" ##### STREAM QUERY PROCESS ERROR WHILE STREAMING PROCESS WAS RUNNING: " + e.getMessage());
//		}
//		cluster.killTopology("CaptureTopology");
//		cluster.shutdown();
//		LOGGER.info(" ##### STREAM QUERY PROCESS HAS FINISHED ... ");
//
//	}

	private int getRemainingTime(long startRateTime) {
		int waitSeconds = 0;
		int secondsToRate = (int) TimeUnit.SECONDS.toSeconds(System.currentTimeMillis() - startRateTime);
		if (secondsToRate < 60) {
			waitSeconds = secondsToRate;
		}
		return waitSeconds;
	}

	private boolean limitReached() {
		return currentRequests == twitterRate;
	}

//	public TwitterUser getUserProfile(String userID) throws TwitterException {
//		TwitterUser userProf = new TwitterUser();
//		String checkRateMsg = checkRateLimitStatus();
//		boolean limitReached = checkRateMsg.trim() != "";
//
//		User twitterUser = twitter.showUser(Integer.parseInt(userID));
//		userProf.setId("" + twitterUser.getId());
//		userProf.setName(twitterUser.getName());
//		userProf.setScreenName(twitterUser.getScreenName());
//		userProf.setDescription(twitterUser.getDescription());
//		userProf.setLang(twitterUser.getLang());
//		if (twitterUser.getCreatedAt() != null) {
//			userProf.setCreatedAt(twitterUser.getCreatedAt().toString());
//		}
//		userProf.setStatusesCount("" + twitterUser.getStatusesCount());
//		userProf.setGeoEnabled(twitterUser.isGeoEnabled());
//		userProf.setFollowersCount("" + twitterUser.getFollowersCount());
//		userProf.setFriendsCount("" + twitterUser.getFriendsCount());
//		if (twitterUser.getURL() != null) {
//			userProf.setUrl(twitterUser.getURL().toString());
//		}
//		userProf.setListedCount("" + twitterUser.getListedCount());
//		userProf.setUtcOffset("" + twitterUser.getUtcOffset());
//
//		// Followers&Friends
//		// TO-DO: develop this by means of Hadoop Job
//		if (!limitReached) {
//			IDs ids = twitter.getFriendsIDs(Integer.parseInt(userID), -1);
//			if (ids != null) {
//				userProf.setUserFriends(formatUserIds(ids.getIDs()));
//			}
//			ids = twitter.getFollowersIDs(Integer.parseInt(userID), -1);
//			if (ids != null) {
//				userProf.setUserFollowers(formatUserIds(ids.getIDs()));
//			}
//		}
//		return userProf;
//	}

	private String formatUserIds(long[] ids) {
		String idsStr = "";
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				if (i == 0) {
					idsStr += "" + ids[i];
				} else {
					idsStr += ", " + ids[i];
				}
			}
		}
		return idsStr;
	}

	private synchronized void recalculateLimitRate() {

		long timestamp = System.currentTimeMillis();
		long minutesToWindowRate = TimeUnit.MINUTES.toMinutes(timestamp - windowsRateTimestamp);
		if (minutesToWindowRate < windowsRateTime) {
			twitterRate = (int) ((windowsRate - currentRequests) / (windowsRateTime - minutesToWindowRate));

			// Less than 30 req have been done during previous minutes
			if (twitterRate > minRate) {
				twitterRate = minRate;
			} else {
				twitterRate = twitterRate - currentRequests;
			}

		} else {
			windowsRateTimestamp = timestamp;
			twitterRate = windowsRate / windowsRateTime;
		}
		currentRequests = 0;
	}

	private String checkRateLimitStatus() {
		String msg = "";
		try {
			Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus();
			for (String endpoint : rateLimitStatus.keySet()) {
				RateLimitStatus status = rateLimitStatus.get(endpoint);
				// System.out.println(" Remaining: " +
				// status.getRemaining()+"\n");
				if (status.getRemaining() <= 2) {
					int remainingTime = status.getSecondsUntilReset();
					if (remainingTime > 0) {
						msg = "Twitter request rate limit reached trying to get Followers and Friends. Waiting "
								+ remainingTime / 60 + " minutes to request again.";
					}

					/*
					 * try { Thread.sleep(remainingTime*1000); } catch
					 * (InterruptedException e) { e.printStackTrace(); }
					 */
				}
			}
		} catch (TwitterException te) {
			System.err.println(te.getMessage());
			/*
			 * if (te.getStatusCode()==503) { try { Thread.sleep(120*1000);//
			 * wait 2 minutes } catch (InterruptedException e) {
			 * e.printStackTrace(); } }
			 */
		} catch (Exception e) {
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

}
