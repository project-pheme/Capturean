package atos.knowledgelab.capture.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PipelineNode {

	@JsonProperty("topic_name")
	String topicName;
	
	@JsonProperty("node_name")
	String nodeName;
	
	@JsonProperty("node_description")
	String nodeDescription;
	
	@JsonProperty("monitor_enabled")
	String monitorEnabled;
	
	@JsonProperty("next_node")
	String nextNodeName;

	//do not leak this property
	@JsonIgnoreProperties
	String notificationEmail;
	
	public String getNotificationEmail() {
		return notificationEmail;
	}
	public void setNotificationEmail(String notificationEmail) {
		this.notificationEmail = notificationEmail;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getNodeDescription() {
		return nodeDescription;
	}
	public void setNodeDescription(String nodeDescription) {
		this.nodeDescription = nodeDescription;
	}
	public String getMonitorEnabled() {
		return monitorEnabled;
	}
	public void setMonitorEnabled(String monitor) {
		this.monitorEnabled = monitor;
	}
	public String getNextNodeName() {
		return nextNodeName;
	}
	public void setNextNodeName(String nextNodeName) {
		this.nextNodeName = nextNodeName;
	}
	
}
