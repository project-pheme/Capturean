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
package pheme.gathering.kafka;

import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.util.TweetUtils;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class TwitterStatusListener implements StatusListener {
	private final static Logger LOGGER = Logger.getLogger(TwitterStatusListener.class.getName());
	private final StreamProducer kafkaProducer;
	private TwitterStream twitterStream;
	private String kafkaTopic;
	//private String dataChannelId;
	//private String dataSourceId;
    private QueryData query;

	public TwitterStatusListener(TwitterStream twitterStream, StreamProducer kafkaProducer, String kafkaTopic, QueryData query) {
		this.kafkaProducer = kafkaProducer;
		this.twitterStream = twitterStream;
		this.kafkaTopic = kafkaTopic;
		//this.dataChannelId = dcId;
    	//this.dataSourceId = dsId;
    	this.query = query;

	}

	// The onStatus method is executed every time a new tweet comes in.
	public void onStatus(Status status) {
		// The EventBuilder is used to build an event using the
		// the raw JSON of a tweet
		//LOGGER.info(status.getUser().getScreenName() + ": " + status.getText());
		
		
		//create object to be send
		StreamItem si = new StreamItem();
		si.setDataChannelId(query.getDcID());
		si.setDataSourceId(query.getDsID());
		si.setQueryData(query);
		si.setTweet(TweetUtils.getTweetFromStatus(status));
		
		kafkaProducer.send(si);

	}

	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		LOGGER.info("WARNING: onDeletionNotice Twitter API message: " + statusDeletionNotice);
	}

	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		LOGGER.info("WARNING: onTrackLimitationNotice Twitter API message: " + numberOfLimitedStatuses);
	}

	public void onScrubGeo(long userId, long upToStatusId) {
	}

	public void onException(Exception ex) {
		LOGGER.info("Twitter exception: " + ex.getMessage());
		//LOGGER.info("Shutting down Twitter sample stream...");
		//twitterStream.shutdown();
	}

	public void onStallWarning(StallWarning warning) {
		LOGGER.info("Twitter API Stall Warning: " + warning.getMessage());
	}
}
