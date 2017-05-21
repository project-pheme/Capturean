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

public class CaptureConstants {

	public static final String CAPTURE_END_DATE = "captureEndDate";
	public static final String CAPTURE_START_DATE = "captureStartDate";
	public static final String DC_ID = "dcID";
	public static final String DS_ID = "dsID";
	public static final String TWEET_ID = "tweetID";
	public static final String DC_TWEET_ID = "dcTweetID"; //unique key for capture-tweet-fast new core.
	public static final String LAST_TWEET_ID = "lastTweetID"; // lo de Miguel
	public static final String FROM_LAST_TWEET_ID = "fromLastTweetID"; // lo de Miguel
	public static final String CHRONOLOGICAL_ORDER = "chronologicalOrder"; // lo de Nines :)
	public static final String TOTAL_TWEET_COUNT = "totalTweetCount"; 
	public static final String DC_STATE = "dataChannelState"; 
	public static final String DS_STATE = "dsState";
	public static final String HISTORICAL_LIMIT = "historicalLimit"; 
	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String SEARCH = "search";
	public static final String STREAM = "stream";
	public static final String KEYWORDS = "keywords";
	public static final String TEXT = "text";
	public static final String USER_ID = "userID";
	public static final String SOURCE = "source";
	public static final String USER_SCREEN_NAME = "userScreenName";
	public static final String USER_NAME = "userName";
	public static final String USER_DESCRIPTION = "userDescription";
	public static final String CREATED_AT = "createdAt";
	public static final String IN_REPLY = "inReplyToId";
	public static final String ORIGINAL_TWEET = "originalTweetId";
	public static final String RETWEETS = "retweetCount";
	public static final String FAV_COUNT = "favouriteCount";
	public static final String HASHTAGS = "hashTags";
	public static final String SOURCE_URLS = "sourceUrls";
	public static final String PLACE = "place";
	public static final String FOLLOWERS = "userFollowers";
	public static final String FRIENDS = "userFollowes";
	public static final String LATITUDE = "latitude";
	public static final String LONGITUDE = "longitude";
	public static final String JSON = "json";
	public static final String LANG = "lang";
	
	
	public static final String ACTIVE = "active";
	public static final String NON_ACTIVE = "inactive";
	public static final String TWITTER_SPOUT = "twitter_spout";
	public static final String HBASE_BOLT = "hbase_bolt";
	public static final String SOLR_BOLT = "solr_bolt";
	
	
	// New schema: add the data soruce type
	public static final String TYPE_DS = "typeDS";
	public static final String TWITTER = "twitter";
	public static final String LAT_LONG = "latLong";
	// Additional fields for classification results
	public static final String FEATURE_SENTIMENT = "sentiment_feature";
	public static final String FEATURE_STRESS = "stress_feature";
	public static final String FEATURE_DANGEROUSNESS = "dangerousness_feature";
	
	// Pheme project schema improvements
	public static final String MAX_HISTORICAL_LIMIT = "historicalLimit";
	public static final String DATE = "date";
	public static final String END_DATE = "endDate";
	public static final String TYPE_MEASURE = "type_measure";
	public static final String MEASUREMENT = "measurement";
	public static final String PERIODICITY = "periodicty";
	
	public static final String TAGCLOUD = "tagcloud";
	public static final String VOLUME = "volume";
	public static final String SENTIMENT_VOLUME_POSITIVE = "sentiment_volume_positive";
	public static final String SENTIMENT_VOLUME_NEGATIVE = "sentiment_volume_negative";
	public static final String SENTIMENT_VOLUME_NEUTRO = "sentiment_volume_neutro";
	public static final String SENTIMENT_AVERAGE = "sentiment_average";
	
}
