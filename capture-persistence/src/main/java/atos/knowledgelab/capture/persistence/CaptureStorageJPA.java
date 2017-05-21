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

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import atos.knowledgelab.capture.bean.Aggregator;
import atos.knowledgelab.capture.bean.AggregatorList;
import atos.knowledgelab.capture.bean.Brand;
import atos.knowledgelab.capture.bean.BrandList;
import atos.knowledgelab.capture.bean.CompetitorAnalysis;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.EngagementAnalysis;
import atos.knowledgelab.capture.bean.InfluentialAnalysis;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.ReachAnalysis;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;
import atos.knowledgelab.capture.util.CaptureConstants;

public class CaptureStorageJPA implements CaptureStorageIf {

	private final static Logger LOGGER = Logger
			.getLogger(CaptureStorageJPA.class.getName());

	//@PersistenceContext(name = "Capture-SQL")
	EntityManager em = Persistence.createEntityManagerFactory("Capture-SQL").createEntityManager();
	//EntityManager em = Persistence.createEntityManagerFactory("Capture").createEntityManager();

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#storeDataChannel(atos.knowledgelab.capture.bean.DataChannel)
	 */
	
	public synchronized void storeDataChannel(DataChannel dChannel) throws CaptureException {
		EntityTransaction tx = em.getTransaction();
		TwitterDataSource ds = (TwitterDataSource) dChannel.getDataSources()
				.iterator().next();
		LOGGER.info(" >>> DS: " + ds.getDstype() + ", keywords = "
				+ ds.getKeywords());
		try {
			tx.begin();
			em.merge(dChannel);
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction adding a new data channel: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction adding a new data channel: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#updateDataChannel(atos.knowledgelab.capture.bean.DataChannel)
	 */
	
	public synchronized void updateDataChannel(DataChannel newDataChannel)
			throws CaptureException {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			DataChannel dc = em.find(DataChannel.class,
					newDataChannel.getChannelID());
			em.detach(dc);
			merge(newDataChannel, dc);

			if (dc.getStatus().equals(CaptureConstants.NON_ACTIVE)) {
				throw new CaptureException(
						" ERROR: DataChannel is currently logically deleted ...");
			}
			if (!dc.getDataSources().isEmpty()) {
				Iterator<DataSource> itDs = dc.getDataSources().iterator();
				dc.setDataSources(new ArrayList<DataSource>());

				while (itDs.hasNext()) {
					Object ds = itDs.next();
					LOGGER.info(" DS = " + ((DataSource) ds).getSourceID());
					Object persistentDs = findDs(em, ds);

					// LOGGER.info(" DS PERS. = " +
					// ((DataSource)persistentDs).getSourceID());
					if (persistentDs != null) {
						em.detach(persistentDs);
						merge(ds, persistentDs);
						// dc.getDataSources().remove(ds);
						dc.getDataSources().add((DataSource) persistentDs);
					} else {
						dc.getDataSources().add((DataSource) ds);
					}
				}
			}

			em.merge(dc);
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction updating existing data channel: "
					+ e.getMessage());			
			throw new CaptureException(
					" ERROR in transaction updating existing data channel: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#deleteDataChannel(atos.knowledgelab.capture.bean.DataChannel)
	 */
	
	public synchronized void deleteDataChannel(DataChannel dChannel) throws CaptureException {
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			dChannel.setStatus(CaptureConstants.NON_ACTIVE);
			// em.remove(dc);
			em.merge(dChannel);
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction deleting data channel "
					+ dChannel.getChannelID() + ": " + e.getMessage());
			throw new CaptureException(
					" ERROR in transaction deleting data channel"
							+ dChannel.getChannelID() + ": " + e.getMessage(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#getDataChannel(java.lang.String)
	 */
	
	public synchronized DataChannel getDataChannel(String dcId) throws CaptureException {
		DataChannel dc = new DataChannel();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			dc = em.find(DataChannel.class, dcId);
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR recovering Data Channel " + dcId
					+ " from NO-SQL storage: " + e.getMessage());
			throw new CaptureException(" ERROR recovering Data Channel " + dcId
					+ " from NO-SQL storage: " + e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();

		}
		return dc;
	}

	private void merge(Object newObj, Object obj) throws Exception {
		BeanInfo beanInfo = Introspector.getBeanInfo(newObj.getClass());

		// Iterate over all the attributes
		for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
			// Only copy writable attributes
			if (descriptor.getWriteMethod() != null) {
				Object originalValue = descriptor.getReadMethod()
						.invoke(newObj);
				// Only copy not null to destination
				if (originalValue != null) {
					// Object defaultValue =
					// descriptor.getReadMethod().invoke(destination);
					descriptor.getWriteMethod().invoke(obj, originalValue);
				}
			}
		}
	}

	private Object findDs(EntityManager em, Object ds) {
		try {
			return em.find(ds.getClass(), ((DataSource) ds).getSourceID());
		} catch (Exception e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#getDataChannels(int, int)
	 */
	
	public synchronized DataChannelList getDataChannels(int numResults, int page)
			throws CaptureException {
		DataChannelList dChannelList = new DataChannelList();
		String order = " ORDER BY dc.updateDate descending";
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			LOGGER.info(" #### RUNNING QUERY : "
					+ "SELECT dc FROM DataChannel dc " + order);
			TypedQuery <DataChannel> q = em.createQuery("SELECT dc FROM DataChannel dc " + order, DataChannel.class)
					.setFirstResult((page - 1) * numResults)
					.setMaxResults(numResults);

			List <DataChannel> results = q.getResultList();
			if (results != null && !results.isEmpty()) {
				Iterator <DataChannel> iter = results.iterator();
				while (iter.hasNext()) {
					DataChannel dc = iter.next();					
					dChannelList.addDataChannel(dc);
				}
			}
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR recovering Data Channels from NO-SQL storage: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR recovering Data Channels from NO-SQL storage: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
		return dChannelList;
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#getUserProfile(java.lang.String)
	 */
	
	public synchronized TwitterUser getUserProfile(String userId) {
		TwitterUser uProf = null;
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			uProf = em.find(TwitterUser.class, userId);
			tx.commit();

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
		return uProf;
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#addUserProfile(atos.knowledgelab.capture.bean.TwitterUser)
	 */
	
	public synchronized void addUserProfile(TwitterUser user) throws CaptureException {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.merge(user);
			tx.commit();

		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction adding a new data channel: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction adding a new data channel: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#getTweetsFromDC(java.lang.String, int, int)
	 */
	
	public synchronized DcTweetList getTweetsFromDC(String dcId, int page, int numResults) throws CaptureException {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			DcTweetList ret = new DcTweetList();
			ret.setPage(page);
			ret.setPageSize(numResults);
			
			//List<Tweet> results = em.createQuery("SELECT tweet FROM Tweet tweet", Tweet.class).setFirstResult(page * numResults).setMaxResults(numResults).getResultList();		
			
			List<Tweet> results = new ArrayList<Tweet>();
			
			DataChannel dc = em.find(DataChannel.class, dcId);
			
			Iterator <DataSource> it = dc.getDataSources().iterator();
			
			while(it.hasNext()){
				DataSource ds = it.next();
				if(ds instanceof TwitterDataSource){
					TwitterDataSource tds = (TwitterDataSource)ds;
					results.addAll(tds.getTweets());
				}
			}
			
			if(results != null && results.size() > 0){
				ret.setTotalTweets(results.size());
				ret.setTweets(results);
			}
			else
				ret.setTotalTweets(0);
			
			tx.commit();
			
			return ret;
			
		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction listing tweets: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction listing tweets: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureStorageIf#getFacetTweetsFromDC(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	
//	public synchronized DcTweetList getFacetTweetsFromDC(String dcId, String typeDataSource,
//			String facetName, String value, int page, int numResults) throws CaptureException{
//		// TODO Auto-generated method stub
//		return null;
//	}

	/*private static List<Tweet> getTweetList(List<RawTweet> rawTweets){
		
		if(rawTweets == null)
			return null;
		
		LinkedList <Tweet> tweetResult = new LinkedList<Tweet>();
		Iterator <RawTweet> it = rawTweets.iterator();
		while(it.hasNext()){
			RawTweet auxRaw = it.next();
			Tweet auxTweet = new Tweet();
			auxTweet.setId(auxRaw.getTweetID());
			auxTweet.setRawJson(auxRaw.getJsonRawTweet());													
		}
		
		return tweetResult;
	}*/

	
	public synchronized void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID) throws CaptureException {
		
		if(tweetList == null || tweetList.getTweets() == null || tweetList.getTweets().isEmpty()){
			LOGGER.fine("No tweets to add to the data channel");
			return;
		}
		
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Iterator <Tweet> it = tweetList.getTweets().iterator();
			while(it.hasNext()){
				Tweet tweet = it.next();
				Tweet auxTweet = em.find(Tweet.class, tweet.getDataID());
				if(auxTweet != null)
					tweet = auxTweet;
				TwitterDataSource ds = em.find(TwitterDataSource.class,dsID);
				if(ds != null){					
					tweet.getDataSources().add(ds);					
					ds.getTweets().add(tweet);
					em.merge(ds);
				}
				em.merge(tweet);							
			}
			tx.commit();

		} catch (Exception e) {			
			LOGGER.severe(" ERROR in transaction adding tweets to a data channel: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction adding tweets to a data channel: "
							+ e.getMessage(), e);			
		} finally {
			if (tx.isActive())
				tx.rollback();
		}
		
	}

	
	public synchronized boolean isQueryActive(String queryID) throws CaptureException{
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query q = em.createQuery("SELECT DISTINCT dc FROM DataChannel dc, IN (dc.dataSources) ds WHERE ds.sourceID = :sourceID", DataChannel.class);
			q.setParameter("sourceID",queryID);
			DataChannel dc = (DataChannel) q.getSingleResult();
			tx.commit();
			
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
			Date endDate =  df.parse(dc.getEndCaptureDate());
						
			if (endDate.before(new Date()))
				return false;
			else
				return true;
			
		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction listing tweets: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction listing tweets: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
	}

	
	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMap() throws CaptureException {
		EntityTransaction tx = em.getTransaction();
		try {
			LinkedHashMap <String, QueryData> ret = new LinkedHashMap <String, QueryData>();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
			
			tx.begin();
			
			List <DataChannel> dcList = em.createQuery("SELECT dc FROM DataChannel dc", DataChannel.class).getResultList();
			
			Iterator <DataChannel> dcIt = dcList.iterator();
			
			while(dcIt.hasNext()){
				DataChannel dc = (DataChannel) dcIt.next();
								
				Date endDate =  df.parse(dc.getEndCaptureDate());				
				if (!endDate.before(new Date())){
					Iterator <DataSource> dsIt = dc.getDataSources().iterator();
					while(dsIt.hasNext()){
						DataSource ds = dsIt.next();
						if(ds instanceof TwitterDataSource){
							TwitterDataSource tds = (TwitterDataSource) ds;
							ret.put(tds.getSourceID(), new QueryData(dc.getChannelID(), tds.getSourceID(), dc.getType(), tds.getKeywords(), dc.getStartCaptureDate(), dc.getEndCaptureDate()));
						}
					}
				}
			}
			
			tx.commit();			
			return ret;
			
		} catch (Exception e) {
			LOGGER.severe(" ERROR in transaction getting active queries: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in transaction getting active queries: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive())
				tx.rollback();
		}
	}
	
	public synchronized LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType) throws CaptureException {
		//TODO develop method with dsType parameter
		return getActiveQueriesMap();
	}
	
	public VolumeResultList getVolumeFromDC(String dcId, String typeDataSource,
			String fInit, String fEnd, int parseInt) throws CaptureException {
		throw new CaptureException("Unnimplemented method");
	}

	@Override
	public DcTweetList getFacetTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults)
			throws CaptureException {
		throw new CaptureException("Unnimplemented method");

	}

//	@Override
//	public void indexOcurrences (List<TermOccurrenceAnalysis> ocurrenceList)
//			throws CaptureException {
//		// TODO Auto-generated method stub
//		throw new CaptureException("Unnimplemented method");
//	}

	@Override
	public void updateDataSource(QueryData query)
			throws CaptureException {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void indexVolumeOcurrences(
			List<VolumeOcurrenceAnalysis> ocurrenceList)
			throws CaptureException {
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
		LOGGER.log(Level.INFO, "Entramos en capture storage JPA");
		indexVolumeOcurrences(ocurrenceList);
		
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
