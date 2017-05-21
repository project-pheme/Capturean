package atos.knowledgelab.capture.importer.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputTweet {
	
	
	@JsonProperty("userID")
	String userID;

	@JsonProperty("userScreenName")
	String userScreenName;
	
	@JsonProperty("tweetID")
	String tweetID ;
	
	@JsonProperty("createdAt")
	String createdAt;
	
	@JsonProperty("text")
	String text;
	
	@JsonProperty("type")
	String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getUserScreenName() {
		return userScreenName;
	}

	public void setUserScreenName(String userScreenName) {
		this.userScreenName = userScreenName;
	}

	public String getTweetID() {
		return tweetID;
	}

	public void setTweetID(String tweetID) {
		this.tweetID = tweetID;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
	
	
}
