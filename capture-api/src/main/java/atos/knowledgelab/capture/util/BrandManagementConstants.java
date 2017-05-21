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

import java.nio.ByteBuffer;

public class BrandManagementConstants {

	public static final String userAggregate_tablename = "capture_useraggregate_fast";
	public static final String userAggregate_columnFamily_reach = "capture_reach";
	public static final String userAggregate_columnFamily_engagement = "capture_engagement";
	public static final String userAggregate_columnFamily_influential = "capture_influential";
	public static final String userAggregate_col_reach_followers = "followers";
	public static final String userAggregate_col_reach_followerslist = "followers_list";
	public static final String userAggregate_col_reach_mentions = "mentions";
	public static final String userAggregate_col_reach_mentionslist = "mentions_list";
	public static final String userAggregate_col_reach_retweets = "retweets";
	public static final String userAggregate_col_reach_retweetslist = "retweets_list";
	public static final String userAggregate_col_reach_subfollowers = "subfollowers";
	public static final String userAggregate_col_reach_total = "total";
	public static final String userAggregate_col_engagement_retweets = "retweets";
	public static final String userAggregate_col_engagement_replies = "replies";
	public static final String userAggregate_col_engagement_mentions = "mentions";
	public static final String userAggregate_col_engagement_favorites = "favorites";
	public static final String userAggregate_col_engagement_total = "total";
	public static final String userAggregate_col_influential_retweets = "influentialretweets";
	public static final String userAggregate_col_influential_replies = "influentialreplies";
	public static final String userAggregate_col_influential_mentions = "influentialmentions";
	public static final String userAggregate_col_influential_influencers = "influentialinfluencers";
	public static final String userAggregate_col_influential_tweets = "influentialtweets";
	public static final String userAggregateReach = "01";
	public static final String userAggregateEngagement = "02";
	public static final String userAggregateInfluential = "03";	
	
	public static final String captureBrandTable = "capture_brand_fast";
	public static final String captureBrandColFamily = "capture_brand";
	public static final String captureBrand_col_brandID = "brandid";
	public static final String captureBrand_col_twitterID = "twitterid";
	public static final String captureBrand_col_screenName = "screenname";
	public static final String captureBrand_col_name = "name";
	public static final String captureBrand_col_alternativeNames = "alternativenames";
	public static final String captureBrand_col_dcIDs = "dcids";
	public static final String captureBrand_col_status = "status";
	public static final String captureBrand_col_competitors = "competitors";
	
	public static final String MOST_INFLUENCIAL_TWEETS_RETWEETED = "retweeted";
	public static final String MOST_INFLUENCIAL_TWEETS_REPLIED = "replied";
	public static final String MOST_INFLUENCIAL_TWEETS_MENTIONED = "mentioned";
	public static final String MOST_INFLUENCIAL_INFLUENCERS = "influencers";
	
	
	public static final String userAggregateAmbassador = "04";
	public static final String userAggregate_columnFamily_ambassador = "capture_ambassador";
	public static final String userAggregate_col_ambassador_followers = "followers";
	public static final String userAggregate_col_ambassador_retweets = "retweets";
	public static final String userAggregate_col_ambassador_favorites = "favorites";
	public static final String userAggregate_col_ambassador_hashtags = "hashtags";
	public static final String userAggregate_col_ambassador_tweets = "tweets";
	public static final String userAggregate_col_ambassador_replies = "replies";
	public static final String userAggregate_col_ambassador_quality = "quality";
	
	public static final String userAggregateAmbassador_ATOS = "05";

	
}
