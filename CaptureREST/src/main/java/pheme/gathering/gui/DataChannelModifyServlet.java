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

import org.apache.commons.lang.StringUtils;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.MessageBean;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.bean.TwitterDataSource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class DataChannelModifyServlet extends HttpServlet {

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

				WebResource webResource = client.resource(endpoint + "/datachannel/" + req.getParameter("id"));

				ClientResponse response = webResource.accept("application/xml").get(ClientResponse.class);

				if (response.getStatus() != 200) {
					throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
				}

				DataChannel output = response.getEntity(DataChannel.class);

				//formating comillas in keywords
				List<DataSource> sourcesList = output.getDataSources();
				List<DataSource> sourcesListFinal = new ArrayList<DataSource>();
				for (DataSource source : sourcesList){
					if (source instanceof TwitterDataSource) {
						TwitterDataSource ts = (TwitterDataSource) source;
						String keywordsList = ts.getKeywords();
						if (keywordsList != null){
							String keywordsListFinal = keywordsList.replaceAll("\"", "&quot;").trim();
							ts.setKeywords(keywordsListFinal);
							sourcesListFinal.add(ts);
						}
					}
					if (source instanceof RedditDataSource) {
						RedditDataSource rs = (RedditDataSource) source;
						String keywords = rs.getKeywords();
						if (keywords != null){
							String keywordsFinal = keywords.replaceAll("\"", "&quot;").trim();
							rs.setKeywords(keywordsFinal);
							sourcesListFinal.add(rs);
						}
					}
				}
				output.setDataSources(sourcesListFinal);
				
    			req.setAttribute("datachannel", output);				
				req.getRequestDispatcher("modify.jsp").forward(req, resp);
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
			DataChannel dc = new DataChannel();
			//dc.setStatus("active");
			
			LinkedList<DataSource> hs = new LinkedList<DataSource>();
			dc.setDataSources(hs);

			Map<String, String[]> map = req.getParameterMap();
			PrintWriter out = resp.getWriter();
			Set set = map.entrySet();
			Iterator it = set.iterator();
			//HashMap<String, TwitterDataSource> tdsList = new HashMap<String, TwitterDataSource>();
			HashMap<String, DataSource> dsList = new HashMap<String, DataSource>();
			
			while (it.hasNext()) {
				Map.Entry<String, String[]> entry = (Entry<String, String[]>) it.next();
				String paramName = entry.getKey();
				String[] paramValues = entry.getValue();
				if (paramValues.length == 1) {
					String paramValue = paramValues[0];
					if (paramValue.length() == 0) {
						
					} else {
						if (paramName.equalsIgnoreCase("dc-id")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setChannelID(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-name")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setName(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-desc")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setDescription(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-type")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setType(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-created")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setCreationDate(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-updated")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setUpdateDate(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-startdate")) {
							//System.out.println(paramName + " - " + paramValue);

							dc.setStartCaptureDate(paramValue);
						}
						if (paramName.equalsIgnoreCase("dc-enddate")) {
							//System.out.println(paramName + " - " + paramValue);
							dc.setEndCaptureDate(paramValue);
						}

						if (paramName.startsWith("keywords")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									tds.setKeywords(paramValue);
									tds.setDstype("Twitter");
									dsList.put(index, tds);
								} else {
									tds.setKeywords(paramValue);
									tds.setDstype("Twitter");
								}
							}
							//System.out.println(paramName + " - " + paramValue);

						}
						
						if (paramName.startsWith("sourceid")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									tds.setSourceID(paramValue);
									dsList.put(index, tds);
								} else {
									tds.setSourceID(paramValue);
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
						if (paramName.startsWith("fromlasttweetid")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									if (paramValue.equalsIgnoreCase("on")) {
										tds.setFromLastTweetId(true);	
									} else {
										tds.setFromLastTweetId(false);
									}
									dsList.put(index, tds);
								} else {
									if (paramValue.equalsIgnoreCase("on")) {
										tds.setFromLastTweetId(true);	
									} else {
										tds.setFromLastTweetId(false);
									}
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
						if (paramName.startsWith("lasttweetid")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									tds.setLastTweetId(Long.parseLong(paramValue));
									dsList.put(index, tds);
								} else {
									tds.setLastTweetId(Long.parseLong(paramValue));
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
						if (paramName.startsWith("sortedhistorical")) {
							//System.out.println(paramName + " - " + paramValue);
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									if (paramValue.equalsIgnoreCase("sorted")) {
										tds.setChronologicalOrder(true);	
									} else {
										tds.setChronologicalOrder(false);
									}
									dsList.put(index, tds);
								} else {
									if (paramValue.equalsIgnoreCase("sorted")) {
										tds.setChronologicalOrder(true);	
									} else {
										tds.setChronologicalOrder(false);
									}
								}
							}
						}
						
						if (paramName.startsWith("historicalcount")) {
							//System.out.println(paramName + " - " + paramValue);
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								TwitterDataSource tds = (TwitterDataSource) dsList.get(index);
								if (tds == null) {
									tds = new TwitterDataSource();
									if (StringUtils.isNumeric(paramValue) ) {
										tds.setHistoricalLimit(Long.parseLong(paramValue));	
									} else {
										tds.setHistoricalLimit(0L);	
									}
									dsList.put(index, tds);
								} else {
									if (StringUtils.isNumeric(paramValue) ) {
										tds.setHistoricalLimit(Long.parseLong(paramValue));	
									} else {
										tds.setHistoricalLimit(0L);	
									}
								}
							}
						}
						
						//REDDIT parameters
						if (paramName.startsWith("redditkeywords")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								RedditDataSource rds = (RedditDataSource) dsList.get(index);
								if (rds == null) {
									rds = new RedditDataSource();
									rds.setKeywords(paramValue);
									dsList.put(index, rds);
								} else {
									rds.setKeywords(paramValue);
								}
							}
							//System.out.println(paramName + " - " + paramValue);

						}
						
						if (paramName.startsWith("redditsourceid")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								RedditDataSource rds = (RedditDataSource) dsList.get(index);
								if (rds == null) {
									rds = new RedditDataSource();
									rds.setSourceID(paramValue);
									dsList.put(index, rds);
								} else {
									rds.setSourceID(paramValue);
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
						if (paramName.startsWith("redditsubreddits")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								List<String> subreddits = new ArrayList<String>();
								if (!paramValue.trim().equals("")){
									subreddits = Arrays.asList(paramValue.replaceAll("( )*,( )*", ",").replaceAll("\\[", "").replaceAll("\\]", "").trim().split(","));
								}
								RedditDataSource rds = (RedditDataSource) dsList.get(index);
								if (rds == null) {
									rds = new RedditDataSource();
									rds.setSubreddits(subreddits);
									rds.setDstype("reddit");
									dsList.put(index, rds);
								} else {
									rds.setSubreddits(subreddits);
									rds.setDstype("reddit");
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
						if (paramName.startsWith("reddittype")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								RedditDataSource rds = (RedditDataSource) dsList.get(index);
								if (rds == null) {
									rds = new RedditDataSource();
									rds.setRedditType(paramValue);
									dsList.put(index, rds);
								} else {
									rds.setRedditType(paramValue);
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
							
						if (paramName.startsWith("redditpost")) {
							String[] val = paramName.split("-");
							if (val.length == 2) {
								String index = val[1];
								RedditDataSource rds = (RedditDataSource) dsList.get(index);
								if (rds == null) {
									rds = new RedditDataSource();
									rds.setPost(paramValue);
									dsList.put(index, rds);
								} else {
									rds.setPost(paramValue);
								}
							}
							//System.out.println(paramName + " - " + paramValue);
						}
						
					}
				}
			}
			
			//add Datasources to data channel
			for (String index : dsList.keySet()) {
				hs.add(dsList.get(index));
				//System.out.println(" > " + index + " - " );
			}
			
			
//			Writer writer = new StringWriter();
//	    	JAXBContext jc = JAXBContext.newInstance(DataChannel.class);
//	        Marshaller marshaller = jc.createMarshaller();
//	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//	        JAXBElement<DataChannel> jaxbElement = new JAXBElement<DataChannel>(new QName("streamItem"), DataChannel.class, dc);
//	        marshaller.marshal(jaxbElement, writer);
//	        System.out.println(writer.toString());
			
			Client client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter("admin", "66.admin"));

			ServletContext context = getServletContext();
    		String endpoint = (String) context.getAttribute("endpoint");

			WebResource webResource = client.resource(endpoint + "/datachannel/" + dc.getChannelID());
			MessageBean response = webResource.type("application/xml").put(MessageBean.class, dc);
			
			req.setAttribute("result", response.getMessageState());
			if (response.getMessageData() != null) {
				req.setAttribute("datachannel", response.getMessageData().get_global_id());
			}
			
			req.getRequestDispatcher("confirmation.jsp").forward(req, resp);
			
		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("result", e.getMessage());
			req.getRequestDispatcher("confirmation.jsp").forward(req, resp);
		}

	}
}
