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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import atos.knowledgelab.capture.util.DataChannelState;

@XmlRootElement(name="twitter")
@XmlAccessorType(XmlAccessType.NONE)
public class TwitterDataSource extends DataSource {
	
	@XmlTransient
	private String keywords;
	
	@XmlTransient
	private long lastTweetId;
	
	@XmlTransient
	private boolean fromLastTweetId;
	
	@XmlTransient
	private boolean chronologicalOrder;

	@XmlTransient
	private Long historicalLimit = 0L;

	@XmlTransient
	private Long totalTweetCount = 0L;

	@XmlTransient
	private Long tweetRate5m = 0L;
	
	@XmlTransient
	private Long tweetRate10m = 0L;

	@XmlTransient
	private Long tweetRate15m = 0L;

	@XmlTransient
	private DataChannelState state;
	
	@XmlTransient
	private List<Tweet> tweets = new ArrayList<Tweet>();

	@XmlElement(name="keywords")
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {		
		this.keywords = keywords;
	}
	
	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	@XmlElement(name="lastTweetId")
	public long getLastTweetId() {
		return lastTweetId;
	}

	public void setLastTweetId(long lastTweetId) {
		this.lastTweetId = lastTweetId;
	}

	@XmlElement(name="fromLastTweetId")
	public boolean getFromLastTweetId() {
		return fromLastTweetId;
	}

	public void setFromLastTweetId(boolean fromLastTweetId) {
		this.fromLastTweetId = fromLastTweetId;
	}
	
	@JsonIgnore
	@Override
	public List <? extends CaptureData> getData() {
		return tweets;
	}

	public void setChronologicalOrder(boolean chronologicalOrder) {
		this.chronologicalOrder = chronologicalOrder;
	}	
	
	@XmlElement(name="chronologicalOrder")
	public boolean getChronologicalOrder () {
		return this.chronologicalOrder;
	}
	
	@XmlElement(name="historicalLimit")
	public Long getHistoricalLimit() {
		return historicalLimit;
	}

	public void setHistoricalLimit(Long historicalLimit) {
		this.historicalLimit = historicalLimit;
	}

	@XmlElement(name="totalTweetCount")
	public Long getTotalTweetCount() {
		return totalTweetCount;
	}

	public void setTotalTweetCount(Long totalTweetCount) {
		this.totalTweetCount = totalTweetCount;
	}

	public Long getTweetRate5m() {
		return tweetRate5m;
	}

	public void setTweetRate5m(Long tweetRate5m) {
		this.tweetRate5m = tweetRate5m;
	}

	public Long getTweetRate10m() {
		return tweetRate10m;
	}

	public void setTweetRate10m(Long tweetRate10m) {
		this.tweetRate10m = tweetRate10m;
	}

	@XmlElement(name="state")
	public DataChannelState getState() {
		return state;
	}

	public void setState(DataChannelState state) {
		this.state = state;
	}

	public Long getTweetRate15m() {
		return tweetRate15m;
	}

	public void setTweetRate15m(Long tweetRate15m) {
		this.tweetRate15m = tweetRate15m;
	}
}
