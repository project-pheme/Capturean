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
import java.io.StringWriter;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import pheme.gathering.search.TweetManager;
import twitter4j.OEmbed;
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
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TweetList;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.ifaces.CaptureStorageIf;
import atos.knowledgelab.capture.util.CaptureConstants;

public class CaptureFacadeStandalone extends CaptureGenericFacade {

	private static CaptureFacadeStandalone instance;

	private final static Logger LOGGER = Logger
			.getLogger(CaptureFacadeStandalone.class.getName());

	private QueryQueue queryQueue;

	private CaptureStorageIf cs = null;

	CaptureFacadeStandalone() throws Exception {
		queryQueue = QueryQueue.getInstance();
	}
	
	@Override
	public void setStorage(CaptureStorageIf cs) throws Exception {
		this.cs = cs;
		super.setStorage(cs);
	}

	@Override
	public void addTweetsToDataChannel(TweetList tweetList, String dcID,
			String dsID, QueryData keywords) throws Exception {

		try {
			//JPAHBaseManager.getInstance().addTweetsToDataChannel(tweetList,dcID, dsID);
			cs.addTweetsToDataChannel(tweetList, dcID, dsID);
			
		} catch (Exception e) {
			throw new CaptureException(
					" ERROR in transaction adding tweets to DC: " + dcID + ": "
							+ e.getMessage(), e);
		}
	}

	@Override
	public QueryQueue getQueryQueue() {
		return this.queryQueue;
	}

	public static CaptureFacadeStandalone getInstance() throws Exception {
		if (instance == null)
			instance = new CaptureFacadeStandalone();
		return instance;
	}


}
