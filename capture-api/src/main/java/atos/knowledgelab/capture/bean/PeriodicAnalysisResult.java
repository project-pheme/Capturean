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

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

public class PeriodicAnalysisResult {
	
	public enum Periodicity {seconds15, minutes1, minutes5, minutes15, minutes30, hourly, daily}
	
	public enum AnalysisType {volume, sentiment_volume_positive, sentiment_volume_negative, sentiment_volume_neutro, tagcloud}
	
	@Field
	private String analysisId;
	
	@Field
	private Date aDate;
	
	@Field
	private Periodicity periodicity;
				
	@Field
	private AnalysisType analysisType;
	
	@Field
	private DataChannel dataChannel;
	
	@Field
	private DataPool dataPool;

	public Date getaDate() {
		return aDate;
	}

	public void setaDate(Date aDate) {
		this.aDate = aDate;
	}

	public Periodicity getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(Periodicity periodicity) {
		this.periodicity = periodicity;
	}

	public AnalysisType getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(AnalysisType analysisType) {
		this.analysisType = analysisType;
	}

	public DataChannel getDataChannel() {
		return dataChannel;
	}

	public void setDataChannel(DataChannel dataChannel) {
		this.dataChannel = dataChannel;
	}

	public DataPool getDataPool() {
		return dataPool;
	}

	public void setDataPool(DataPool dataPool) {
		this.dataPool = dataPool;
	}

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}	
	
	public static Periodicity convertPeriodicity (String sPeriodicity){
		
		if (Periodicity.daily.toString().equals(sPeriodicity))
			return Periodicity.daily;
		
		if (Periodicity.hourly.toString().equals(sPeriodicity))
			return Periodicity.hourly;
		
		if (Periodicity.minutes30.toString().equals(sPeriodicity))
			return Periodicity.minutes30;
		
		if (Periodicity.minutes15.toString().equals(sPeriodicity))
			return Periodicity.minutes15;
		
		if (Periodicity.minutes5.toString().equals(sPeriodicity))
			return Periodicity.minutes5;
		
		if (Periodicity.minutes1.toString().equals(sPeriodicity))
			return Periodicity.minutes1;
		
		if (Periodicity.seconds15.toString().equals(sPeriodicity))
			return Periodicity.seconds15;
		
		return null;
	}
	
	public static AnalysisType convertAnalysisType (String sAnalysisType){
			
		if (AnalysisType.volume.toString().equals(sAnalysisType))
			return AnalysisType.volume;
		
		if (AnalysisType.sentiment_volume_positive.toString().equals(sAnalysisType))
			return AnalysisType.sentiment_volume_positive;
		
		if (AnalysisType.sentiment_volume_negative.toString().equals(sAnalysisType))
			return AnalysisType.sentiment_volume_negative;
		
		if (AnalysisType.tagcloud.toString().equals(sAnalysisType))
			return AnalysisType.tagcloud;
		
		if (AnalysisType.sentiment_volume_neutro.toString().equals(sAnalysisType))
			return AnalysisType.sentiment_volume_neutro;
		
		return null;
	}
}
