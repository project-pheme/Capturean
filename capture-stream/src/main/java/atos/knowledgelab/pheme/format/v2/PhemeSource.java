/*******************************************************************************
 * Copyright (C) 2016  ATOS Spain S.A.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
package atos.knowledgelab.pheme.format.v2;

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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonRootName(value="pheme_document")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhemeSource implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("id_str")
	private String tweetID;
	
	@JsonProperty("lang")
	@XmlTransient
	private String lang;

	@JsonProperty("lang_id")
	@JsonInclude(Include.NON_NULL)
	@XmlTransient
	private List<Object> langId; // = new ArrayList<Object>();

	@Field
	@XmlTransient
	private String text;
	
	@JsonProperty("source_type")
	@XmlTransient
	private String sourceType;

	@JsonProperty("pheme_sdqc")
	@JsonInclude(Include.NON_NULL)
	@XmlTransient	
	SDQC sdqc; // = new SDQC();
	
	@Field
	@XmlTransient
	@JsonProperty("user_id")
	private String userID;
	
	@Field
	@XmlTransient
	private /* transient */ String source;
	
	@Field
	@XmlTransient
	@JsonProperty("user_screen_name")
	private String userScreenName;
	
	@Field
	@XmlTransient
	private String userName;
	
	@Field
	@XmlTransient
	private String userDescription;
	
	@Field
	@XmlTransient
	@JsonProperty("created_at")
	private String createdAt;
	
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
	@JsonProperty("event_cluster")
	private String eventCluster;

	@Field
	@XmlTransient
	private Integer favouriteCount;
	
	@Field
	@XmlTransient
	private String hashTags;
	
	@Field
	@XmlTransient
	@JsonProperty("source_url")
	private String sourceUrl;
	
//	@Field
//	@XmlTransient
//	private Object place;
	
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
	
	@JsonRawValue
	@JsonProperty("raw_json")
	private Object rawJson;
	
	@Field("sentiment_feature")
	@XmlTransient
	private Double sentiment;
	
	@JsonProperty("dc_id")
	@XmlTransient
	private String dcID;

	@JsonProperty("features")
	@XmlTransient
	private StigmaAdvertFeatures saFeatures;

	@JsonProperty("relations")
	@XmlTransient
	private PhemeSourceRelations relations;
	
	@JsonProperty("pheme_rumour_confidence")
	@XmlTransient
	private String phemeRumourConfidence;

	@JsonProperty("pheme_veracity")
	@JsonInclude(Include.NON_NULL)
	@XmlTransient
	private PhemeVeracity phemeVeracity;

	@JsonProperty("user")
	@XmlTransient
	private PhemeSourceUser user;
	
	
	public PhemeVeracity getPhemeVeracity() {
		return phemeVeracity;
	}

	public void setPhemeVeracity(PhemeVeracity phemeVeracity) {
		this.phemeVeracity = phemeVeracity;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
	
	public List<Object> getLangId() {
		return langId;
	}

	public void setLangId(List<Object> lang_id) {
		this.langId = lang_id;
	}
	
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	

	public String getEventCluster() {
		return eventCluster;
	}

	public void setEventCluster(String eventCluster) {
		this.eventCluster = eventCluster;
	}
	
	public SDQC getSdqc() {
		return sdqc;
	}
	public void setSdqc(SDQC sdqc) {
		this.sdqc = sdqc;
	}

	
	@JsonRawValue
	@Field("id_str")
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
	@JsonProperty("user_screen_name")
	public String getUserScreenName() {
		return userScreenName;
	}

	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}
	
	@XmlElement(name="createdAt")
	@JsonProperty("created_at")
	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	@XmlElement(name="hashTags")
	public String getHashTags() {
		return hashTags;
	}

	public void setHashTags(String hashTags) {
		this.hashTags = hashTags;
	}

	@XmlElement(name="source_url")
	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrls) {
		this.sourceUrl = sourceUrls;
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

//	@XmlElement(name="place")
//	public Object getPlace() {
//		return place;
//	}
//
//	public void setPlace(Object place) {
//		this.place = place;
//	}

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

	@JsonRawValue
	@Field("raw_json")
	public Object getRawJson() {
		return rawJson;
	}

	public void setRawJson(Object rawJson) {
		this.rawJson = rawJson;
	}

	//New schema: add new field latLong	
	@XmlElement(name="latLong")
	public String getLatLong() {
		return latLong;
	}

	public void setLatLong(String latLong) {
		this.latLong = latLong;
	}

	@XmlElement(name="sentiment")
	public Double getSentiment() {
		return sentiment;
	}

	public void setSentiment(Double sentiment) {
		this.sentiment = sentiment;
	}

	@XmlElement(name="dcID")
	public String getDcID() {
		return dcID;
	}

	public void setDcID(String dcID) {
		this.dcID = dcID;
	}

	public StigmaAdvertFeatures getSAFeatures() {
		return saFeatures;
	}

	public void setSAFeatures(StigmaAdvertFeatures features) {
		this.saFeatures = features;
	}

	public PhemeSourceRelations getRelations() {
		return relations;
	}

	public void setRelations(PhemeSourceRelations relations) {
		this.relations = relations;
	}

	public String getPhemeRumourConfidence() {
		return phemeRumourConfidence;
	}

	public void setPhemeRumourConfidence(String phemeRumourConfidence) {
		this.phemeRumourConfidence = phemeRumourConfidence;
	}

	public PhemeSourceUser getUser() {
		return user;
	}

	public void setUser(PhemeSourceUser user) {
		this.user = user;
	}

	

	
	
}
