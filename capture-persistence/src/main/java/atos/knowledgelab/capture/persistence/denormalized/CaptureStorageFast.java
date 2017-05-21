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
package atos.knowledgelab.capture.persistence.denormalized;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;
import atos.knowledgelab.capture.persistence.SolrManager;
import atos.knowledgelab.capture.util.AggregateType;

public class CaptureStorageFast implements CaptureStorageIf {
	private final static Logger LOGGER = Logger.getLogger(
			CaptureStorageFast.class.getName());
	
	public synchronized void storeDataChannel(DataChannel dChannel)
			throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeDataChannel(dChannel);
			LOGGER.info("Supuesto id despues de cambiarlo por el corto" + dChannel.getChannelID().toString());
			SolrManagerFast.getInstance().addDataChannel(dChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding a new data channel: "
							+ e.getMessage(), e);
		}

	}

	public synchronized void updateDataChannel(DataChannel newDataChannel)
			throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().updateDataChannel(newDataChannel);
			SolrManagerFast.getInstance().addDataChannel(newDataChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating existing data channel: "
							+ e.getMessage(), e);
		}

	}

	public synchronized void updateDataSource(QueryData query)
			throws CaptureException {
		try {
			
			HBaseDenormProxy.getInstance().updateDataSource(query);
			SolrManagerFast.getInstance().updateDataSource(query);
			//throw(new Exception("Unimplemented???"));
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Fatal error while updating DataSource.", e);

			throw new CaptureException(
					" ERROR in transaction updating existing data source in solr: "
							+ e.getMessage(), e);
		}

	}
	
	public synchronized void deleteDataChannel(DataChannel dChannel)
			throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().removeDcById(dChannel.getChannelID());
			SolrManagerFast.getInstance().deleteDataChannel(dChannel);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Fatal error while deleting DataChannel.", e);

			throw new CaptureException(
					" ERROR in transaction deleting data channel"
							+ dChannel.getChannelID() + ": " + e.getMessage(),
					e);
		}

	}

	public synchronized DataChannel getDataChannel(String dcId)
			throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getDcById(dcId);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized DataChannelList getDataChannels(int numResults, int page)
			throws CaptureException {
		try {
			LOGGER.warning("page parameter in getDataChannels is not yet properly used!");
			return HBaseDenormProxy.getInstance().getDataChannels(null, numResults);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}

	public synchronized TwitterUser getUserProfile(String userId)
			throws CaptureException {
		try {
			throw(new Exception("Not implemented!"));
			//return JPAHBaseManager.getInstance().getUserProfile(userId);
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
			return HBaseDenormProxy.getInstance().getTweetsFromDc(dcId, null, numResults, false);
			
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
//			return SolrManagerFast.getInstance().getFacetTweetsFromDC(dcId,
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
			List<Tweet> l = new ArrayList<Tweet>();
			l.addAll(tweetList.getTweets());
			
			HBaseDenormProxy.getInstance().addTweetsToDCBatch(l, dcID);
			//JPAHBaseManager.getInstance().addTweetsToDataChannel(tweetList,dcID, dsID);
			
			QueryData qd = new QueryData();
			qd.setDcID(dcID);
			qd.setDsID(dsID);
			qd.setComposedID(dcID + "-" + dsID);
			SolrManagerFast.getInstance().indexTweets(tweetList, qd);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding tweets to DC: " + dcID + ": "
							+ e.getMessage(), e);
		}

	}

	
	public synchronized boolean isQueryActive(String queryID)
			throws CaptureException {
		try {
			return SolrManagerFast.getInstance().isQueryActive(queryID);
		} catch (Exception e) {
			throw new CaptureException(" ERROR in while checking active query"
					+ ": " + e.getMessage(), e);
		}
	}

	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMap()
			throws CaptureException {
		try {
			return SolrManagerFast.getInstance().getActiveQueries();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in while getting active query map" + ": "
							+ e.getMessage(), e);
		}
	}
	
	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType)
			throws CaptureException {
		try {
			return SolrManagerFast.getInstance().getActiveQueriesByDSType(dsType);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in while getting active query map ByDSType" + ": "
							+ e.getMessage(), e);
		}
	}

//	public synchronized VolumeResultList getVolumeFromDC(String dcId,
//			String typeDataSource, String fInit, String fEnd, int parseInt)
//			throws CaptureException {
//		try {
//			//throw new CaptureException("Unnimplemented method");
//			//LOGGER.warning("Last argument (Aggregation type) is probably used in an incorrect way!");
//			//HBaseDenormProxy.getInstance().getVolumeFromDC(dcId, typeDataSource, fInit, fEnd, AggregateType.HOURLY);
//			return SolrManagerFast.getInstance().getVolumeFromDC(dcId,
//					typeDataSource, fInit, fEnd, parseInt);
//			
//		} catch (Exception e) {
//			throw new CaptureException(" ERROR while getting volumen from DC:"
//					+ ": " + e.getMessage(), e);
//		}
//	}

	@Override
	public synchronized DcTweetList getFacetTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException{
		try {
			return SolrManagerFast.getInstance().getFacetTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting facets tweets:"
					+ ": " + e.getMessage(), e);		}
	}
	
	@Override
	public synchronized DcTweetList getFacetWithNoFqTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException{
		try {
			return SolrManagerFast.getInstance().getFacetWithNoFqTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting facets tweets:"
					+ ": " + e.getMessage(), e);		}
	}

//	@Override
//	public synchronized void indexOcurrences (List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
//		// TODO Auto-generated method stub
//		try {
//			SolrManagerFast.getInstance().indexOcurrences(ocurrenceList);
//		} catch (Exception e) {
//			throw new CaptureException(" ERROR while getting facets tweets:"
//					+ ": " + e.getMessage(), e);		}
//	}

	@Override
	public DcTweetList getTweetsFromDC(String dcId, String lastId, int numResults, boolean reverseOrder) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getTweetsFromDc(dcId, lastId, numResults, reverseOrder);
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Fatal error while retrieving tweets.", e);
			
			throw new CaptureException(
					" ERROR in transaction getting tweets from DC: " + dcId
							+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public DataPoolList getDataPools(int numResults, String fromKey) throws CaptureException {
		try {
			LOGGER.warning("page parameter in getDataChannels is not yet properly used!");
			return HBaseDenormProxy.getInstance().getDataPools(fromKey, numResults);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DataPool getDataPool(String dpId) throws CaptureException, IOException {
		// TODO Auto-generated method stub
		return HBaseDenormProxy.getInstance().getDpById(dpId);

	}
	
	@Override
	public void indexTagCloudOcurrences(
			List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
		try {
			SolrManagerFast.getInstance().indexTagCloudOcurrences(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while indexing tag cloud ocurrence:"
					+ ": " + e.getMessage(), e);		}
		
	}

	@Override
	public void indexVolumeOcurrences(
			List<VolumeOcurrenceAnalysis> ocurrenceList)
			throws CaptureException {
		try {
			SolrManagerFast.getInstance().indexVolumeOcurrences(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while indexing volumen ocurrences"
					+ ": " + e.getMessage(), e);		}
		// TODO Auto-generated method stub
		
	}

	public String addAggregator(Aggregator aggregator) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().addAggregator(aggregator);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding a new aggregator: "
							+ e.getMessage(), e);
		}
		
	}
	
	@Override
	public void updateAggregator(Aggregator aggregator, String aggID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().updateAggregator(aggregator, aggID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating a new aggregator: "
							+ e.getMessage(), e);
		}
		
	}

	@Override
	public Aggregator getAggregator(String aggID) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getAggregator(aggID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting a new aggregator: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void deleteAggregator(String aggID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().deleteAggregator(aggID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction removing a new aggregator: "
							+ e.getMessage(), e);
		}
		
	}

	@Override
	public AggregatorList getAggregators(int numResults, int page)
			throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getAggregators();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting aggregators: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeAGPid(String aggID, String pid) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			HBaseDenormProxy.getInstance().storeAGPid(aggID, pid);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storing process: "
							+ e.getMessage(), e);
	}
	}

	@Override
	public void deleteAGPid(String aggID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().deleteAGPid(aggID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction delete process: "
							+ e.getMessage(), e);
		
	}
	}

	@Override
	public String getAGPid(String aggID) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getAGPid(aggID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting process: "
							+ e.getMessage(), e);
	}
	}

	@Override
	public Map<String, String> getAGPids() throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getAGPids();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting process: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeTermOccurrenceAnalysis(
			List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
		try {
			//System.out.println("antes de AGREGAMOSSSSSSSSSSSSSSSSSSSS*****************************************************************");
			HBaseDenormProxy.getInstance().storeTermOccurrenceAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeTermOccurrenceAnalysis: "
							+ e.getMessage(), e);
		}
		
	}

	@Override
	public void storeVolumeOcurrenceAnalysis(
			List<VolumeOcurrenceAnalysis> ocurrenceList)
			throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeVolumeOcurrenceAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeVolumeOcurrenceAnalysis: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeBMPid(String brandID, String pid) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeBMPid(brandID, pid);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storing brand process: "
							+ e.getMessage(), e);
		}	
		
	}

	@Override
	public void deleteBMPid(String brandID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().deleteBMPid(brandID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction deleting brand process: "
							+ e.getMessage(), e);
		}	
	}

	@Override
	public String getBMPid(String brandID) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getBMPid(brandID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting brand process: "
							+ e.getMessage(), e);
		}	
	}

	@Override
	public Map<String, String> getBMPids() throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getBMPids();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting brand process: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public String addBrand(Brand brand) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().addBrand(brand);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding brand: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void updateBrand(Brand newBrand, String brandID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().updateBrand(newBrand, brandID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating brand: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void deleteBrand(String brandID) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().deleteBrand(brandID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction deleting brand: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public Brand getBrand(String brandID) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getBrand(brandID);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting brand: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public BrandList getBrands(int numResults, int page) throws CaptureException {
		try {
			return HBaseDenormProxy.getInstance().getBrands();
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction getting brands: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Integer> getTwitterInfluential(String userID, Date date, String top) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return HBaseDenormProxy.getInstance().getTwitterInfluential(userID, date, top);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction twitter influenntial: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeDataPool(DataPool dPool) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeDataPool(dPool);
			LOGGER.info("Supuesto id despues de cambiarlo por el corto" + dPool.getPoolID().toString());
			//SolrManagerFast.getInstance().addDataChannel(dChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding a new data pool: "
							+ e.getMessage(), e);
		}
		
	}

	@Override
	public void updateDataPool(DataPool newDataPool) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().updateDataPool (newDataPool);
			//SolrManagerFast.getInstance().addDataChannel(newDataChannel);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction updating existing data pool: "
							+ e.getMessage(), e);
		}
		
	}

	@Override
	public void deleteDataPool(DataPool dPool) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().removeDpById(dPool.getPoolID());
			//SolrManagerFast.getInstance().deleteDataChannel(dChannel);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Fatal error while deleting DataPool.", e);

			throw new CaptureException(
					" ERROR in transaction deleting data pool"
							+ dPool.getPoolID() + ": " + e.getMessage(),
					e);
		}
		
	}

	@Override
	public QueryData getActiveStreamQuery() throws CaptureException {
		
		try {
			return SolrManagerFast.getInstance().getActiveStreamQuery();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Fatal error while getting active stream query.", e);

			throw new CaptureException(
					" ERROR while getting active stream query" +
							": " + e.getMessage(),
					e);
		}
	}

	@Override
	public void storeReachAnalysis(List<ReachAnalysis> ocurrenceList) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeReachAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeReachAnalysis: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeEngagementAnalysis(List<EngagementAnalysis> ocurrenceList) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeEngagementAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeEngagementAnalysis: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeInfluentialAnalysis(List<InfluentialAnalysis> ocurrenceList) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeInfluentialAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeInfluentialAnalysis: "
							+ e.getMessage(), e);
		}
	}

	@Override
	public void storeCompetitorAnalysis(List<CompetitorAnalysis> ocurrenceList) throws CaptureException {
		try {
			HBaseDenormProxy.getInstance().storeCompetitorAnalysis(ocurrenceList);
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction storeCompetitorAnalysis: "
							+ e.getMessage(), e);
		}
	}
}
