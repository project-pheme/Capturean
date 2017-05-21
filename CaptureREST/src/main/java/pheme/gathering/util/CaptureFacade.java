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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.ActiveQueriesList;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.WebIntentTwitter;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.QueryQueue;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;
import atos.knowledgelab.capture.persistence.JPAHBaseManager;
import atos.knowledgelab.capture.persistence.SolrManager;
import atos.knowledgelab.capture.util.CaptureConstants;
import twitter4j.OEmbed;

public class CaptureFacade implements CaptureFacadeIf {
	
	public CaptureFacade() throws Exception {
						
			hbaseJPA = JPAHBaseManager.getInstance();
			solr = SolrManager.getInstance();
			queryQueue = QueryQueue.getInstance();
			
	}

	private JPAHBaseManager hbaseJPA;
	private SolrManager solr; 
	private QueryQueue queryQueue;
	private final static Logger LOGGER = Logger.getLogger(CaptureFacade.class.getName());
	
	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#addDataChannel(atos.knowledgelab.capture.bean.DataChannel)
	 */
	@Override
	public String addDataChannel(DataChannel dataChannel) throws Exception {
		String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString();
		     	
		// Add dataChannelID, if needed
		if(dataChannel.getChannelID() == null){
			UUID uuid = UUID.randomUUID();
			dataChannel.setChannelID(uuid.toString());
		}
		// Add dataSourcesID, if needed
		if(dataChannel.getDataSources() != null && !dataChannel.getDataSources().isEmpty()){
			Iterator <DataSource> dsIt = dataChannel.getDataSources().iterator();
			while(dsIt.hasNext()){
				DataSource next = dsIt.next();
				if(next.getSourceID() == null)
					next.setSourceID(UUID.randomUUID().toString());
			}
		}
		    		
		// Add timestamps
		dataChannel.setCreationDate(timestamp);
		dataChannel.setUpdateDate(timestamp);
		
		checkDCConsistency(dataChannel); 

		//Insert in HBase    		
        hbaseJPA.addDataChannelToHBase(dataChannel);
		
        // Last version includes three global indexes: data_channels, tweets and user_profiles
        solr.addDataChannel(dataChannel);
        
        if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
	        // Update queries in queue
	        LOGGER.info(" #CaptureFacade: updating query Queue with new query ... ");
	        updateQueryQueue(dataChannel, false);
        } else if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
        	// Launch streaming capture process
        	LOGGER.info(" #CaptureFacade: launching streaming capture process ... ");
        	TwitterStreamQuerySolver.getInstance().startStream();
        }
        
        //Create a new Core in Solr - One core by DC (previous version) 
        //solr.addCore(uuid.toString());
        return dataChannel.getChannelID();
	}
	
	private void checkDCConsistency(DataChannel dataChannel) throws Exception {
		
		if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
		   if (!dataChannel.getDataSources().isEmpty() && dataChannel.getDataSources().size() > 1) {
			// The stream DC has more than one DS
			   throw new CaptureException(" ERROR in data channel specification. More that one data source has been included in the xml file. ");
		   } 
		   if (solr.getActiveStreamQuery()!=null) {
			   // An stream DC already exists
			   throw new CaptureException(" ERROR adding new data channel. There is already an stream DC defined and only one stream DC definition is allowed. ");
		   }
		   
		}
		
	}

	private void updateQueryQueue(DataChannel dataChannel, boolean wasDeleted) {
		List<DataSource> dSources = dataChannel.getDataSources();
		if (!dSources.isEmpty() && (isActive(dataChannel) || wasDeleted)) {

			Iterator <DataSource> itDs = dSources.iterator();
			while (itDs.hasNext()) {
				Object ds = itDs.next();
				if (ds instanceof TwitterDataSource) {
					TwitterDataSource tds = (TwitterDataSource) ds;
					String dsId = tds.getSourceID();
					if (wasDeleted) {
						queryQueue.deleteQueryToQueue(dsId);	
					} else {
						LOGGER.info(" #CaptureFacade: updating query Queue with new query = " + tds.getKeywords());
						queryQueue.addQueryToQueue(dsId, new QueryData(dataChannel.getChannelID(), dsId, dataChannel.getType(), tds.getKeywords(), dataChannel.getStartCaptureDate(), dataChannel.getEndCaptureDate()));
					}	
				}	
			}
		}
	}

	private boolean isActive(DataChannel dataChannel) {
		long timestamp = System.currentTimeMillis();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
	    Date date = null;
	    try {
	        date = format.parse(dataChannel.getEndCaptureDate());
	    } 
	    catch (ParseException ex) 
	    { 	    	
	       return false;
	    }
	    return date.after(new Date(timestamp));
			
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#updateDataChannel(atos.knowledgelab.capture.bean.DataChannel, java.lang.String)
	 */
	@Override
	public void updateDataChannel(DataChannel newDataChannel, String dcId) throws Exception {
		newDataChannel.setChannelID(dcId);
		hbaseJPA.updateDataChannelInHBase(newDataChannel);
		DataChannel dataChannel = hbaseJPA.getDataChannelFromHBase(dcId);
		solr.addDataChannel(dataChannel);
		
		if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
			// Update queries in queue
	        updateQueryQueue(dataChannel, false);
		} else if (dataChannel.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
        	// Launch streaming capture process
			LOGGER.info(" #CaptureFacade: update Stream DataChannel");
			TwitterStreamQuerySolver.getInstance().stopStream();
        	TwitterStreamQuerySolver.getInstance().startStream();
        }    
	}
	
	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#deleteDataChannel(java.lang.String)
	 */
	@Override
	public void deleteDataChannel(String dcId) throws Exception {
		DataChannel dcData = hbaseJPA.getDataChannelFromHBase(dcId);
		hbaseJPA.deleteDataChannelFromHBase(dcId);
		solr.deleteDataChannel(dcData);
		
		if (dcData.getType().equalsIgnoreCase(CaptureConstants.SEARCH)) {
			//Delete queries from queue
			updateQueryQueue(dcData, true);
		} else if (dcData.getType().equalsIgnoreCase(CaptureConstants.STREAM)) {
        	// Launch streaming capture process
			TwitterStreamQuerySolver.getInstance().stopStream();
		}	
	}
	
	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#getTweetsFromDC(java.lang.String, int, int)
	 */
	@Override
	public DcTweetList getTweetsFromDC (String dcId, int page, int numResults) throws Exception {
		return solr.getTweetsFromDC(dcId, page, numResults);
	}
	
	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#getFacetTweetsFromDC(java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int)
	 */
	@Override
//	public DcTweetList getFacetTweetsFromDC (String dcId, String typeDataSource, String facetName, String value, int page, int numResults) throws Exception {
//		return solr.getFacetTweetsFromDC(dcId, typeDataSource, facetName, value, page, numResults);
//	}
	
	public DcTweetList getFacetTweets (String dcId, String typeDataSource, String filterExpressionRw, String sorter, String mode, String fields, int page, int numResults) throws Exception{
		return solr.getFacetTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, page, numResults);
	}
	
	public VolumeResultList getVolumeFromDC (String dcId, String typeDataSource, String fInit, String fEnd, int size) throws Exception {
		return solr.getVolumeFromDC (dcId, typeDataSource, fInit, fEnd, size);
	}
	
	public DataChannel getDataChannel(String dcId) throws CaptureException {
		return hbaseJPA.getDataChannelFromHBase(dcId);
	}
	
	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#getDataChannels(int, int)
	 */
	@Override
	public DataChannelList getDataChannels(int numResults, int page) throws Exception {
		return hbaseJPA.getDataChannelsFromHBase(numResults, page);
	}	

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#getTwitterUser(java.lang.String)
	 */
	@Override
	public TwitterUser getTwitterUser( String userId) throws Exception {
		return hbaseJPA.getUserProfile(userId);
	}

	/* (non-Javadoc)
	 * @see pheme.gathering.util.CaptureFacadeIf#getActiveQueries()
	 */
	@Override
	public ActiveQueriesList getActiveQueries() {		
		ActiveQueriesList queries = new ActiveQueriesList();
		Set<Entry<String, QueryData>> querySet =  queryQueue.getAllQueriesOnQueue();
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

	@Override
	public void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID, QueryData keywords) throws Exception {
		
		long hbaseStart = System.currentTimeMillis(); 
		hbaseJPA.addTweetsToDataChannel(tweetList, dcID, dsID);
		long hbaseElapsedTime = System.currentTimeMillis() - hbaseStart;
		LOGGER.info("Hbase writing time: " + hbaseElapsedTime/1000F + " s");
		
		//Indexing Tweets
		//simple profiling method
		long solrStart = System.currentTimeMillis();    
		solr.indexTweets(tweetList, keywords);
		long solrElapsedTime = System.currentTimeMillis() - solrStart;
		LOGGER.info("Solr writing time: " + solrElapsedTime/1000F + " s");
		
	}

	@Override
	public QueryQueue getQueryQueue() {
		
		return this.queryQueue;
	}

	@Override
	public boolean isQueryActive(String queryID) throws Exception {
		
		return solr.isQueryActive(queryID);
	}

	@Override
	public LinkedHashMap<String, QueryData> getActiveQueriesMap() throws Exception {
		return solr.getActiveQueries();
	}
	
	@Override
	public LinkedHashMap<String, QueryData> getActiveQueriesMapByDSType(String dsType) throws Exception {
		return solr.getActiveQueriesByDSType(dsType);
	}

	@Override
	public void setStorage(CaptureStorageIf cs) throws Exception {
		LOGGER.warning("CaptureFacade does not use CaptureStorageIf, your module should not be used!");
		//throw new CaptureException("Unimplemented method");
		
	}

	@Override
	public void updateDataSource(QueryData query) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DcTweetList getTweetsFromDC(String dcId, String lastId, int numResults, boolean reverse) throws Exception {
		// TODO Auto-generated method stub
		LOGGER.severe("The method getTweetsFromDC(String dcId, String lastId, int numResults, boolean reverse)"
				+ "is not implemented for this facade!");
		return null;
	}

	@Override
	public DataPoolList getDataPools(int numResults, String fromKey) {
		// TODO Auto-generated method stub
		LOGGER.severe("This method is not implemented for this facade!");
		return null;
	}

	@Override
	public DataPool getDataPool(String dpId) {
		// TODO Auto-generated method stub
		LOGGER.severe("This method is not implemented for this facade!");
		return null;
	}

	@Override
	public Map<String, TweetThread> getTweetThread(List<String> tweetIdList) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, WebIntentTwitter> getInfluential (String userId, Date date, String top) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addDataPool(DataPool dataPool) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateDataPool(DataPool newDataPool, String dpId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteDataPool(String dpId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryData getActiveStreamQuery() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DcTweetList getFacetWithNoFqTweets(String dcId, String typeDataSource, String filterExpressionRw,
			String sorter, String mode, String fields, int page, int numResults) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, WebIntentTwitter> getMemberList(String owner, String listId) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
