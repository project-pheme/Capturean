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

import java.util.logging.Level;
import java.util.logging.Logger;

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
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.MessageBean;
import atos.knowledgelab.capture.bean.MessageData;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeResultList;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import pheme.gathering.util.CaptureFacadeFactory;

@Path("datapool")
public class DataPoolManager {
		
	private static final String OP_OK = "200 OK";

	private Logger log = Logger.getLogger(DataPoolManager.class.getName());
	
	private CaptureFacadeIf cFacade = null;

	public DataPoolManager() {

	}
	
	/**
	 * This method add a new Data Pool in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * @throws Exception
	 * 
	 */
	@POST
	@Produces({"application/json", "application/xml"})
	public MessageBean addDataPool(DataPool dataPool) throws Exception {

		MessageBean msg = new MessageBean();
		try {
			
			String dcId = getCaptureFacadeIf().addDataPool(dataPool);
	
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dcId);
			msg.setMessageData(data);
		} catch (Exception e) {			
			log.log(Level.SEVERE, "Error while adding dataPool:", e);
			msg.setMessageState(e.getMessage());
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
	@Path("/{id}")
	@Produces({"application/json", "application/xml"})
	public DataPool getDataPool(@PathParam("id") String dpId)	throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getDataPool(dpId);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting dataPool:", e);
			throw new Exception("ERROR trying to get the Data Pool with id= " + dpId	+ " : " + e.getMessage());
		}

	}

	/**
	 * This method update the Data Pool with id = dcId in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * @throws Exception 
	 * 
	 */
	@PUT
	@Path("/{id}")
	@Produces({"application/json", "application/xml"})
	public MessageBean updateDataPool(@PathParam("id") String dpId,
			DataPool newDataPool) throws Exception {

		MessageBean msg = new MessageBean();
		try {			
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			cFacade.updateDataPool(newDataPool, dpId);
			msg.setMessageState(OP_OK);
			MessageData data = new MessageData();
			data.set_global_id(dpId);
			msg.setMessageData(data);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while updating dataPool:", e);
			msg.setMessageState(e.getMessage());
		}	
		return msg;
		
	}

	/**
	 * This method delete the Data Channel with id = dcId in the No-SQL storage.
	 * 
	 * @return message with action status.
	 * @throws Exception 
	 * 
	 */
	@DELETE
    @Path("/{id}")
	@Produces({"application/json", "application/xml"})
	public MessageBean deleteDataPool(@PathParam("id") String dpId) throws Exception {
    	MessageBean msg = new MessageBean();    	 
    	CaptureFacadeIf cFacade; 
    	try {
    		cFacade = getCaptureFacadeIf();
			cFacade.deleteDataPool(dpId);
			msg.setMessageState(OP_OK);
	    	MessageData data = new MessageData();
	    	data.set_global_id(dpId);
	    	msg.setMessageData(data);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while deleting dataPool:", e);
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
	public DataPoolList getDataPools(
			@DefaultValue("2000") @QueryParam("numResults") String numResults,
			@DefaultValue("") @QueryParam("fromKey") String fromKey)
			throws Exception {

		try {
			CaptureFacadeIf cFacade = getCaptureFacadeIf();
			return cFacade.getDataPools(Integer.parseInt(numResults), fromKey);			
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while listing dataChannels:", e);
			throw new Exception("ERROR trying to get all Data Channel in the system:  "	+ e.getMessage());
		}

	}
	


	/**
	 * This method get all data captured from the Data Channel.
	 * 
	 * @return data.
	 * 
	 */
	@GET
	@Path("/{id}/data")
	@Produces({"application/json", "application/xml"})
	public DcTweetList getDatafromDataPool(@PathParam("id") String dcId,
			 								  @DefaultValue("100") @QueryParam("numResults") String numResults,
			 								  @DefaultValue("1") @QueryParam("page") String page,
			 								  @DefaultValue("") @QueryParam("fromId") String fromId,
			 								  @DefaultValue("false") @QueryParam("reverse") String reverseOrder)
			throws Exception {

		//JAXBContext context = JAXBContext.newInstance(DataSource.class);
		CaptureFacadeIf cFacade; 
    	
		try {

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
			

		} catch (Exception e) {
			log.log(Level.SEVERE, "Error while getting data from dataChannel:", e);
			throw new Exception("Error trying to get all data captured from Data Channel with id= " + dcId + " : " + e.getMessage());
		}

	}
	
	
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
	@Path("/data")
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
	@Path("/twitter/user/{id}")
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

}
