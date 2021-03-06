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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.solr.client.solrj.beans.Field;

public class InfluentialAnalysis extends PeriodicAnalysisResult{

	@XmlTransient
	private List<String> tweets = new ArrayList<String>();
	
	@XmlTransient
	private Map<String,Integer> influencers = new HashMap<String,Integer>();
	
	@XmlTransient
	private Map<String,Integer> mentions = new HashMap<String,Integer>();
	
	@XmlTransient
	private Map<String,Integer> retweets = new HashMap<String,Integer>();
	
	@XmlTransient
	private Map<String,Integer> replies = new HashMap<String,Integer>();
	
	@XmlTransient
	private TwitterUser user;

	public List<String> getTweets() {
		return tweets;
	}

	public void setTweets(List<String> tweets) {
		this.tweets = tweets;
	}
	
	public Map<String,Integer> getInfluencers() {
		return influencers;
	}

	public void setInfluencers(Map<String,Integer> influencers) {
		this.influencers = influencers;
	}
	
	public Map<String,Integer> getMentions() {
		return mentions;
	}

	public void setMentions(Map<String,Integer> mentions) {
		this.mentions = mentions;
	}
	
	public Map<String,Integer> getRetweets() {
		return retweets;
	}

	public void setRetweets(Map<String,Integer> retweets) {
		this.retweets = retweets;
	}
	
	public Map<String,Integer> getReplies() {
		return replies;
	}

	public void setReplies(Map<String,Integer> replies) {
		this.replies = replies;
	}
	
	public TwitterUser getUser() {
		return user;
	}

	public void setUser(TwitterUser user) {
		this.user = user;
	}
}
