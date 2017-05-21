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
package pheme.gathering.gui;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.MessageBean;
import atos.knowledgelab.capture.bean.TwitterDataSource;

public class DataPoolModifyServlet extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {

			if (req.getParameter("id") != null) {
				Client client = Client.create();
				//add authentication data.
				//TODO  This is to be extended.
				client.addFilter(new HTTPBasicAuthFilter("admin", "66.admin"));
				
				ServletContext context = getServletContext();
	    		String endpoint = (String) context.getAttribute("endpoint");

				WebResource webResource = client.resource(endpoint + "/datapool/" + req.getParameter("id"));

				ClientResponse response = webResource.accept("application/xml").get(ClientResponse.class);

				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}

				DataPool output = response.getEntity(DataPool.class);
				
				//formating comillas in keyword list
    			List<String> keywordsList = output.getKeywords();
    			String datapoolDCKeywords = "";
    			for (String key : keywordsList){
    				if(datapoolDCKeywords.equals("")){
    					datapoolDCKeywords = key.replaceAll("\"", "&quot;");
    				} 
	  				else{
	  					datapoolDCKeywords = datapoolDCKeywords + "," + key.replaceAll("\"", "&quot;");
	  				}
    			}
    			datapoolDCKeywords = datapoolDCKeywords.replaceAll("( )*,( )*", ",").replaceAll("\\[", "").replaceAll("\\]", "").trim();

    			//get dcs allowed list
    			String datapoolDCAllowed = "";
    			List<DataChannel> dcsAllowed = output.getDcsAllowed();
    			for (DataChannel dc : dcsAllowed){
    				if(datapoolDCAllowed.equals("")){
        				datapoolDCAllowed = dc.getChannelID();
    				} 
	  				else{
	    				datapoolDCAllowed = datapoolDCAllowed + "," + dc.getChannelID();
	  				}
    			}
    			  			
				req.setAttribute("datapool", output);
				req.setAttribute("datapoolDCKeywords", datapoolDCKeywords);
				req.setAttribute("datapoolDCAllowed", datapoolDCAllowed);
				req.getRequestDispatcher("modifyDataPool.jsp").forward(req, resp);
			} else {

				//TODO set error message
				req.setAttribute("error", "ID not specified");
				req.getRequestDispatcher("error.jsp").forward(req, resp);
			}

		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("result", e);
			req.getRequestDispatcher("error.jsp").forward(req, resp);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			DataPool dp = new DataPool();
			//dc.setStatus("active");
			

			Map<String, String[]> map = req.getParameterMap();
			PrintWriter out = resp.getWriter();
			Set set = map.entrySet();
			Iterator it = set.iterator();
			HashMap<String, TwitterDataSource> tdsList = new HashMap<String, TwitterDataSource>();
			
			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = (Entry<String, String[]>) it.next();
				String paramName = entry.getKey();
				String[] paramValues = entry.getValue();
				if (paramValues.length == 1) {
					String paramValue = paramValues[0];
					if (paramValue.length() == 0) {
						
					} else {
						if (paramName.equalsIgnoreCase("dp-id")) {
							//System.out.println(paramName + " - " + paramValue);
							dp.setPoolID(paramValue);
						}
					
						if (paramName.equalsIgnoreCase("dp-name")) {
							//System.out.println(paramName + " - " + paramValue);
							dp.setName(paramValue);
						}
						if (paramName.equalsIgnoreCase("dp-desc")) {
							//System.out.println(paramName + " - " + paramValue);
							dp.setDescription(paramValue);
						}
						if (paramName.equalsIgnoreCase("dp-keywords")) {
							//System.out.println(paramName + " - " + paramValue);
							List<String> keywordsList = new ArrayList<String>();
							if (!paramValue.trim().equals("")){
								keywordsList =  Arrays.asList(paramValue.replaceAll("( )*,( )*", ",").replaceAll("\\[", "").replaceAll("\\]", "").trim().split(","));
							}
							dp.setKeywords(keywordsList);
						}
						if (paramName.equalsIgnoreCase("dp-dc-allowed")) {
							List<DataChannel> dcList = new ArrayList<DataChannel>();
							if (!paramValue.trim().equals("")){
								List<String> dcIdList = new ArrayList<String>();
								dcIdList = Arrays.asList(paramValue.replaceAll("( )*,( )*", ",").replaceAll("\\[", "").replaceAll("\\]", "").trim().split(","));
								Iterator<String> itDcID = dcIdList.iterator();
								while (itDcID.hasNext()){
									String dcID = itDcID.next();
									DataChannel dc = new DataChannel();
									dc.setChannelID(dcID);
									dcList.add(dc);
								}
							}
							dp.setDcsAllowed(dcList);
						}
						if (paramName.equalsIgnoreCase("dp-lang")) {
							dp.setLang(paramValue);
						}
						
						
					}
				}
			}
			
			if (dp.getName() == null) dp.setName("");
			if (dp.getDescription() == null) dp.setDescription("");
			if (dp.getLang() == null) dp.setLang("");
			
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("admin", "66.admin"));

			ServletContext context = getServletContext();
    		String endpoint = (String) context.getAttribute("endpoint");

			WebResource webResource = client.resource(endpoint + "/datapool/" + dp.getPoolID());
			MessageBean response = webResource.type("application/xml").put(MessageBean.class, dp);
			
			req.setAttribute("result", response.getMessageState());
			if (response.getMessageData() != null) {
				req.setAttribute("datapool", response.getMessageData().get_global_id());
			}
			
			req.getRequestDispatcher("confirmationDataPool.jsp").forward(req, resp);
			
		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("result", e.getMessage());
			req.getRequestDispatcher("confirmationDataPool.jsp").forward(req, resp);
		}

	}

}
