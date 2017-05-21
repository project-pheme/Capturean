package atos.knowledgelab.capture.counter;

public class Tick {

	Long timestamp;
	Long count;
	
	public Tick(Long timestampLong, Long countLong) {
		this.timestamp = timestampLong;
		this.count = countLong;
	}
	
	public Tick() {
		
	}
	
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public Long getCount() {
		return count;
	}
	public void setCount(Long count) {
		this.count = count;
	}
	
	
	
}
