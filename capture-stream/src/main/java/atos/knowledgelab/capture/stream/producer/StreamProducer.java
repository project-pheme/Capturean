/*******************************************************************************
 * Copyright (C) 2016  ATOS Spain S.A.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
package atos.knowledgelab.capture.stream.producer;

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.stream.StreamItem;
//import atos.knowledgelab.capture.stream.config.JSONJAXBContextResolver;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.serializers.ISerialize;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import com.sun.jersey.api.json.JSONMarshaller;

public class StreamProducer<T> {

	private Producer<String, String> kafkaProducer;
	private static Logger LOGGER = Logger.getLogger(StreamProducer.class.getName());
	private String kafkaTopic;	
	private String streamSerializer;
	private JAXBContext jc;
	//private JSONMarshaller marshaller;
	private ObjectMapper mapper;
	private final String defaultSerializer = "atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize";
	private ISerialize<T> serializer;
	
	public StreamProducer(StreamProducerConfig config, ISerialize<T> serializerObject) throws Exception {

		/** Kafka producer properties **/

		ProducerConfig kafkaConfig = new ProducerConfig(config);
		kafkaProducer = new Producer<String, String>(kafkaConfig);

		kafkaTopic = config.getProperty("kafka.topic");
		streamSerializer = config.getProperty("stream.serializer.class");
		
		if (streamSerializer == null || streamSerializer.length() == 0) {
			streamSerializer = defaultSerializer;
		}
		//create JAXB context for stream item class
		//NOTE: Do not invoke this in a loop, to avoid classloader leak problem.
		//this.jc = new JSONJAXBContextResolver().getContext(StreamItem.class);
		//this.marshaller = ((JSONJAXBContext) jc).createJSONMarshaller();
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		
		serializer = serializerObject; 
	}

	/*
	 * Sending a single StreamItem object through the kafka channel.
	 * Please note that this method is using JAXB Marshaller object, that is NOT
	 * thread safe. For now this method is "synchronized".  
	 * 
	 */
	public synchronized void send(T item) {

		Writer writer = new StringWriter();

		// serialize the java class to JSON file
		try {

			//JAXBElement<StreamItem> jaxbElement = new JAXBElement<StreamItem>(new QName("streamItem"), StreamItem.class, item);
			//marshaller.marshallToJSON(jaxbElement, writer);
			
			//String message = mapper.writeValueAsString(item);
			
			String message = serializer.serialize(item);
			
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(kafkaTopic, message);
			kafkaProducer.send(data);
			
		} catch (Exception e) {
			LOGGER.info("Stream producer: Error while sending tweet! " + e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);						
		}


	}
	
	/*
	 * Sending a single Tweet object through the kafka channel.
	 * Please note that this method is using JAXB Marshaller object, that is NOT
	 * thread safe. For now this method is "synchronized".  
	 * 
	 */
	public synchronized void send(Tweet tweet) {
		// The EventBuilder is used to build an event using the
		// the raw JSON of a tweet

		// create object to be send
		StreamItem si = new StreamItem();

		si.setTweet(tweet);

		Writer writer = new StringWriter();

		// serialize the java class to XML file
		try {
			
			//JAXBElement<StreamItem> jaxbElement = new JAXBElement<StreamItem>(new QName("streamItem"), StreamItem.class, si);
			//marshaller.marshallToJSON(jaxbElement, writer);
			String message = mapper.writeValueAsString(tweet);
			
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(kafkaTopic, message);
			kafkaProducer.send(data);
			
		} catch (Exception e) {
			LOGGER.info("Stream producer: Error while sending tweet! ");
			LOGGER.log(Level.SEVERE, e.getMessage(), e);			
		}

	}
	
	public void close() {
		kafkaProducer.close();
	}
}
