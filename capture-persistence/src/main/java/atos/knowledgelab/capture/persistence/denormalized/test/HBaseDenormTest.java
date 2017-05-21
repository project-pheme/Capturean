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
import java.util.ArrayList;
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

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;
import atos.knowledgelab.capture.util.CaptureConstants;



public class HBaseDenormTest {

	
	
	
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		//UUID dcId = UUID.randomUUID();
		String dcId = "81bf4579-24fc-4ced-a773-a5443580518g";
		//String dcId = "81bf4579-24fc-4ced-a773-a5443580518f";
		int tweetCount = 10000;
		System.out.println("Data channel ID: " + dcId);
		
		HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		
		//create some DC
		DataChannel dc = new DataChannel();
		dc.setCreationDate("2015-07-30 17:37:08.000");
		dc.setUpdateDate("2015-07-30 17:37:08.000");
		dc.setStartCaptureDate("2015-07-30 18:37:08.000");
		dc.setEndCaptureDate("2015-07-30 19:37:08.000");
		dc.setName("My Data channel");
		dc.setType(CaptureConstants.SEARCH);
		dc.setChannelID(UUID.randomUUID().toString());
		
		TwitterDataSource ds1 = new TwitterDataSource();
		ds1.setDstype(CaptureConstants.TWITTER);
		ds1.setKeywords("key1 key2 lang:en \"hello world\"");
		ds1.setSourceID(UUID.randomUUID().toString());
		
		TwitterDataSource ds2 = new TwitterDataSource();
		ds2.setDstype(CaptureConstants.TWITTER);
		ds2.setKeywords("key0 key0 lang:en \"hello world\"");
		ds2.setSourceID(UUID.randomUUID().toString());
		
		List<DataSource> dsList = new ArrayList<>();
		dsList.add(ds1);
		dsList.add(ds2);
		
		dc.setDataSources(dsList);
		
		hbp.storeDataChannel(dc);
		
				
		
		DataChannelList dcl = hbp.getDataChannels();
		System.out.println("Checking...");
		for (DataChannel d : dcl.getDataChannels()) {
			System.out.println(d);
			for (DataSource s : d.getDataSources()) {
				System.out.print("");
				System.out.println(s);
			}
		}
		
		dcl = hbp.getDataChannels(dcId, 100);
		System.out.println("Checking...");
		for (DataChannel d : dcl.getDataChannels()) {
			System.out.println(d);
			for (DataSource s : d.getDataSources()) {
				System.out.print("");
				System.out.println(s);
			}
		}
		
		DcTweetList list;
		try {
			System.out.println("Scan & aggregate");
			long startTime = System.currentTimeMillis();
			
			
			list = hbp.getTweetsFromDc(dcId);
			System.out.println(list.getTweets().size());
			
			long endTime = System.currentTimeMillis();
			System.out.println("Reading whole DC took " + (endTime - startTime) + " milliseconds.");
		} catch (InstantiationException | SecurityException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
