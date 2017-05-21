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

import atos.knowledgelab.capture.bean.Aggregator;
import atos.knowledgelab.capture.bean.AggregatorList;
import atos.knowledgelab.capture.bean.Brand;
import atos.knowledgelab.capture.bean.BrandList;
import atos.knowledgelab.capture.bean.CompetitorAnalysis;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.EngagementAnalysis;
import atos.knowledgelab.capture.bean.InfluentialAnalysis;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.ReachAnalysis;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.exception.CaptureException;

public interface CaptureStorageIf {

	public abstract void storeDataChannel(DataChannel dChannel)
			throws CaptureException;

	public abstract void updateDataChannel(DataChannel newDataChannel)
			throws CaptureException;

	public abstract void deleteDataChannel(DataChannel dChannel)
			throws CaptureException;

	public abstract DataChannel getDataChannel(String dcId)
			throws CaptureException;

	public abstract DataChannelList getDataChannels(int numResults, int page)
			throws CaptureException;

	public abstract TwitterUser getUserProfile(String userId)
			throws CaptureException;

	public abstract void addUserProfile(TwitterUser user)
			throws CaptureException;

	public abstract DcTweetList getTweetsFromDC(String dcId, int page,
			int numResults) throws CaptureException;

	public abstract DcTweetList getTweetsFromDC(String dcId, String lastId,
			int numResults, boolean reverse) throws CaptureException;

	public abstract void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID) throws CaptureException;

	public abstract boolean isQueryActive(String queryID) throws CaptureException;

	public abstract LinkedHashMap<String, QueryData> getActiveQueriesMap() throws CaptureException;

	public abstract LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType) throws CaptureException;

	public abstract QueryData getActiveStreamQuery() throws CaptureException;
	
	public abstract DcTweetList getFacetTweets(String dcId, 
			String typeDataSource, String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException;	
	
	public abstract DcTweetList getFacetWithNoFqTweets(String dcId, 
			String typeDataSource, String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException;

	public abstract void updateDataSource(QueryData query) throws CaptureException;

	public abstract void storeDataPool (DataPool dPool)
			throws CaptureException;
	
	public abstract void updateDataPool (DataPool newDataPool)
			throws CaptureException;

	public abstract void deleteDataPool (DataPool dPool)
			throws CaptureException;
	
	public abstract DataPoolList getDataPools(int numResults, String fromKey) 
			throws CaptureException;

	public abstract DataPool getDataPool(String dpId)
			throws CaptureException, IOException;
	
	public abstract void indexTagCloudOcurrences (List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException;
	
	public abstract void indexVolumeOcurrences (List<VolumeOcurrenceAnalysis> ocurrenceList) throws CaptureException;
	
	public abstract void storeTermOccurrenceAnalysis (List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException;
	
	public abstract void storeVolumeOcurrenceAnalysis (List<VolumeOcurrenceAnalysis> ocurrenceList) throws CaptureException;
	
	public abstract String addAggregator (Aggregator aggregator) throws CaptureException;
	
	public abstract void updateAggregator (Aggregator aggregator, String aggID) throws CaptureException;
	
	public abstract Aggregator getAggregator (String aggID) throws CaptureException;
	
	public abstract void deleteAggregator (String aggID) throws CaptureException;
	
	public abstract AggregatorList getAggregators (int numResults, int page)
			throws CaptureException;
	
	public abstract void storeAGPid(String aggID, String pid) throws CaptureException;
	
	public abstract void deleteAGPid(String aggID) throws CaptureException;
	
	public abstract String getAGPid(String aggID) throws CaptureException;
	
	public abstract Map<String,String> getAGPids() throws CaptureException;
	
	public abstract void storeBMPid(String brandID, String pid) throws CaptureException;
	
	public abstract void deleteBMPid(String brandID) throws CaptureException;
	
	public abstract String getBMPid(String brandID) throws CaptureException;
	
	public abstract Map<String,String> getBMPids() throws CaptureException;

	public abstract String addBrand(Brand brand) throws CaptureException;

	public abstract void updateBrand(Brand newBrand, String brandID) throws CaptureException;

	public abstract void deleteBrand(String brandID) throws CaptureException;

	public abstract Brand getBrand(String brandID) throws CaptureException;

	public abstract BrandList getBrands(int numResults, int page) throws CaptureException;
	
	// top puede ser "retweeted", "replied", "mentioned", "influencers"
	public abstract Map<String, Integer> getTwitterInfluential (String userID, Date date, String top) throws CaptureException;
	
	public abstract void storeReachAnalysis (List<ReachAnalysis> ocurrenceList) throws CaptureException;
	public abstract void storeEngagementAnalysis (List<EngagementAnalysis> ocurrenceList) throws CaptureException;
	public abstract void storeInfluentialAnalysis (List<InfluentialAnalysis> ocurrenceList) throws CaptureException;
	public abstract void storeCompetitorAnalysis (List<CompetitorAnalysis> ocurrenceList) throws CaptureException;
}