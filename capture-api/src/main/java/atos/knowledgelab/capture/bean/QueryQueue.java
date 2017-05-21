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
package atos.knowledgelab.capture.bean;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

public class QueryQueue {

	public QueryQueue(){
		activeQueries = new LinkedHashMap<String, QueryData>(1000);
	}
	
	//Singleton Pattern
	public static QueryQueue getInstance() throws Exception {
	  if(instance == null) {
	     instance = new QueryQueue();         
	  }
	  return instance;
	}
	
	private static QueryQueue instance = null;
	private LinkedHashMap<String, QueryData> activeQueries;
	private QueryData activeQuery;
	private final static Logger LOGGER = Logger.getLogger(QueryQueue.class.getName());

	public void loadQueryQueue (LinkedHashMap<String, QueryData> queries) {
		this.activeQueries = queries;
	}
	
	public synchronized void addQueryToQueue (String id, QueryData query) {
		this.activeQueries.put(id, query);
		LOGGER.info(" #QueryQueue: NOTIFYING NEW QUERY ... ");
		notifyAll();	
	}
	
	public synchronized void deleteQueryToQueue(String dsID) {
		this.activeQueries.remove(dsID);
		LOGGER.info(" #QueryQueue: DELETED QUERY " + dsID);
	}
	
	public synchronized QueryData getQueryFromQueueById (String id) {
		QueryData query = null;
		if (!activeQueries.isEmpty()) {
			LOGGER.info(" #QueryQueue: RECOVERING QUERY WITH ID = " + id);
			query = activeQueries.get(id);
		}
		return query;
	}
	
	public synchronized QueryData getActiveQuery () {		
		if (activeQueries.isEmpty()) {
			LOGGER.info(" #QueryQueue: WAIT FOR ACTIVE QUERIES ... ");
			try {
				wait();
			} catch (InterruptedException e) {
				LOGGER.severe(" #QueryQueue: ERROR trying to wait for active queries. Exception: " + e.getMessage());
			}
		}
		activeQuery = activeQueries.values().iterator().next();
		return activeQuery;
	}
	
	public synchronized void waitForNewQueries() {		
		try {
			wait();
		} catch (InterruptedException e) {
			LOGGER.severe(" #QueryQueue: ERROR trying to wait for active queries. Exception: " + e.getMessage());
		}
		return;
	}
	
	public QueryData getQueryOnExecution() {
		return activeQuery;
	}

	public Set<Entry<String, QueryData>> getAllQueriesOnQueue() {		
		return activeQueries.entrySet();
	}
	
}
