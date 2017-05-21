package atos.knowledgelab.capture.importer.beans;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Veracity {
	
	// -1 ... 1
	double score;	
	double probability;

	public double getScore() {
		return score;
	}

	public void setScore(double label) {
		this.score = label;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	
	
}
