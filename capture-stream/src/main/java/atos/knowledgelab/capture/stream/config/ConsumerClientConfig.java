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
package atos.knowledgelab.capture.stream.config;

public class ConsumerClientConfig {

	//set up some default values
	
	String zooKeeperEndPoint = "localhost:2181";
	String groupId = "group1";
	String topicName = "test";
	Integer threadNum = 4;
	String streamDeserializer = "atos.knowledgelab.capture.stream.serializers.impl.StreamItemDeserialize";
	
	
	public Integer getThreadNum() {
		return threadNum;
	}
	public void setThreadNum(Integer threadNum) {
		this.threadNum = threadNum;
	}
	public String getZooKeeperEndPoint() {
		return zooKeeperEndPoint;
	}
	public void setZooKeeperEndPoint(String zooKeeperEndPoint) {
		this.zooKeeperEndPoint = zooKeeperEndPoint;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public String getStreamDeserializer() {
		return streamDeserializer;
	}
	public void setStreamDeserializer(String streamDeserializer) {
		this.streamDeserializer = streamDeserializer;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Zookeeper: " + this.getZooKeeperEndPoint() + "\n");
		sb.append("Group Id: " + this.getGroupId() + "\n");
		sb.append("Topic Name: " + this.getTopicName() + "\n");
		sb.append("Number of parallel threads (consumers): " + this.getThreadNum() + "\n");
		sb.append("Stream deserializer: " + this.getStreamDeserializer() + "\n");
		
		return sb.toString();
	}
	
}
