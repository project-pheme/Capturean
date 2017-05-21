package atos.knowledgelab.capture.importer.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.ARRAY)
public class VeracityKV {
	
	// -1 ... 1

	@JsonProperty("veracity_score")
	int score;	

	@JsonProperty("veracity_confidence")
	double confidence;

	public int getScore() {
		return score;
	}

	public void setScore(int label) {
		this.score = label;
	}

	public double getConfidence() {
		return confidence;
	}

	public void setConfidence(double probability) {
		this.confidence = probability;
	}
	
	
	
}
