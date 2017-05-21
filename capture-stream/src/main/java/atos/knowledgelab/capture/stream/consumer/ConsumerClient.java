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

import java.util.Observer;
import java.util.logging.Logger;

import atos.knowledgelab.capture.client.messages.MessageDispatcher;
import atos.knowledgelab.capture.stream.config.ConsumerClientConfig;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;

public class ConsumerClient<T> {
	private final static Logger LOGGER = Logger.getLogger(ConsumerClient.class.getName());
	
	private final MessageDispatcher dispatcher;
	private ConsumerGroupSubscriber consumerGroupSubscriber;
	
	private ConsumerClientConfig configuration;
	
	private IDeserialize<T> streamDeserializer; 
	
	public ConsumerClient(ConsumerClientConfig config, IDeserialize<T> deserializerObject) {
		//store the configuration internally
		configuration = config;
		streamDeserializer = deserializerObject;
		
		//setup storage manager classes
		LOGGER.info("Initializing consumer application.");
		//print current client configuration
		LOGGER.info("Consumer configuration: ");
		LOGGER.info(configuration.toString());
		
		//setup message dispatcher
		LOGGER.info("Setting up message dispatcher...");
		dispatcher = new MessageDispatcher<T>();
		
		//setting up kafka consumers, but don't start them yet.
		//allow user to setup the observers, etc.
		LOGGER.info("Setting up Kafka consumers...");

		consumerGroupSubscriber = new ConsumerGroupSubscriber<T>(
				configuration.getZooKeeperEndPoint(), 
				configuration.getGroupId(), 
				configuration.getTopicName(),
				dispatcher,
				streamDeserializer);
		LOGGER.info("Kafka consumers ready!");
        
	}
	
	public void start() {
		consumerGroupSubscriber.run(configuration.getThreadNum());
	}
	
	public void stop() {
		consumerGroupSubscriber.shutdown();
	}
	
	public MessageDispatcher<T> getDispatcher() {
		return dispatcher;
	}
	
	public void subscribeObserver(Observer o) {
		dispatcher.addObserver(o);
	}
	
	public void unsubscribeObserver(Observer o) {
		dispatcher.deleteObserver(o);
	}
}
