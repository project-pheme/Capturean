/*******************************************************************************
 * 	Copyright (C) 2017  ATOS Spain S.A.
 *
 * 	This file is part of the Capturean software.
 *
 * 	This program is dual licensed under the terms of GNU Affero General
 * 	Public License and proprietary for commercial usage.
 *
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as
 * 	published by the Free Software Foundation, either version 3 of the
 * 	License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 	You can be released from the requirements of the license by purchasing
 * 	a commercial license or negotiating an agreement with Atos Spain S.A.
 * 	Buying such a license is mandatory as soon as you develop commercial
 * 	activities involving the Capturean software without disclosing the source 
 * 	code of your own applications. 
 *
 * 	
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.bean;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.AnalysisType;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;

@XmlRootElement(name="aggregator")
public class Aggregator {

	public enum Status {waiting, running, finished, failed}
	
	@XmlTransient
	private String aggID;
	
	@XmlTransient
	private String initDate;
	
	@XmlTransient
	private String endDate;
	
	@XmlTransient
	private Periodicity periodicity;
	
	@XmlTransient
	private String description;
	
	@XmlTransient
	private String datachannels;
	
	@XmlTransient
	private List<AnalysisType> analysisType = new ArrayList<AnalysisType>();
	
	@XmlTransient
	private String periodically;
	
	@XmlTransient
	private Status status;
	
	@XmlTransient
	private String type;
	
	public Aggregator(){}
	
	public Aggregator(String aggID, String initDate, String endDate, Periodicity periodicity, String description, String datachannels, List<AnalysisType> analysisType, String periodically){
		this.aggID = aggID;
		this.initDate = initDate;
		this.endDate = endDate;
		this.periodicity = periodicity;
		this.description = description;
		this.datachannels = datachannels;
		this.analysisType = analysisType;
		this.periodically = periodically;
		this.type = type;
	}
	
	
	public String getAggID() {
		return aggID;
	}
	public void setAggID(String aggID) {
		this.aggID = aggID;
	}
	
	public String getInitDate() {
		return initDate;
	}
	public void setInitDate(String initDate) {
		this.initDate = initDate;
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	
	public Periodicity getPeriodicity() {
		return periodicity;
	}
	public void setPeriodicity(Periodicity periodicity) {
		this.periodicity = periodicity;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getDatachannels() {
		return datachannels;
	}
	public void setDatachannels(String datachannel) {
		this.datachannels = datachannel;
	}
	
	public List<AnalysisType> getAnalysisType() {
		return analysisType;
	}
	public void setAnalysisType(List<AnalysisType> analysisType) {
		this.analysisType = analysisType;
	}
	
	public void addAnalysisType(AnalysisType analysisType) {
		this.analysisType.add(analysisType);
	}
	
	public String getPeriodically() {
		return periodically;
	}
	public void setPeriodically(String periodically) {
		this.periodically = periodically;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public static Status convertStatus (String sStatus){
		// To do
		if (Status.waiting.toString().equals(sStatus))
			return Status.waiting;
		
		if (Status.running.toString().equals(sStatus))
			return Status.running;
		
		if (Status.finished.toString().equals(sStatus))
			return Status.finished;
		
		if (Status.failed.toString().equals(sStatus))
			return Status.failed;
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
