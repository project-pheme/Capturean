package atos.knowledgelab.capture.counter;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

public class TickDBRow {
	
	public final static String ID_FIELD_NAME = "timestamp";
	public final static String TIMESTAMP_FIELD_NAME = "timestamp";
	public final static String LEVEL_FIELD_NAME = "level";
	public final static String TOPICNAME_FIELD_NAME = "topic_name";
	public final static String COUNT_FIELD_NAME = "count";

	@DatabaseField(generatedId = true, columnName = ID_FIELD_NAME)
	int id;

	@DatabaseField(columnName = TIMESTAMP_FIELD_NAME)
	Date timestamp;
	
	@DatabaseField(columnName = LEVEL_FIELD_NAME)
	String level;
	
	@DatabaseField(columnName = TOPICNAME_FIELD_NAME)
	String topicName;
	
	@DatabaseField(columnName = COUNT_FIELD_NAME)
	int count;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	
}
