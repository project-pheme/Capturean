package atos.knowledgelab.capture.importer.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardDocumentMetadata {
	
	@JsonProperty("published_date")
	String publishedDate;
	
	@JsonProperty("user_id")
	String userID;

	@JsonProperty("user_screen_name")
	String userScreenName;
	
	@JsonProperty("tweet_id")
	String tweetID;
	
	@JsonProperty("twitter_lang_id")
	String twitterLanguageID;

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishedDate) {
		this.publishedDate = publishedDate;
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

	public String getTwitterLanguageID() {
		return twitterLanguageID;
	}

	public void setTwitterLanguageID(String twitterLanguageID) {
		this.twitterLanguageID = twitterLanguageID;
	}
	
	
	
}
