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
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.persistence.CollectionSerializer;
import atos.knowledgelab.capture.util.CaptureConstants;
import atos.knowledgelab.capture.util.TopEntryComparator;

import com.thoughtworks.xstream.XStream;

// Las fechas en twitter (ISO 8601 timezone): 2015-05-01T21:40:54+02:00
// Las fechas como se guardan en solr (UTZ): 2015-05-01T19:40:54Z --> por tanto las fechas que vengan en cualquier query con fechas tiene que venir en UTC

public class SolrManagerFast {
	
	private static SolrManagerFast instance = null;
	private final static Logger LOGGER = Logger.getLogger(SolrManagerFast.class.getName());
	
	private final static String DC_INDEX = "dcIndex";
	private final static String TWEETS_INDEX = "tweetsIndex";
	private final static String USERS_INDEX = "usersIndex";
	private final static String AGGREGATES_BATCH_INDEX = "aggregatesBatchIndex";
	private static final String HTTP_PREFIX = "http://";
	
	
	String dcIndex;
	String tweetsIndex;
	String usersIndex; 
	String logsFile;
	String termsIndex;
	String aggregatesBatchIndex;


	protected SolrManagerFast() throws Exception {
		Properties properties = new Properties();
		
		try {
			//Loading Solr properties
			properties.load(this.getClass().getClassLoader().getResourceAsStream("solrCapture.properties"));
			String solrServer = properties.getProperty("solr.server");
			dcIndex = properties.getProperty("solr.index.dc-fast");
			tweetsIndex = properties.getProperty("solr.index.tweets-fast");
			usersIndex = properties.getProperty("capture-twitter-users");
			logsFile = properties.getProperty("solr.logFile");
			aggregatesBatchIndex = properties.getProperty("solr.index.batch");
		    
			SolrClient solrDC = initSolrIndex(HTTP_PREFIX + solrServer + "/" + dcIndex);			    		    
			SolrClient solrTW = initSolrIndex(HTTP_PREFIX + solrServer + "/" + tweetsIndex);
			SolrClient solrUS = initSolrIndex(HTTP_PREFIX + solrServer + "/" + usersIndex);
			SolrClient solrAGGBAT = initSolrIndex(HTTP_PREFIX + solrServer + "/" + aggregatesBatchIndex);
			
			solrServerPool.put(DC_INDEX, solrDC);
			solrServerPool.put(TWEETS_INDEX, solrTW);
			solrServerPool.put(USERS_INDEX, solrUS);
			solrServerPool.put(AGGREGATES_BATCH_INDEX, solrAGGBAT);
			
		    /*List<String> dcIds = jpaHbase.getDataChannelIdsFromHBase();
		    if (!dcIds.isEmpty()) {
			    Iterator<String> itIds = dcIds.iterator();
			    while (itIds.hasNext()) {
			       String dcId = itIds.next();			       
			       EmbeddedSolrServer server = new EmbeddedSolrServer( container, dcId );
			       solrServerPool.put(dcId, server);
			    }   
		    }*/
			
			
		} catch (Exception e) {
			LOGGER.severe(" ERROR Creating solr conection pool - " + e.getMessage());			
		}
	}
	
	//Singleton Pattern
	public static SolrManagerFast getInstance() throws Exception {
	  if(instance == null) {
	     instance = new SolrManagerFast();         
	  }
	  return instance;
	}
	
	private SolrClient initSolrIndex(String serverUrl) throws MalformedURLException,
	  SolrServerException, IOException {
	  HttpSolrClient server = new HttpSolrClient(serverUrl);
	  //server.setMaxRetries(1); // defaults to 0.  > 1 not recommended.
	  server.setConnectionTimeout(15000); // 5 seconds to establish TCP
	  // Setting the XML response parser is only required for cross
	  // version compatibility and only when one side is 1.4.1 or
	  // earlier and the other side is 3.1 or later.
	  server.setParser(new XMLResponseParser()); // binary parser is used by default
	  // The following settings are provided here for completeness.
	  // They will not normally be required, and should only be used 
	  // after consulting javadocs to know whether they are truly required.
	  server.setSoTimeout(40000);  // socket read timeout
	  server.setDefaultMaxConnectionsPerHost(100);
	  server.setMaxTotalConnections(100);
	  server.setFollowRedirects(false);  // defaults to false
	  // allowCompression defaults to false.
	  // Server side must support gzip or deflate for this to have any effect.

	  server.setParser(new XMLResponseParser());
	  return server;
	}
		
	private Map<String, SolrClient> solrServerPool = new HashMap<String, SolrClient>();	
		
    public SolrClient getSolrServerConection (String dcId) {    	
    	return solrServerPool.get(dcId);
    }
    
    public SolrClient getSolrCoreServer () {    	
    	return solrServerPool.get("0");
    }
    
    
    /*public EmbeddedSolrServer addCore (String dcId) throws IOException {    	
   
    	EmbeddedSolrServer coreServer = solrServerPool.get("0");
    	//File home = new File(scfg.getIndexHome());
    	//File solr = new File(home, "solr.xml");
    	String corename = "capture-" + dcId;
    	//CoreContainer container = new CoreContainer(home, solr);
    	//create the directory
    	File f = new File(coreServer.getCoreContainer().getSolrHome(), corename + "/conf"); 
    	f.mkdirs();
    	
    	//Elevate Queries file    	    	
    	File is = new File(this.getClass().getClassLoader().getResource("elevate.xml").getFile());
        File output = new File(coreServer.getCoreContainer().getSolrHome()+"/" + corename + "/conf", "elevate.xml");        
        is.renameTo(output);

    	//SolrServer server = new EmbeddedSolrServer(container, "empty"); //default
    	//EmbeddedSolrServer newServer = new EmbeddedSolrServer(coreServer.getCoreContainer(), corename);
    	//create the core
    	try {    		    		
			CoreAdminRequest.createCore(corename, corename, coreServer, "solrconfig.xml", "schema.xml");		    		
			CoreAdminRequest.persist("solr.xml", coreServer);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
    	EmbeddedSolrServer newServer = new EmbeddedSolrServer(coreServer.getCoreContainer(), corename);
    	solrServerPool.put(dcId, newServer);
    	return newServer;
    }*/
    
    
    /**
     * Important changes:
     * Now we don't index some fields, such as JSON source of tweet, user description
     * 
     * 
     * @param tweetList
     * @param queryData
     * @throws CaptureException
     */
	public void indexTweets(TweetList tweetList, QueryData queryData) throws CaptureException {
		SolrClient solr = this.solrServerPool.get(TWEETS_INDEX);
		SolrInputDocument tweetData;
		Collection<SolrInputDocument> tweetDataCollection = null;
		
		
    	/* Solr fields*/
		if (tweetList!=null && !tweetList.getTweets().isEmpty()) {
			Tweet tweet =null;
			try {
				Iterator<Tweet> itTweets = tweetList.getTweets().iterator();
		    	
		    	tweetDataCollection = new ArrayList<SolrInputDocument>();
				  
				while (itTweets.hasNext()) {
				
		    	  tweet = itTweets.next();		    	  
		    	  tweetData = new SolrInputDocument();
		    	  
		    	  //Build Solr Tweet Doc
		    	  tweetData.addField(CaptureConstants.TWEET_ID, tweet.getId());
		    	  tweetData.addField(CaptureConstants.TEXT, tweet.getText());
		    	  tweetData.addField(CaptureConstants.USER_ID, tweet.getUserID()!=null ? tweet.getUserID(): "");
		    	  //tweetData.addField(CaptureConstants.SOURCE, tweet.getSource()!=null ? tweet.getSource(): "");
		    	  tweetData.addField(CaptureConstants.USER_SCREEN_NAME, tweet.getUserScreenName()!=null ? tweet.getUserScreenName(): "");
		    	  //tweetData.addField(CaptureConstants.USER_NAME, tweet.getUserName()!=null ? tweet.getUserName(): "");
		    	  //tweetData.addField(CaptureConstants.USER_DESCRIPTION, tweet.getUserDescription()!=null ? tweet.getUserDescription(): "");
		    	  //Additional features of tweets (from classification module "sentiment-classificator")
		    	  if (tweet.getSentiment()!=null) {
			    	  tweetData.addField(CaptureConstants.FEATURE_SENTIMENT, tweet.getSentiment());

		    	  }
		    	  if (tweet.getStress()!=null) {
			    	  tweetData.addField(CaptureConstants.FEATURE_STRESS, tweet.getStress());

		    	  }
		    	  if (tweet.getDangerousness()!=null) {
			    	  tweetData.addField(CaptureConstants.FEATURE_DANGEROUSNESS, tweet.getDangerousness());

		    	  }
		    	  
		    	  if (tweet.getCreatedAt()!=null) {
		    		  tweetData.addField(CaptureConstants.CREATED_AT, tweet.getCreatedAt());
		    	  }
		    	  
		    	  tweetData.addField(CaptureConstants.IN_REPLY, tweet.getInReplyToId()!=null ? tweet.getInReplyToId(): "");
		    	  tweetData.addField(CaptureConstants.ORIGINAL_TWEET, tweet.getOriginalTweetId()!=null ? tweet.getOriginalTweetId(): "");
		    	  
		    	  if (tweet.getRetweetCount()!=null) {
		    		  tweetData.addField(CaptureConstants.RETWEETS, tweet.getRetweetCount());
		    	  }
		    	  if (tweet.getFavouriteCount()!=null) {
		    		  tweetData.addField(CaptureConstants.FAV_COUNT, tweet.getFavouriteCount());
		    	  }
		    	  		    	  
		    	  tweetData.addField(CaptureConstants.HASHTAGS, tweet.getHashTags()!=null ? tweet.getHashTags(): "");
		    	  tweetData.addField(CaptureConstants.SOURCE_URLS, tweet.getSourceUrls()!=null ? tweet.getSourceUrls(): "");
		    	  tweetData.addField(CaptureConstants.PLACE, tweet.getPlace()!=null ? tweet.getPlace(): "");
		    	  
		    	  if (tweet.getUserFollowers()!=null) {
		    		  tweetData.addField(CaptureConstants.FOLLOWERS, tweet.getUserFollowers());
		    	  }
		    	  if (tweet.getUserFollowes()!=null) {
		    		  tweetData.addField(CaptureConstants.FRIENDS, tweet.getUserFollowes());
		    	  }
		    	  
		    	  tweetData.addField(CaptureConstants.LATITUDE, tweet.getLatitude()!=null ? tweet.getLatitude(): "");
		    	  tweetData.addField(CaptureConstants.LONGITUDE, tweet.getLongitude()!=null ? tweet.getLongitude(): "");		    	 
		    	  //tweetData.addField(CaptureConstants.JSON, tweet.getRawJson()!=null ? tweet.getRawJson(): "");
		    	  
		    	  // New schema: add a new field latLong
		    	  if (tweet.getLatLong()!=null) {
		    		  tweetData.addField(CaptureConstants.LAT_LONG, tweet.getLatLong());
		    	  }
		    	  
		    	  //NEW: dcID in the dc-tweet-fast core.
		    	  tweetData.addField(CaptureConstants.DC_TWEET_ID, queryData.getDcID() + "-" + tweet.getId());

		    	  //We need also a uniqueKey, because we want to update Tweet details
		    	  tweetData.addField(CaptureConstants.DC_ID, queryData.getDcID());
		    	  tweetData.addField(CaptureConstants.TYPE_DS, CaptureConstants.TWITTER);
		    	  
		    	  // New schema: add a new field latLong
		    	  if (tweet.getLang()!=null) {
		    		  tweetData.addField(CaptureConstants.LANG, tweet.getLang());
		    	  }
		    	  
		    	  tweetDataCollection.add(tweetData);

		    	} 
				//collection based commit
				//we add the whole collection at once and perform commit.
				solr.add(tweetDataCollection);				
				solr.commit();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, " ERROR adding/updating new Tweet to Solr", e);
				
				try {
					CollectionSerializer.serializeToFile((ArrayList<SolrInputDocument>) tweetDataCollection, logsFile);
				} catch (IOException e1) {					
					LOGGER.log(Level.SEVERE, " ERROR serializing the tweetDataCollection: " + e1.getMessage(), e1);					
				}
				
				try {
					solr.rollback();
				} catch (Exception ex) {
					LOGGER.log(Level.ALL, "ERROR in rollback", e);

					throw new CaptureException("ERROR in rollback adding/updating new Tweet to Solr", e);
				}	
				
				//LOGGER_fail.info(tweet.getRawJson());
				//LOGGER.severe(" ERROR adding/updating new Tweet to Solr: " + e.getMessage());
				

				throw new CaptureException("ERROR adding/updating new Tweet to Solr", e);
			}
		}

	}
	
	public LinkedHashMap<String, QueryData> getActiveQueries() throws Exception {
	   	
		 LinkedHashMap<String, QueryData> activeQueries = new LinkedHashMap<String, QueryData>(); 
		 String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString().replace(' ', 'T') + "Z" ;
		 
		 String query = CaptureConstants.TYPE + ":" + CaptureConstants.SEARCH + " AND " + CaptureConstants.CAPTURE_END_DATE + ": ["+ timestamp +" TO *]";	
		 
		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 solrParams.set("q", query);
		 solrParams.set("sort", CaptureConstants.CAPTURE_START_DATE + " asc");
		 solrParams.set("rows", 100000000);
		 List<QueryData> queries = this.solrServerPool.get(SolrManagerFast.DC_INDEX).query(solrParams).getBeans(QueryData.class);
		 LOGGER.info(" #ACTIVE QUERIES: " + queries.size());
		 if (!queries.isEmpty()) {
			 Iterator<QueryData> itQ = queries.iterator();
			 while (itQ.hasNext()) {
				 QueryData queryObj = itQ.next();
				 try {
					 queryObj.setLastID(Long.parseLong(queryObj.getLastTweetID()));
				 } catch (NumberFormatException e) {
					 LOGGER.info("Last tweet id parse error, default to 0");
					 queryObj.setFromLastID(false);
				 }
				 LOGGER.info(" #ACTIVE QUERY: " + queryObj.getDsID() + "/" + queryObj.getQuery());
				 activeQueries.put(queryObj.getDsID(), queryObj);
			 }
		 }
	     return activeQueries;
	}
	
	public LinkedHashMap<String, QueryData> getActiveQueriesByDSType(String dsType) throws Exception {
	   	
		 LinkedHashMap<String, QueryData> activeQueries = new LinkedHashMap<String, QueryData>(); 
		 String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString().replace(' ', 'T') + "Z" ;
		 
		 LOGGER.info(" #DS type: " + dsType);
		 String query = CaptureConstants.TYPE + ":" + CaptureConstants.SEARCH + " AND " + CaptureConstants.TYPE_DS + ":" + dsType + " AND " + CaptureConstants.CAPTURE_END_DATE + ": ["+ timestamp +" TO *]";	

		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 solrParams.set("q", query);
		 solrParams.set("sort", CaptureConstants.CAPTURE_START_DATE + " asc");
		 solrParams.set("rows", 100000000);
		 List<QueryData> queries = this.solrServerPool.get(SolrManagerFast.DC_INDEX).query(solrParams).getBeans(QueryData.class);
		 LOGGER.info(" #ACTIVE QUERIES: " + queries.size());
		 if (!queries.isEmpty()) {
			 Iterator<QueryData> itQ = queries.iterator();
			 while (itQ.hasNext()) {
				 QueryData queryObj = itQ.next();
				 try {
					 queryObj.setLastID(Long.parseLong(queryObj.getLastTweetID()));
				 } catch (NumberFormatException e) {
					 LOGGER.info("Last tweet id parse error, default to 0");
					 queryObj.setFromLastID(false);
				 }
				 LOGGER.info(" #ACTIVE QUERY: " + queryObj.getDsID() + "/" + queryObj.getQuery());
				 activeQueries.put(queryObj.getDsID(), queryObj);
			 }
		 }
	     return activeQueries;
	}
	
	public QueryData getActiveQuery(String id) throws Exception {
	   	
		 String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString().replace(' ', 'T') + "Z" ;
		 
		 String query = CaptureConstants.DS_ID + ":" + id + " AND " + CaptureConstants.CAPTURE_END_DATE + ": ["+ timestamp +" TO *]";	
		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 solrParams.set("q", query);
		 solrParams.set("sort", CaptureConstants.CAPTURE_START_DATE + " asc");
		 QueryResponse response = solrServerPool.get(DC_INDEX).query(solrParams);
		 return response.getResults().getNumFound() > 0 ? response.getBeans(QueryData.class).get(0) : null;	     
	}
	
	public QueryData getActiveStreamQuery() throws Exception {
	   	
		 String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString().replace(' ', 'T') + "Z" ;
		 
		 String query = CaptureConstants.TYPE + ":" + CaptureConstants.STREAM + " AND " + CaptureConstants.CAPTURE_END_DATE + ": ["+ timestamp +" TO *]";	
		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 solrParams.set("q", query);
		 solrParams.set("sort", CaptureConstants.CAPTURE_START_DATE + " asc");
		 solrParams.set("sort", "_version_" + " desc");
		 QueryResponse response = solrServerPool.get(SolrManagerFast.DC_INDEX).query(solrParams);
		 return response.getResults().getNumFound() > 0 ? response.getBeans(QueryData.class).get(0) : null;	     
	}

	public String addDataChannel(DataChannel dataChannel) throws CaptureException {
		SolrClient solr = this.solrServerPool.get(DC_INDEX);
		SolrInputDocument dcData;
		String composedID = "";
		
    	/* Solr fields*/
		if (dataChannel!=null && !dataChannel.getDataSources().isEmpty()) {
			try {
				Iterator<DataSource> itDS = dataChannel.getDataSources().iterator();
		    	while (itDS.hasNext()) {
		    	  Object ds = itDS.next();
		    	  //if (ds instanceof TwitterDataSource) {
			    	  dcData = new SolrInputDocument();
			    	  composedID = dataChannel.getChannelID() + "-" + ((DataSource) ds).getSourceID();
			    	  LOGGER.info("Composed DC-DS ID en solr: " + composedID);
			    	  dcData.addField(CaptureConstants.ID, composedID);
			    	  dcData.addField(CaptureConstants.DC_ID, dataChannel.getChannelID());
			    	  dcData.addField(CaptureConstants.TYPE, dataChannel.getType());
			    	  dcData.addField(CaptureConstants.DS_ID, ((DataSource) ds).getSourceID());
			    	  // New schema: add a new typeDS field and put it lower case
			    	  if (((DataSource) ds).getDstype() != null){
			    		  dcData.addField(CaptureConstants.TYPE_DS, ((DataSource) ds).getDstype().toLowerCase());
			    	  }
			    	  if (ds instanceof TwitterDataSource) {
			    		  dcData.addField(CaptureConstants.KEYWORDS, ((TwitterDataSource) ds).getKeywords());
			    	  }	  
			    	  if (ds instanceof RedditDataSource) {
			    		  dcData.addField("redditKeywords", ((RedditDataSource) ds).getKeywords());
			    		  dcData.addField("redditType", ((RedditDataSource) ds).getRedditType());
			    		  dcData.addField("subreddits", ((RedditDataSource) ds).getSubreddits());
			    		  dcData.addField("post", ((RedditDataSource) ds).getPost());
			    	  }	 
			    	  dcData.addField(CaptureConstants.CAPTURE_START_DATE, toUtcDate(dataChannel.getStartCaptureDate()));
			    	  dcData.addField(CaptureConstants.CAPTURE_END_DATE, toUtcDate(dataChannel.getEndCaptureDate()));
			    	  
			    	  if (ds instanceof TwitterDataSource) {
			    		  dcData.addField(CaptureConstants.FROM_LAST_TWEET_ID, ((TwitterDataSource) ds).getFromLastTweetId());
				    	  dcData.addField(CaptureConstants.LAST_TWEET_ID, ((TwitterDataSource) ds).getLastTweetId());
				    	  dcData.addField(CaptureConstants.CHRONOLOGICAL_ORDER, ((TwitterDataSource) ds).getChronologicalOrder());
						  dcData.setField(CaptureConstants.HISTORICAL_LIMIT, ((TwitterDataSource) ds).getHistoricalLimit());
						  dcData.setField(CaptureConstants.TOTAL_TWEET_COUNT, ((TwitterDataSource) ds).getTotalTweetCount());
						  dcData.setField(CaptureConstants.DS_STATE, ((TwitterDataSource) ds).getState());
			    	  }
			    	  
			    	  solr.add(dcData);
			    	  solr.commit();  
		    	  //}
		    	}  
		
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, " ERROR adding/updating new data channel to Solr: " + composedID );
				LOGGER.log(Level.SEVERE, " ERROR adding/updating new data channel to Solr: " + e.getMessage(), e);
				throw new CaptureException("ERROR adding/updating new data channel to Solr: " + e.getMessage(), e);
			}
		}
    	
    	LOGGER.info(" ### ADDING NEW DATA CHANNEL DATA TO SOLR: " + composedID);    	
    	return composedID;		
	}
	
	public String updateDataSource (QueryData query) throws CaptureException, SolrServerException, IOException {
		SolrClient solr = this.solrServerPool.get(DC_INDEX);
		SolrDocumentList dcList;
		SolrDocument dcData;
		String composedID = "";
		
		LOGGER.info("DC: " + query.getDcID() + "| DS: " + query.getDsID() + "| Last ID: " + query.getLastID());
		
    	/* Solr fields*/
		if (query!=null) {
			composedID = query.getDcID() + "-" + query.getDsID();
			// Sacar el objeto de solr apartir del composerID
			dcList = new SolrDocumentList();
			dcData = solr.getById(composedID);  // SE HAN QUITADO LOS DUPLICADOS PROBAR CON  ESTE MÉTODO!!!

			// actualizar los campos
			// entre ellos el lastID	
			dcData.setField(CaptureConstants.LAST_TWEET_ID, query.getLastID());
			dcData.setField(CaptureConstants.FROM_LAST_TWEET_ID, true);
			dcData.setField(CaptureConstants.HISTORICAL_LIMIT, query.getHistoricalLimit());
			dcData.setField(CaptureConstants.TOTAL_TWEET_COUNT, query.getTotalTweetCount());
			dcData.setField(CaptureConstants.DS_STATE, query.getState());
			dcData.setField(CaptureConstants.CHRONOLOGICAL_ORDER, query.isChronologicalOrder());
			
			SolrInputDocument dcInputData = ClientUtils.toSolrInputDocument(dcData); 
	    	solr.add(dcInputData);
	    	solr.commit();
		}
    	
    	LOGGER.info(" ### ADDING NEW DATA CHANNEL DATA TO SOLR: " + composedID);    	
    	return composedID;		
	}

	public void deleteDataChannel(DataChannel dc) throws CaptureException {
		SolrClient solr = this.solrServerPool.get(DC_INDEX);
		try {
			solr.deleteByQuery(CaptureConstants.DC_ID + ":\"" + dc.getChannelID() + "\"");
			solr.commit();
			//Iterator<DataSource> itDS = dc.getDataSources().iterator();
	    	//while (itDS.hasNext()) {
	    	  //Object ds = itDS.next();		    	  
	    	  
	    	  //Delete query from Queue
	    	  /*if (ds instanceof TwitterDataSource) {
	    		  String dsID = ((TwitterDataSource) ds).getSourceID();
	    		  twitter.deleteQueryToQueue(dsID);
	    	  }*/	  
	    	//}
		} catch (Exception e) {
			LOGGER.severe(" ERROR deleting data channel from Solr: " + e.getMessage());
			throw new CaptureException("ERROR deleting data channel from Solr: " + e.getMessage(), e);
		}		
	}

	public boolean isQueryActive(String queryId) throws Exception {
		 String timestamp = new java.sql.Timestamp(new java.util.Date().getTime()).toString().replace(' ', 'T') + "Z" ;
		 String query = CaptureConstants.DS_ID + ":\"" + queryId + "\" AND " + CaptureConstants.CAPTURE_END_DATE + ": {"+timestamp+ " TO *}";	
		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 solrParams.set("q", query);
		 solrParams.set("sort", CaptureConstants.CAPTURE_START_DATE + " asc");
		 LOGGER.info(" # ACTIVE QUERY EVALUATION: " + query);				 
		 long queries = this.solrServerPool.get(DC_INDEX).query(solrParams).getResults().getNumFound();		
	     return queries > 0;
	}

// 	Reemplazed by a hbase search 
//	public DcTweetList getTweetsFromDC(String dcId, int page, int numResults) throws CaptureException {
//		try {
//		
//			String query = "{!join from=" + CaptureConstants.TWEET_ID + " to= " + CaptureConstants.TWEET_ID + " fromIndex=" + this.dcIndex + "}dcID:" + dcId;	
//			ModifiableSolrParams solrParams = new ModifiableSolrParams();
//			solrParams.set("q", query);
//			solrParams.set("sort", CaptureConstants.TWEET_ID + " desc");
//			solrParams.set("start", (page-1)*numResults);
//			solrParams.set("rows", numResults);
//			QueryResponse response = this.solrServerPool.get(TWEETS_INDEX).query(solrParams);		
//			
//			DcTweetList tweetList = new DcTweetList(solrServerPool.get(TWEETS_INDEX).query(solrParams).getBeans(Tweet.class));
//			tweetList.setPage(page);
//			tweetList.setPageSize(numResults);
//			tweetList.setTotalTweets(response.getResults().getNumFound());
//	        return tweetList;
//	   } catch (Exception e) {
//		   LOGGER.severe(" ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage());
//			throw new CaptureException("ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage(), e);
//	   }
//		
//	}   
	
// 	Reemplazed by followin method  getFacetTweets
//	public DcTweetList getFacetTweetsFromDC (String dcId, String typeDataSource, String facetName, String value, int page, int numResults) throws CaptureException {
//		try {		
//			
//			// Old query
//			//String join = "{!join from=" + CaptureConstants.TWEET_ID + " to= " + CaptureConstants.TWEET_ID + " fromIndex=" + this.dcIndex + "}";
//			//String filterJoinedTable = "dcID:" + dcId;/*AND typeDataSource = typeDataSource)*/
//			//String query = join+filterJoinedTable;
//			//New query
//			String mainJoin = "{!join from=" + CaptureConstants.TWEET_ID + " to= " + CaptureConstants.TWEET_ID + " fromIndex=" + this.dcIndex + "}dcID:" + dcId;
//			String secondJoin = " AND _query_:{!join from=" + CaptureConstants.DS_ID + " to= " + CaptureConstants.DS_ID +" fromIndex=" + this.dcIndex + "}typeDS:" + typeDataSource;
//			String query = mainJoin+secondJoin;
//			
//			
//			ModifiableSolrParams solrParams = new ModifiableSolrParams();
//			solrParams.set("q", query);
//			if ("createdAt".equals(facetName)){
//				String[] timeRange = value.split(";");
//				solrParams.set("fq", facetName+":["+ timeRange[0] +" TO "+ timeRange[1] + "]");
//			}
//			else {
//				solrParams.set("fq", facetName+":"+value);
//			}
//			solrParams.set("sort", CaptureConstants.TWEET_ID + " desc");
//			solrParams.set("start", (page-1)*numResults);
//			solrParams.set("rows", numResults);
//			QueryResponse response = this.solrServerPool.get(TWEETS_INDEX).query(solrParams);		
//			DcTweetList tweetList = new DcTweetList(solrServerPool.get(TWEETS_INDEX).query(solrParams).getBeans(Tweet.class));
//			tweetList.setPage(page);
//			tweetList.setPageSize(numResults);
//			tweetList.setTotalTweets(response.getResults().getNumFound());
//	        return tweetList;
//	   } catch (Exception e) {
//		   LOGGER.severe(" ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage());
//			throw new CaptureException("ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage(), e);
//	   }
//		
//	}


	public DcTweetList getFacetTweets (String dcId, String typeDataSource, String filtersExpresion, String sorter, String mode, String fields, int page, int numResults) throws CaptureException {
		try {		
			
			String query = "*:*";
			
			if (!"".equals(dcId)){
				query = CaptureConstants.DC_ID + ":" + dcId;
				if (!"".equals(typeDataSource))
					query = query + " AND " + CaptureConstants.TYPE_DS + ":" + typeDataSource;
			}
			else{
				if (!"".equals(typeDataSource))
					query =  CaptureConstants.TYPE_DS + ":" + typeDataSource;
			}
	
			
			LOGGER.info(" # Query: " + query);	
			LOGGER.info(" # filtersExpresion: " + filtersExpresion);
			ModifiableSolrParams solrParams = new ModifiableSolrParams();

			// Anadimos la query
			solrParams.set("q", query);
			
			// Anadimos las facetas
			solrParams.set("fq", filtersExpresion);
			
			// Anadimos otros parametros
			if ("".equals(mode)) mode = "desc";
			

			//if ("".equals(sorter)) solrParams.set("sort", CaptureConstants.TWEET_ID + " " + mode);
			//else solrParams.set("sort", sorter + " " + mode);
			
			if (!"".equals(sorter)) solrParams.set("sort", sorter + " " + mode);
			
			// Anadimos el(los) parametro(s) para selecionar el field que se devuelve en los resultados
			if (!"".equals(fields)) solrParams.set("fl", fields);
			
			solrParams.set("start", (page-1)*numResults);
			solrParams.set("rows", numResults);
			QueryResponse response = this.solrServerPool.get(TWEETS_INDEX).query(solrParams);		
			DcTweetList tweetList = new DcTweetList(response.getBeans(Tweet.class));
			tweetList.setPage(page);
			tweetList.setPageSize(numResults);
			tweetList.setTotalTweets(response.getResults().getNumFound());
	        return tweetList;
	   } catch (Exception e) {
		   LOGGER.severe(" ERROR get facect queries from Solr: " + e.getMessage());
		   throw new CaptureException("ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage(), e);
	   }
		
	}
	
	public DcTweetList getFacetWithNoFqTweets (String dcId, String typeDataSource, String filtersExpresion, String sorter, String mode, String fields, int page, int numResults) throws CaptureException {
		try {		
			
			String query = "*:*";
			
			if (!"".equals(dcId)){
				query = CaptureConstants.DC_ID + ":" + dcId;
				if (!"".equals(typeDataSource))
					query = query + " AND " + CaptureConstants.TYPE_DS + ":" + typeDataSource;
			}
			else{
				if (!"".equals(typeDataSource))
					query =  CaptureConstants.TYPE_DS + ":" + typeDataSource;
			}
	
			
			LOGGER.info(" # Query: " + query);	
			LOGGER.info(" # filtersExpresion: " + filtersExpresion);
			ModifiableSolrParams solrParams = new ModifiableSolrParams();

			if (!"".equals(filtersExpresion)){
				query = query + " AND " + filtersExpresion;
			}
			// Anadimos la query
			solrParams.set("q", query);
			
			// Anadimos las facetas
			//solrParams.set("fq", filtersExpresion);
			
			// Anadimos otros parametros
			if ("".equals(mode)) mode = "desc";
			if ("".equals(sorter)) solrParams.set("sort", CaptureConstants.TWEET_ID + " " + mode);
			else solrParams.set("sort", sorter + " " + mode);
			
			// Anadimos el(los) parametro(s) para selecionar el field que se devuelve en los resultados
			if (!"".equals(fields)) solrParams.set("fl", fields);
			
			solrParams.set("start", (page-1)*numResults);
			solrParams.set("rows", numResults);
			QueryResponse response = this.solrServerPool.get(TWEETS_INDEX).query(solrParams);		
			DcTweetList tweetList = new DcTweetList(response.getBeans(Tweet.class));
			tweetList.setPage(page);
			tweetList.setPageSize(numResults);
			tweetList.setTotalTweets(response.getResults().getNumFound());
	        return tweetList;
	   } catch (Exception e) {
		   LOGGER.severe(" ERROR get facect queries from Solr: " + e.getMessage());
		   throw new CaptureException("ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage(), e);
	   }
		
	}


//	// Temporal method for volume
//	public VolumeResultList getVolumeFromDC (String dcId, String typeDataSource, String fInit, String fEnd, int size) throws CaptureException {
//		try {		
//
//			LOGGER.info("get volumen from datachannel: "+dcId+" from: "+ fInit + " until: "+fEnd+ " each: "+size+" segs");
//			VolumeResultList volumenResultList = new VolumeResultList();
//			LinkedHashSet<VolumeResult> resultList = new LinkedHashSet<VolumeResult>();
//			Date dInit = toDate(fInit);
//			Date dEnd = toDate (fEnd);
//			Date initialQueryDate = dInit;
//			Date endQueryDate = dEnd;
//			int seg = restarFechas (dInit, dEnd);
//			
//			
//			String mainJoin = "{!join from=" + CaptureConstants.TWEET_ID + " to= " + CaptureConstants.TWEET_ID + " fromIndex=" + this.dcIndex + "}dcID:" + dcId;
//			String secondJoin = " AND _query_:{!join from=" + CaptureConstants.DS_ID + " to= " + CaptureConstants.DS_ID +" fromIndex=" + this.dcIndex + "}typeDS:" + typeDataSource;
//			String query = mainJoin+secondJoin;
//			
//			System.out.println(query);
//			
//			ModifiableSolrParams solrParams = new ModifiableSolrParams();
//			for (int i=0; i<seg/size; i++){ 
//				endQueryDate = sumarSegundosFecha(initialQueryDate, size);
//				solrParams.set("q", query);
//				String fqParameter = "createdAt"+":["+ toUTCString(initialQueryDate) +" TO "+ toUTCString(endQueryDate) + "]";
//				solrParams.set("fq", fqParameter);
//				DcTweetList tweetList = new DcTweetList(solrServerPool.get(TWEETS_INDEX).query(solrParams).getBeans(Tweet.class));
//				long positive  = 0;
//				long negative = 0;
//				long dangerousness = 0;
//				long nonDangerousness = 0;
//				long stress = 0;
//				long nonStress = 0;
//
//				
//				//long total = 0;
//				
//				Iterator <Tweet> it = tweetList.getTweets().iterator();
//				while(it.hasNext()){							
//					Tweet nxt = it.next();
//					//total++;
//					//LOGGER.info("Computing tweet: " + nxt.getId() + " sentiment: " + nxt.getSentiment());
//					if(nxt.getSentiment() != null && nxt.getSentiment() > 0.0)
//						positive ++;
//					if(nxt.getSentiment() != null && nxt.getSentiment() < 0.0)
//						negative ++;
//					//LOGGER.info("VolCount: total " + total + " positive " + positive + " negative " + negative);
//					if(nxt.getDangerousness() != null && nxt.getDangerousness() > 0.0)
//						dangerousness ++;
//					else
//						nonDangerousness ++;
//					
//					if(nxt.getStress() != null && nxt.getStress() > 0.0)
//						stress ++;
//					else
//						nonStress ++;
//					
//				}
//				
//				VolumeResult result = new VolumeResult();
//				result.setDc(dcId);
//				result.setfInit(initialQueryDate);
//				result.setfEnd(endQueryDate);
//				result.setSize(size);
//				result.setVolume(tweetList.getTweets().size());
//				result.setVolumeNegative(negative);
//				result.setVolumePositive(positive);
//				result.setVolumeDangerousness(dangerousness);
//				result.setVolumeNonDangerousness(nonDangerousness);
//				result.setVolumeStress(stress);
//				result.setVolumeNonStress(nonStress);
//				resultList.add(result);
//				initialQueryDate = endQueryDate;
//			}
//			volumenResultList.setItems(resultList);
//	        return volumenResultList;
//	   } catch (Exception e) {
//		   LOGGER.severe(" ERROR recovering volume from data channel "+ dcId +" from Solr: " + e.getMessage());
//			throw new CaptureException("ERROR recovering tweets from data channel "+dcId+" from Solr: " + e.getMessage(), e);
//	   }
//		
//	}
	
	public LinkedHashMap<String, QueryData> getDSFromTweet (String tweetId, int page, int numResults) throws Exception {
	   	
		 LinkedHashMap<String, QueryData> dataSources = new LinkedHashMap<String, QueryData>(); 
		 
		 String query = CaptureConstants.TWEET_ID + ":" + tweetId;	
		 ModifiableSolrParams solrParams = new ModifiableSolrParams();
		 LOGGER.info(" #Query: " + query);
		 solrParams.set("q", query);
		 solrParams.set("start", (page-1)*numResults);
		 solrParams.set("rows", numResults);
		 List<QueryData> queries = this.solrServerPool.get(SolrManagerFast.DC_INDEX).query(solrParams).getBeans(QueryData.class);
		 
		 LOGGER.info(" #Num data sources: " + queries.size());
		 if (!queries.isEmpty()) {
			 Iterator<QueryData> itQ = queries.iterator();
			 while (itQ.hasNext()) {
				 QueryData queryObj = itQ.next();
				 LOGGER.info(" #ACTIVE QUERY: " + queryObj.getDsID() + "/" + queryObj.getDcID());
				 dataSources.put(queryObj.getDsID(), queryObj);
			 }
		 }
	     return dataSources;
	}
	
	public static String toUtcDate(String dateStr) throws ParseException {
		SimpleDateFormat genericDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS");
		Date date = genericDateFormat.parse(dateStr);
		SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		//dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormatUTC.format(date); 
		
	}
	
	// Auxiliar methods for volume
	public static Date toDate(String dateStr) throws ParseException {
		SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		return dateFormatUTC.parse(dateStr);		
	}
	
	public static String toUTCString(Date date) throws ParseException {
		SimpleDateFormat dateFormatUTC = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		dateFormatUTC.setTimeZone(TimeZone.getTimeZone("UTC"));
		return dateFormatUTC.format(date);		
	}
	
	public static int restarFechas(Date fechaIn, Date fechaFinal ){
		long in = fechaIn.getTime();
		long fin = fechaFinal.getTime();
		Long diff= (fin-in)/1000;
		return diff.intValue();
		}

	 public Date sumarSegundosFecha(Date fecha, int size){
		 Calendar calendar = Calendar.getInstance();
		 calendar.setTime(fecha); // Configuramos la fecha que se recibe
		 calendar.add(Calendar.SECOND, size);  // numero de horas a anadir, o restar en caso de horas
		 return calendar.getTime(); // Devuelve el objeto Date con los nuevos seg anadidos
	 }
	 
		public static Date toUTCDate(Date date) throws ParseException {
			DateFormat gmtFormat = new SimpleDateFormat();
			gmtFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			return gmtFormat.parse(gmtFormat.format(date));		
			
		}
	 
	 
		public static void main(String[] args) throws Exception {
			// TODO Auto-generated method stub
			// para probar este main es necesario meter en el pom
//			<dependency>
//			<groupId>org.slf4j</groupId>
//			<artifactId>slf4j-api</artifactId>
//			<version>1.7.2</version>
//		    </dependency>
//		    <dependency>
//			<groupId>commons-logging</groupId>
//			<artifactId>commons-logging</artifactId>
//			<version>1.1.1</version>
//		    </dependency>
			SolrManagerFast.getInstance().getDSFromTweet("569609092567863297",1, 100);
		}
		
		public void indexTagCloudOcurrences (List<TermOccurrenceAnalysis> ocurrenceList) throws CaptureException {
			
			LOGGER.info("Indexing tagcloud ocurrences.... ");
			SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
			SolrInputDocument ocurrenceData;
			Collection<SolrInputDocument> ocurrencesDataCollection = null;
			TermOccurrenceAnalysis ocurrence;
			XStream xstream = new XStream();
			 
	    	/* Solr fields*/
			if (ocurrenceList!=null && !ocurrenceList.isEmpty()) {
				try {
					Iterator<TermOccurrenceAnalysis> itOcurrence = ocurrenceList.iterator();
			    	ocurrencesDataCollection = new ArrayList<SolrInputDocument>();
					  
					while (itOcurrence.hasNext()) {
						 ocurrence = itOcurrence.next();		    	  
				    	 ocurrenceData = new SolrInputDocument();
				    	 String dcId = ocurrence.getDataChannel().getChannelID();
				    	 //Build Solr Tweet Doc
				    	 ocurrenceData.addField(CaptureConstants.DC_ID, dcId);
				    	 ocurrenceData.addField(CaptureConstants.DATE, ocurrence.getaDate());
				    	
				    	 String id = ocurrence.getPeriodicity().toString() + "_" + CaptureConstants.TAGCLOUD + "_" + dcId + "_" + toUTCString(ocurrence.getaDate());
				    	 ocurrenceData.addField(CaptureConstants.ID, id);
				    	 ocurrenceData.addField(CaptureConstants.TYPE_MEASURE, CaptureConstants.TAGCLOUD);
				    	 
				    	 
				    	 if (ocurrence.getTermOcc()!=null) {
				    		 String xmlOcurrences = xstream.toXML(ocurrence.getTermOcc());
				    		 LOGGER.info("Hay ocurrences: "+ xmlOcurrences);			    		 
					    	 ocurrenceData.addField(CaptureConstants.TAGCLOUD, xmlOcurrences);
				    	 }
				    	 if (ocurrence.getPeriodicity()!=null) {
				    		 ocurrenceData.addField(CaptureConstants.PERIODICITY, ocurrence.getPeriodicity());
				    	  }
				    	 LOGGER.info("Indexing --> DC: "+ dcId + ", Date: "+ ocurrence.getaDate() + ", Periodicity: "+ ocurrence.getPeriodicity());
				    	 ocurrencesDataCollection.add(ocurrenceData);
			    	} 
					LOGGER.info("Before indexing");
					solr.add(ocurrencesDataCollection);
					solr.commit();
					LOGGER.info("Indexed term ocurrences: " + ocurrenceList.size());
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, "ERROR adding ocurrences in solr", e);
					
					try {
						CollectionSerializer.serializeToFile((ArrayList<SolrInputDocument>) ocurrencesDataCollection, logsFile);
					} catch (IOException e1) {					
						LOGGER.log(Level.SEVERE, "ERROR serializing the ocurrencesDataCollection: " + e1.getMessage(), e1);					
					}

					
					try {
						solr.rollback();
					} catch (Exception ex) {
						LOGGER.log(Level.ALL, "ERROR in rollback", ex);
						throw new CaptureException("ERROR in rollback adding/updating new Tweet to Solr", ex);
					}	
				}
			}

		}
		
		public void indexVolumeOcurrences (List<VolumeOcurrenceAnalysis> ocurrenceList) throws CaptureException {
			LOGGER.info("Indexing volume ocurrences.... ");
			SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
			SolrInputDocument ocurrenceData;
			Collection<SolrInputDocument> ocurrencesDataCollection = null;
			VolumeOcurrenceAnalysis ocurrence;
			 
	    	/* Solr fields*/
			if (ocurrenceList!=null && !ocurrenceList.isEmpty()) {
				try {
					Iterator<VolumeOcurrenceAnalysis> itOcurrence = ocurrenceList.iterator();
			    	ocurrencesDataCollection = new ArrayList<SolrInputDocument>();
					  
					while (itOcurrence.hasNext()) {
						 ocurrence = itOcurrence.next();		    	  
				    	 ocurrenceData = new SolrInputDocument();
				    	 String dcId = ocurrence.getDataChannel().getChannelID();
				    	 //Build Solr Tweet Doc
				    	 ocurrenceData.addField(CaptureConstants.DC_ID, dcId);
				    	 ocurrenceData.addField(CaptureConstants.DATE, ocurrence.getaDate());
				    	 String id = ocurrence.getPeriodicity().toString() + "_" + ocurrence.getAnalysisType() + "_" + dcId + "_" + toUTCString(ocurrence.getaDate());
				    	 ocurrenceData.addField(CaptureConstants.ID, id);
				    	 ocurrenceData.addField(CaptureConstants.TYPE_MEASURE, ocurrence.getAnalysisType());
				    	 ocurrenceData.addField(CaptureConstants.MEASUREMENT, ocurrence.getMeasurement());
				    	 ocurrenceData.addField(CaptureConstants.PERIODICITY, ocurrence.getPeriodicity());
				    	 
				    	 LOGGER.info("Indexing --> DC: "+ dcId + ", Date: "+ ocurrence.getaDate() + ", Periodicity: "+ ocurrence.getPeriodicity() + ", Type Measure: " + ocurrence.getAnalysisType());
				    	 ocurrencesDataCollection.add(ocurrenceData);
			    	} 
					LOGGER.info("Before indexing");
					solr.add(ocurrencesDataCollection);
					solr.commit();
					LOGGER.info("Indexed term ocurrences: " + ocurrenceList.size());
				} catch (Exception e) {
					e.printStackTrace();
					LOGGER.log(Level.SEVERE, "ERROR adding ocurrences in solr", e);
					
					try {
						CollectionSerializer.serializeToFile((ArrayList<SolrInputDocument>) ocurrencesDataCollection, logsFile);
					} catch (IOException e1) {					
						LOGGER.log(Level.SEVERE, "ERROR serializing the ocurrencesDataCollection: " + e1.getMessage(), e1);					
					}

					
					try {
						solr.rollback();
					} catch (Exception ex) {
						LOGGER.log(Level.ALL, "ERROR in rollback", ex);
						throw new CaptureException("ERROR in rollback adding/updating new Tweet to Solr", ex);
					}	
				}
			}

		}

	// ***************************************************** methods for the dashboard ****************************************************************
	
	// populars
	Map<Date, Map<DataChannel, Integer>> getTopVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per, int numberOfResults){
		
		LOGGER.info("Getting populars in solr...");
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		Integer total=0;
		Map<Date, Map<DataChannel, Integer>> topVolume = new HashMap<Date, Map<DataChannel, Integer>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
				// Preparamos las fechas que vamos a pregutar
				String fqParameterDate = CaptureConstants.DATE+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterDCs = CaptureConstants.DC_ID+ ":(" +dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameter = fqParameterDCs + " AND "+ fqParameterDate;
				//String query = CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.VOLUME;
				String query = CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.VOLUME +" AND " + CaptureConstants.PERIODICITY + ":" + per;
				
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + " ,the fq query: " +fqParameter);	
				
				
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 String sDc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 Integer iMeasurement = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 
						 if (ranking.get(sDc) == null){
							 ranking.put(sDc, iMeasurement);
						 }else{
							 ranking.put(sDc, ranking.get(sDc) + iMeasurement);
						 }
						 total = total + iMeasurement;
						
					}// End while
				}// End if
				
				LOGGER.info("Ranking: "+ ranking.toString());	
	    		ArrayList<Entry<String, Integer>> losValores = new ArrayList<Entry<String, Integer>>(ranking.entrySet());
	    		try{
		    		Collections.sort(losValores, new TopEntryComparator());
	    		}catch (Exception e) {
	    			LOGGER.info("Error comparing: ");
	    		}
	  
	            // creamos el bean que se va a enviar
	    		
	    		if (losValores.size() < numberOfResults) numberOfResults=losValores.size();
	    		HashMap <DataChannel, Integer> rankedMap = new HashMap<DataChannel, Integer>();
	    		for (Entry<String, Integer> it : losValores.subList(0, numberOfResults)) {
	    			DataChannel newDC = new DataChannel();
					newDC.setChannelID(it.getKey());
					int percentaje = it.getValue()*100/total;
	    			//rankedMap.put(newDC, it.getValue());
					rankedMap.put(newDC, percentaje);
	    		}
	    		LOGGER.info("Ranked: "+ rankedMap.toString());
	    		topVolume.put(endDate, rankedMap);
	    		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the volume", e);
		}
		return topVolume;
	}
	
	// popularity
	Map<Date, Integer> getVolumePer(List<String> dataPoolIdList, String dataPoolId, Date initDate, Date endDate, Periodicity per){
		
		LOGGER.info("Getting popularity in solr...");
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		Integer total=0;
		Map<Date, Integer> popularity = new HashMap<Date, Integer>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
				
				// Preparamos las fechas que vamos a pregutar
				String fqParameterDate = CaptureConstants.DATE+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterDCs = CaptureConstants.DC_ID+ ":(" +dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameterPeriodicity = CaptureConstants.PERIODICITY + ":" + per;
				String fqParameterTypeMeasure = CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.VOLUME;
				//String fqParameter = fqParameterDCs + " AND " + fqParameterDate + " AND " + fqParameterPeriodicity + " AND " + fqParameterTypeMeasure;
				//String query = "*:*";
				//String fqParameter = fqParameterDCs + " AND " + fqParameterDate;
				//String query = fqParameterTypeMeasure+ " AND " + fqParameterPeriodicity;
				String query = fqParameterDCs + " AND " + fqParameterDate + " AND " + fqParameterPeriodicity + " AND " + fqParameterTypeMeasure;
				String fqParameter = "";
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + ", the fq query: " +fqParameter);	
				
				long solrStart = System.currentTimeMillis();  
				SolrDocumentList result = solr.query(solrParams).getResults();
				long solrElapsedTime = System.currentTimeMillis() - solrStart;
				LOGGER.info("Solr query time: " + solrElapsedTime + " ms");
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 String sDc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 Integer iMeasurement = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 if (ranking.get(sDc) == null){
							 ranking.put(sDc, iMeasurement);
						 }else{
							 ranking.put(sDc, ranking.get(sDc) + iMeasurement);
						 }
						 total = total+iMeasurement;
					}// End while
				}// End if
				
				LOGGER.info("Ranking: "+ ranking.toString() + ", total:" + total);	
	            // creamos el bean que se va a enviar
				if (ranking!=null && !ranking.isEmpty()) popularity.put(endDate, ranking.get(dataPoolId)*100/total);
	    		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the popularity", e);
		}
		return popularity;
	}
	
	
	// sentimentDegree
	Map<Date, Map<DataChannel, Double>> getSentimentDegree2 (List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		LOGGER.info("Getting sentiment degree in solr...");
		Map<Date, Map<String, Integer[]>> sentiment = new HashMap<Date, Map<String, Integer[]>>();
		Map<Date, Map<DataChannel, Double>> sentimentFinal = new HashMap<Date, Map<DataChannel, Double>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
	
				// Preparamos las fechas que vamos a pregutar
				String fqParameterDate = CaptureConstants.DATE+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterDCs = CaptureConstants.DC_ID+ ":(" + dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameter = fqParameterDCs + " AND "+ fqParameterDate;
				String typeMeasureQuery = CaptureConstants.TYPE_MEASURE + ":" + "(" + CaptureConstants.SENTIMENT_VOLUME_POSITIVE + " " + CaptureConstants.SENTIMENT_VOLUME_NEGATIVE + ")";
				String query = typeMeasureQuery +" AND " + CaptureConstants.PERIODICITY + ":" + per.toString();
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + " ,the fq query: " +fqParameter);	
				
				
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 Date date = (Date) queryObj.getFieldValue(CaptureConstants.DATE);
						 String dc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 
						 int positives=0;
						 int negatives=0;
						 if (CaptureConstants.SENTIMENT_VOLUME_POSITIVE.equals(queryObj.getFieldValue(CaptureConstants.TYPE_MEASURE)))
							 positives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 else
							 negatives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 
						
						 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
						 if (dateSentiment == null){
							 dateSentiment = new HashMap<String, Integer[]>();
							 Integer sentiments[] = {positives, negatives};
							 dateSentiment.put(dc, sentiments);
						 }else{
							 Integer sentiments[] = dateSentiment.get(dc);
							 if (sentiments == null){
								 // ya estaba la fecha registrada pero no ese datachannel
								 sentiments = new Integer[2];
								 sentiments[0] = positives;
								 sentiments[1] = negatives;
							 }
							 else{
								 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
								 if (positives > 0) sentiments[0] = positives;
								 if (negatives > 0) sentiments[1] = negatives;
							 }
							 dateSentiment.put(dc, sentiments);
						 }
						 sentiment.put(date, dateSentiment);
					}// End while
				}// End if	
				
				LOGGER.info("El acumulado: "+ sentiment.toString());
				Iterator<Date> itDates = (Iterator<Date>) sentiment.keySet().iterator();
				while (itDates.hasNext()) {
					// Vamos sacando por cada fecha
					Date dateFinal = itDates.next();
					Map<DataChannel, Double> newDate = new HashMap<DataChannel, Double>();
					Map<String, Integer[]> res = sentiment.get(dateFinal);
					Iterator<String> dcKey = res.keySet().iterator();
					while (dcKey.hasNext()) {
						// Vamos sacando por datachannel
						String dcFinal = dcKey.next();
						Integer[] sentimentsRes = res.get(dcFinal);
						//LOGGER.log(Level.INFO, "pos: ", sentimentsRes[0]);
						//LOGGER.log(Level.INFO, "neg: ", sentimentsRes[1]);
						DataChannel newDC = new DataChannel();
						newDC.setChannelID(dcFinal);
						double degree = (((sentimentsRes[0]-sentimentsRes[1]) * 1.0)/((sentimentsRes[0]+sentimentsRes[1]) * 1.0));
						//LOGGER.log(Level.INFO, "Degree: ", degree);
						newDate.put(newDC, degree);
					}
					sentimentFinal.put(dateFinal, newDate);
				}
				 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the sentiment", e);
		}
		return sentimentFinal;
	}
	
	// sentimentDegree
	Map<Date, Map<DataChannel, Double>> getSentimentDegree (List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		LOGGER.info("Getting sentiment degree in solr...");
		Map<String, Integer[]> sentiment = new HashMap<String, Integer[]>();
		Map<Date, Map<DataChannel, Double>> sentimentFinal = new HashMap<Date, Map<DataChannel, Double>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
	
				// Preparamos las fechas que vamos a pregutar
				String fqParameterDate = CaptureConstants.DATE+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterDCs = CaptureConstants.DC_ID+ ":(" + dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameter = fqParameterDCs + " AND "+ fqParameterDate;
				String typeMeasureQuery = CaptureConstants.TYPE_MEASURE + ":" + "(" + CaptureConstants.SENTIMENT_VOLUME_POSITIVE + " " + CaptureConstants.SENTIMENT_VOLUME_NEGATIVE + ")";
				String query = typeMeasureQuery +" AND " + CaptureConstants.PERIODICITY + ":" + per.toString();
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + " ,the fq query: " +fqParameter);	
				
				
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						SolrDocument queryObj = itQ.next();
						String dc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 
						int positives=0;
						int negatives=0;
						if (CaptureConstants.SENTIMENT_VOLUME_POSITIVE.equals(queryObj.getFieldValue(CaptureConstants.TYPE_MEASURE)))
							 positives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						else
							 negatives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 

						Integer sentiments[] = sentiment.get(dc);
						if (sentiments == null){
								 // este datachannel no tenia todavia acumulados
								 sentiments = new Integer[2];
								 sentiments[0] = positives;
								 sentiments[1] = negatives;
						}
						else{
								 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
								 if (positives > 0) sentiments[0] = sentiments[0] + positives;
								 if (negatives > 0) sentiments[1] = sentiments[1] + negatives;
						}
						sentiment.put(dc, sentiments);
					}// End while
				}// End if	
				
				LOGGER.info("El acumulado: "+ sentiment.toString());
				
			
	
				Map<DataChannel, Double> singleDate = new HashMap<DataChannel, Double>();
				Iterator<String> itDc = (Iterator<String>) sentiment.keySet().iterator();
				while (itDc.hasNext()) {
						// Vamos sacando por datachannel
						String dcId = itDc.next();
						Integer[] dcSentiments = sentiment.get(dcId);
						
						DataChannel newDC = new DataChannel();
						newDC.setChannelID(dcId);
						double degree = 0.0;
						if (dcSentiments.length>1){
							LOGGER.log(Level.INFO, "pos: ", dcSentiments[0].toString());
							LOGGER.log(Level.INFO, "neg: ", dcSentiments[1].toString());
							degree = (((dcSentiments[0]-dcSentiments[1]) * 1.0)/((dcSentiments[0]+dcSentiments[1]) * 1.0));
						}
						LOGGER.log(Level.INFO, "Degree: ", degree);
						singleDate.put(newDC, degree);
				}
				sentimentFinal.put(endDate, singleDate);
	
				 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the sentiment", e);
		}
		return sentimentFinal;
	}
	
	// sentiment
	@SuppressWarnings("null")
	Map<Date, Map<DataChannel, Integer[]>> getSentiment (List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		LOGGER.info("Getting sentiment in solr...");
		Map<Date, Map<String, Integer[]>> sentiment = new HashMap<Date, Map<String, Integer[]>>();
		Map<Date, Map<DataChannel, Integer[]>> sentimentFinal = new HashMap<Date, Map<DataChannel, Integer[]>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
	
				// Preparamos las fechas que vamos a pregutar
				String fqParameterDCs = CaptureConstants.DC_ID+ ":(" + dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameterDate = CaptureConstants.DATE+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterTypeMeasureQuery = CaptureConstants.TYPE_MEASURE + ":" + "(" + CaptureConstants.SENTIMENT_VOLUME_POSITIVE + " " + CaptureConstants.SENTIMENT_VOLUME_NEGATIVE + ")";
				String fqParameterPeriodicity = CaptureConstants.PERIODICITY + ":" + per;
				String fqParameter = fqParameterDCs + " AND " + fqParameterDate + " AND " +  fqParameterTypeMeasureQuery + " AND " +  fqParameterPeriodicity;
				String query = "*:*";
				//String fqParameter = fqParameterDCs + " AND "+ fqParameterDate;
				//String query = fqParameterTypeMeasureQuery +" AND " + fqParameterPeriodicity;
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + ", the fq query: " +fqParameter);	
				
				
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 Date date = (Date) queryObj.getFieldValue(CaptureConstants.DATE);
						 String dc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 
						 int positives=0;
						 int negatives=0;
						 if (CaptureConstants.SENTIMENT_VOLUME_POSITIVE.equals(queryObj.getFieldValue(CaptureConstants.TYPE_MEASURE)))
							 positives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 else
							 negatives = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 
						
						 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
						 if (dateSentiment == null){
							 dateSentiment = new HashMap<String, Integer[]>();
							 Integer sentiments[] = {positives, negatives};
							 dateSentiment.put(dc, sentiments);
						 }else{
							 Integer sentiments[] = dateSentiment.get(dc);
							 if (sentiments == null){
								 // ya estaba la fecha registrada pero no ese datachannel
								 sentiments = new Integer[2];
								 sentiments[0] = positives;
								 sentiments[1] = negatives;
							 }
							 else{
								 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
								 if (positives > 0) sentiments[0] = positives;
								 if (negatives > 0) sentiments[1] = negatives;
							 }
							 dateSentiment.put(dc, sentiments);
						 }
						 sentiment.put(date, dateSentiment);
					}// End while
				}// End if	
				
				Iterator<Date> itDates = (Iterator<Date>) sentiment.keySet().iterator();
				while (itDates.hasNext()) {
					// Vamos sacando por cada fecha
					Date dateFinal = itDates.next();
					Map<DataChannel, Integer[]> newDate = new HashMap<DataChannel, Integer[]>();
					Map<String, Integer[]> res = sentiment.get(dateFinal);
					Iterator<String> dcKey = res.keySet().iterator();
					while (dcKey.hasNext()) {
						// Vamos sacando por datachannel
						String dcFinal = dcKey.next();
						DataChannel newDC = new DataChannel();
						newDC.setChannelID(dcFinal);
						newDate.put(newDC, res.get(dcFinal));
					}
					sentimentFinal.put(dateFinal, newDate);
				}
				 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the sentiment", e);
		}
		return sentimentFinal;
	}
	
	// newVolume
	Map<Date, Map<DataChannel, Integer>> getVolume (List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		LOGGER.info("Getting volume in solr...");
		Map<Date, Map<DataChannel, Integer>> volume = new HashMap<Date, Map<DataChannel, Integer>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		
		try{
	
				// Preparamos todos los fields por los que vamos a filtrar
				String fqParameterDCs = CaptureConstants.DC_ID + ":(" +dataPoolIdList.toString().replace(",", " ").replace("[", "").replace("]", "")+")";
				String fqParameterDate = CaptureConstants.DATE +":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				String fqParameterTypeMeasure = CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.VOLUME;
				String fqParameterPeriodicity = CaptureConstants.PERIODICITY + ":" + per;
				String fqParameter = fqParameterDCs + " AND " + fqParameterDate + " AND " + fqParameterTypeMeasure + " AND " + fqParameterPeriodicity;
				//String query = CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.VOLUME +" AND " + CaptureConstants.PERIODICITY + ":" + per.toString();
				String query = "*:*";
				
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("rows", Integer.MAX_VALUE);
				LOGGER.info("The query: "+query + ",the fq query: " +fqParameter);	
				
				
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				
				if (!result.isEmpty()){
					
					Iterator<SolrDocument> itQ = result.iterator();
					while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 Date date = (Date) queryObj.getFieldValue(CaptureConstants.DATE);
						 String dc = (String) queryObj.getFieldValue(CaptureConstants.DC_ID);
						 DataChannel newDC = new DataChannel();
						 newDC.setChannelID(dc);
						
						 Integer medida = ((Double) queryObj.getFieldValue(CaptureConstants.MEASUREMENT)).intValue();
						 Map<DataChannel, Integer> dateVolume  = volume.get(date);
						 if (dateVolume== null){
							 dateVolume = new HashMap<DataChannel, Integer>();
							 dateVolume.put(newDC, medida);
						 }else{
							 dateVolume.put(newDC, medida);
						 }
						 volume.put(date, dateVolume);
					}// End while
				}// End if	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the volume", e);
		}
		LOGGER.info("Got volume in solr");
		return volume;
	}
	
	
	public Map<DataChannel, Map<String, Integer>> getTagCloud(List<String> dataPoolIdList, Date date) {
		// TODO Auto-generated method stub
		LOGGER.info("Getting tag cloud for: "+ date.toString() +" y los datachannels: " + dataPoolIdList.toString());
		Map<DataChannel, Map<String, Integer>> tagCloud = new HashMap<DataChannel, Map<String, Integer>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		XStream xstream = new XStream();
		
		// Creamos la fecha del fin del dia
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date); // Configuramos la fecha que se recibe
		calendar.add(Calendar.HOUR, 23); 
		calendar.add(Calendar.MINUTE, 59);
		calendar.add(Calendar.SECOND, 59);
		calendar.add(Calendar.MILLISECOND, 000);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date endDate = calendar.getTime();
		
		
		// Preparamos las fechas que vamos a pregutar
		String fqParameter = ""; // Parametro por defecto
		try {
			fqParameter = CaptureConstants.DATE+":["+ toUTCString(date) +" TO "+ toUTCString(endDate) + "]";
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Iterator<String> it = dataPoolIdList.iterator();
		while(it.hasNext()) {
			try {
				String dc = it.next();
				LOGGER.info("Getting tag cloud for: " + dc);
				Map<String, Integer> dcTagCloud = new HashMap<String, Integer>();
				ModifiableSolrParams solrParams = new ModifiableSolrParams();
				String query = CaptureConstants.DC_ID + ":" + dc + " AND " + CaptureConstants.TYPE_MEASURE + ":" + CaptureConstants.TAGCLOUD;
				solrParams.set("q", query);
				solrParams.set("fq", fqParameter);
				solrParams.set("sort", CaptureConstants.DATE+" desc");
				LOGGER.info("The query: "+ query + " ,the fq query: " + fqParameter);			
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				if (!result.isEmpty()){
					// Sacamos el primero, es decir el mas reciente
					SolrDocument queryObj = result.get(0);
					dcTagCloud = (HashMap <String, Integer>) xstream.fromXML((String) queryObj.getFieldValue(CaptureConstants.TAGCLOUD));
				}
				// Y añadimos al tagClud general el dc
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(dc);
				tagCloud.put (newDC, dcTagCloud);
				LOGGER.info("Got tag cloud for: " + dc);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, "ERROR generating tag cloud", e);
			}
		}
		LOGGER.info("Got tag cloud");
		return tagCloud;
	}
	
	public Map<DataChannel, Map<String, Integer>> getTagCloudAccumulated(
			List<String> dcIdList, Date initDate, Date endDate, int top) {
		// TODO Auto-generated method stub
		LOGGER.info("Getting top "+ top + " tag cloud for: "+ initDate.toString() +" to " +endDate.toString()+" y el datachannel: " + dcIdList.toString());
		Map<DataChannel, Map<String, Integer>> tagCloud = new HashMap<DataChannel, Map<String, Integer>>();
		SolrClient solr = this.solrServerPool.get(AGGREGATES_BATCH_INDEX);
		XStream xstream = new XStream();
				
		Iterator<String> it = dcIdList.iterator();
		while(it.hasNext()) {
			
			// vamos a sacar todos los resultados para un dc
			String dc = it.next();
			LOGGER.info("Getting dc tag cloud for: " + dc);
			String query = CaptureConstants.DC_ID + ":" + dc + " AND " + CaptureConstants.TYPE_MEASURE + ":" + "terms";
			ModifiableSolrParams solrParams = new ModifiableSolrParams();
			solrParams.set("q", query);
			String fqParameter;
			Map<String, Integer> dcTagCloud = new HashMap<String, Integer>();
			try {
				fqParameter = "endDate"+":["+ toUTCString(initDate) +" TO "+ toUTCString(endDate) + "]";
				solrParams.set("fq", fqParameter);
				LOGGER.info("The query: "+query + " ,the fq query: " +fqParameter);			
				SolrDocumentList result = solr.query(solrParams).getResults();
				LOGGER.info("Num resultados: "+ result.size());
				if (!result.isEmpty()){
					 // Acumulamos todos los resultados obtenidos para ese datachannel en esa fecha consultada.
					 Iterator<SolrDocument> itQ = result.iterator();
					 while (itQ.hasNext()) {
						 SolrDocument queryObj = itQ.next();
						 @SuppressWarnings("unchecked")
						 //Iterator<String> itKey = (Iterator<String>) queryObj.getTermOcc().keySet().iterator();
						 HashMap <String, Integer> ocurrences = (HashMap <String, Integer>) xstream.fromXML((String) queryObj.getFieldValue(CaptureConstants.MEASUREMENT));
						 Iterator<String> itKey = ocurrences.keySet().iterator();
						 while (itKey.hasNext()) {
							 String word = itKey.next();
							 Integer count = dcTagCloud.get(word);
							 if (count == null) dcTagCloud.put(word, ocurrences.get(word));
							 else dcTagCloud.put(word, count+ocurrences.get(word));
						 }
					 }
				}
//				List<TermOccurrenceAnalysis> queries = solr.query(solrParams).getBeans(TermOccurrenceAnalysis.class);
//				if (!queries.isEmpty()) {
//					 LOGGER.info("Results for the query in SOLR " + dc);
//					 // Acumulamos todos los resultados obtenidos para ese datachannel en esa fecha consultada.
//					 Iterator<TermOccurrenceAnalysis> itQ = queries.iterator();
//					 while (itQ.hasNext()) {
//						 TermOccurrenceAnalysis queryObj = itQ.next();
//						 @SuppressWarnings("unchecked")
//						 //Iterator<String> itKey = (Iterator<String>) queryObj.getTermOcc().keySet().iterator();
//						 Set<String> words = queryObj.getTermOcc().keySet();
//						 Iterator<String> itKey = words.iterator();
//						 while (itKey.hasNext()) {
//							 String word = itKey.next();
//							 Integer count = dcTagCloud.get(word);
//							 if (count == null) dcTagCloud.put(word, queryObj.getTermOcc().get(word));
//							 else dcTagCloud.put(word, count+queryObj.getTermOcc().get(word));
//						 }
//					 }
//				}
				
				
				// TODO order and get top N from dcTagCloud
				
				
				// Y añadimos al tagClud general el dc
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(dc);
				tagCloud.put (newDC, dcTagCloud);
				LOGGER.info("Got dc tag cloud for: " + dc);
			} catch (ParseException | SolrServerException | IOException e) {
				LOGGER.log(Level.WARNING, "Error in getTagCloudAcumulated:", e);
			}
		}
		LOGGER.info("Got tag cloud");
		return tagCloud;
	}
	
}
