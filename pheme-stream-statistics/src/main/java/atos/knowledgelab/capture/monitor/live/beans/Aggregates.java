package atos.knowledgelab.capture.monitor.live.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class Aggregates {

	
	String name = "aggregated";
	Aggregate aggregate;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Aggregate getAggregate() {
		return aggregate;
	}
	public void setAggregate(Aggregate aggregate) {
		this.aggregate = aggregate;
	}
	
}
