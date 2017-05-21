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
package pheme.gathering.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import pheme.gathering.search.TweetManager;
import atos.knowledgelab.capture.bean.ActiveQueriesList;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.QueryQueue;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.WebIntentTwitter;
import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize;
import atos.knowledgelab.capture.util.BrandManagementConstants;
import atos.knowledgelab.capture.util.CaptureConstants;

public class CaptureGenericFacade implements CaptureFacadeIf {

	private static CaptureGenericFacade instance;

	private final static Logger LOGGER = Logger
			.getLogger(CaptureGenericFacade.class.getName());

	private QueryQueue queryQueue;

	private CaptureStorageIf cs = null;
	
	private StreamProducer sp = null;

	CaptureGenericFacade() throws Exception {
		queryQueue = QueryQueue.getInstance();

		initKafka("kafka-search.properties");
	}

	@Override
	public String addDataChannel(DataChannel dChannel) throws Exception {

		String timestamp = new java.sql.Timestamp(
				new java.util.Date().getTime()).toString();

		// Add dataChannelID, if needed
		if (dChannel.getChannelID() == null) {
			UUID uuid = UUID.randomUUID();
			dChannel.setChannelID(uuid.toString());
		}
		// Add dataSourcesID, if needed
		if (dChannel.getDataSources() != null
				&& !dChannel.getDataSources().isEmpty()) {
			Iterator<DataSource> dsIt = dChannel.getDataSources().iterator();
			while (dsIt.hasNext()) {
				DataSource next = dsIt.next();
				if (next.getSourceID() == null)
					next.setSourceID(UUID.randomUUID().toString());
			}
		}

		// Add timestamps
		dChannel.setCreationDate(timestamp);
		dChannel.setUpdateDate(timestamp);

		checkDCConsistency(dChannel);

		// Insert in HBase
		cs.storeDataChannel(dChannel);

		if (dChannel.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
			// Update queries in queue
			// LOGGER.info(" #CaptureFacade: updating query Queue with new query ... ");
			updateQueryQueue(dChannel, false);
		} else if (dChannel.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
			// Launch streaming capture process
			LOGGER.info(" #CaptureFacade: launching streaming capture process ... ");
			TwitterStreamQuerySolver.getInstance().startStream();
		}

		return dChannel.getChannelID();

	}

	@Override
	public void updateDataChannel(DataChannel newDataChannel, String dcId)
			throws Exception {
		newDataChannel.setChannelID(dcId);
		
		// Add dataSourcesID, if needed
		// (In case when we MODIFY DC and ADD a new DS)
		if (newDataChannel.getDataSources() != null
				&& !newDataChannel.getDataSources().isEmpty()) {
			Iterator<DataSource> dsIt = newDataChannel.getDataSources().iterator();
			while (dsIt.hasNext()) {
				DataSource next = dsIt.next();
				
				if (next.getSourceID() == null){
					next.setSourceID(UUID.randomUUID().toString());
				}
				// When update a datachannel and set the chronologicalOrder flag a true, we must reset the LastTweetId in order to get from the newest one and
				// and not capturing from a former lastTweetID.
				if (next instanceof TwitterDataSource) {
					TwitterDataSource tds = (TwitterDataSource) next;
					if (tds.getChronologicalOrder() || !tds.getFromLastTweetId()){
						tds.setLastTweetId(0);
					}
					LOGGER.info(" #El last tweet ID en Capture generic facade: " + tds.getLastTweetId() + 
							" DC: " + newDataChannel.getChannelID() +
							" DS: " + next.getSourceID());
				}
			}
		}
		
		cs.updateDataChannel(newDataChannel);
		DataChannel dataChannel = getDataChannel(dcId);

		if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
			// Update queries in queue
			updateQueryQueue(dataChannel, false);
		} else if (dataChannel.getType().equalsIgnoreCase(
				CaptureConstants.STREAM)) {
			// Launch streaming capture process
			LOGGER.info(" #CaptureFacade: update Stream DataChannel");
			TwitterStreamQuerySolver.getInstance().stopStream();
			TwitterStreamQuerySolver.getInstance().startStream();
		}
	}

	@Override
	public void deleteDataChannel(String dcId) throws Exception {
		DataChannel dcData = getDataChannel(dcId);
		cs.deleteDataChannel(dcData);

		if (dcData.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
			// Delete queries from queue
			updateQueryQueue(dcData, true);
		} else if (dcData.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
			// Launch streaming capture process
			TwitterStreamQuerySolver.getInstance().stopStream();
		}
	}

	@Override
	public DcTweetList getTweetsFromDC(String dcId, int page, int numResults)
			throws Exception {
		return cs.getTweetsFromDC(dcId, page, numResults);
	}

//	@Override
//	public DcTweetList getFacetTweetsFromDC(String dcId, String typeDataSource,
//			String facetName, String value, int page, int numResults)
//			throws Exception {
//		return cs.getFacetTweetsFromDC(dcId, typeDataSource, facetName, value,
//				page, numResults);
//	}

	@Override
	public DataChannel getDataChannel(String dcId) throws CaptureException {
		return cs.getDataChannel(dcId);
	}

	@Override
	public DataChannelList getDataChannels(int numResults, int page)
			throws Exception {
		return cs.getDataChannels(numResults, page);
	}

	@Override
	public TwitterUser getTwitterUser(String userId) throws Exception {
		TwitterUser user = cs.getUserProfile(userId);
		if (user == null) {
			user = TweetManager.getInstance().getUserProfile(userId);
			cs.addUserProfile(user);
		}
		return user;
	}

	@Override
	public ActiveQueriesList getActiveQueries() {
		ActiveQueriesList queries = new ActiveQueriesList();
		Set<Entry<String, QueryData>> querySet = queryQueue
				.getAllQueriesOnQueue();
		int i = 1;
		if (!querySet.isEmpty()) {
			Iterator<Entry<String, QueryData>> itQ = querySet.iterator();
			while (itQ.hasNext()) {
				QueryData query = itQ.next().getValue();
				query.setPositioninQueue(i);
				queries.addQuery(query);
				i++;
			}
			queries.setQueryInExecution(queryQueue.getQueryOnExecution());
		}
		return queries;
	}

	private void updateQueryQueue(DataChannel dataChannel, boolean wasDeleted) {
		List<DataSource> dSources = dataChannel.getDataSources();
		if (!dSources.isEmpty() && (isActive(dataChannel) || wasDeleted)) {

			Iterator<DataSource> itDs = dSources.iterator();
			while (itDs.hasNext()) {
				Object ds = itDs.next();
				if (ds instanceof TwitterDataSource) {
					TwitterDataSource tds = (TwitterDataSource) ds;
					String dsId = tds.getSourceID();
					if (wasDeleted) {
						queryQueue.deleteQueryToQueue(dsId);
					} else {
						LOGGER.info(" #CaptureFacade: updating query Queue with new query = "
								+ tds.getKeywords());
						QueryData query = new QueryData(
								dataChannel.getChannelID(), 
								dsId,
								dataChannel.getType(), 
								tds.getKeywords(), 
								dataChannel.getStartCaptureDate(),
								dataChannel.getEndCaptureDate());
						
						query.setFromLastID(tds.getFromLastTweetId());
						query.setLastID(tds.getLastTweetId());
						query.setChronologicalOrder(tds.getChronologicalOrder());
						query.setHistoricalLimit(tds.getHistoricalLimit());

						queryQueue.addQueryToQueue(
								dsId,
								query);
					}
				}
			}
		}
	}
	
	@Override
	public void updateDataSource(QueryData query)
			throws Exception {
		cs.updateDataSource(query);
	

	}

	private boolean isActive(DataChannel dataChannel) {
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSSSSS");
		Date date = null;
		try {
			date = format.parse(dataChannel.getEndCaptureDate());
		} catch (ParseException ex) {
			return false;
		}
		return date.after(new Date(timestamp));

	}

	private void checkDCConsistency(DataChannel dataChannel) throws Exception {

		if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
			if (!dataChannel.getDataSources().isEmpty()
					&& dataChannel.getDataSources().size() > 1) {
				// The stream DC has more than one DS
				throw new CaptureException(
						" ERROR in data channel specification. More that one data source has been included in the xml file. ");
			}
		}
	}

	@Override
	public void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID, QueryData keywords) throws Exception {

		Iterator<Tweet> it = tweetList.getTweets().iterator();
		
		while (it.hasNext()) {
			Tweet tweet = it.next();
						
			StreamItem si = new StreamItem();
			si.setDataChannelId(dcID);
			si.setDataSourceId(dsID);

			// Ojo que aqui hay parametros de la query que no se estan actualizando y por lo tanto se ponen a reset, no se utilizan, pero por saberlo.
			QueryData qd = new QueryData();
			qd.setDcID(dcID);
			qd.setDsID(dsID);

			si.setQueryData(qd);

			// Set the tweet into the StreamItem
			si.setTweet(tweet);

			try {							
				sp.send(si);
			} catch (Exception e) {
				throw new CaptureException(
						"Error while sending tweet message to kafka: "
								+ e.getMessage(), e);
			}

		}
	}

	@Override
	public QueryQueue getQueryQueue() {
		return this.queryQueue;
	}

	public static CaptureGenericFacade getInstance() throws Exception {
		if (instance == null)
			instance = new CaptureGenericFacade();
		return instance;
	}

	@Override
	public boolean isQueryActive(String queryID) throws Exception {
		return cs.isQueryActive(queryID);
	}

	@Override
	public LinkedHashMap<String, QueryData> getActiveQueriesMap()
			throws Exception {
		return cs.getActiveQueriesMap();
	}
	
	@Override
	public LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType)
			throws Exception {
		return cs.getActiveQueriesMapByDSType(dsType);
	}

//	@Override
//	public VolumeResultList getVolumeFromDC(String dcId, String typeDataSource,
//			String fInit, String fEnd, int parseInt) throws Exception {
//		return cs.getVolumeFromDC(dcId, typeDataSource, fInit, fEnd, parseInt);
//	}

	@Override
	public void setStorage(CaptureStorageIf cs) throws Exception {
		this.cs = cs;
	}

	@Override
	public DcTweetList getFacetTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException{
		return cs.getFacetTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);

	}
	
	@Override
	public DcTweetList getFacetWithNoFqTweets(String dcId, String typeDataSource,
			String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws CaptureException{
		return cs.getFacetWithNoFqTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);

	}
	
	private void initKafka(String kafkaPF) throws Exception {
		  // Kafka producer properties 
		  Properties props = new Properties();
		  props.load(this.getClass().getClassLoader().getResourceAsStream(kafkaPF));
		  
		  props.put("metadata.broker.list",  props.getProperty("metadata.broker.list"));
		  props.put("serializer.class",  props.getProperty("serializer.class"));
		  props.put("request.required.acks",  props.getProperty("request.required.acks"));
		  //props.put("partitioner.class", props.getProperty("partitioner.class"));
		  
		  //ProducerConfig config = new ProducerConfig(props);
		  //kafkaProducer = new Producer<String, String>(config);

		  
		  StreamProducerConfig conf = new StreamProducerConfig();
		  
		  // TODO: load configuration from file?
		  // props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));

		  //Here it is important to specify the broker (kafka node or nodes)
		  //more details here: http://kafka.apache.org/08/configuration.html
		  props.load(this.getClass().getClassLoader().getResourceAsStream(kafkaPF));
		  
		  conf.put("metadata.broker.list",  props.getProperty("metadata.broker.list"));
		  conf.put("serializer.class",  props.getProperty("serializer.class"));
		  conf.put("request.required.acks",  props.getProperty("request.required.acks"));
		  conf.put("metadata.broker.list", props.getProperty("metadata.broker.list"));
		  //conf.put("zookeeper.connect", "localhost:2181");
		  conf.put("kafka.topic", props.getProperty("kafka.topic"));  
		  
		  ISerialize<StreamItem> serializer = new StreamItemSerialize();		  
		  sp = new StreamProducer(conf, serializer);   
		}

	@Override
	public DcTweetList getTweetsFromDC(String dcId, String lastId, int numResults, boolean reverse) throws Exception {
		// TODO Auto-generated method stub
		return cs.getTweetsFromDC(dcId, lastId, numResults, reverse);
		
	}
	
	@Override
	public DataPoolList getDataPools(int numResults, String fromKey) throws Exception {
		return cs.getDataPools(numResults, fromKey);
	}

	@Override
	public DataPool getDataPool(String dpId) throws CaptureException, IOException {
		return cs.getDataPool(dpId);
	}
	
	//@Override
	
	public Map<String, WebIntentTwitter> getInfluential (String userId, Date date, String top) throws Exception {
		if (BrandManagementConstants.MOST_INFLUENCIAL_INFLUENCERS.equals(top))
			return TweetManager.getInstance().getWebIntentUser (cs.getTwitterInfluential (userId, date, top));
		else
			return TweetManager.getInstance().getWebIntentTimeline(cs.getTwitterInfluential (userId, date, top));
	}
	
	//@Override
	public Map<String, TweetThread> getTweetThread(List<String> tweetIdList) throws Exception {
		return TweetManager.getInstance().getTweetThread(tweetIdList);
	}

	@Override
	public String addDataPool(DataPool dataPool) throws Exception {
		String timestamp = new java.sql.Timestamp(
				new java.util.Date().getTime()).toString();

		// Add dataPoolID, if needed
		if (dataPool.getPoolID() == null) {
			UUID uuid = UUID.randomUUID();
			dataPool.setPoolID(uuid.toString());
		}

		// Insert in HBase
		cs.storeDataPool(dataPool);

		return dataPool.getPoolID();
	}

	@Override
	public void updateDataPool(DataPool newDataPool, String dpId) throws Exception {
		newDataPool.setPoolID(dpId);
		cs.updateDataPool(newDataPool);	
	}

	@Override
	public void deleteDataPool(String dpId) throws Exception {
		DataPool dataPool = getDataPool(dpId);
		cs.deleteDataPool(dataPool);
		
	}

	@Override
	public QueryData getActiveStreamQuery() throws Exception {
		
		return cs.getActiveStreamQuery();
	}

	@Override
	public Map<String, WebIntentTwitter> getMemberList(String owner, String listId) throws Exception {
		// TODO Auto-generated method stub
		return TweetManager.getInstance().getMemberList (owner, listId);
	}


}
