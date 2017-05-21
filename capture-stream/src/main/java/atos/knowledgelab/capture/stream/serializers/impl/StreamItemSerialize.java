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

import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.jersey.api.json.JSONJAXBContext;
//import com.sun.jersey.api.json.JSONMarshaller;





import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.exception.CaptureException;
//import atos.knowledgelab.capture.stream.config.JSONJAXBContextResolver;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;
import atos.knowledgelab.capture.stream.serializers.ISerialize;

public class StreamItemSerialize implements ISerialize<StreamItem> {

	private static JAXBContext jc = null;
	//private JSONMarshaller marshaller;
	private StringWriter writer;
	private String serialisationFormat = "json";
	static Logger LOGGER = Logger.getLogger(StreamItemSerialize.class.getName());
	private ObjectMapper mapper;

	public StreamItemSerialize() {
		try {
//			if (jc == null) {
//				jc = new JSONJAXBContextResolver().getContext(StreamItem.class);
//			}
			//this.marshaller = ((JSONJAXBContext) jc).createJSONMarshaller();
			mapper = new ObjectMapper();
			mapper.setSerializationInclusion(Include.NON_NULL);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String serialize(StreamItem item) throws CaptureException {
		//StreamItem si = (StreamItem) item;
		//this.writer = new StringWriter();
		//JAXBElement<StreamItem> jaxbElement = new JAXBElement<StreamItem>(new QName("streamItem"), StreamItem.class, item);
		try {
			//marshaller.marshallToJSON(jaxbElement, writer);
			String message = mapper.writeValueAsString(item);
			
			return message;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.severe(" ERROR in serializing a Stream Item: "
					+ e.getMessage());
			throw new CaptureException(
					" ERROR in serializing a Stream Item: "
							+ e.getMessage(), e);
		}
		
	}

	





	
}
