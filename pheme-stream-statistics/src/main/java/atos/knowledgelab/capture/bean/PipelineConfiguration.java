package atos.knowledgelab.capture.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class PipelineConfiguration {

	String appName;
	String zooKeeperEndpoint;
	String groupId;
	Integer threadNumber;
	List<PipelineNode> nodeChain;
	
	@JsonIgnoreProperties
	String userName;
	
	@JsonIgnoreProperties
	String password;

	@JsonIgnoreProperties
	String fromAddress;

	public Integer getThreadNumber() {
		return threadNumber;
	}
	public void setThreadNumber(Integer threadNumber) {
		this.threadNumber = threadNumber;
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<PipelineNode> getNodeChain() {
		return nodeChain;
	}
	public void setNodeChain(List<PipelineNode> nodeChain) {
		this.nodeChain = nodeChain;
	}
	public String getZooKeeperEndpoint() {
		return zooKeeperEndpoint;
	}
	public void setZooKeeperEndpoint(String zooKeeperEndpoint) {
		this.zooKeeperEndpoint = zooKeeperEndpoint;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}

	
}
