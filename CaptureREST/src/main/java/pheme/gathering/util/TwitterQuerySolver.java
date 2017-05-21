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
package pheme.gathering.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import pheme.gathering.search.TweetAdHocQuery;
import pheme.gathering.search.TweetManager;

public class TwitterQuerySolver extends Thread {

	private static TwitterQuerySolver instance = null;
	private TweetManager twitter;	
	private TweetAdHocQuery twitterAdHoc;
	private CaptureFacadeIf cfi;
	private final static Logger LOGGER = Logger
			.getLogger(TwitterQuerySolver.class.getName());

	protected TwitterQuerySolver() throws Exception {
		LOGGER.info(" #TwitterQuerySolver: STARTING ...");
		this.twitter = TweetManager.getInstance();
		this.twitterAdHoc = new TweetAdHocQuery();
		this.cfi = CaptureFacadeFactory.getInstance();
	}

	// Singleton Pattern
	public static TwitterQuerySolver getInstance() throws Exception {
		if (instance == null) {
			instance = new TwitterQuerySolver();
		}
		return instance;
	}

	private void init() {
		try {
			LOGGER.info(" #TwitterQuerySolver: LOADING ACTIVE QUERIES ...");
			cfi.getQueryQueue().loadQueryQueue(cfi.getActiveQueriesMapByDSType("twitter"));
		} catch (Exception e) {
			LOGGER.severe(" #TwitterQuerySolver: ERROR while loading query queue:"
					+ e.getMessage());
		}
	}
	
	public TweetAdHocQuery getTwitterAdHocQuery() {
		return this.twitterAdHoc;
	}

	@Override
	public void run() {
		LOGGER.info(" #TwitterQuerySolver: RUN CAPTURE PROCESS ...");
		QueryData query = new QueryData();
		query.setQuery("Empty Query");

		init();

		while (true /* !activeQueries.isEmpty() */) {

			try {
				
				LinkedHashMap<String, QueryData> queriesMap = cfi.getActiveQueriesMapByDSType("twitter");
				Set<Entry<String, QueryData>> queries = queriesMap.entrySet();
				
				//debug:
				for (Entry<String, QueryData> q : queries) {
					ObjectMapper om = new ObjectMapper();
					om.setSerializationInclusion(Include.NON_NULL);
					String s = q.getKey() + " " + om.writeValueAsString(q.getValue());
					
					System.out.println(s);
					
				}
				
				
				
				
				
				//System.out.println(Arrays.toString(cfi.getActiveQueriesMap().entrySet().toArray()));
				
				
				// First query to be executed
				query = cfi.getQueryQueue().getActiveQuery();

				String queryId = query.getDsID();

				LOGGER.info(" #TwitterQuerySolver: QUERY_ID = " + queryId);

				// query = activeQueries.get(queryId);

				// Remove Query from Queue
				// activeQueries.deleteQueryToQueue(queryId);

				LOGGER.info(" #TwitterQuerySolver: LAUNCHING QUERY = " 
						+ query.getQuery() 
						+ ", use chronologilcal: " + query.isChronologicalOrder()
						+ ", use lastTweetID: " + query.isFromLastID() 
						+ ", lastTweetID value: " + query.getLastID());

				// Search Tweets
				TweetList tweetList = twitter.searchTweets(query);
				LOGGER.info("Number of tweets: " + tweetList.getCount()
						+ " for query " + query.getQuery());

				// Store Tweets
				cfi.addTweetsToDataChannel(tweetList, query.getDcID(),
						query.getDsID(), query);
				
				// Update the datasource en solr
				cfi.updateDataSource(query);

				QueryData upToDateQuery = cfi.getQueryQueue()
						.getQueryFromQueueById(queryId);

				// ***** SETTING LASTTWEETID !!! *****
				// Ojo, aqui, si el datachannel ha sido modificado (conretamente el lastTweetId) en medio de la query, cuando acabe la query pondra 
				// el lastTweetId que sale de la searchTweet y por tanto machacara el que se acabe de meter en el datachannel.
				// Este lastTweetId esta modificado solo para cuando se reinicia un datachannel sorted, pero no pasad nada, por qne la siguinete query se resetea.
				upToDateQuery.setLastID(query.getLastID());
				
				if (upToDateQuery != null) {

					// Remove Query from Queue
					cfi.getQueryQueue().deleteQueryToQueue(queryId);

					if (cfi.isQueryActive(queryId)) {
						// Put query in the Queue
						cfi.getQueryQueue().addQueryToQueue(queryId,
								upToDateQuery);
					}
				}
				
			} catch (Exception e) {
				LOGGER.severe(" #TwitterQuerySolver: ERROR capturing data from Twitter with query: "
						+ query.getQuery()
						+ ". Exception in thread associate to run(): "
						+ e.getMessage());
				e.printStackTrace();
				//this.run();
			}
		}

	}

}
