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

import org.apache.solr.client.solrj.beans.Field;

import atos.knowledgelab.capture.util.DataChannelState;

public class QueryData {
	
	@Field("id")
	private String composedID;
	
	@Field
	private String dcID;
	
	@Field
	private String dsID;
	
	@Field
	private String tweetID;
	
	@Field
	private long lastID;

	//this is workaround. those fields (lastID & lastTweetID) should be merged in the future:
	@Field
	private String lastTweetID;
	
	@Field("fromLastTweetID")
	private boolean fromLastID;
	
	@Field
	private String type;
		
	@Field("keywords")
	private String query;
	
	@Field
	private String captureStartDate;
	
	@Field
	private String captureEndDate;
	
	private int positioninQueue = 0; 
	
	@Field
	private boolean chronologicalOrder;

	@Field
	private long historicalLimit = 0;
	
	@Field
	private Long totalTweetCount = 0L;

	
	@Field
	private DataChannelState state;

	public QueryData(){
		
	}
	
	public QueryData(String dcId, String dsId, String type, String keywords, String startDate, String endDate) {
		this.composedID = dcId + "-" + dsId;
		this.dcID = dcId;
		this.dsID = dsId;
		this.lastID = Long.MAX_VALUE;
		this.fromLastID = false;
		this.type = type;
		this.query = keywords;
		this.captureStartDate = startDate;
		this.captureEndDate = endDate;
		
		this.state = DataChannelState.READY;
	}
	public String getDsID() {
		return dsID;
	}
	public void setDsID(String dsID) {
		this.dsID = dsID;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}

	public String getComposedID() {
		return composedID;
	}

	public void setComposedID(String composedID) {
		this.composedID = composedID;
	}

	public String getDcID() {
		return dcID;
	}

	public void setDcID(String dcID) {
		this.dcID = dcID;
	}

	public String getTweetID() {
		return tweetID;
	}

	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCaptureStartDate() {
		return captureStartDate;
	}

	public void setCaptureStartDate(String captureStartDate) {
		this.captureStartDate = captureStartDate;
	}

	public String getCaptureEndDate() {
		return captureEndDate;
	}

	public void setCaptureEndDate(String captureEndDate) {
		this.captureEndDate = captureEndDate;
	}

	public int getPositioninQueue() {
		return positioninQueue;
	}

	public void setPositioninQueue(int positioninQueue) {
		this.positioninQueue = positioninQueue;
	}
	
	public long getLastID() {
		return lastID;
	}

	public void setLastID(long lastID) {
		this.lastID = lastID;
	}

	public String getLastTweetID() {
		return lastTweetID;
	}

	public void setLastTweetID(String lastTweetID) {
		this.lastTweetID = lastTweetID;
	}

	public boolean isFromLastID() {
		return fromLastID;
	}

	public void setFromLastID(boolean fromLastID) {
		this.fromLastID = fromLastID;
	}

	public boolean isChronologicalOrder() {
		return chronologicalOrder;
	}

	public void setChronologicalOrder(boolean chronologilcalOrder) {
		this.chronologicalOrder = chronologilcalOrder;
	}

	public long getHistoricalLimit() {
		return historicalLimit;
	}

	public void setHistoricalLimit(long historicalLimit) {
		this.historicalLimit = historicalLimit;
	}

	public DataChannelState getState() {
		return state;
	}

	public void setState(DataChannelState state) {
		this.state = state;
	}

	public Long getTotalTweetCount() {
		return totalTweetCount;
	}

	public void setTotalTweetCount(Long totalTweetCount) {
		this.totalTweetCount = totalTweetCount;
	}
	

}
