package atos.knowledgelab.capture.importer.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardDocumentFeatures {

	@JsonProperty("anti_stigma")
	@XmlTransient	
	Integer antiStigma;
	
	@JsonProperty("advert")
	@XmlTransient	
	Integer advert;
	
	@JsonProperty("sentiment")
	@XmlTransient	
	String sentiment;
	
	@JsonProperty("event_cluster")
	@XmlTransient	
	List<Long> cluster = new ArrayList<Long>();
	
	@JsonProperty("pheme_sdqc")
	@XmlTransient
	@JsonUnwrapped
	SDQCKV sdqc = new SDQCKV();

	@JsonProperty("veracity")
	@XmlTransient	
	@JsonUnwrapped
	VeracityKV veracity = new VeracityKV();

	@JsonProperty("suicide_relevant")
	@XmlTransient	
	Integer suicideRelevant;

	
	public Integer getSuicideRelevant() {
		return suicideRelevant;
	}
	public void setSuicideRelevant(Integer suicideRelevant) {
		this.suicideRelevant = suicideRelevant;
	}
	public VeracityKV getVeracity() {
		return veracity;
	}
	public void setVeracity(VeracityKV veracity) {
		this.veracity = veracity;
	}
	public SDQCKV getSdqc() {
		return sdqc;
	}
	public void setSdqc(SDQCKV sdqc) {
		this.sdqc = sdqc;
	}
	public Integer getAntiStigma() {
		return antiStigma;
	}
	public void setAntiStigma(Integer antiStigma) {
		this.antiStigma = antiStigma;
	}
	public Integer getAdvert() {
		return advert;
	}
	public void setAdvert(Integer advert) {
		this.advert = advert;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	public List<Long> getCluster() {
		return cluster;
	}
	public void setCluster(List<Long> cluster) {
		this.cluster = cluster;
	}
	
	
	
	
}
