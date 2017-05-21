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

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;
import atos.knowledgelab.capture.util.AggregateType;

public class ReadDC {

	public static void main(String[] args) throws IOException {
		
		String dcId = "4c66981d-fa26-4e75-a0d0-14757e142e53";
		//String dcId = "81bf4579-24fc-4ced-a773-a5443580518f";
		int tweetCount = 10000;
		System.out.println("Data channel ID: " + dcId);
		
		HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		
		//System.in.read();
		
		DcTweetList tweetList;
		try {
			System.out.println("Read 100 tweets from DC");
			long startTime = System.currentTimeMillis();
			
			
			tweetList = hbp.getTweetsFromDc(dcId, "", 100, false);
			System.out.println(tweetList.getTweets().size());
			
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
		} catch (InstantiationException | SecurityException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			System.out.println("Read whole DC");
//			long startTime = System.currentTimeMillis();
//			
//			
//			list = hbp.getTweetsFromDc(dcId);
//			System.out.println(list.size());
//			
//			long endTime = System.currentTimeMillis();
//			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
//		} catch (InstantiationException | SecurityException | ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
		
//		try {
//			System.out.println("Count1");
//			long startTime = System.currentTimeMillis();
//			long count = hbp.getDcCountSlow2(dcId);
//			long endTime = System.currentTimeMillis();
//			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
//			System.out.println("count: " + count);
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		try {
			System.out.println("Count2");
			long startTime = System.currentTimeMillis();
			long count = hbp.getDcCountById(dcId);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			System.out.println("count: " + count);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			System.out.println("Count3");
			long startTime = System.currentTimeMillis();
			long count = hbp.getDcCountSlow(dcId);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			System.out.println("count: " + count);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.MONTHLY);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.DAILY);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.HOURLY);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.TEN_MINUTES);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.PER_MINUTE);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.getVolumeFromDC(dcId, "T", "2015-06-01T12:00:00Z", "2015-08-10T12:00:00Z", AggregateType.PER_SECOND);
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
	}

}
