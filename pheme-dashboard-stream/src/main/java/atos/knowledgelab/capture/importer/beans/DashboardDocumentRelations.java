package atos.knowledgelab.capture.importer.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardDocumentRelations {

	@JsonProperty("reply_to")
	@XmlTransient
	//List<String> replyTo;
	String replyTo;
	
	@JsonProperty("parent_doc")
	@XmlTransient
	String parentDoc;
	
	@JsonProperty("retweeted_from")
	@XmlTransient	
	String retweetedFrom;

//	public List<String> getReplyTo() {
//		return replyTo;
//	}
//
//	public void setReplyTo(List<String> replyTo) {
//		this.replyTo = replyTo;
//	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
		
	public String getParentDoc() {
		return parentDoc;
	}

	public void setParentDoc(String parentDoc) {
		this.parentDoc = parentDoc;
	}

	public String getRetweetedFrom() {
		return retweetedFrom;
	}

	public void setRetweetedFrom(String retweetedFrom) {
		this.retweetedFrom = retweetedFrom;
	}
	
	
}
