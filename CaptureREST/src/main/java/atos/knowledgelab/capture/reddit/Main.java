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

import java.text.ParseException;
import java.util.ArrayList;

import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.reddit.SubredditBatch.CDN_Unable;

public class Main {

	public static void main(String[] args) throws CaptureException, ParseException, CDN_Unable, InterruptedException {


		String dcID_test = "12345678";
		String start_test = "2017-02-03 15:04:24.000";
		String end_test = "2017-02-03 16:04:24.000";
		
		// BATCH PROCESSING
		//
		SubredditBatch subredditBatch = new SubredditBatch(dcID_test,start_test,end_test);

		if (args.length==1){ // 

			// PRESET TESTS WITHOUT QUERY:
			if (args[0].equals("-p")){
				// Test for single post
				subredditBatch.getPost("https://www.reddit.com/r/AskReddit/comments/411l9a/youve_just_arrived_in_2016_from_20_years_ago_what/");
			}
			else if (args[0].equals("-s")){
				// Test for a single subreddit:
				//subredditBatch.getSubReddits("subreddit.txt", "");
				ArrayList<String> subreddits = new ArrayList<String>();
				subreddits.add("ADHD");
				subredditBatch.getSubReddits(subreddits, "");
			}

			else if (args[0].equals("-ss")){
				// Test for a list of subreddits:
				//subredditBatch.getSubReddits("subreddits.txt", "");
				ArrayList<String> subreddits = new ArrayList<String>();
				subreddits.add("add");
				subreddits.add("addiction");
				subreddits.add("ADHD");
				subredditBatch.getSubReddits(subreddits, "");
			}
		}

		else if (args.length==2){ 


			// PRESET TESTS WITH QUERY:
			if(args[0].equals("-p")){
				// Test for single post
				subredditBatch.getPost("https://www.reddit.com/r/AskReddit/comments/411l9a/youve_just_arrived_in_2016_from_20_years_ago_what/");
				System.out.println("POST OPTION DOES NOT USE QUERIES");
			}

			else if (args[0].equals("-s") && args[1].equals("-q")){
				// Test for a single subreddit:
				System.out.println("EMPTY QUERY");
			}

			else if (args[0].equals("-ss") && args[1].equals("-q")){
				// Test for a list of subreddits:
				System.out.println("EMPTY QUERY");
			}

			// NEW TESTS WITHOUT QUERY:
			else if (args[0].equals("-np")){
				// Test for single post:
				subredditBatch.getPost(args[1]);
			}

			else if (args[0].equals("-nss")){
				// Test for a list of subreddits:
				//subredditBatch.getSubReddits(args[1], "");
				ArrayList<String> subreddits = new ArrayList<String>();
				String[] srs = args[1].split(",");
				for (String sr : srs){
					subreddits.add(sr);
				}
				subredditBatch.getSubReddits(subreddits, "");
			}
		}

		else if (args.length==3){ 

			if (args[1].equals("-q")){

				if (args[0].equals("-p") && args[1].equals("-q")){
					System.out.println("POST OPTION DOES NOT USE QUERIES");
				}

				else if (args[0].equals("-s") && args[1].equals("-q")){
					//subredditBatch.getSubReddits("subreddit.txt", args[2]);
					ArrayList<String> subreddits = new ArrayList<String>();
					subreddits.add("ADHD");
					subredditBatch.getSubReddits(subreddits, args[2]);
				}

				else if (args[0].equals("-ss") && args[1].equals("-q")){
					//subredditBatch.getSubReddits("subreddits.txt", args[2]);
					ArrayList<String> subreddits = new ArrayList<String>();
					subreddits.add("add");
					subreddits.add("addiction");
					subreddits.add("ADHD");
					subredditBatch.getSubReddits(subreddits, args[2]);
				}

				else if (args[0].equals("-np") && args[1].equals("-q")){

					System.out.println("PLEASE SPECIFY A POST URL");
				}

				else if (args[0].equals("-nss") && args[1].equals("-q")){

					System.out.println("PLEASE SPECIFY A SUBREDDITS FILE");
				}
			}

			// Query required:
			else if (args[2].equals("-q")){
				System.out.println("EMPTY QUERY");
			}



		}

		else if (args.length==4){ 

			if (args[0].equals("-p") || args[0].equals("-s") || args[0].equals("-ss")){
				System.out.println("INVALID OPTIONS");
			}

			if (args[0].equals("-np")){
				subredditBatch.getPost(args[1]);
				System.out.println("POST OPTION DOES NOT USE QUERIES");
			}

			else if(args[0].equals("-nss")){
				//subredditBatch.getSubReddits(args[1], args[3]);
				ArrayList<String> subreddits = new ArrayList<String>();
				String[] srs = args[1].split(",");
				for (String sr : srs){
					subreddits.add(sr);
				}
				subredditBatch.getSubReddits(subreddits, args[3]);
			}

		}


		// If no arguments are passed, run the test for a single post
		else {

			System.out.println("ELSE: PRESET TEST FOR SINGLE POST");

			// Test for single post
			subredditBatch.getPost("https://www.reddit.com/r/AskReddit/comments/411l9a/youve_just_arrived_in_2016_from_20_years_ago_what/");
		}

		// //BATCH PROCESSING




		// STREAM PROCESSING:
		/*
		SubredditStream subredditStream = new SubredditStream("askreddit");
		subredditStream.listenComments();
		 */ // STREAM PROCESSING

	}

}
