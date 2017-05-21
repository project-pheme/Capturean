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
package pheme.gathering.kafka;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.kafka.clients.producer.ProducerConfig;

import pheme.gathering.search.TweetManager;
import twitter4j.FilterQuery;
import twitter4j.TwitterStream;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize;

public class TwitterStreamProducer {
	
    /** The actual Twitter stream. It's set up to collect raw JSON data */
    private TwitterStream twitterStream;
    private String[] keywords = new String[1];
    private final static Logger LOGGER = Logger.getLogger(TwitterStreamProducer.class.getName());
    private TwitterStatusListener twitterStatusListener;
    private String kafkaTopic;

    private QueryData query;

    private StreamProducer<StreamItem> sp;
    
    public TwitterStreamProducer (TwitterStream tStream, QueryData query, String type) throws Exception{
    	this.twitterStream = tStream;
    	this.keywords[0] = query.getQuery();

    	this.query = query;

    	
		/** Kafka producer properties **/
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));
		
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
		props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));
		
		conf.put("metadata.broker.list",  props.getProperty("metadata.broker.list"));
		conf.put("serializer.class",  props.getProperty("serializer.class"));
		conf.put("request.required.acks",  props.getProperty("request.required.acks"));
		conf.put("metadata.broker.list", props.getProperty("metadata.broker.list"));
		//conf.put("zookeeper.connect", "localhost:2181");
		conf.put("kafka.topic", props.getProperty("kafka.topic"));
		
		//THIS IS A BUGFIX IN KAFKA BROKER v0.10.0.0, see:
		//https://github.com/dpkp/kafka-python/issues/718
		//https://issues.apache.org/jira/browse/KAFKA-3789
		//https://github.com/apache/kafka/pull/1467
		
		conf.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
		
		kafkaTopic = props.getProperty("kafka.topic");
		
		ISerialize<StreamItem> serializer = new StreamItemSerialize();
		sp = new StreamProducer<StreamItem>(conf, serializer);
		
    }

    public void start() {
    	
    	LOGGER.info(" #### STARTING TWITTER STREAM API ");
    	
    	twitterStatusListener = new TwitterStatusListener(twitterStream, sp, kafkaTopic, query);
    	
    	//TODO make an enum type for queryType keyword, locations, lang
    	//in general this is some tricky shit.
		
    	try {
        	FilterQuery fq = new FilterQuery();  
        	
        	//query grammar is the following:
        	//<keywords>;<language>;<geo>;<follow>
        	String[] s = keywords[0].split(";");
        	if (s.length == 4) {
        		fq.track(s[0].split(","));
        		fq.language(s[1].split(","));
        		fq.locations(getLocationCoordinates(s[2]));
        		fq.follow(getFollowees(s[3]));
        	}
        	if (s.length == 3) {
        		fq.track(s[0].split(","));
        		fq.language(s[1].split(","));
        		fq.locations(getLocationCoordinates(s[2]));
        	}
        	if (s.length == 2) {
        		fq.track(s[0].split(","));
        		fq.language(s[1].split(","));
        	}
        	if (s.length == 1) {
        		fq.track(s[0].split(","));
        	}
        	
    		
    		// Bind the listener
    		twitterStream.addListener(twitterStatusListener);
    		
    		//filter command starts twitter stream
    		twitterStream.filter(fq);
    		
    	} catch (Exception e) {
    		LOGGER.warning("Failed to start Twitter stream: query parsing error " + e.getMessage());

    	}
        
		
		
    }
    
    public void shutdown() {
    	
    	LOGGER.info(" #### SHUTTING DOWN TWITTER STREAM API ");
    	//twitterStream.removeListener(twitterStatusListener);
    	twitterStream.shutdown();
    	//sp.close();
    }
    
    public double[][] getLocationCoordinates(String s) {
    	try {
    		String[] location = s.split(",");
    		double d1 = Double.parseDouble(location[0]);
    		double d2 = Double.parseDouble(location[1]);
    		double d3 = Double.parseDouble(location[2]);
    		double d4 = Double.parseDouble(location[3]);
    		return new double[][] {{d1, d2},{d3, d4}};
    	} catch (Exception e) {
    		LOGGER.info("Twitter stream query: geolocation string " + s + " is invalid");
    		return null;
    	}
    	
    }
    
    public long[] getFollowees(String s) {
    	try {
    		String[] followees = s.split(",");
    		//ArrayList<Long> fArray = new ArrayList<Long>();
    		long[] fArray = new long[followees.length];
    		int i = 0;
    		for (String f: followees) {
    			fArray[i] = Long.parseLong(f);
    			i++;
    		}
    		
    		return fArray;
    	} catch (Exception e) {
    		LOGGER.info("Twitter stream query: geolocation string " + s + " is invalid");
    		return null;
    	}
    	
    }
    
}
