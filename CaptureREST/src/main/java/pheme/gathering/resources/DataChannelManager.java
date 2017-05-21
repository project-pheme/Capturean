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
package pheme.gathering.resources;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import atos.knowledgelab.capture.bean.ActiveQueriesList;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.MessageBean;
import atos.knowledgelab.capture.bean.MessageData;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.reddit.RedditManager;
import atos.knowledgelab.capture.reddit.datachannels.DatachannelManager;
import pheme.gathering.util.CaptureFacadeFactory;

@Path("datachannel")
public class DataChannelManager {
		
	private static final String OP_OK = "200 OK";
	private static final String OP_ERR = "500 Internal Server Error";

	private Logger log = Logger.getLogger(DataChannelManager.class.getName());
	
	private CaptureFacadeIf cFacade = null;

	public DataChannelManager() {

	}
	
	/**
	 * This method add a new Data Channel in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * @throws Exception
	 * 
	 */
	@POST
	@Produces({"application/json", "application/xml"})
	public MessageBean addDataChannel(DataChannel dataChannel) {

		MessageBean msg = new MessageBean();
		try {
			boolean containsReddit = false;
			boolean containsTwitter = false;
			for (DataSource dc : dataChannel.getDataSources()) {
				if (dc.getDstype().equalsIgnoreCase("twitter")) {
					containsTwitter = true;
				}
				if (dc.getDstype().equalsIgnoreCase("reddit")) {
					containsReddit = true;
				}
			}
			
			String dcId = null;
			//if (containsTwitter) {
				dcId = getCaptureFacadeIf().addDataChannel(dataChannel);
			//}
			
			if (containsReddit) {
				if (dcId == null) {
					dcId = generateCrc32Id();
				}
				dataChannel.setChannelID(dcId);
				
				//DatachannelManager.getInstance().addDataChannel(dataChannel);
				RedditManager.getInstance().runRedditDatachannel(dataChannel);
				//end Reddit thing
			}

			
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error while adding dataChannel:", e);
			msg.setMessageState(OP_ERR + e.getMessage());
		}


		return msg;
	}

	/**
	 * This method get a Data Channel by Id in the No-SQL storage.
	 * 
	 * @return data channel info.
	 * 
	 */
	@GET
	@Path("{id}")
	@Produces({"application/json", "application/xml"})
	public DataChannel getDataChannel(@PathParam("id") String dcId)	throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			DataChannel dc = cFacade.getDataChannel(dcId);
			
			//Begin Reddit thing
			//DataChannel redditDc = DatachannelManager.getInstance().getDataChannel(dcId);
			
			
			//return mergeDataChannel(dc, redditDc);
			
			return dc;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting dataChannel:", e);
			throw new Exception("ERROR trying to get the Data Channel with id= " + dcId	+ " : " + e.getMessage());
		}

	}

	/**
	 * This method update the Data Channel with id = dcId in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * 
	 */
	@PUT
	@Path("{id}")
	@Produces({"application/json", "application/xml"})
	public MessageBean updateDataChannel(@PathParam("id") String dcId,
			DataChannel newDataChannel) {

		MessageBean msg = new MessageBean();
		try {			
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			cFacade.updateDataChannel(newDataChannel, dcId);
			
			//Begin Reddit thing
			//DatachannelManager.getInstance().addDataChannel(newDataChannel);		
			RedditManager.getInstance().updateRedditDatachannel(newDataChannel);
			//end Reddit thing
			
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while updating dataChannel:", e);
			msg.setMessageState(e.getMessage());
		}	
		return msg;
	}

	/**
	 * This method delete the Data Channel with id = dcId in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * 
	 */
	@DELETE
    @Path("{id}")
	@Produces({"application/json", "application/xml"})
	public MessageBean deleteDataChannel(@PathParam("id") String dcId) {
    	MessageBean msg = new MessageBean();    	 
    	CaptureFacadeIf cFacade; 
    	try {
    		cFacade = getCaptureFacadeIf();
			cFacade.deleteDataChannel(dcId);
			
			//Begin Reddit thing
			RedditManager.getInstance().stopRedditDatachannel(dcId);
			//end Reddit thing
			
			msg.setMessageState(OP_OK);
	    	MessageData data = new MessageData();
	    	data.set_global_id(dcId);
	    	msg.setMessageData(data);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while deleting dataChannel:", e);
			msg.setMessageState(e.getMessage());
		}
    	return msg;
	}

	/**
	 * This method get all Data Channel configuration in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * 
	 */
	@GET
	@Produces({"application/json", "application/xml"})
	public DataChannelList getDataChannels(
			@DefaultValue("2000") @QueryParam("numResults") String numResults,
			@DefaultValue("1") @QueryParam("page") String page)
			throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			DataChannelList dataChannels = cFacade.getDataChannels(Integer.parseInt(numResults), Integer.parseInt(page));		
			
			//Begin Reddit thing
			//get Reddit datachannels
			List<DataChannel> list = DatachannelManager.getInstance().getDataChannels();
			DataChannelList dcl = new DataChannelList();
			dcl.setDataChannels(list);
			
			//TODO: merges from left list to the right list ONLY!
			return mergeDataChannels(dataChannels, dcl);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while listing dataChannels:", e);
			throw new Exception("ERROR trying to get all Data Channel in the system:  "	+ e.getMessage());
		}

	}
	
	private DataChannelList mergeDataChannels(DataChannelList dcList1, DataChannelList dcList2) {

		List<DataChannel> list1 = dcList1.getDataChannels();
		List<DataChannel> list2 = dcList2.getDataChannels();

		Set<String> merged = new HashSet<String>();
		//this is sub-optimal, TODO: fix this
		for (DataChannel dc1 : list1) {
			for (DataChannel dc2 : list2) {
				if (dc1.getChannelID() == dc2.getChannelID()) {
					merged.add(dc1.getChannelID());
					
					dc1 = mergeDataChannel(dc1, dc2);
				}
			}
		}
		
		/*
		for (DataChannel dc : list2) {
			if (merged.contains(dc.getChannelID()) == false) {
				list1.add(dc);
			}
		}*/
		
		return dcList1;
	}
	private DataChannel mergeDataChannel(DataChannel dc1, DataChannel dc2) {

		if (dc1 == null) {
			return dc2;
		}
		
		if (dc2 == null) {
			return dc1;
		}
		
		List<DataSource> dsList1 = dc1.getDataSources();
		List<DataSource> dsList2 = dc2.getDataSources();

		List<DataSource> mergeList = new ArrayList<DataSource>();
		Set<String> merged = new HashSet<String>();
		for (DataSource ds1 : dsList1) {
			for (DataSource ds2 : dsList2) {
				if (ds1.getSourceID().equalsIgnoreCase(ds2.getSourceID())) {
					mergeList.add(ds1);
					merged.add(ds1.getSourceID());
				}
			}
		}
		
		//
		for (DataSource ds : dsList2) {
			if (merged.contains(ds.getSourceID()) == false) {
				mergeList.add(ds);
			}
		}
		
		
		dc1.setDataSources(mergeList);
		
		return dc1;
	}

	/**
	 * 
	 * This is actually a good idea.
	 * I think we need such method for twitter datachannel too!
	 * 
	 * Currently works for Reddit only.
	 * 
	 * @param dcId
	 * @return
	 * @throws Exception
	 */
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
	
	
//	/**
//	 * This method add a new Data Channel in the No-SQL storage.
//	 * 
//	 * @return message with action status.
//	 * @throws Exception
//	 * 
//	 */
//	@POST
//	@Produces({"application/json", "application/xml"})
//	public MessageBean addDataPool(DataChannel dataPool) {
//
//		MessageBean msg = new MessageBean();
//		try {
//			
//			String dpId = getCaptureFacadeIf().addDataPool(dataPool);
//	
//			msg.set_messageState(OP_OK);
//			MessageData data = new MessageData();
//			data.set_global_id(dpId);
//			msg.set_messageData(data);
//		} catch (Exception e) {			
//			log.log(Level.SEVERE, "Error while adding dataChannel:", e);
//			msg.set_messageState(e.getMessage());
//		}
//
//
//		return msg;
//	}
//	
//
//	/**
//	 * This method get a Data Channel by Id in the No-SQL storage.
//	 * 
//	 * @return data channel info.
//	 * 
//	 */
//	@GET
//	@Path("/{id}")
//	@Produces({"application/json", "application/xml"})
//	public DataPool getDataPool(@PathParam("id") String dpId)	throws Exception {
//
//		try {
//			CaptureFacadeIf cFacade = getCaptureFacadeIf();
//			return cFacade.getDataPool(dpId);
//		} catch (Exception e) {
//			
//			log.log(Level.SEVERE, "Error while getting dataChannel:", e);
//			throw new Exception("ERROR trying to get the Data Channel with id= " + dpId	+ " : " + e.getMessage());
//		}
//
//	}
//	
//	
//	/**
//	 * This method get all Active Queries placed on the Queue
//	 * 
//	 * @return message with active queries data.
//	 * 
//	 */
	@GET
	@Path("activeQueries")
	@Produces({"application/json", "application/xml"})
	public ActiveQueriesList getActiveQueries() throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getActiveQueries();			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while gettingActiveQueries:", e);
			throw new Exception("ERROR trying to get all Active Queries placed on the Queue:  "	+ e.getMessage());
		}

	}


	/*@GET
	@Path("/{id}/{action}")
	@Produces("application/xml")
	public String executeActionOverDC(@PathParam("id") String dcId,
			@PathParam("action") String action) throws Exception {

		JAXBContext context = JAXBContext.newInstance(DataChannel.class);
		try {
			Object obj = hbaseJPA.executeActionOnDC(dcId, action);
			StringWriter writer = new StringWriter();

			if (obj instanceof TweetList) {
				context = JAXBContext.newInstance(TweetList.class);
			}
			// TODO- New actions
			Marshaller m = context.createMarshaller();
			m.marshal(obj, writer);
			return writer.toString();

		} catch (Exception e) {
			throw new Exception(
					"Error trying to get the Data Channel with id= " + dcId
							+ " : " + e.getMessage());
		}

	}*/

	/**
	 * This method get all data captured from the Data Channel.
	 * 
	 * @return data.
	 * 
	 */
	@GET
	@Path("{id}/data")
	@Produces({"application/json", "application/xml"})
	public DcTweetList getDatafromDataChannel(@PathParam("id") String dcId,
			 								  @DefaultValue("100") @QueryParam("numResults") String numResults,
			 								  @DefaultValue("1") @QueryParam("page") String page,
			 								  @DefaultValue("") @QueryParam("fromId") String fromId,
			 								  @DefaultValue("false") @QueryParam("reverse") String reverseOrder)
			throws Exception {

		//JAXBContext context = JAXBContext.newInstance(DataSource.class);
		CaptureFacadeIf cFacade; 
    	
		try {
			/*Object obj = hbaseJPA.getDataFromDataChannel(dcId);
			StringWriter writer = new StringWriter();

			if (obj instanceof TweetList) {
				context = JAXBContext.newInstance(TweetList.class);
			}
			Marshaller m = context.createMarshaller();
			m.marshal(obj, writer);
			return writer.toString();*/
			cFacade = getCaptureFacadeIf();
			
			//provide compatibility:
			//at this point we don't know exactly which facade is being used,
			//so we invoke the method based on given parameters. If this is wrong
			//an exception will be thrown at the later stage.
			try {
				return cFacade.getTweetsFromDC(dcId, fromId, Integer.parseInt(numResults), Boolean.parseBoolean(reverseOrder));

			} catch (CaptureException e) {
				if (e.getMessage().equalsIgnoreCase("Unnimplemented method")) {
					log.log(Level.INFO, "Compatibility fallback, use older method for getting tweets.");
					return cFacade.getTweetsFromDC(dcId, Integer.parseInt(page), Integer.parseInt(numResults));	
				} else {					
					return null;
				}
			}
			
//			if (fromTweetId != "" || reverseOrder == "true") {
//				return cFacade.getTweetsFromDC(dcId, fromTweetId, Integer.parseInt(numResults), Boolean.parseBoolean(reverseOrder));
//			} else {
//				return cFacade.getTweetsFromDC(dcId, Integer.parseInt(page), Integer.parseInt(numResults));				
//			}
			

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting data from dataChannel:", e);
			throw new Exception("Error trying to get all data captured from Data Channel with id= " + dcId + " : " + e.getMessage());
		}

	}
	

//	@GET
//	@Path("/{id}/{typeDataSource}/{facetName}")
//	@Produces({"application/json", "application/xml"})
//	public DcTweetList getFacetsDatafromDataChannel (@PathParam("id") String dcId,
//									  @DefaultValue("twitter") @PathParam("typeDataSource") String typeDataSource,
//									  @PathParam("facetName") String facetName,
//									  @DefaultValue("*") @QueryParam ("value") String value,
//									  @DefaultValue("100") @QueryParam("numResults") String numResults,
//	 								  @DefaultValue("1") @QueryParam("page") String page)	throws Exception {
//
//		try {
//			System.out.println(" Enter in get facets for data channel ...");
//			CaptureFacadeIf cFacade = getCaptureFacadeIf();
//			return cFacade.getFacetTweetsFromDC(dcId, typeDataSource, facetName, value, Integer.parseInt(page), Integer.parseInt(numResults));
//		} catch (Exception e) {
//			log.log(Level.SEVERE, "Error while getting data from dataChannel:", e);
//			throw new Exception("Error trying to get data captured from Data Channel with id " + dcId	+ " for facetName/value: " + facetName +"/" + value + " : " + e.getMessage());
//		}
//
//	}
	
//	@GET
//	@Path("/volume")
//	@Produces({"application/json", "application/xml"})
//	public VolumeResultList getVolumeFromDC (
//									  @QueryParam("id") String dcId,
//									  @DefaultValue("twitter") @QueryParam("typeDataSource") String typeDataSource,
//									  @QueryParam ("fInit") String fInit,
//									  @QueryParam ("fEnd") String fEnd,
//									  @DefaultValue("5") @QueryParam("sizeSec") String size)	throws Exception {
//
//		try {
//			System.out.println(" Enter in volume ...");
//			CaptureFacadeIf cFacade = getCaptureFacadeIf();
//			return cFacade.getVolumeFromDC(dcId, typeDataSource, fInit, fEnd, Integer.parseInt(size));
//		} catch (Exception e) {
//			throw new Exception("Error trying to get volume for data Channel with id " + dcId + " : " + e.getMessage(), e);
//		}
//
//	}

	@GET
	@Path("data")
	@Produces({"application/json", "application/xml"})
	public DcTweetList getFacetsData (@DefaultValue("") @QueryParam ("id") String dcId,
									  @DefaultValue("") @QueryParam("typeDataSource") String typeDataSource,
									  @DefaultValue("") @QueryParam ("filterExpression") String filterExpression,
									  @DefaultValue("") @QueryParam("sorter") String sorter,
									  @DefaultValue("desc") @QueryParam("mode") String mode,
									  @DefaultValue("") @QueryParam("fields") String fields,
									  @DefaultValue("100") @QueryParam("numResults") String numResults,
	 								  @DefaultValue("1") @QueryParam("page") String page)	throws Exception {

		try {
			System.out.println(" Enter in filter expression : ");
			String filterExpressionRw = reWriteFilter(filterExpression);
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getFacetTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, Integer.parseInt(page), Integer.parseInt(numResults));
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting data from dataChannel:", e);
			throw new Exception("Error trying to get data for filterExpression: " + filterExpression + " : " + e.getMessage());
		}

	}
	
	@GET
	@Path("/dataNoFq")
	@Produces({"application/json", "application/xml"})
	public DcTweetList getFacetWithNoFqTweets (@DefaultValue("") @QueryParam ("id") String dcId,
									  @DefaultValue("") @QueryParam("typeDataSource") String typeDataSource,
									  @DefaultValue("") @QueryParam ("filterExpression") String filterExpression,
									  @DefaultValue("") @QueryParam("sorter") String sorter,
									  @DefaultValue("desc") @QueryParam("mode") String mode,
									  @DefaultValue("") @QueryParam("fields") String fields,
									  @DefaultValue("100") @QueryParam("numResults") String numResults,
	 								  @DefaultValue("1") @QueryParam("page") String page)	throws Exception {

		try {
			System.out.println(" Enter in filter expression withot: ");
			String filterExpressionRw = reWriteFilter(filterExpression);
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getFacetWithNoFqTweets(dcId, typeDataSource, filterExpressionRw, sorter, mode, fields, Integer.parseInt(page), Integer.parseInt(numResults));
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting data from dataChannel:", e);
			throw new Exception("Error trying to get data for filterExpression: " + filterExpression + " : " + e.getMessage());
		}

	}
	
	 private String reWriteFilter(String filterExpression) {
			String fRw = filterExpression;
			fRw = fRw.replace("'%", "*");
			fRw = fRw.replace("%'", "*");
			fRw = fRw.replace("\"%", "*");
			fRw = fRw.replace("%\"", "*");
			fRw = fRw.replace('%', '*');
			fRw = fRw.replace('%', ' ');
			fRw = fRw.replace('\'', '"');
			fRw = fRw.replace("=", ":");		
			fRw = fRw.replace(" LIKE ", ":");
			fRw = fRw.replace("( ", "(");
			fRw = fRw.replace(" )", ")");
			fRw = fRw.replaceAll("\\s+", "*");
			fRw = fRw.replace("*AND*", " AND ");
			fRw = fRw.replace("*OR*", " OR ");
			fRw = fRw.replace("*TO*", " TO ");
			fRw = fRw.replace("http:", "http\\:");
			fRw = fRw.replace("https:", "https\\:");
			return fRw;
		}
	
	
	/**
	 * This method get the twitter user profile.
	 * 
	 * @return user profile.
	 * 
	 */
	@GET
	@Path("twitter/user/{id}")
	@Produces({"application/json", "application/xml"})
	public TwitterUser getTwitterUser(@PathParam("id") String userId)
			throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getTwitterUser(userId);			
		} catch (Exception e) {			
			throw new Exception("ERROR trying to get the User Profile for id= "	+ userId + " : " + e.getMessage());
		}

	}
	
	private CaptureFacadeIf getCaptureFacadeIf() throws Exception{
		if(cFacade == null)
			//cFacade = new CaptureFacade();
			cFacade = CaptureFacadeFactory.getInstance();
		return cFacade;			
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
