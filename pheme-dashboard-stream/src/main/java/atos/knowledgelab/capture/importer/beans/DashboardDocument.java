package atos.knowledgelab.capture.importer.beans;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DashboardDocument {

	
	@JsonProperty("user_id")
	String userID;

	@JsonProperty("user_screen_name")
	String userScreenName;
	
	@JsonProperty("tweet_id")
	String tweetID;
	
	@JsonProperty("created_at")
	String createdAt;
	
	@JsonProperty("content")
	String content;
	
	@JsonProperty("content_type")
	String contentType;
	
	@JsonProperty("repository_id")
	String repositoryId;
	
	@JsonProperty("uri")
	String uri;
	
	@JsonProperty("type")
	String type;
	
	@JsonProperty("title")
	String title;
	
	@JsonProperty("language_id")
	String languageID;
	
	@JsonProperty("features")
	@XmlTransient
	private DashboardDocumentFeatures features;

	@JsonProperty("relations")
	@XmlTransient
	private DashboardDocumentRelations relations;
	
	public String getLanguageID() {
		return languageID;
	}

	public void setLanguageID(String languageID) {
		this.languageID = languageID;
	}

	public DashboardDocumentMetadata getMetadata() {
		return metadata;
	}

	public void setMetadata(DashboardDocumentMetadata metadata) {
		this.metadata = metadata;
	}

	@JsonProperty("meta_data")
	DashboardDocumentMetadata metadata;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getRepositoryId() {
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId) {
		this.repositoryId = repositoryId;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DashboardDocumentRelations getRelations() {
		return relations;
	}

	public void setRelations(DashboardDocumentRelations relations) {
		this.relations = relations;
	}

	public DashboardDocumentFeatures getFeatures() {
		return features;
	}

	public void setFeatures(DashboardDocumentFeatures features) {
		this.features = features;
	}
	
}
