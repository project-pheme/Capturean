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
package atos.knowledgelab.capture.stream.example.simple;

import java.util.Date;
import java.util.Properties;

import kafka.producer.ProducerConfig;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.config.ConsumerClientConfig;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.capture.stream.consumer.ConsumerClient;
import atos.knowledgelab.capture.stream.producer.StreamProducer;
import atos.knowledgelab.capture.stream.serializers.ISerialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize;

public class ExampleProducer {

	public static void main(String[] args) throws InterruptedException {
		
		//Configuration of the producer
		StreamProducerConfig conf = new StreamProducerConfig();
		
		// TODO: load configuration from file?
		// props.load(this.getClass().getClassLoader().getResourceAsStream("kafka.properties"));

		//Here it is important to specify the broker (kafka node or nodes)
		//more details here: http://kafka.apache.org/08/configuration.html
		conf.put("metadata.broker.list", "localhost:9092");
		conf.put("zookeeper.connect", "localhost:2181");
		conf.put("serializer.class", "kafka.serializer.StringEncoder");
		conf.put("request.required.acks", "1");
		conf.put("kafka.topic", "test");
		// props.put("partitioner.class", props.getProperty("partitioner.class"));

		ISerialize<StreamItem> serializer = new StreamItemSerialize();
		
		try {
			StreamProducer sp = new StreamProducer(conf, serializer);
			
			for (int i=0; i<1000; i++) {
				StreamItem si = new StreamItem();
				Tweet t = new Tweet();
				t.setText("Tweet number " + i);
				t.setCreatedAt(new Date());
				t.setUserScreenName("User1");
				si.setTweet(t);
				sp.send(si);	
				
				Thread.sleep(500);
			}
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

}
