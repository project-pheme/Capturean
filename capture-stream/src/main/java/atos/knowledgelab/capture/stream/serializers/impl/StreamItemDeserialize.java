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
package atos.knowledgelab.capture.stream.serializers.impl;

import java.io.StringReader;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import com.sun.jersey.api.json.JSONUnmarshaller;



import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.exception.CaptureException;
//import atos.knowledgelab.capture.stream.config.JSONJAXBContextResolver;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;

public class StreamItemDeserialize implements IDeserialize<StreamItem> {
	
	//private static JAXBContext jc;
	//private JSONUnmarshaller unmarshaller;
	static Logger LOGGER = Logger.getLogger(StreamItemDeserialize.class.getName());
	private ObjectMapper mapper;

	public StreamItemDeserialize() {
	    try {
//	    	if (jc == null){
//	    		jc = new JSONJAXBContextResolver().getContext(StreamItem.class);
//	    	}
	    	//this.unmarshaller = ((JSONJAXBContext) jc).createJSONUnmarshaller();
	    	mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized StreamItem deserialize(String msg) throws CaptureException {
		try {
			//LOGGER.info("A que se deben esas excepciones: "+msg);
			//return unmarshaller.unmarshalFromJSON(new StringReader(msg), StreamItem.class);
			StreamItem streamItemElement = mapper.readValue(msg, StreamItem.class);
			return streamItemElement;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOGGER.info("A que se deben esas excepciones: "+msg);
			e.printStackTrace();
			LOGGER.severe(" ERROR in deserializing a Stream Item: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in deserializing a Stream Item: "
							+ e.getMessage(), e);
		}
	}
	
}
