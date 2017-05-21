package atos.knowledgelab.capture.stream.translator;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.codehaus.jackson.impl.JsonWriteContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import com.sun.jersey.api.json.JSONMarshaller;



import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.pheme.format.v2.StreamItem;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class StreamProducer {

	private Producer<String, String> kafkaProducer;
	private static Logger LOGGER = Logger.getLogger(StreamProducer.class.getName());
	private String kafkaTopic;
	private StreamProducerConfig configuration;
	private JAXBContext jc;
//	private JSONMarshaller marshaller;
	private String serialisationFormat = "json";
	private ObjectMapper mapper;
	public StreamProducer(StreamProducerConfig config) throws JAXBException {

		/** Kafka producer properties **/
//		Properties props = new Properties();
//		// props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));
//
//		props.put("metadata.broker.list", "localhost:9092");
//		props.put("serializer.class", "kafka.serializer.StringEncoder");
//		props.put("request.required.acks", "1");
		// props.put("partitioner.class", props.getProperty("partitioner.class"));

		ProducerConfig kafkaConfig = new ProducerConfig(config);
		kafkaProducer = new Producer<String, String>(kafkaConfig);

		kafkaTopic = config.getProperty("kafka.topic");
		//kafkaTopic = "test";

		//create JAXB context for stream item class
		//NOTE: Do not invoke this in a loop, to avoid classloader leak problem.

		

		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
	}

	/*
	 * Sending a single StreamItem object through the kafka channel.
	 * Please note that this method is using JAXB Marshaller object, that is NOT
	 * thread safe. For now this method is "synchronized".  
	 * 
	 */
	public synchronized void send(StreamItem item) {
		// The EventBuilder is used to build an event using the
		// the raw JSON of a tweet
		//LOGGER.info(item.getTweet().getUserScreenName() + ": " + item.getTweet().getText());

		// create object to be send
		// StreamItem si = new StreamItem();
		// si.setDataChannelId(dataChannelId);
		// si.setDataSourceId(dataSourceId);
		// si.setQueryData(query);
		// si.setTweet(TweetUtils.getTweetFromStatus(status));

		//Writer writer = new StringWriter();

		// serialize the java class to XML file
		try {
			//JAXBContext jc = JAXBContext.newInstance(StreamItem.class);
			//Marshaller marshaller = jc.createMarshaller();
			// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			String s = mapper.writeValueAsString(item.getPhemeSource());
			
			// KeyedMessage<String, String> data = new KeyedMessage<String,
			// String>(kafkaTopic, DataObjectFactory.getRawJSON(status));
			KeyedMessage<String, String> data = new KeyedMessage<String, String>(kafkaTopic, s);
			//LOGGER.info(s);
			kafkaProducer.send(data);
			

			
		} catch (Exception e) {
			LOGGER.info("Stream producer: Error while sending tweet! " + e);
			LOGGER.log(Level.SEVERE, e.getMessage(), e);			
			e.printStackTrace();	
		}


	
		
	}
}
