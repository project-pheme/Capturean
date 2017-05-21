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
package atos.knowledgelab.capture.api;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.TwitterDataSource;
//import atos.knowledgelab.capture.util.DataChannelState;

public class DCJSONSerialisation {

	public static void main(String[] args) throws JsonProcessingException {

//		DataChannel dc1 = new DataChannel();
//		
//		dc1.setChannelID("1ba615a");
//		dc1.setName("DC 1 NAME");
//		dc1.setDcState(DataChannelState.RUNNING);
//		dc1.setTweetRate(12.5);
//		//dc1.setMaxHistoricalTweetLimit(1000L);
//		dc1.setTotalTweetCount(123L);
//		
//		TwitterDataSource tds = new TwitterDataSource();
//		tds.setHistoricalLimit(1000L);
//		
//		ArrayList<DataSource> list = new ArrayList<DataSource>();
//		list.add(tds);
//		
//		dc1.setDataSources(list);
//		
//		
//		ObjectMapper om = new ObjectMapper();
//		om.enable(SerializationFeature.INDENT_OUTPUT);
//		
//		System.out.println(om.writeValueAsString(dc1));
	}

}
