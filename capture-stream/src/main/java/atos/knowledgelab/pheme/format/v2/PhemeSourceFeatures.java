/*******************************************************************************
 * Copyright (C) 2016  ATOS Spain S.A.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
package atos.knowledgelab.pheme.format.v2;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhemeSourceFeatures {

	@JsonProperty("anti_stigma")
	@XmlTransient	
	String antiStigma;
	
	@JsonProperty("advert")
	@XmlTransient	
	String advert;
	
	@JsonProperty("sentiment")
	@XmlTransient	
	String sentiment;
	
	@JsonProperty("event_cluster")
	@XmlTransient	
	List<Long> cluster = new ArrayList<Long>();
	
	
	public String getAntiStigma() {
		return antiStigma;
	}
	public void setAntiStigma(String antiStigma) {
		this.antiStigma = antiStigma;
	}
	public String getAdvert() {
		return advert;
	}
	public void setAdvert(String advert) {
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
