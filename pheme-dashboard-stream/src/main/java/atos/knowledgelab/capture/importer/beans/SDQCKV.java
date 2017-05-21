package atos.knowledgelab.capture.importer.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SDQCKV {
	
	@JsonProperty("sdqc_type")
	String label;
	
	@JsonProperty("sdqc_confidence")
	double probability;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	
	
}
