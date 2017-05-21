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
package atos.knowledgelab.capture.ifaces;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import atos.knowledgelab.capture.bean.ActiveQueriesList;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.WebIntentTwitter;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.QueryQueue;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.exception.CaptureException;
import twitter4j.OEmbed;

public interface CaptureFacadeIf {

	public String addDataChannel(DataChannel dataChannel)
			throws Exception;

	public void updateDataChannel(DataChannel newDataChannel,
			String dcId) throws Exception;

	public void deleteDataChannel(String dcId) throws Exception;

	public DcTweetList getTweetsFromDC(String dcId, int page,
			int numResults) throws Exception;	

	public DcTweetList getTweetsFromDC(String dcId, String lastId,
			int numResults, boolean reverseOrder) throws Exception;	

	public DcTweetList getFacetTweets (String dcId, String typeDataSource, String filterExpressionRw, String sorter, String mode, String fields, int page, 
			int numResults) throws Exception;
	
	public DcTweetList getFacetWithNoFqTweets (String dcId, String typeDataSource, String filterExpressionRw, String sorter, String mode, String fields, int page, 
			int numResults) throws Exception;
	
	public DataChannel getDataChannel(String dcId)
			throws CaptureException;

	public DataChannelList getDataChannels(int numResults, int page)
			throws Exception;

	public TwitterUser getTwitterUser(String userId) throws Exception;
	
	public QueryData getActiveStreamQuery() throws Exception;

	public ActiveQueriesList getActiveQueries();
	
	public boolean isQueryActive(String queryId) throws Exception;
	
	public LinkedHashMap<String, QueryData> getActiveQueriesMap() throws Exception;
	
	public LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType) throws Exception;
	
	public void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID, QueryData keywords) throws Exception;
	
	public abstract QueryQueue getQueryQueue();

	public abstract void setStorage(CaptureStorageIf cs) throws Exception;

	public abstract void updateDataSource(QueryData query)
			throws Exception;

	public String addDataPool (DataPool dataPool) throws Exception;

	public void updateDataPool (DataPool newDataPool, String dpId) throws Exception;

	public void deleteDataPool (String dpId) throws Exception;
	
	public DataPoolList getDataPools(int numResults, String fromKey) throws Exception;

	public DataPool getDataPool(String dpId) throws CaptureException, IOException;

	public Map<String, WebIntentTwitter> getInfluential (String userId, Date date, String top) throws Exception;

	public Map<String, TweetThread> getTweetThread(List<String> tweetIdList) throws Exception;

	public Map<String, WebIntentTwitter> getMemberList (String owner, String listId) throws Exception;
}