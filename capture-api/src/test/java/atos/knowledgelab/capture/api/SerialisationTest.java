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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.MapResults;
import atos.knowledgelab.capture.bean.MapWrapper;

public class SerialisationTest {

	public static void main(String[] args) throws JsonProcessingException {
		// TODO Auto-generated method stub

		MapResults<DataChannel, Map<String, Integer>> mr = new MapResults();
		
		Map<String, Integer> words1 = new HashMap<String, Integer>();
		Map<String, Integer> words2 = new HashMap<String, Integer>();
		Map<DataChannel, MapWrapper> channels = new HashMap();
		
		words1.put("word1", 1);
		words1.put("word2", 5);
		words1.put("word3", 11);
		words1.put("word4", 10);

		words2.put("rio", 10);
		words2.put("olypics", 22);
		words2.put("medal", 33);

		
		DataChannel dc1 = new DataChannel();
		dc1.setChannelID("1ba615a");
		dc1.setName("DC 1 NAME");
		
		DataChannel dc2 = new DataChannel();
		dc2.setChannelID("b8a123a");
		dc2.setName("DC 2 NAME");
		
		ObjectMapper om = new ObjectMapper();
		om.enable(SerializationFeature.INDENT_OUTPUT);
		
		MapWrapper words1Wrapper = new MapWrapper();
		words1Wrapper.setResultsMap(words1);
		
		MapWrapper words2Wrapper = new MapWrapper();
		words2Wrapper.setResultsMap(words1);
		
		Map<DataChannel, MapWrapper> dcResMap = new HashMap();
		dcResMap.put(dc1, words1Wrapper);
		dcResMap.put(dc2, words2Wrapper);


		MapWrapper outerWrapper = new MapWrapper();
		outerWrapper.setResultsMap(dcResMap);
		
		mr.setMap(outerWrapper);
		
		System.out.println("\nWord count serialisation");
		System.out.println(om.writeValueAsString(mr));

		System.out.println("\nData Channel serialisation");
		System.out.println(om.writeValueAsString(dc1));
		
//		try {
//			System.out.println(dateWrapper.getSerializableForm());
//		} catch (NoSuchFieldException | SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
