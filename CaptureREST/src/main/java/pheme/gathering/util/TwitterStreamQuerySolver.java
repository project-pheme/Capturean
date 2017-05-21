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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.persistence.JPAHBaseManager;
import atos.knowledgelab.capture.persistence.SolrManager;
import atos.knowledgelab.capture.persistence.denormalized.SolrManagerFast;
import pheme.gathering.kafka.TwitterStreamWorker;
import pheme.gathering.search.TweetManager;
//import org.apache.hadoop.hbase.avro.generated.HBase;

public class TwitterStreamQuerySolver {

	private QueryData streamQuery = null;
	private static TwitterStreamQuerySolver instance = null;	
	private TweetManager twitter;
	private CaptureFacadeIf cfi;

	private final static Logger LOGGER = Logger.getLogger(TwitterStreamQuerySolver.class.getName());
	private String mode;
	private ExecutorService executorService;
	private TwitterStreamWorker workerThread;
	private Future workerThreadHandle;

	protected TwitterStreamQuerySolver() throws Exception {
		LOGGER.info(" #TwitterStreamQuerySolver: STARTING ...");		
		this.twitter = TweetManager.getInstance();
		this.cfi = CaptureFacadeFactory.getInstance();
		executorService = Executors.newFixedThreadPool(1);
	}

	// Singleton Pattern
	public static TwitterStreamQuerySolver getInstance() throws Exception {
		if (instance == null) {
			instance = new TwitterStreamQuerySolver();
		}
		return instance;
	}

	private void init() throws Exception {
		LOGGER.info(" #TwitterStreamQuerySolver: LOADING ACTIVE STREAM QUERY ...");
		streamQuery = cfi.getActiveStreamQuery();
		mode = twitter.getTwitterStreamMode();
	}

	public void startStream() {
		LOGGER.info(" #TwitterStreamQuerySolver: Starting stream ... ");

		try {
			init();
			if (streamQuery != null) {
				if (mode.equalsIgnoreCase("kafka") == true) {
					this.run();
				}
				// legacy
//				if (mode.equalsIgnoreCase("storm") == true) {
//					twitter.streamTweetsStorm(streamQuery);
//				}
				if (mode.equalsIgnoreCase("dummy") == true) {
					LOGGER.info(" ##### DUMMY STREAMING NOT IMPLEMENTED ... ");
				}
			}
			
		} catch (InterruptedException e) {
			LOGGER.severe(" #TwitterStreamQuerySolver: Stream interrupted. Query: " + streamQuery + ".");
		} catch (Exception e) {
			LOGGER.severe(" #TwitterStreamQuerySolver: ERROR capturing data from Twitter with stream query: " + streamQuery
					+ ". Exception in thread associate to run(): " + e.getMessage());
		}
	}

	public void stopStream() {
		LOGGER.info(" #TwitterStreamQuerySolver: Stop streaming ...");

		if (workerThread != null) {
			workerThreadHandle.cancel(true);
			
			//block & wait for thread to terminate.

			executorService.shutdown();
			try {
				executorService.awaitTermination(10, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void run() {
		if (streamQuery != null) {
			LOGGER.info(" #TwitterStreamQuerySolver: Run stream query " + streamQuery.getQuery());
			executorService = Executors.newScheduledThreadPool(1);
			workerThread = new TwitterStreamWorker(twitter.getTwitterStreamFactory(), streamQuery, /* temporal fix */ "location");
			workerThreadHandle = executorService.submit(workerThread);
			
		}				
	}

}
