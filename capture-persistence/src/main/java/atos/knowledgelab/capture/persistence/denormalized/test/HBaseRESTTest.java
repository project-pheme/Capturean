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
package atos.knowledgelab.capture.persistence.denormalized.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;

public class HBaseRESTTest {

	public static void main(String[] args) throws Exception {
	
		HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		
		List<String> dataPoolIdList = new ArrayList<String>();
		
		dataPoolIdList.add("63654292");
		dataPoolIdList.add("7026f4a1");
		
		Date initDate = toDate("2015-09-03T00:00:00.000Z");
		Date endDate = toDate("2015-09-04T00:00:00.000Z");
		/*
		dataPoolIdList.add("e569ab8e-e616-4bfa-aaa9-8003db06905d");
		dataPoolIdList.add("b890f322-3985-454a-b31c-b32a7cb55e45");
		dataPoolIdList.add("db4774ff-aff3-44a0-972e-5c70daf6e5af");
		dataPoolIdList.add("e569ab8e-e616-4bfa-aaa9-8003db06905d");
		dataPoolIdList.add("74338547-6a6b-4809-adf2-a259b9e4bf85");
		
		Date initDate = toDate("2015-04-22T00:00:00.000Z");
		Date endDate = toDate("2015-04-25T00:00:00.000Z");
		*/
		Periodicity per = Periodicity.daily;
		int numberOfResults = 10;
		
		Map<Date, Map<DataChannel, Integer>> topVolume = hbp.getTopVolume(dataPoolIdList, initDate, endDate, per, numberOfResults);
		System.out.println(topVolume.toString());
		
		Map<Date, Integer> getVolumePer = hbp.getVolumePer(dataPoolIdList, "63654292", initDate, endDate, per);
		System.out.println(getVolumePer.toString());
		
		Map<Date, Map<DataChannel, Double>> getSentimentDegree = hbp.getSentimentDegree(dataPoolIdList, initDate, endDate, per);
		System.out.println(getSentimentDegree.toString());
		
		Map<Date, Map<DataChannel, Integer[]>> getSentiment = hbp.getSentiment(dataPoolIdList, initDate, endDate, per);
		System.out.println(getSentiment.toString());
		
		Map<Date, Map<DataChannel, Integer>> getVolume = hbp.getVolume(dataPoolIdList, initDate, endDate, per);
		System.out.println(getVolume.toString());
		
		Map<DataChannel, Map<String, Integer>> getTagCloud = hbp.getTagCloud(dataPoolIdList, initDate, per);
		System.out.println(getTagCloud.toString());
	
	}
	
	public static Date toDate(String sDate) throws ParseException{
		SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return dateFormatUTC.parse(sDate);
	}
	
}
