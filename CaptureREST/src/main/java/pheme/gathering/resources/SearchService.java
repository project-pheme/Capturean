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

import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;

import pheme.gathering.search.TweetAdHocQuery;
import pheme.gathering.util.TwitterListener;
import pheme.gathering.util.TwitterQuerySolver;
import twitter4j.TwitterException;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.Tweet;

@Path("search")
public class SearchService {

	private final static Logger LOGGER = Logger.getLogger(SearchService.class.getName());

	public SearchService() {
		
	}
	
	/**
	 * This method provide a quick query preview service, to check query keywords
	 * before running an actual datachannel. It is used to fine tune twitter
	 * queries to avoid noisy keywords or find more precise queries.
	 * 
	 * 
	 * @param keywords Twitter search keywords, as accepted by Twitter API. See 
	 * https://dev.twitter.com/rest/reference/get/search/tweets for query grammar.
	 * @param searchMode One of the following modes: live, most_popular, mixed. Default is "live" (latest).
	 * @param maxResults Specify max number of results. Default: 500·  
	 * @return
	 */
	@GET
	@Path("tweets")
	@Produces({"application/json", "application/xml"})
	public List<Tweet> searchTweets(@QueryParam("keywords") String keywords,
			@DefaultValue("LIVE") @QueryParam("mode") TweetSearchMode searchMode,
			@DefaultValue("100") @QueryParam("max_results") String maxResults 
			) {
		
		if (keywords == null || keywords.length() == 0) {
			//throw new WebApplicationException(new Throwable("Parameter \"keywords\" not specified"), 400);
			throw new CaptureServiceException(400, "Parameter \"keywords\" not specified");
		} else {
			LOGGER.info("Query: " + keywords);
			QueryData qd = new QueryData();
			qd.setQuery(keywords);
			
			List<Tweet> list = null;
			
			try {
				int intMaxResults = Integer.parseInt(maxResults);
				
				list = TwitterQuerySolver.getInstance().getTwitterAdHocQuery().adHocSearch(qd, searchMode, intMaxResults, 1000);
				
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return list;
		}
		
		
		
	}
	
	@GET
	@Path("test")
	@Produces({"application/json", "application/xml"})
	public String test(@DefaultValue("LIVE") @QueryParam("mode") TweetSearchMode searchMode) {
	//DcTweetList searchTweets(@DefaultValue("latest") @QueryParam("mode") TweetSearchMode searchMode) {
		
		
		return "OK!";
		
	}
}
