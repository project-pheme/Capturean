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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atos.knowledgelab.capture.bean.Aggregator.Status;

@XmlRootElement(name="brand")
public class Brand {
	
	@XmlTransient
	private String brandID;
	
	@XmlTransient
	private String twitterID;
	
	@XmlTransient
	private String screenName;
	
	@XmlTransient
	private String name;
	
	@XmlTransient
	private List<String> alternativeNames = new ArrayList<String>();
	
	@XmlTransient
	private List<String> dcIDs = new ArrayList<String>();
	
	@XmlTransient
	private List<String> competitors = new ArrayList<String>();
	
	@XmlTransient
	private Status status;
	
	public enum Status {waiting, running, finished, failed}
	
	public Brand(){}
	
	public Brand(String brandID, String twitterID, String screenName, String name, List<String> alternativeNames, List<String> dcIDs, List<String> competitors){
		this.brandID = brandID;
		this.twitterID = twitterID;
		this.screenName = screenName;
		this.name = name;
		this.alternativeNames = alternativeNames;
		this.dcIDs = dcIDs;
		this.competitors = competitors;
	}
	
	
	public String getBrandID() {
		return brandID;
	}
	public void setBrandID(String brandID) {
		this.brandID = brandID;
	}
	
	public String getTwitterID() {
		return twitterID;
	}
	public void setTwitterID(String twitterID) {
		this.twitterID = twitterID;
	}
	
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public void setAlternativeNames(List<String> alternativeNames) {
		this.alternativeNames = alternativeNames;
	}
	
	public List<String> getAlternativeNames() {
		return alternativeNames;
	}
	
	public void addAlternativeNames(String alternativeNames) {
		this.alternativeNames.add(alternativeNames);
	}
	
	public void setDcIDs(List<String> dcIDs) {
		this.dcIDs = dcIDs;
	}
	
	public List<String> getDcIDs() {
		return dcIDs;
	}
	
	public void addDcIDs(String dcIDs) {
		this.dcIDs.add(dcIDs);
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
	public List<String> getCompetitors() {
		return competitors;
	}
	
	public void setCompetitors(List<String> competitors) {
		this.competitors = competitors;
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

}
