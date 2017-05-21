package atos.knowledgelab.capture.monitor.live.beans;

import java.util.HashMap;
import java.util.Map;

public class Aggregate {

	Map<String, Long> all = new HashMap<String, Long>();
	Map<String, Long> filter = new HashMap<String, Long>();
	
	public Map<String, Long> getAll() {
		return all;
	}
	public void setAll(Map<String, Long> all) {
		this.all = all;
	}
	public Map<String, Long> getFilter() {
		return filter;
	}
	public void setFilter(Map<String, Long> filter) {
		this.filter = filter;
	}

	
}
