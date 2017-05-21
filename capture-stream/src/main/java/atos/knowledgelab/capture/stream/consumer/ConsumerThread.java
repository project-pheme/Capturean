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

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import com.sun.jersey.api.json.JSONMarshaller;
//import com.sun.jersey.api.json.JSONUnmarshaller;




import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.client.messages.MessageDispatcher;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;
//import atos.knowledgelab.capture.newbean.PhemeSource;
//import atos.knowledgelab.capture.stream.config.JSONJAXBContextResolver;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;

public class ConsumerThread<T> implements Runnable {
    private KafkaStream stream;
    private int threadNumber;
    private MessageDispatcher<T> dispatcher;
	private final static Logger LOGGER = Logger.getLogger(ConsumerThread.class.getName());
	private JAXBContext jc;
	//private JSONUnmarshaller unmarshaller;
	private ObjectMapper mapper;
	private final IDeserialize<T> deserializer;
	
    public ConsumerThread(
    		KafkaStream a_stream, 
    		int a_threadNumber, 
    		MessageDispatcher dispatcher,
    		IDeserialize<T> deserializerObject) throws JAXBException {
        
    	this.threadNumber = a_threadNumber;
        this.stream = a_stream;
        this.dispatcher = dispatcher;
        this.deserializer = deserializerObject;
        
        //this.jc = new JSONJAXBContextResolver().getContext(StreamItem.class);
		//this.unmarshaller = ((JSONJAXBContext) jc).createJSONUnmarshaller();
        mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
    }
 
    
    public void run() {
        ConsumerIterator<byte[], byte[]> it = this.stream.iterator();
        
        //the main loop where we receive messages form the kafka broker
        while (it.hasNext()) {

        	//unmarshall xml message:
            
			try {
	        	String msg = new String(it.next().message(), "UTF-8");
	        	//LOGGER.info("Thread " + threadNumber + ": " + msg);

	        	//StreamItem streamItemElement = unmarshaller.unmarshalFromJSON(new StringReader(msg), StreamItem.class);
				T streamItemElement = deserializer.deserialize(msg);
				
				//StreamItem streamItemElement = mapper.readValue(msg, StreamItem.class);
				
				
				dispatcher.offer(streamItemElement);
				
			} catch (IOException e1) {
				// TODO check for parser exception, and in case of invalid character 
				// try to convert to 0xFFFD or drop. and repeat unmarshall
				// illegal chars: 0x8, 0x19, 0x10
				LOGGER.log(Level.SEVERE, "Stream Consumer: Deserialization problem: ", e1);
				e1.printStackTrace();
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Stream Consumer: Deserialization problem: ", e);
				e.printStackTrace();
			}
            
        }
        
        System.out.println("Shutting down Thread: " + this.threadNumber);
    }
}