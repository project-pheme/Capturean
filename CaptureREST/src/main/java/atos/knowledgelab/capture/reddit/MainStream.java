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

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class MainStream {

	public static void main(String[] args) throws InterruptedException {

		HttpResponse<String> response = null;
		System.out.println(String.valueOf((System.currentTimeMillis()/1000)-604800));
		System.out.println(String.valueOf(System.currentTimeMillis()/1000));
		try {
			response = Unirest.get("http://stream.pushshift.io/?subreddit=askreddit&previous=10000") // get all the comments ('limit' parameter = integer max value)
					.header("User-Agent", "Mozilla/5.0") // user-agent header required to avoid rejection from reddit servers
					.asString();
		} catch (UnirestException e) {e.printStackTrace();}
		System.out.println(response.getBody());
		System.exit(0);
		
		String dcID_test = "12345678";
		String startDate_test = "2017-02-02 13:47:54.000";
		String endDate_test = "2017-02-03 17:47:54.000";
		
		// STREAM PROCESSING:
		if (args.length == 1){
			SubredditStream subredditStream = new SubredditStream(dcID_test,args[0]);
			subredditStream.listenComments(startDate_test,endDate_test);
		}
		else {
			SubredditStream subredditStream = new SubredditStream(dcID_test,"askreddit");
			subredditStream.listenComments(startDate_test,endDate_test);
		}
		 // STREAM PROCESSING

	}

}
