package atos.knowledgelab.capture.counter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TweetCounter {

	ConcurrentLinkedQueue<Tick> seconds = new ConcurrentLinkedQueue<Tick>();
	ConcurrentLinkedQueue<Tick> minutes = new ConcurrentLinkedQueue<Tick>();
	ConcurrentLinkedQueue<Tick> hours = new ConcurrentLinkedQueue<Tick>();

	List<Object> messages = new ArrayList<Object>();
	
	Long lastSecondTimestamp = 0L;
	Long lastMinuteTimestamp = 0L;
	Long lastHourTimestamp = 0L;

	Long lastSecondCount = 0L;
	Long lastMinuteCount = 0L;
	Long lastHourCount = 0L;
	
	String lastMessage = "";

	public TweetCounter() {

	}

//	private static class SingletonHolder {
//		private static final TweetCounter INSTANCE = new TweetCounter();
//	}
//
//	public static TweetCounter getInstance() {
//		return SingletonHolder.INSTANCE;
//	}

	public ConcurrentLinkedQueue<Tick> getSeconds() {
		return seconds;
	}

	public void setSeconds(ConcurrentLinkedQueue<Tick> seconds) {
		this.seconds = seconds;
	}

	public ConcurrentLinkedQueue<Tick> getMinutes() {
		return minutes;
	}

	public void setMinutes(ConcurrentLinkedQueue<Tick> minutes) {
		this.minutes = minutes;
	}

	public ConcurrentLinkedQueue<Tick> getHours() {
		return hours;
	}

	public void setHours(ConcurrentLinkedQueue<Tick> hours) {
		this.hours = hours;
	}

	public Long getLastSecondTimestamp() {
		return lastSecondTimestamp;
	}

	public void setLastSecondTimestamp(Long lastSecondTimestamp) {
		this.lastSecondTimestamp = lastSecondTimestamp;
	}

	public Long getLastMinuteTimestamp() {
		return lastMinuteTimestamp;
	}

	public void setLastMinuteTimestamp(Long lastMinuteTimestamp) {
		this.lastMinuteTimestamp = lastMinuteTimestamp;
	}

	public Long getLastHourTimestamp() {
		return lastHourTimestamp;
	}

	public void setLastHourTimestamp(Long lastHourTimestamp) {
		this.lastHourTimestamp = lastHourTimestamp;
	}

	public Long getLastSecondCount() {
		return lastSecondCount;
	}

	public void setLastSecondCount(Long lastSecondCount) {
		this.lastSecondCount = lastSecondCount;
	}

	public Long getLastMinuteCount() {
		return lastMinuteCount;
	}

	public void setLastMinuteCount(Long lastMinuteCount) {
		this.lastMinuteCount = lastMinuteCount;
	}

	public Long getLastHourCount() {
		return lastHourCount;
	}

	public void setLastHourCount(Long lastHourCount) {
		this.lastHourCount = lastHourCount;
	}
	
	public synchronized void incrementLastSecondCount() {
		this.lastSecondCount++;
	}
	public synchronized void incrementLastMinuteCount() {
		this.lastMinuteCount++;
	}
	public synchronized void incrementLastHourCount() {
		this.lastHourCount++;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public List<Object> getMessages() {
		return messages;
	}

	public void setMessages(List<Object> messages) {
		this.messages = messages;
	}
}
