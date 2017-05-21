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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonRootName;

@XmlRootElement(name="tweet")
@JsonRootName(value = "tweet")
public class Tweet extends CaptureData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Field("tweetID")
	@XmlTransient
	private String tweetID;
		
	
	@Field
	@XmlTransient
	private String text;
	
	@Field
	@XmlTransient
	private String lang;
	
	@Field
	@XmlTransient
	private String userID;
	
	@Field
	@XmlTransient
	private /* transient */ String source;
	
	@Field
	@XmlTransient
	private String userScreenName;
	
	@Field
	@XmlTransient
	private String userName;
	
	@Field
	@XmlTransient
	private String userDescription;
	
	@Field
	@XmlTransient
	private Date createdAt;
	
	@Field
	@XmlTransient
	private String inReplyToId;
	
	@Field
	@XmlTransient
	private String originalTweetId;
	
	@Field
	@XmlTransient
	private Integer retweetCount;
	
	@Field
	@XmlTransient
	private Integer favouriteCount;
	
	@Field
	@XmlTransient
	private String hashTags;
	
	@Field
	@XmlTransient
	private String sourceUrls;
	
	@Field
	@XmlTransient
	private String place;
	
	@Field
	@XmlTransient
	private Integer userFollowes;
	
	@Field
	@XmlTransient
	private Integer userFollowers;
	
	@Field
	@XmlTransient
	private String latitude;
	
	@Field
	@XmlTransient
	private String longitude;
	
	@Field("latLong")
	@XmlTransient
	private String latLong;
	
	@Field("json")
	@XmlTransient
	private String rawJson;
	
	@Field("sentiment_feature")
	@XmlTransient
	private Double sentiment;
	
	@Field("stress_feature")
	@XmlTransient
	private Double stress;
	
	@Field("dangerousness_feature")
	@XmlTransient
	private Double dangerousness;
	
	
	@Field("sentiment_raw")
	@XmlTransient
	private Double sentiment_raw;
	
	@XmlTransient	
	private List<String> annotation_geo = new ArrayList<String>();
	
	@XmlTransient	
	private List<String> annotation_political = new ArrayList<String>();
	
	
	@XmlTransient	
	private List<TwitterDataSource> dataSources = new ArrayList<TwitterDataSource>();
	
	@XmlElement(name="tweetID")
	public String getId() {
		return tweetID;
	}

	public void setId(String id) {
		this.tweetID = id;
	}

	@XmlElement(name="text")
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	@XmlElement(name="source")
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@XmlElement(name="userScreenName")
	public String getUserScreenName() {
		return userScreenName;
	}

	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}
	
//	@XmlElement(name="createdAt")
//	public String getCreatedAt() {
//		return createdAt;
//	}
//
//	public void setCreatedAt(String createdAt) {
//		this.createdAt = createdAt;
//	}

	@XmlElement(name="createdAt")
	public Date getCreatedAt() throws ParseException {
//		String utcFormat = "yyyy-MM-dd'T'HH:mm:ssXXX";
//		SimpleDateFormat utcf = new SimpleDateFormat(utcFormat, Locale.ENGLISH);
//		utcf.setTimeZone(TimeZone.getTimeZone("UTC"));
//		utcf.setLenient(true);
//		return utcf.parse (utcf.format (createdAt));
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@XmlElement(name="hashTags")
	public String getHashTags() {
		return hashTags;
	}

	public void setHashTags(String hashTags) {
		this.hashTags = hashTags;
	}

	@XmlElement(name="sourceUrls")
	public String getSourceUrls() {
		return sourceUrls;
	}

	public void setSourceUrls(String sourceUrls) {
		this.sourceUrls = sourceUrls;
	}

	@XmlElement(name="userName")
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement(name="userDescription")
	public String getUserDescription() {
		return userDescription;
	}

	public void setUserDescription(String userDescription) {
		this.userDescription = userDescription;
	}

	@XmlElement(name="userFollowes")
	public Integer getUserFollowes() {
		return userFollowes;
	}
	
	public void setUserFollowes(Integer uFollowes) {
		this.userFollowes = uFollowes;
	}

	@XmlElement(name="userFollowers")
	public Integer getUserFollowers() {
		return userFollowers;
	}
	
	public void setUserFollowers(Integer uFollowers) {
		this.userFollowers = uFollowers;
	}

	@XmlElement(name="retweetCount")
	public Integer getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Integer retweetCount) {
		this.retweetCount = retweetCount;
	}

	@XmlElement(name="favouriteCount")
	public Integer getFavouriteCount() {
		return favouriteCount;
	}

	public void setFavouriteCount(Integer favoriteCount) {
		this.favouriteCount = favoriteCount;
	}

	@XmlElement(name="inReplyToId")
	public String getInReplyToId() {
		return inReplyToId;
	}

	public void setInReplyToId(String inReplyToId) {
		this.inReplyToId = inReplyToId;
	}

	@XmlElement(name="originalTweetId")
	public String getOriginalTweetId() {
		return originalTweetId;
	}

	public void setOriginalTweetId(String originalTweetId) {
		this.originalTweetId = originalTweetId;
	}

	@XmlElement(name="place")
	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	@XmlElement(name="userID")
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	@XmlElement(name="latitude")
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	@XmlElement(name="longitude")
	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@XmlElement(name="rawJson")
	public String getRawJson() {
		return rawJson;
	}

	public void setRawJson(String rawJson) {
		this.rawJson = rawJson;
	}

	//New schema: add new fiedl latLong	
	@XmlElement(name="latLong")
	public String getLatLong() {
		return latLong;
	}

	public void setLatLong(String latLong) {
		this.latLong = latLong;
	}
	
	@XmlElementWrapper(name="dataSources")
	@XmlElements({
	     @XmlElement(name="dsId", type=DataSource.class)	     
	})
	public List<TwitterDataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<TwitterDataSource> dataSources) {
		this.dataSources = dataSources;
	}

	@XmlElement(name="sentiment")
	public Double getSentiment() {
		return sentiment;
	}

	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}

	@XmlElement(name="stress")
	public Double getStress() {
		return stress;
	}

	public void setStress(Double stress) {
		this.stress = stress;
	}

	@XmlElement(name="dangerousness")
	public Double getDangerousness() {
		return dangerousness;
	}

	public void setDangerousness(Double dangerousness) {
		this.dangerousness = dangerousness;
	}	
	
	
	@XmlElement(name="sentimentRaw")
	public Double getSentimentRaw() {
		return sentiment_raw;
	}

	public void setSentimentRaw(Double sentiment_raw) {
		this.sentiment_raw = sentiment_raw;
	}
	
	@XmlElement(name="annotationGeo")
	public List<String> getAnnotationGeo() {
		return annotation_geo;
	}

	public void setAnnotationGeo(List<String> annotation_geo) {
		this.annotation_geo = annotation_geo;
	}

	@XmlElement(name="annotationPolitical")
	public List<String> getAnnotationPolitical() {
		return annotation_political;
	}

	public void setAnnotationPolitical(List<String> annotation_political) {
		this.annotation_political = annotation_political;
	}

	@XmlElement(name="lang")
	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	
}
