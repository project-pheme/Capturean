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
package atos.knowledgelab.capture.reddit;

import java.util.ArrayList;
import java.util.List;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.exception.CaptureException;

public class MainTest {

	public static void main(String[] args) throws CaptureException, InterruptedException {

		DataChannel dc = new DataChannel();
		dc.setChannelID("12345678");
		//dc.setType("search");
		dc.setType("stream");
		dc.setStartCaptureDate("2017-02-02 15:19:24.000");
		dc.setEndCaptureDate("2017-02-17 09:47:24.000");

		RedditDataSource rds = new RedditDataSource();
		rds.setDstype("reddit");
		ArrayList<String> subreddits = new ArrayList<String>();
		subreddits.add("askreddit");
		//subreddits.add("add");
		//subreddits.add("addiction");
		//subreddits.add("ADHD");
		rds.setSubreddits(subreddits);
		//rds.setKeywords("title:diagnosis");
		rds.setKeywords("diagnosis");
		rds.setRedditType("subreddit");
		//rds.setRedditType("post");
		rds.setPost("https://www.reddit.com/r/AskReddit/comments/5rre3w/college_ras_of_reddit_whats_the_most_ridiculous");
		
		List<DataSource> dsl = new ArrayList<DataSource>();
		dsl.add(rds);
		
		dc.setDataSources(dsl);
		
		
		RedditManager.getInstance().runRedditDatachannel(dc);
				
		System.out.println("\n\n\n\n");
		for (String key : RedditManager.getInstance().getRedditList().keySet()){
			System.out.println("batch key -> "+key);
		}	
		for (String key : RedditManager.getInstance().getRedditStreamList().keySet()){
			System.out.println("stream key -> "+key);
		}	
		System.out.println("\n\n\n\n");
		
		//Thread.sleep(30000);
		Thread.sleep(60000);
		
		//RedditManager.getInstance().updateRedditDatachannel(dc);
		RedditManager.getInstance().stopRedditDatachannel(dc.getChannelID());
		
		System.out.println("\n\n\n\n");
		for (String key : RedditManager.getInstance().getRedditList().keySet()){
			System.out.println("batch key -> "+key);
		}	
		for (String key : RedditManager.getInstance().getRedditStreamList().keySet()){
			System.out.println("stream key -> "+key);
		}	
		System.out.println("\n\n\n\n");
		
	}

}
