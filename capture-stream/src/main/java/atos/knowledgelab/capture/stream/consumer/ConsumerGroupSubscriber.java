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
package atos.knowledgelab.capture.stream.consumer;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import atos.knowledgelab.capture.client.messages.MessageDispatcher;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;

public class ConsumerGroupSubscriber<T> {
    private final ConsumerConnector consumer;
    private final String topic;
    private ExecutorService executor;
    private final MessageDispatcher dispatcher;
    private final IDeserialize<T> deserializer;
	private final static Logger LOGGER = Logger.getLogger(ConsumerGroupSubscriber.class.getName());

	
    public ConsumerGroupSubscriber(
    		String a_zookeeper, 
    		String a_groupId, 
    		String a_topic, 
    		MessageDispatcher<T> dispatcher,
    		IDeserialize<T> deserializerObject) {
    	ConsumerConfig cc = createConsumerConfig(a_zookeeper, a_groupId);
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(cc);
        
        this.topic = a_topic;
        this.dispatcher = dispatcher;
        this.deserializer = deserializerObject;
    }
 
    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
    }
 
    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        //System.out.println(consumerMap.toString());
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
 
        // Launch all the threads within the threadpool executor service
        executor = Executors.newFixedThreadPool(a_numThreads);
 
        //for each kafka stream, assign a worker thread that will handle the messages 
        //all consumer threads will be in the same kafka consumer group
        int threadNumber = 0;
        try {
            for (final KafkaStream stream : streams) {
                executor.submit(new ConsumerThread(stream, threadNumber, dispatcher, deserializer));
                threadNumber++;
            }
        	
        } catch (JAXBException e) {
        	LOGGER.info("Can't initialize consumer threads. JAXB Context initialisation error.");
        	e.printStackTrace();
        }
    }
 
    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        //props.put("zookeeper.session.timeout.ms", "20000");
        props.put("rebalance.backoff.ms", "20000");
        //props.put("zookeeper.sync.time.ms", "1000");
        //props.put("auto.commit.interval.ms", "1000");
 
        return new ConsumerConfig(props);
    }


}
