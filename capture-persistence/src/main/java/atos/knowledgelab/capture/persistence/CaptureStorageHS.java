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
package atos.knowledgelab.capture.persistence;

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
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;

public class CaptureStorageHS implements CaptureStorageIf {

	public synchronized void storeDataChannel(DataChannel dChannel)
			throws CaptureException {
		try {
			JPAHBaseManager.getInstance().addDataChannelToHBase(dChannel);
			SolrManager.getInstance().addDataChannel(dChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding a new data channel: "
							+ e.getMessage(), e);
		}

	}

	public synchronized void updateDataChannel(DataChannel newDataChannel)
			throws CaptureException {
		try {
			JPAHBaseManager.getInstance().updateDataChannelInHBase(
					newDataChannel);
			DataChannel dataChannel = JPAHBaseManager.getInstance()
					.getDataChannelFromHBase(newDataChannel.getChannelID());
			SolrManager.getInstance().addDataChannel(dataChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating existing data channel: "
							+ e.getMessage(), e);
		}

	}

	public synchronized void updateDataSource(QueryData query)
			throws CaptureException {
		try {
			
			SolrManager.getInstance().updateDataSource(query);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating existing data source in solr: "
							+ e.getMessage(), e);
		}

	}
	
	public synchronized void deleteDataChannel(DataChannel dChannel)
			throws CaptureException {
		try {
			JPAHBaseManager.getInstance().deleteDataChannelFromHBase(
					dChannel.getChannelID());
			SolrManager.getInstance().deleteDataChannel(dChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction deleting data channel"
							+ dChannel.getChannelID() + ": " + e.getMessage(),
					e);
		}

	}

	public synchronized DataChannel getDataChannel(String dcId)
			throws CaptureException {
		return JPAHBaseManager.getInstance().getDataChannelFromHBase(dcId);
	}

	public synchronized DataChannelList getDataChannels(int numResults, int page)
			throws CaptureException {
		return JPAHBaseManager.getInstance().getDataChannelsFromHBase(
				numResults, page);
	}

	public synchronized TwitterUser getUserProfile(String userId)
			throws CaptureException {
		try {
			return JPAHBaseManager.getInstance().getUserProfile(userId);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting user profile" + userId
							+ ": " + e.getMessage(), e);
		}
	}

	public synchronized void addUserProfile(TwitterUser user)
			throws CaptureException {
		throw new CaptureException("Unnimplemented method");

	}

	public synchronized DcTweetList getTweetsFromDC(String dcId, int page,
			int numResults) throws CaptureException {
		try {
			return SolrManager.getInstance().getTweetsFromDC(dcId, page,
					numResults);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting tweets from DC: " + dcId
							+ ": " + e.getMessage(), e);
		}
	}

//	public synchronized DcTweetList getFacetTweetsFromDC(String dcId,
//			String typeDataSource, String facetName, String value, int page,
//			int numResults) throws CaptureException {
//		try {
//			return SolrManager.getInstance().getFacetTweetsFromDC(dcId,
//					typeDataSource, facetName, value, page, numResults);
//		} catch (Exception e) {
//			throw new CaptureException(
//					" ERROR in transaction getting faceted tweets from DC: "
//							+ dcId + ": " + e.getMessage(), e);
//		}
//	}

	public synchronized void addTweetsToDataChannel(TweetList tweetList,
			String dcID, String dsID) throws CaptureException {

		try {
			JPAHBaseManager.getInstance().addTweetsToDataChannel(tweetList,dcID, dsID);
			QueryData qd = new QueryData();
			qd.setDcID(dcID);
			qd.setDsID(dsID);
			qd.setComposedID(dcID + "-" + dsID);
			SolrManager.getInstance().indexTweets(tweetList, qd);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding tweets to DC: " + dcID + ": "
							+ e.getMessage(), e);
		}

	}

	
	public synchronized boolean isQueryActive(String queryID)
			throws CaptureException {
		try {
			return SolrManager.getInstance().isQueryActive(queryID);
		} catch (Exception e) {
			throw new CaptureException(" ERROR in while checking active query"
					+ ": " + e.getMessage(), e);
		}
	}

	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMap()
			throws CaptureException {
		try {
			return SolrManager.getInstance().getActiveQueries();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in while getting active query map" + ": "
							+ e.getMessage(), e);
		}
	}
	
	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType)
			throws CaptureException {
		try {
			//TODO develop method with dsType parameter
			//return SolrManager.getInstance().getActiveQueriesByDSType(dsType);
			return SolrManager.getInstance().getActiveQueries();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in while getting active query map ByDSType" + ": "
							+ e.getMessage(), e);
		}
	}

	public synchronized VolumeResultList getVolumeFromDC(String dcId,
			String typeDataSource, String fInit, String fEnd, int parseInt)
			throws CaptureException {
		try {
			return SolrManager.getInstance().getVolumeFromDC(dcId,
					typeDataSource, fInit, fEnd, parseInt);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting volumen from DC:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public synchronized DcTweetList getFacetTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException{
		try {
			return SolrManager.getInstance().getFacetTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting facets tweets:"
					+ ": " + e.getMessage(), e);		}
	}

//	@Override
//	public synchronized void indexOcurrences (List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
//		// TODO Auto-generated method stub
//		try {
//			SolrManager.getInstance().indexOcurrences(ocurrenceList);
//		} catch (Exception e) {
//			throw new CaptureException(" ERROR while getting facets tweets:"
//					+ ": " + e.getMessage(), e);		}
//	}
	
	public synchronized List<String> getDcIds () throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getDcIds();
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting facets tweets:"
					+ ": " + e.getMessage(), e);		}
	}

	@Override
	public DcTweetList getTweetsFromDC(String dcId, String lastId, int numResults, boolean reverse) throws CaptureException {
		// TODO Auto-generated method stub
		throw new CaptureException("Unnimplemented method");
	}

	@Override
	public DataPoolList getDataPools(int numResults, String fromKey) throws CaptureException {
		// TODO Auto-generated method stub
		throw new CaptureException("Unnimplemented method");
	}

	@Override
	public DataPool getDataPool(String dpId) throws CaptureException, IOException {
		// TODO Auto-generated method stub
		throw new CaptureException("Unnimplemented method");
	}

	@Override
	public void indexTagCloudOcurrences(
			List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
		try {
			SolrManager.getInstance().indexTagCloudOcurrences(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while indexing tag cloud ocurrence:"
					+ ": " + e.getMessage(), e);		}
		
	}

	@Override
	public void indexVolumeOcurrences(
			List<VolumeOcurrenceAnalysis> ocurrenceList)
			throws CaptureException {
		try {
			SolrManager.getInstance().indexVolumeOcurrences(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while indexing volumen ocurrences"
					+ ": " + e.getMessage(), e);		}
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addAggregator(Aggregator aggregator) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAggregator(Aggregator aggregator, String aggID)
			throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Aggregator getAggregator(String aggID) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAggregator(String aggID) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AggregatorList getAggregators(int numResults, int page)
			throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeAGPid(String aggID, String pid) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAGPid(String aggID) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAGPid(String aggID) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getAGPids() throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeTermOccurrenceAnalysis(
			List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeVolumeOcurrenceAnalysis(
			List<VolumeOcurrenceAnalysis> ocurrenceList)
			throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeBMPid(String brandID, String pid) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteBMPid(String brandID) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getBMPid(String brandID) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getBMPids() throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addBrand(Brand brand) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBrand(Brand newBrand, String brandID) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteBrand(String brandID) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Brand getBrand(String brandID) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BrandList getBrands(int numResults, int page) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Integer> getTwitterInfluential(String userID, Date date, String top) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeDataPool(DataPool dPool) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDataPool(DataPool newDataPool) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteDataPool(DataPool dPool) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryData getActiveStreamQuery() throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DcTweetList getFacetWithNoFqTweets(String dcId, String typeDataSource, String filterExpressionRw,
			String sorter, String mode, String fields, int page, int numResults) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void storeReachAnalysis(List<ReachAnalysis> ocurrenceList) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeEngagementAnalysis(List<EngagementAnalysis> ocurrenceList) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeInfluentialAnalysis(List<InfluentialAnalysis> ocurrenceList) throws CaptureException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeCompetitorAnalysis(List<CompetitorAnalysis> ocurrenceList) throws CaptureException {
		// TODO Auto-generated method stub
		
	}


}
