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

@XmlRootElement(name="reddit")
@XmlAccessorType(XmlAccessType.NONE)
public class RedditDataSource extends DataSource {
	
	@XmlTransient
	private String redditType;
	
	@XmlTransient
	private List<String> subreddits = new ArrayList<String>();
	
	@XmlTransient
	private String post;
	
	@XmlTransient
	private String keywords;
	
	@XmlTransient
	private String lastReddit;
	
	@XmlTransient
	private List<RedditPost> redditPosts = new ArrayList<RedditPost>();
	
	@XmlTransient
	private List<Tweet> tweets = new ArrayList<Tweet>();

	@XmlElement(name="redditType")
	public String getRedditType() {
		return redditType;
	}

	public void setRedditType(String redditType) {		
		this.redditType = redditType;
	}
	
	@XmlElement(name="subreddits")
	public List<String> getSubreddits() {
		return subreddits;
	}

	public void setSubreddits(List<String> list) {		
		this.subreddits = list;
	}
	
	@XmlElement(name="post")
	public String getPost() {
		return post;
	}

	public void setPost(String post) {		
		this.post = post;
	}
	
	@XmlElement(name="keywords")
	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {		
		this.keywords = keywords;
	}
	
	public List<RedditPost> getRedditPosts() {
		return redditPosts;
	}

	public void setRedditPosts(List<RedditPost> redditPosts) {
		this.redditPosts = redditPosts;
	}
		
	@JsonIgnore
	@Override
	public List <? extends CaptureData> getData() {
		//return redditPosts;
		return tweets;
	}

}
