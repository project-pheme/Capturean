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
package atos.knowledgelab.capture.reddit;

import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.MessageBean;
import atos.knowledgelab.capture.bean.MessageData;
import atos.knowledgelab.capture.reddit.datachannels.DatachannelManager;

@Path("reddit/datachannel")
public class ServiceTest {

	private static final String OP_OK = "200 OK";
	private static final String OP_ERR = "500 Internal Server Error";

	private Logger log = Logger.getLogger(ServiceTest.class.getName());


	
	@POST
	@Produces({"application/json", "application/xml"})
	public MessageBean addDataChannel(DataChannel dataChannel) {
		MessageBean msg = new MessageBean();
		try {
			
			//String dcId = generateCrc32Id();
			//dataChannel.setChannelID(dcId);
			
			String dcId = "fd98d61e";
			dataChannel.setChannelID(dcId);
			
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
			
			DatachannelManager.getInstance().addDataChannel(dataChannel);
			
			RedditManager.getInstance().runRedditDatachannel(dataChannel);
			
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error while adding dataChannel:", e);
			msg.setMessageState(OP_ERR);
		}


		return msg;	
	}
	
	@PUT
	@Produces({"application/json", "application/xml"})
	public MessageBean updateDataChannel(DataChannel dataChannel) {
		MessageBean msg = new MessageBean();
		try {
			
			String dcId = dataChannel.getChannelID();
			
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
			
			DatachannelManager.getInstance().addDataChannel(dataChannel);
			
			RedditManager.getInstance().updateRedditDatachannel(dataChannel);
			
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error while updating dataChannel:", e);
			msg.setMessageState(OP_ERR);
		}


		return msg;	
	}
	
	@GET
	@Path("/{id}/stop")
	@Produces({"application/json", "application/xml"})
	public MessageBean stopDataChannel(@PathParam("id") String dcId) throws Exception {

		MessageBean msg = new MessageBean();
		try {
			
			//DataChannel dataChannel = DatachannelManager.getInstance().getDataChannel(dcId);
						
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
						
			RedditManager.getInstance().stopRedditDatachannel(dcId);
			
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error while stoping dataChannel:", e);
			msg.setMessageState(OP_ERR);
		}

		return msg;	
	}
	
	@GET
	@Path("/{id}")
	@Produces({"application/json", "application/xml"})
	public DataChannel getDataChannel(@PathParam("id") String dcId)	throws Exception {

		try {
			//CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return DatachannelManager.getInstance().getDataChannel(dcId);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting dataChannel:", e);
			throw new Exception("ERROR trying to get the Data Channel with id= " + dcId	+ " : " + e.getMessage());
		}

	}
	
	@GET
	@Produces({"application/json", "application/xml"})
	public DataChannelList getDataChannels(
			@DefaultValue("2000") @QueryParam("numResults") String numResults,
			@DefaultValue("1") @QueryParam("page") String page)
			throws Exception {

		try {
			List<DataChannel> list = DatachannelManager.getInstance().getDataChannels();
			DataChannelList dcl = new DataChannelList();
			dcl.setDataChannels(list);
			return dcl;
			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while listing dataChannels:", e);
			throw new Exception("ERROR trying to get all Data Channel in the system:  "	+ e.getMessage());
		}

	}
	
	public String generateCrc32Id() {
		UUID uuid = UUID.randomUUID();
		CRC32 crc = new CRC32();
		crc.update(uuid.toString().getBytes(Charset.forName("UTF-8")));
		//provide necessary padding in order to have always 8 characters.
		String key = String.format("%8s", Long.toHexString(crc.getValue())).replace(" ", "0");
		return key;
	}
}
