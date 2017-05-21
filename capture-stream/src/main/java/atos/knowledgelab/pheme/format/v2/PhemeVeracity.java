package atos.knowledgelab.pheme.format.v2;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class PhemeVeracity {
	
	String label;
	double confidence;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double probability) {
		this.confidence = probability;
	}
}
	
	
	
