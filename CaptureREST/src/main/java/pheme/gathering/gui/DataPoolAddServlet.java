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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.MessageBean;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class DataPoolAddServlet extends HttpServlet {
	

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

//					System.out.println("Output from Server .... \n");
//					System.out.println(output);

					req.setAttribute("datapool", output);
					req.getRequestDispatcher("details.jsp").forward(req, resp);
				} else {
					
					req.getRequestDispatcher("error.jsp").forward(req, resp);
				}

			} catch (Exception e) {

				e.printStackTrace();
				req.setAttribute("error", e);
				req.getRequestDispatcher("error.jsp").forward(req, resp);
			}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			try {
				System.out.println("Entra en el servlet");
				DataPool dp = new DataPool();
				//dc.setStatus("active");
				
				LinkedList<DataChannel> hs = new LinkedList<DataChannel>();
				dp.setDcsAllowed(hs);
				
				
				Map<String, String[]> map = req.getParameterMap();
				PrintWriter out = resp.getWriter();
				Set set = map.entrySet();
				Iterator it = set.iterator();
				
				while (it.hasNext()) {
					Map.Entry<String, String[]> entry = (Entry<String, String[]>) it.next();
					String paramName = entry.getKey();
					String[] paramValues = entry.getValue();
					if (paramValues.length == 1) {
						String paramValue = paramValues[0];
						if (paramValue.length() == 0) {
							
						} else {
							if (paramName.equalsIgnoreCase("dp-name")) {
								dp.setName(paramValue);
							}
							if (paramName.equalsIgnoreCase("dp-desc")) {
								dp.setDescription(paramValue);
							}
							if (paramName.equalsIgnoreCase("dp-keywords")) {
								List<String> keywordsList = new ArrayList<String>();
								if (!paramValue.trim().equals("")){
									keywordsList = Arrays.asList(paramValue.replaceAll("( )*,( )*", ",").replaceAll("\\[", "").replaceAll("\\]", "").trim().split(","));
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
								//System.out.println(paramName + " - " + paramValue);
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

				WebResource webResource = client.resource(endpoint + "/datapool/");
				MessageBean response = webResource.type("application/xml").post(MessageBean.class, dp);
				
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
