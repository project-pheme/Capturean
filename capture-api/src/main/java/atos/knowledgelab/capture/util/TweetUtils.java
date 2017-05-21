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
package atos.knowledgelab.capture.util;

import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TwitterUser;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.TwitterObjectFactory;
import twitter4j.URLEntity;
import twitter4j.User;


public class TweetUtils {

	static Logger log = Logger.getLogger(TweetUtils.class.getName());
	
	/*
	 * This method converts between Status format (th format of tweet status 
	 * from twitter4j library to the Tweet format (native format for capture)
	 * 
	 * 
	 */
	public static Tweet getTweetFromStatus(Status tweet){
		// Get Tweet in json Format
		String json = TwitterObjectFactory.getRawJSON(tweet);
    	
    	Tweet phemeTweet = new Tweet();                	
        phemeTweet.setId(""+tweet.getId());
        phemeTweet.setDataID(Tweet.class.getSimpleName() + phemeTweet.getId());
        if (tweet.getUser()!=null) {
        	phemeTweet.setUserID(""+tweet.getUser().getId());
        	phemeTweet.setUserScreenName(tweet.getUser().getScreenName());                                    
            phemeTweet.setUserDescription(tweet.getUser().getDescription());
            // New schema: we have changed the type of userFollowers to Integer, then it can not be null and is not necesary el dostring
            // phemeTweet.setUserFollowers(""+tweet.getUser().getFollowersCount());  
            phemeTweet.setUserFollowers(tweet.getUser().getFollowersCount()); 
            // New schema: we have changed the type of userFollowes to Integer, then it can not be null and is not necesary el dostring
            // phemeTweet.setUserFollowes(""+tweet.getUser().getFriendsCount());
            phemeTweet.setUserFollowes(tweet.getUser().getFriendsCount());
        }	
        phemeTweet.setText(tweet.getText());
        phemeTweet.setLang(tweet.getLang());
        phemeTweet.setSource(tweet.getSource());
        // new schema: we have changed the type of tetweetCount to Integer, then it can not be null and is not necesary el dostring
        // phemeTweet.setRetweetCount(""+tweet.getRetweetCount());
        phemeTweet.setRetweetCount(tweet.getRetweetCount());
        // new schema: we have changed the type of favouriteCount to Integer, then it can not be null and is not necesary el dostring
        // phemeTweet.setFavouriteCount(""+tweet.getFavoriteCount());
        phemeTweet.setFavouriteCount(tweet.getFavoriteCount());
        phemeTweet.setInReplyToId(""+tweet.getInReplyToStatusId());                                        
        
        // New schema: change format date
        //phemeTweet.setCreatedAt(tweet.getCreatedAt().toString());
        try {
			phemeTweet.setCreatedAt(formatToUTC(tweet.getCreatedAt()));
		} catch (ParseException e) {
			log.log(Level.WARNING, "Error while getting tweet from status: ", e);
		}
        phemeTweet.setRawJson(json);
        
        // Retweet Status
        if (tweet.getRetweetedStatus()!=null) {
        	phemeTweet.setOriginalTweetId(""+tweet.getRetweetedStatus().getId());
        }	
        // Place data
        if (tweet.getPlace()!=null) {
        	phemeTweet.setPlace(""+tweet.getPlace().getFullName());                        
        }
        // Geo data
        if (tweet.getGeoLocation()!=null) {
            /*Geolocalization geo = new Geolocalization();	                    
            geo.setLatitude(""+ tweet.getGeoLocation().getLatitude());
            geo.setLongitude(""+ tweet.getGeoLocation().getLongitude());
            phemeTweet.setGeo(geo);*/
        	phemeTweet.setLatitude(""+tweet.getGeoLocation().getLatitude());
        	phemeTweet.setLongitude(""+tweet.getGeoLocation().getLongitude());
        	
        	// new schema: add location
    		phemeTweet.setLatLong(formatToLatLong(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude()));
        }
        // Hashtags entities
        if (tweet.getHashtagEntities()!=null) {
        	HashtagEntity[] hashTags = tweet.getHashtagEntities();
        	String hashTexts = "";
        	for (int i=0; i<hashTags.length; i++) {
        		if (hashTexts.trim()!="") {
        			hashTexts += ", ";	
        		}
        		hashTexts += hashTags[i].getText();
        	}
        	phemeTweet.setHashTags(hashTexts);
        }
        
        // URL entities
        if (tweet.getURLEntities()!=null) {
        	URLEntity[] urls = tweet.getURLEntities();
        	String sourceUrls = "";
        	for (int i=0; i<urls.length; i++) {
        		if (sourceUrls.trim()!="") {
        			sourceUrls += ", ";	
        		}
        		sourceUrls += urls[i].getURL();
        	}
        	phemeTweet.setSourceUrls(sourceUrls);
        }  
        
        // Get User Profile
        /*if (!limitReached && reqCount <= 15) {
           this.getUserProfile(""+tweet.getUser().getId());
           reqCount++;
        }*/
        
        return phemeTweet;
	}
	
	public static Tweet getTweetFromStatus(Status tweet, String json){
		    	
    	Tweet phemeTweet = new Tweet();                	
        phemeTweet.setId(""+tweet.getId());
        phemeTweet.setDataID(Tweet.class.getSimpleName() + phemeTweet.getId());
        if (tweet.getUser()!=null) {
        	phemeTweet.setUserID(""+tweet.getUser().getId());
        	phemeTweet.setUserScreenName(tweet.getUser().getScreenName());                                    
            phemeTweet.setUserDescription(tweet.getUser().getDescription());
            // New schema: we have changed the type of userFollowers to Integer, then it can not be null and is not necesary el dostring
            // phemeTweet.setUserFollowers(""+tweet.getUser().getFollowersCount());  
            phemeTweet.setUserFollowers(tweet.getUser().getFollowersCount()); 
            // New schema: we have changed the type of userFollowes to Integer, then it can not be null and is not necesary el dostring
            // phemeTweet.setUserFollowes(""+tweet.getUser().getFriendsCount());
            phemeTweet.setUserFollowes(tweet.getUser().getFriendsCount());
        }	
        phemeTweet.setText(tweet.getText());
        phemeTweet.setSource(tweet.getSource());
        // new schema: we have changed the type of tetweetCount to Integer, then it can not be null and is not necesary el dostring
        // phemeTweet.setRetweetCount(""+tweet.getRetweetCount());
        phemeTweet.setRetweetCount(tweet.getRetweetCount());
        // new schema: we have changed the type of favouriteCount to Integer, then it can not be null and is not necesary el dostring
        // phemeTweet.setFavouriteCount(""+tweet.getFavoriteCount());
        phemeTweet.setFavouriteCount(tweet.getFavoriteCount());
        phemeTweet.setInReplyToId(""+tweet.getInReplyToStatusId());                                        
        
        // New schema: change format date
        //phemeTweet.setCreatedAt(tweet.getCreatedAt().toString());
        try {
			phemeTweet.setCreatedAt(formatToUTC(tweet.getCreatedAt()));
		} catch (ParseException e) {
			log.log(Level.WARNING, "Error while getting tweet from status: ", e);
		}
        phemeTweet.setRawJson(json);
        
        // Retweet Status
        if (tweet.getRetweetedStatus()!=null) {
        	phemeTweet.setOriginalTweetId(""+tweet.getRetweetedStatus().getId());
        }	
        // Place data
        if (tweet.getPlace()!=null) {
        	phemeTweet.setPlace(""+tweet.getPlace().getFullName());                        
        }
        // Geo data
        if (tweet.getGeoLocation()!=null) {
            /*Geolocalization geo = new Geolocalization();	                    
            geo.setLatitude(""+ tweet.getGeoLocation().getLatitude());
            geo.setLongitude(""+ tweet.getGeoLocation().getLongitude());
            phemeTweet.setGeo(geo);*/
        	phemeTweet.setLatitude(""+tweet.getGeoLocation().getLatitude());
        	phemeTweet.setLongitude(""+tweet.getGeoLocation().getLongitude());
        	
        	// new schema: add location
    		phemeTweet.setLatLong(formatToLatLong(tweet.getGeoLocation().getLatitude(), tweet.getGeoLocation().getLongitude()));
        }
        // Hashtags entities
        if (tweet.getHashtagEntities()!=null) {
        	HashtagEntity[] hashTags = tweet.getHashtagEntities();
        	String hashTexts = "";
        	for (int i=0; i<hashTags.length; i++) {
        		if (hashTexts.trim()!="") {
        			hashTexts += ", ";	
        		}
        		hashTexts += hashTags[i].getText();
        	}
        	phemeTweet.setHashTags(hashTexts);
        }
        
        // URL entities
        if (tweet.getURLEntities()!=null) {
        	URLEntity[] urls = tweet.getURLEntities();
        	String sourceUrls = "";
        	for (int i=0; i<urls.length; i++) {
        		if (sourceUrls.trim()!="") {
        			sourceUrls += ", ";	
        		}
        		sourceUrls += urls[i].getURL();
        	}
        	phemeTweet.setSourceUrls(sourceUrls);
        }  
        
        // Get User Profile
        /*if (!limitReached && reqCount <= 15) {
           this.getUserProfile(""+tweet.getUser().getId());
           reqCount++;
        }*/
        
        return phemeTweet;
	}
	
	// New schema: change format date
	private static Date formatToUTC(Date date) throws ParseException {
//	 	String utcFormat = "yyyy-MM-dd'T'HH:mm:ss'Z'";
//		SimpleDateFormat utcf = new SimpleDateFormat(utcFormat, Locale.ENGLISH);
//		utcf.setLenient(true);
//		String stringUTC =  utcf.format (date);
//		return utcf.parse (stringUTC);
		return date;
	}
	
	// New schema: change format date
	private static String formatToLatLong(double latitude, double longitude) {
		return Double.toString(latitude) +","+ Double.toString(longitude);
	}

	public static String serializeTweetToXML(Tweet tweet) throws JAXBException {
		Writer writer = new StringWriter();
    	JAXBContext jc = JAXBContext.newInstance(Tweet.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        JAXBElement<Tweet> jaxbElement = new JAXBElement<Tweet>(new QName("streamItem"), Tweet.class, tweet);
        marshaller.marshal(jaxbElement, writer);

        return writer.toString();
	}
	
	
	public static TwitterUser getTwitterUserFromUser(User user){
		
		TwitterUser captureUser = new TwitterUser();
		if (user.getCreatedAt()!=null) {
			captureUser.setCreatedAt(user.getCreatedAt());
		}
		captureUser.setDescription(user.getDescription());
		captureUser.setFollowersCount(user.getFollowersCount());
		captureUser.setFriendsCount(user.getFriendsCount());
		captureUser.setId(String.valueOf(user.getId()));
		captureUser.setLang(user.getLang());
		captureUser.setLocation(user.getLocation());
		captureUser.setGeoEnabled(user.isGeoEnabled());
		captureUser.setName(user.getName());
		captureUser.setScreenName(user.getScreenName());
		if (user.getURL()!=null) {
			captureUser.setUrl(user.getURL().toString());
			}
		captureUser.setStatusesCount(user.getStatusesCount());
		captureUser.setListedCount(""+user.getListedCount());
		captureUser.setUtcOffset(""+user.getUtcOffset());
		
		return null;
		
	}

}
