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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.exception.CaptureException;

public class RedditManager {
	
	private Map<String, RedditBatchThread> redditList = new HashMap<String, RedditBatchThread>();
	private Map<String, List<RedditStreamThread>> redditStreamList = new HashMap<String, List<RedditStreamThread>>();
	
	private RedditManager() {

	}

	private static class SingletonHolder {
		private static final RedditManager INSTANCE = new RedditManager();
	}

	public static RedditManager getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	public void runRedditDatachannel(DataChannel dc) throws CaptureException {
		
		final String dcID = dc.getChannelID();
		String dcType = dc.getType();
		final String startDate = dc.getStartCaptureDate();
		final String endDate = dc.getEndCaptureDate();
		
		List<DataSource> dsl = dc.getDataSources();
		for (DataSource ds : dsl){
			if(ds instanceof RedditDataSource){
				RedditDataSource rds = (RedditDataSource) ds;
				final String redditType = rds.getRedditType();
				final List<String> subreddits = rds.getSubreddits();
				final String post = rds.getPost();
				final String keywords = rds.getKeywords();
				
				if (dcType.equals("search")){
					RedditBatchThread rbt = new RedditBatchThread(redditType, subreddits, dcID, startDate, endDate,post,keywords);
					rbt.start();
					redditList.put(dcID, rbt);
				}
				else if (dcType.equals("stream")){
					for (final String subreddit : subreddits){
						RedditStreamThread rst = new RedditStreamThread(dcID, subreddit, startDate, endDate);
						rst.start();
						
						List<RedditStreamThread> rst_list = new ArrayList<RedditStreamThread>();
						if (redditStreamList.containsKey(dcID)){
							rst_list = redditStreamList.get(dcID);
						}
						rst_list.add(rst);
						redditStreamList.put(dcID,rst_list);
					}							
					
				}
			}
		}
		
		
	}
	
	public void stopRedditDatachannel(String dcID) throws CaptureException {
				
		if (redditList.containsKey(dcID)){
			RedditBatchThread rbt = redditList.get(dcID);
			rbt.terminate();
		
			redditList.remove(dcID);
		}
		if (redditStreamList.containsKey(dcID)){
			List<RedditStreamThread> rst_list = redditStreamList.get(dcID);
			for (RedditStreamThread rst : rst_list){
				rst.terminate();
			}
			
			redditStreamList.remove(dcID);
		}
		
	}
	
	public void updateRedditDatachannel(DataChannel dc) throws CaptureException {
		
		stopRedditDatachannel(dc.getChannelID());
		runRedditDatachannel(dc);
		
	}
	
	public Map<String, RedditBatchThread> getRedditList(){
		return redditList;
	}
	
	public void setRedditList(Map<String, RedditBatchThread> redditList){
		this.redditList=redditList;
	}
	
	public Map<String, List<RedditStreamThread>> getRedditStreamList(){
		return redditStreamList;
	}
	
	public void setRedditStreamList(Map<String, List<RedditStreamThread>> redditStreamList){
		this.redditStreamList=redditStreamList;
	}
	
}
