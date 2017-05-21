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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;



public class HBaseDenormBenchmark {

	
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		//UUID dcId = UUID.randomUUID();
		String dcId = "81bf4579-24fc-4ced-a773-a5443580518g";
		//String dcId = "81bf4579-24fc-4ced-a773-a5443580518f";
		int tweetCount = 10000;
		System.out.println("Data channel ID: " + dcId);
		
		HBaseDenormProxy hbp = new HBaseDenormProxy();
		
		
		System.out.println("Generating tweets...");
		List<Tweet> tweetList = hbp.generateTweets(tweetCount);
		System.out.println("Done.");
		System.out.println();
		
		long startTime = System.currentTimeMillis();
		//hbp.addTweetsToDC(tweetList, dcId.toString());
		long endTime = System.currentTimeMillis();
		System.out.println("Adding tweets (one by one) took " + (endTime - startTime) + " milliseconds");
		
		List<Tweet> tweetList2 = hbp.generateTweets(tweetCount);
		startTime = System.currentTimeMillis();
		hbp.addTweetsToDCBatch(tweetList2, dcId.toString());
		endTime = System.currentTimeMillis();
		
		System.out.println("Adding tweets (batch) took " + (endTime - startTime) + " milliseconds");
		
		
		try {
			startTime = System.currentTimeMillis();
			//hbp.getOneRecord("capture_dc_tweets", "ad80ff7a-3a02-4fac-ad1b-cf867c943a49T2015623183453098391");
			hbp.getOneRecord("capture_dc_tweets", "81bf4579-24fc-4ced-a773-a5443580518f0000T201507241816161008840497151012");
			endTime = System.currentTimeMillis();
			System.out.println("Reading 1 tweet took " + (endTime - startTime) + " milliseconds");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			startTime = System.currentTimeMillis();
			long count = hbp.scanTableAndCountRows("capture_dc_tweets", dcId.toString());
			endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds. Count: " + count);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			startTime = System.currentTimeMillis();
			long count = hbp.scanTableAndCountRows("capture_dc_tweets", "81bf4579-24fc-4ced-a773-a5443580518f0000T201507241901");
			endTime = System.currentTimeMillis();
			System.out.println("Reading bunch of tweets took " + (endTime - startTime) + " milliseconds. Count: " + count);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			startTime = System.currentTimeMillis();
			long count = hbp.scanTableAndCountRows("capture_dc_tweets", dcId.toString(), 100);
			endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds. Count: " + count);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			System.out.println("Scan & aggregate");
			startTime = System.currentTimeMillis();
			Map<String, Integer> agregates = hbp.scanTableAggregates("capture_dc_tweets", dcId.toString(), 100);
			endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
			MapUtils.debugPrint(System.out, "Aggregates", agregates);
			System.out.println("");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
