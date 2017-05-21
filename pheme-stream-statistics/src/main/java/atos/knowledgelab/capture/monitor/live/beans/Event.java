package atos.knowledgelab.capture.monitor.live.beans;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Event {
	
	@JsonProperty("message")
	Object message;
	
	@JsonProperty("input_label")
	String inputLabel;
	
	@JsonProperty("@timestamp")	
	String timestamp;

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		this.message = message;
	}

	public String getInputLabel() {
		return inputLabel;
	}

	public void setInputLabel(String inputLabel) {
		this.inputLabel = inputLabel;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
	
}
