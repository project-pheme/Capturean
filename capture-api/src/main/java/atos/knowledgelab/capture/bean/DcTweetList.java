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
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonRootName;


/**
 * Contains all the data known about the specific event it represents. 
 * The annotations are required to automatically parse the object from XML by using JAXB.
 *
 */
@XmlRootElement(name="tweet_list")
@JsonRootName(value = "tweet_list")
public class DcTweetList {
	
	public DcTweetList(){
		
	}
	
	public DcTweetList(List<Tweet> tweetList) {
		this.tweets = tweetList;
	}

	@XmlTransient
	private int page = 0;
	
	@XmlTransient
	private int pageSize = 0;
	
	@XmlTransient
	private long totalTweets = 0;
	
	@XmlTransient
	private String lastTweetId = "";
	
	@XmlTransient
	private String fromId = "";
	
	@XmlTransient
	private List<Tweet> tweets;
	
	@XmlElement(name="tweet")
	public List<Tweet> getTweets() {
		return tweets;
	}

	public void setTweets(List<Tweet> tweets) {
		this.tweets = tweets;
	}
	
	public void addMetadata (Tweet tweet) {
		if (tweets==null) {
			tweets = new ArrayList<Tweet>();
		} 
		tweets.add(tweet);
	}

	@XmlAttribute
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@XmlAttribute
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@XmlAttribute
	public long getTotalTweets() {
		return totalTweets;
	}

	public void setTotalTweets(long l) {
		this.totalTweets = l;
	}
	
	@XmlAttribute
	public String getLastTweetId() {
		return lastTweetId;
	}

	public void setLastTweetId(String lastTweetId) {
		this.lastTweetId = lastTweetId;
	}

	@XmlAttribute
	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

}
