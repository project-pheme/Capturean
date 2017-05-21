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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import twitter4j.TwitterException;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.util.CaptureConstants;

public class JPAHBaseManager {

	private static JPAHBaseManager instance = null;
	private static EntityManagerFactory emf;
	private static EntityManager em;
	private final static Logger LOGGER = Logger.getLogger(JPAHBaseManager.class
			.getName());
	private final static Logger LOGGER_fail = Logger.getLogger("Hbase_fail");

	protected JPAHBaseManager() {
		emf = Persistence.createEntityManagerFactory("Capture");
		em = emf.createEntityManager();
	}

	public static JPAHBaseManager getInstance() {
		if (instance == null) {
			instance = new JPAHBaseManager();
		}
		return instance;
	}

	public void addDataChannelToHBase(DataChannel dChannel)
			throws CaptureException {
		//EntityManager em = emf.createEntityManager();
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
			throw new CaptureException(
					" ERROR in transaction adding a new data channel: "
							+ e.getMessage(), e);

		} finally {

			if (tx.isActive()) {
				tx.rollback();
			}

			//em.close();
		}
	}

	/**
	 * Get all Data Channels from HBase
	 * 
	 * @param numResults
	 * @param value
	 * @throws IOException
	 */
	public DataChannelList getDataChannelsFromHBase(int numResults, int page)
			throws CaptureException {

		//EntityManager em = emf.createEntityManager();
		DataChannelList dChannelList = new DataChannelList();
		DataChannel dc;
		String order = " ORDER BY dc.updateDate descending";
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			LOGGER.info(" #### RUNNING QUERY : "
					+ "SELECT dc FROM DataChannel dc " + order);
			TypedQuery<DataChannel> q = em.createQuery("SELECT dc FROM DataChannel dc " + order, DataChannel.class)
					.setFirstResult((page - 1) * numResults)
					.setMaxResults(numResults);

			List <DataChannel> results = q.getResultList();
			if (results != null && !results.isEmpty()) {
				Iterator <DataChannel> iter = results.iterator();
				while (iter.hasNext()) {
					Object obj = iter.next();
					System.out.println(">  " + obj);
					dChannelList.addDataChannel((DataChannel) obj);
				}
			}
			tx.commit();

		} catch (Exception e) {
			e.printStackTrace();
			throw new CaptureException(
					" ERROR recovering Data Channels from NO-SQL storage", e);

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}			
			//em.close();
		}
		return dChannelList;
	}

	/**
	 * Get the Data Channel with id = dcId from HBase
	 * 
	 * @param dcId
	 * @throws CaptureException
	 */
	public DataChannel getDataChannelFromHBase(String dcId)
			throws CaptureException {
		//EntityManager em = emf.createEntityManager();
		DataChannel dc = new DataChannel();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			dc = em.find(DataChannel.class, dcId);
			tx.commit();

		} catch (Exception e) {
			throw new CaptureException(" ERROR recovering Data Channel " + dcId
					+ " from NO-SQL storage: " + e.getMessage(), e);

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}			
			//em.close();
		}
		return dc;
	}

	/**
	 * Delete the Data Channel with id = dcId from HBase
	 * 
	 * @param dcId
	 * @return
	 * @throws CaptureException
	 * @throws IOException
	 */
	public void deleteDataChannelFromHBase(String dcId) throws CaptureException {
		//EntityManager em = emf.createEntityManager();
		DataChannel dc;
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			dc = em.find(DataChannel.class, dcId);
			dc.setStatus(CaptureConstants.NON_ACTIVE);
			// em.remove(dc);
			em.merge(dc);
			tx.commit();

		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction deleting data channel" + dcId + ": "
							+ e.getMessage(), e);
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			
			//em.close();
		}
	}

	public void updateDataChannelInHBase(DataChannel newDataChannel)
			throws CaptureException {

		//EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			DataChannel dc = em.find(DataChannel.class,
					newDataChannel.getChannelID());
			//em.detach(dc);
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
						//em.detach(persistentDs);
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
			throw new CaptureException(
					" ERROR in transaction updating existing data channel: "
							+ e.getMessage(), e);

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			//if(em != null)
			//em.close();
		}

	}

	private Object findDs(EntityManager em, Object ds) {
		try {
			return em.find(ds.getClass(), ((DataSource) ds).getSourceID());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Get the User data for a user with id = userId from HBase
	 * 
	 * @param dcId
	 * @throws TwitterException
	 * @throws IOException
	 */
	public TwitterUser getUserProfile(String userId) throws Exception {

		//EntityManager em = emf.createEntityManager();
		TwitterUser uProf = new TwitterUser();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			uProf = em.find(TwitterUser.class, userId);
			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
			//if(em != null)
			//em.close();
		}
		return uProf;
	}

	/* Utility MEthods */
	public void merge(Object newObj, Object obj) throws Exception {
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

	/*
	 * public List<String> getActiveQueries () { EntityManager em =
	 * emf.createEntityManager(); ArrayList<String> dChannelIdList = new
	 * ArrayList<String>(); DataChannel dc; String order =
	 * " ORDER BY dc.updateDate descending"; EntityTransaction tx =
	 * em.getTransaction();
	 * 
	 * try { tx.begin();
	 * 
	 * System.out.println(" #### RUNNING QUERY : " +
	 * "SELECT dc FROM DataChannel dc " + order); Query q =
	 * em.createQuery("SELECT dc.channelID FROM DataChannel dc " + order);
	 * 
	 * List results = q.getResultList(); if (results!=null &&
	 * !results.isEmpty()) { Iterator iter = results.iterator(); while
	 * (iter.hasNext()) { Object obj = iter.next(); System.out.println(">  " +
	 * obj); dChannelIdList.add((String) obj); } } tx.commit();
	 * 
	 * } finally { if (tx.isActive()) { tx.rollback(); } em.close(); } return
	 * dChannelIdList; }
	 */

	public void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID) throws CaptureException {

		//EntityManager em = emf.createEntityManager();
		EntityTransaction tx = null;

		LOGGER.info(" >>> STATRING TX TO ADD TWEET LIST ... ");
		Tweet tweet = null;
		try {
			tx = em.getTransaction();
			tx.begin();
			if (tweetList != null && !tweetList.getTweets().isEmpty()) {

				Iterator<Tweet> itTweets = tweetList.getTweets().iterator();
				while (itTweets.hasNext()) {
					tweet = itTweets.next();
					
					if(tweet.getDataID() == null)
						throw new CaptureException("Recieved tweet has no data ID");
					
					Tweet auxTweet = em.find(Tweet.class, tweet.getDataID());
					if(auxTweet != null){
						LOGGER.fine("Tweet is already stored in Capture: " + tweet.getDataID());
						tweet = auxTweet;						
						if(tweet.getDataID() == null)
							throw new CaptureException("Tweet from storage has no data ID");
					}
					
					TwitterDataSource ds = em.find(TwitterDataSource.class,
							dsID);
					
					if (ds != null){ 
						tweet.getDataSources().add(ds);
						ds.getTweets().add(tweet);
						em.merge(ds);
					}
						
					em.merge(tweet);

					// LOGGER.info(" >>> TWEET " + tweet.getId() + " ADDED: " +
					// tweet.getText());
				}
			}
			tx.commit();
			LOGGER.info(" >>> TX COMMITTED ... ");
		} catch (Exception e) {
			//LOGGER_fail.info(tweet.getRawJson());
			LOGGER.info(" >>> TX ERROR! ... " + e);
			throw new CaptureException(
					" ERROR in transaction adding a new tweet: "
							+ e.getMessage(), e);

		} finally {

			if (tx != null && tx.isActive())
				tx.rollback();
			LOGGER.info(" >>> CLOSING TX and EM TO ADD TWEET LIST ... ");
			//if(em != null)
			//em.close();
		}

	}

}
