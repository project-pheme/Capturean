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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.QueryData;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TwitterStreamWorker extends Thread {
	private final static Logger LOGGER = Logger.getLogger(TwitterStatusListener.class.getName());
	private TwitterStream twitterStream;
	private QueryData query;
	private String queryType;

	public TwitterStreamWorker(TwitterStreamFactory tsf, QueryData qd, String queryType) {
		this.twitterStream = tsf.getInstance();
		this.query = qd;
		this.queryType = queryType;
	}

	@Override
	public void run() {

		LOGGER.info(" ##### STARTING PRODUCER & STREAMING TWEETS ... ");

		TwitterStreamProducer producer;
		try {
			producer = new TwitterStreamProducer(twitterStream, query, this.queryType);
			producer.start();

			try {
				Thread.sleep(calculateSleepingTime(query.getCaptureEndDate()));

			} catch (InterruptedException e) {
				LOGGER.info(" ##### STREAM QUERY INTERRUPTED (POSSIBLY BY USER). Shutting down... ");
				twitterStream.shutdown();
				producer.shutdown();
			}

			twitterStream.shutdown();
			producer.shutdown();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			
		}

		LOGGER.info(" ##### QUERY STREAM PROCESS HAS FINISHED... ");

	}

	// TODO: move to common utility class (same method is repeating in
	// TwitterManager
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
