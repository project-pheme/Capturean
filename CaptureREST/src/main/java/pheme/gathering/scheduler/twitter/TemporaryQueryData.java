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
package pheme.gathering.scheduler.twitter;

import java.util.ArrayList;
import java.util.List;

import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;
import twitter4j.Query;
import twitter4j.QueryResult;

public class TemporaryQueryData {

	QueryData queryData;
	Query query;
	QueryResult result = null;
	ArrayList<Tweet> tweetsSet = new ArrayList<Tweet>();

	long lastID;
	long lastIDTweetLimit;
	boolean hasNextPage;
	long maxID;

	// new fields:
	long totalTweetCount = 0L;
	long historicalLimit = 0L;
	List<Long> tweetRate = new ArrayList<Long>();

	/**
	 * 
	 * A class for storing temporal variables when switching between various
	 * queries.
	 * 
	 * As queries are now running in a "breath first" fashion: in one "round" we
	 * run one query for every active data source. Therefore we are switching
	 * between datasources all the time. In order to maintain the state of the
	 * queries, we have to store it's internal state (such as max id, or last
	 * tweet id, etc). The state is stored after performing a twitter query, and
	 * restored next time the same query is running.
	 * 
	 * 
	 * 
	 * @param captureQuery
	 *            the query data object to be stored.
	 */
	public TemporaryQueryData(QueryData captureQuery) {

		// initial settings for search query loop

		this.queryData = captureQuery;
		this.query = new Query(captureQuery.getQuery());
		this.lastIDTweetLimit = 0;

		this.hasNextPage = true;
		this.lastID = Long.MAX_VALUE;
		this.lastIDTweetLimit = 0;
		this.maxID = 0;

		this.totalTweetCount = captureQuery.getTotalTweetCount(); 
		
		if (captureQuery.isFromLastID() == true) {
			// setting sinceId as lastId query value
			query.setSinceId(captureQuery.getLastID());
			lastIDTweetLimit = captureQuery.getLastID();
		} else {
			query.setSinceId(0);
		}

	}

	public QueryData getQueryData() {
		return queryData;
	}

	public void setQueryData(QueryData queryData) {
		this.queryData = queryData;
	}

	public ArrayList<Tweet> getTweetsSet() {
		return tweetsSet;
	}

	public void setTweetsSet(ArrayList<Tweet> tweetSet) {
		this.tweetsSet = tweetSet;
	}

	public long getLastIDTweetLimit() {
		return lastIDTweetLimit;
	}

	public void setLastIDTweetLimit(long lastIDTweetLimit) {
		this.lastIDTweetLimit = lastIDTweetLimit;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public QueryResult getResult() {
		return result;
	}

	public void setResult(QueryResult result) {
		this.result = result;
	}

	public boolean isHasNextPage() {
		return hasNextPage;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public long getLastID() {
		return lastID;
	}

	public void setLastID(long lastID) {
		this.lastID = lastID;
	}

	public long getMaxID() {
		return maxID;
	}

	public void setMaxID(long maxID) {
		this.maxID = maxID;
	}

	public long getTotalTweetCount() {
		return totalTweetCount;
	}

	public void setTotalTweetCount(long totalTweetCount) {
		this.totalTweetCount = totalTweetCount;
	}

	public List<Long> getTweetRate() {
		return tweetRate;
	}

	public void setTweetRate(List<Long> tweetRate) {
		this.tweetRate = tweetRate;
	}

	public long getHistoricalLimit() {
		return historicalLimit;
	}

	public void setHistoricalLimit(long historicalLimit) {
		this.historicalLimit = historicalLimit;
	}

}
