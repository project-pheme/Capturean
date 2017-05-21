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
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize;
import atos.knowledgelab.capture.util.CaptureConstants;

public class CaptureFacadeStreamAndStore extends CaptureGenericFacade {

	private static CaptureFacadeStreamAndStore instance;

	private final static Logger LOGGER = Logger.getLogger(CaptureFacadeStreamAndStore.class.getName());

	private QueryQueue queryQueue;

	private CaptureStorageIf cs = null;

	private StreamProducer sp = null;

	CaptureFacadeStreamAndStore() throws Exception {
		queryQueue = QueryQueue.getInstance();

		initKafka("kafka-search.properties");
	}

	@Override
	public void setStorage(CaptureStorageIf cs) throws Exception {
		this.cs = cs;
		super.setStorage(cs);
	}

	@Override
	public void addTweetsToDataChannel(TweetList tweetList, String dcID, String dsID, QueryData keywords) throws Exception {

		try {
			Iterator<Tweet> it = tweetList.getTweets().iterator();

			while (it.hasNext()) {
				Tweet tweet = it.next();

				StreamItem si = new StreamItem();
				si.setDataChannelId(dcID);
				si.setDataSourceId(dsID);

				QueryData qd = new QueryData();
				qd.setDcID(dcID);
				qd.setDsID(dsID);

				si.setQueryData(qd);

				// Set the tweet into the StreamItem
				si.setTweet(tweet);

				try {
					sp.send(si);
				} catch (Exception e) {
					throw new CaptureException("Error while sending tweet message to kafka: " + e.getMessage(), e);
				}

			}

			cs.addTweetsToDataChannel(tweetList, dcID, dsID);

		} catch (Exception e) {
			throw new CaptureException(" ERROR in transaction adding tweets to DC: " + dcID + ": " + e.getMessage(), e);
		}
	}

	@Override
	public QueryQueue getQueryQueue() {
		return this.queryQueue;
	}

	public static CaptureFacadeStreamAndStore getInstance() throws Exception {
		if (instance == null)
			instance = new CaptureFacadeStreamAndStore();
		return instance;
	}

	private void initKafka(String kafkaPF) throws Exception {
		// Kafka producer properties
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream(kafkaPF));

		props.put("metadata.broker.list", props.getProperty("metadata.broker.list"));
		props.put("serializer.class", props.getProperty("serializer.class"));
		props.put("request.required.acks", props.getProperty("request.required.acks"));
		// props.put("partitioner.class",
		// props.getProperty("partitioner.class"));

		// ProducerConfig config = new ProducerConfig(props);
		// kafkaProducer = new Producer<String, String>(config);

		StreamProducerConfig conf = new StreamProducerConfig();

		// TODO: load configuration from file?
		// props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));

		// Here it is important to specify the broker (kafka node or nodes)
		// more details here: http://kafka.apache.org/08/configuration.html
		props.load(this.getClass().getClassLoader().getResourceAsStream(kafkaPF));

		conf.put("metadata.broker.list", props.getProperty("metadata.broker.list"));
		conf.put("serializer.class", props.getProperty("serializer.class"));
		conf.put("request.required.acks", props.getProperty("request.required.acks"));
		conf.put("metadata.broker.list", props.getProperty("metadata.broker.list"));
		// conf.put("zookeeper.connect", "localhost:2181");
		conf.put("kafka.topic", props.getProperty("kafka.topic"));

		ISerialize<StreamItem> serializer = new StreamItemSerialize();
		sp = new StreamProducer(conf, serializer);
	}

}
