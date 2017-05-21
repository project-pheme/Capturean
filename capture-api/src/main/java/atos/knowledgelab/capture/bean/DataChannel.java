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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atos.knowledgelab.capture.util.CaptureConstants;
import atos.knowledgelab.capture.util.DataChannelState;


@XmlRootElement(name="dataChannel")
public class DataChannel {
	
	@XmlTransient
	private String channelID;
	
	@XmlTransient
	private String name;
	
	@XmlTransient
	private String type = CaptureConstants.SEARCH;
	
	@XmlTransient
	private String description;
	
	@XmlTransient
	private String creationDate;
	
	@XmlTransient
	private String updateDate;
	
	@XmlTransient
	private String startCaptureDate;
	
	@XmlTransient
	private String endCaptureDate;
	
	@XmlTransient
	private String status = CaptureConstants.ACTIVE;

	@XmlTransient	
	private List<DataSource> dataSources = new ArrayList<DataSource>();
	
	/* New fields for the Pheme dashboard:
	 * - max tweet limit for historical tweets (to get a glimpse of historical event)
	 * - tweet rate - updated value of the tweet rate. This field is important for
	 *   establishing sound scheduler for DC/Query 
	 * - number of tweets retrieved: while this might be obtained from rest/datachannel services,
	 *   it is normally unavailable for stream DCs, when data is sent to different storage.
	 *   This field is for monitoring/GUI purposes
	 *    
	 */
	
	@XmlTransient
	private Long totalTweetCount;
	
	@XmlTransient
	private Double tweetRate;
	
//	@XmlTransient
//	private Long maxHistoricalTweetLimit;
	
	@XmlTransient
	private DataChannelState dcState;
	
	public Long getTotalTweetCount() {
		return totalTweetCount;
	}

	@XmlElement(name="dataChannelState")
	public DataChannelState getDcState() {
		return dcState;
	}

	public void setDcState(DataChannelState dcState) {
		this.dcState = dcState;
	}

	public void setTotalTweetCount(Long totalTweetCount) {
		this.totalTweetCount = totalTweetCount;
	}

	@XmlElement(name="tweetRate")
	public Double getTweetRate() {
		return tweetRate;
	}

	public void setTweetRate(Double tweetRate) {
		this.tweetRate = tweetRate;
	}

//	@XmlElement(name="maxHistoricalTweetLimit")
//	public Long getMaxHistoricalTweetLimit() {
//		return maxHistoricalTweetLimit;
//	}
//
//	public void setMaxHistoricalTweetLimit(Long maxHistoricalTweetLimit) {
//		this.maxHistoricalTweetLimit = maxHistoricalTweetLimit;
//	}

	@XmlElement(name="channelID")
	public String getChannelID() {
		return channelID;
	}

	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}

	@XmlElement(name="name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name="creationDate")
	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	@XmlElement(name="updateDate")
	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	@XmlElementWrapper(name="dataSources")
	@XmlElements({
	     @XmlElement(name="twitter", type=TwitterDataSource.class),
	     @XmlElement(name="reddit", type=RedditDataSource.class)
	})
	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	@XmlElement(name="startCaptureDate")
	public String getStartCaptureDate() {
		return startCaptureDate;
	}

	public void setStartCaptureDate(String startCaptureDate) {
		this.startCaptureDate = startCaptureDate;
	}

	@XmlElement(name="endCaptureDate")
	public String getEndCaptureDate() {
		return endCaptureDate;
	}

	public void setEndCaptureDate(String endCaptureDate) {
		this.endCaptureDate = endCaptureDate;
	}
	
	@XmlElement(name="status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
