package atos.knowledgelab.capture.bean;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TopicDetailBean {

	@JsonProperty("last_counts")
	HashMap<String, Long> lastCounts;
	
	@JsonProperty("last_messages")
	HashMap<String, String> lastMessages;
	
	public HashMap<String, Long> getLastCounts() {
		return lastCounts;
	}
	public void setLastCounts(HashMap<String, Long> lastCounts) {
		this.lastCounts = lastCounts;
	}
	public HashMap<String, String> getLastMessages() {
		return lastMessages;
	}
	public void setLastMessages(HashMap<String, String> lastMessages) {
		this.lastMessages = lastMessages;
	}
	
	
}
