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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import com.sun.tools.javac.code.Attribute.Array;

import atos.knowledgelab.capture.bean.AmbassadorQuality;
import atos.knowledgelab.capture.bean.CompetitorAnalysis;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.EngagementAnalysis;
import atos.knowledgelab.capture.bean.ListWrapper;
import atos.knowledgelab.capture.bean.MapResults;
import atos.knowledgelab.capture.bean.MapWrapper;
import atos.knowledgelab.capture.bean.WebIntentTwitter;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.bean.ReachAnalysis;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.TweetThread;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.ifaces.CaptureAnalyticIf;
import atos.knowledgelab.capture.ifaces.CaptureFacadeIf;
import atos.knowledgelab.capture.persistence.CaptureAnalytic;
import atos.knowledgelab.capture.util.BrandManagementConstants;
import pheme.gathering.util.CaptureFacadeFactory;
import twitter4j.OEmbed;

@Path("analytic")
public class AnalyticManager {

	private static final String OP_OK = "200 OK";
	private static final String OP_NO_OK_MISSING_PARAMETERS = "400 NO OK";
	private Logger log = Logger.getLogger(AnalyticManager.class.getName());
	private CaptureAnalyticIf realCai = new CaptureAnalytic();
	private CaptureFacadeIf cFacade = null;

	
	private CaptureFacadeIf getCaptureFacadeIf() throws Exception{
		if(cFacade == null)
			//cFacade = new CaptureFacade();
			cFacade = CaptureFacadeFactory.getInstance();
		return cFacade;			
	}
	
	@GET
	@Path("/influentials")
	@Produces({ "application/json", "application/xml" })
	public MapResults getInfluentialTweets (
			@QueryParam("userId") String userId,
			@QueryParam("date") Date date,
			@QueryParam("criterion") String criterion) throws Exception {

		MapResults ret = new MapResults();
		try {
			
			String msg = OP_OK;
			Map<String, WebIntentTwitter> retMap = new HashMap<String, WebIntentTwitter>();
			if (!userId.isEmpty() && date!= null && !criterion.isEmpty()){
				log.info("REST Getting influential: " + userId.toString() + " ,date:" + date.toString() + " ,criterion: " + criterion.toString());
				cFacade = getCaptureFacadeIf();
				retMap = cFacade.getInfluential(userId, date, criterion);
			}else msg = OP_NO_OK_MISSING_PARAMETERS;
			
			MapWrapper wr = new MapWrapper<>();
			wr.setResultsMap(retMap);
			
			ret.setMessage(msg);
			ret.setResults(retMap.keySet().size());
			ret.setMap(wr);
			

		} catch (Exception e) {
			log.log(Level.SEVERE, "REST Getting influential:", e);
			throw new Exception("REST Getting influential" + e.getMessage());
		}
		return ret;
	};

	@GET
	@Path("/memberList")
	@Produces({ "application/json", "application/xml" })
	public MapResults getMemberList (
			@QueryParam("owner") String owner,
			@QueryParam("listId") String listId) throws Exception {

		MapResults ret = new MapResults();
		try {
			
			String msg = OP_OK;
			Map<String, WebIntentTwitter> retMap = new HashMap<String, WebIntentTwitter>();
			if (!owner.isEmpty() && !listId.isEmpty()){
				cFacade = getCaptureFacadeIf();
				retMap = cFacade.getMemberList (owner, listId);
			}else msg = OP_NO_OK_MISSING_PARAMETERS;
			
			MapWrapper wr = new MapWrapper<>();
			wr.setResultsMap(retMap);
			
			ret.setMessage(msg);
			ret.setResults(retMap.keySet().size());
			ret.setMap(wr);
			

		} catch (Exception e) {
			log.log(Level.SEVERE, "REST Getting influential:", e);
			throw new Exception("REST Getting influential" + e.getMessage());
		}
		return ret;
	};
	
	
	@GET
	@Path("/tweetThread")
	@Produces({ "application/json", "application/xml" })
	public MapResults getTweetThread (
			@QueryParam("tweetIdL") List<String> tweetIdList) throws Exception {

		MapResults results = new MapResults<>();
		
		String msg = OP_OK;
			
		CaptureFacadeIf cFacade = getCaptureFacadeIf();
		/*	
		Map<String, MapWrapper<String, TweetThread>> retMap = new HashMap<String, MapWrapper<String, TweetThread>>();
		
		if (!tweetIdList.isEmpty()){
			for (String tweetId: tweetIdList){
				List<String> list = new ArrayList<String>();
				list.add(tweetId);
				Map<String, TweetThread> m = cFacade.getTweetThread(list);
				MapWrapper<String, TweetThread> aux = new MapWrapper<String, TweetThread>();
				aux.setResultsMap(m);
				retMap.put(tweetId, aux);
			}
		}else msg = OP_NO_OK_MISSING_PARAMETERS;
		
		MapWrapper<String, MapWrapper<String, TweetThread>> ret = new MapWrapper<String, MapWrapper<String, TweetThread>>();
		ret.setResultsMap(retMap);
		
		results.setMessage(msg);
		results.setResults(ret.getResultsMap().keySet().size());
		results.setMap(ret);
		*/
		
		
		Map<String, TweetThread> retMap = new HashMap<String, TweetThread>();
		if (!tweetIdList.isEmpty() ){
			for (String tweetId: tweetIdList){
				List<String> list = new ArrayList<String>();
				list.add(tweetId);
				Map<String, TweetThread> m = cFacade.getTweetThread(list);
				//MapWrapper<String, TweetThread> aux = new MapWrapper<String, TweetThread>();
				//aux.setResultsMap(m);
				retMap.putAll(m);
			}
		}else msg = OP_NO_OK_MISSING_PARAMETERS;
		
		MapWrapper wr = new MapWrapper<>();
		wr.setResultsMap(retMap);
		
		results.setMessage(msg);
		results.setResults(retMap.keySet().size());
		results.setMap(wr);
	
		return results;
	};
	
//	// // Obtener los N most populars (en cuanto a volomen) de entre una serie
//	// de datachhanels dados
//	@GET
//	@Path("/populars")
//	@Produces({ "application/json", "application/xml" })
//	// public MapResults<Date, Map<DataChannel, Integer>> getTopVolume(
//	public MapResults getTopVolume(
//			@QueryParam("dataPoolIdL") List<String> dataPoolIdList,
//			@QueryParam("initDate") Date initDate,
//			@QueryParam("endDate") Date endDate,
//			@QueryParam("per") Periodicity per,
//			@DefaultValue("3") @QueryParam("nor") int numberOfResults) throws Exception {
//
//		MapResults results = new MapResults<>();
//		try{
//			
//			String msg = OP_OK;
//			Map<Date, MapWrapper<DataChannel, Integer>> retMap = new HashMap<Date, MapWrapper<DataChannel, Integer>>();
//			if (!dataPoolIdList.isEmpty() && initDate != null && endDate != null && per!= null){
//				log.info("REST Getting populars for datachannels: " + dataPoolIdList.toString() + ", for initDate: "+ initDate.toString() + ", for endDate: "+ endDate.toString() + ", for per: "+ per.toString());
//				Map<Date, Map<DataChannel, Integer>> m = realCai.getTopVolume(dataPoolIdList, initDate, endDate, per, numberOfResults);
//
//				Iterator<Entry<Date, Map<DataChannel, Integer>>> it = m.entrySet()
//						.iterator();
//				while (it.hasNext()) {
//					Entry<Date, Map<DataChannel, Integer>> e = it.next();
//					MapWrapper<DataChannel, Integer> aux = new MapWrapper<DataChannel, Integer>();
//					aux.setResultsMap(e.getValue());
//					retMap.put(e.getKey(), aux);
//				}
//			}else msg = OP_NO_OK_MISSING_PARAMETERS;
//			
//			MapWrapper<Date, MapWrapper<DataChannel, Integer>> ret = new MapWrapper<Date, MapWrapper<DataChannel, Integer>>();
//			ret.setResultsMap(retMap);
//			
//			results.setMessage(msg);
//			results.setResults(ret.getResultsMap().keySet().size());
//			results.setMap(ret);
//
//		}catch (CaptureException e1) {
//		// TODO Auto-generated catch block
//		log.log(Level.SEVERE, "Error while getting populars:", e1);
//		throw new Exception("Error while getting populars: " + e1, e1);
//		}
//		return results;
//	};
//
//	// Obtener la popularidad de un datachannel (en cuanto a volomen) de entre
//	// una serie de datachhanels dados
//	@GET
//	@Path("/popularity")
//	@Produces({ "application/json", "application/xml" })
//	public MapResults getVolumePer(
//			@QueryParam("dataPoolIdL") List<String> dataPoolIdList,
//			@QueryParam("dataPoolId") String dataPoolId,
//			@QueryParam("initDate") Date initDate,
//			@QueryParam("endDate") Date endDate,
//			@QueryParam("per") Periodicity per) throws Exception {
//
//		MapResults ret = new MapResults();
//		try{
//			
//			String msg = OP_OK;
//			Map<Date, Integer> retMap = new HashMap<Date, Integer>();
//			if (!dataPoolIdList.isEmpty() && initDate != null && endDate != null && per!= null){
//				log.info("REST Getting popularity for datachannel: " + dataPoolId.toString() + ", for initDate: "+ initDate.toString() + ", for endDate: "+ endDate.toString() + ", for per: "+ per.toString());
//				retMap = realCai.getVolumePer(dataPoolIdList, dataPoolId, initDate, endDate, per);
//			}else msg = OP_NO_OK_MISSING_PARAMETERS;
//			
//			MapWrapper wr = new MapWrapper<>();
//			wr.setResultsMap(retMap);
//			
//			ret.setMessage(msg);
//			ret.setResults(retMap.keySet().size());
//			ret.setMap(wr);
//		}catch (CaptureException e1) {
//			// TODO Auto-generated catch block
//			log.log(Level.SEVERE, "Error while getting populars:", e1);
//			throw new Exception("Error while getting populars: " + e1, e1);
//		}
//		return ret;
//	};
//
//	// Widget 7: para pintar los velicimetros. Dada una lista de datachannels
//	// devolver el sentimiento acumulado (entre initDate and endDate) para cada
//	// uno de ellos.
//	@GET
//	@Path("/sentimentDegree")
//	@Produces({ "application/json", "application/xml" })
//	public MapResults getSentimentDegree(
//			@QueryParam("dataPoolIdL") List<String> dataPoolIdList,
//			@QueryParam("initDate") Date initDate,
//			@QueryParam("endDate") Date endDate,
//			@QueryParam("per") Periodicity per) throws Exception {
//
//		MapResults map = new MapResults();
//		try{
//	
//			String msg = OP_OK;
//			Map<Date, MapWrapper> dateMapW = new HashMap();
//			if (!dataPoolIdList.isEmpty() && initDate != null && endDate != null && per!= null){
//				log.info("REST Getting sentiment degree for datachannels: " + dataPoolIdList.toString() + ", for initDate: "+ initDate.toString() + ", for endDate: "+ endDate.toString() + ", for per: "+ per.toString());
//				Map<Date, Map<DataChannel, Double>> resultMap = realCai.getSentimentDegree(dataPoolIdList, initDate, endDate, per);
//				
//				Iterator<Entry<Date, Map<DataChannel, Double>>> dateIt = resultMap
//						.entrySet().iterator();
//				while (dateIt.hasNext()) {
//					Entry<Date, Map<DataChannel, Double>> e = dateIt.next();
//					MapWrapper dcMapWrapper = new MapWrapper();
//					dcMapWrapper.setResultsMap(e.getValue());
//					dateMapW.put(e.getKey(), dcMapWrapper);
//		
//				}
//			}else msg = OP_NO_OK_MISSING_PARAMETERS;
//	
//			MapWrapper dateWrapper = new MapWrapper();
//			dateWrapper.setResultsMap(dateMapW);
//	
//			map.setMap(dateWrapper);
//			map.setMessage(msg);
//			map.setResults(dateMapW.size());
//	
//		}catch (CaptureException e1) {
//			// TODO Auto-generated catch block
//			log.log(Level.SEVERE, "Error while getting sentiment degree:", e1);
//			throw new Exception("Error while getting sentiment degree: " + e1, e1);
//		}
//		return map;
//	};
//
//	@GET
//	@Path("/sentiment")
//	@Produces({ "application/json", "application/xml" })
//	public MapResults getSentiment(
//			@QueryParam("dataPoolIdL") List<String> dataPoolIdList,
//			@QueryParam("initDate") Date initDate,
//			@QueryParam("endDate") Date endDate,
//			@QueryParam("per") Periodicity per) throws Exception {
//
//		MapResults map = new MapResults();
//		try{
//
//			String msg = OP_OK;
//			Map<Date, MapWrapper> dateMapW = new HashMap();
//			if (!dataPoolIdList.isEmpty() && initDate != null && endDate != null && per!= null){
//				log.info("REST Getting sentiment for datachannels: " + dataPoolIdList.toString() + ", for initDate: "+ initDate.toString() + ", for endDate: "+ endDate.toString() + ", for per: "+ per.toString());
//				Map<Date, Map<DataChannel, Integer[]>> resultMap = realCai.getSentiment(
//						dataPoolIdList, initDate, endDate, per);
//				
//				Iterator<Entry<Date, Map<DataChannel, Integer[]>>> dateIt = resultMap
//						.entrySet().iterator();
//				while (dateIt.hasNext()) {
//					Entry<Date, Map<DataChannel, Integer[]>> e = dateIt.next();
//					
//					Iterator<Entry<DataChannel, Integer[]>> it = e.getValue().entrySet().iterator();
//					Map <DataChannel, ListWrapper> aux = new HashMap<DataChannel, ListWrapper>();
//					while(it.hasNext()){
//						Entry<DataChannel, Integer[]> e1 = it.next();
//						ListWrapper lw = new ListWrapper();
//						lw.setList(Arrays.asList(e1.getValue()));
//						aux.put(e1.getKey(), lw);
//					}
//					
//					MapWrapper auxW = new MapWrapper();
//					auxW.setResultsMap(aux);
//					dateMapW.put(e.getKey(), auxW);
//					
//				}
//			}else msg = OP_NO_OK_MISSING_PARAMETERS;
//
//			MapWrapper dateWrapper = new MapWrapper();
//			dateWrapper.setResultsMap(dateMapW);
//	
//			map.setMap(dateWrapper);
//			map.setMessage(OP_OK);
//			map.setResults(dateMapW.size());
//
//		}catch (CaptureException e1) {
//			// TODO Auto-generated catch block
//			log.log(Level.SEVERE, "Error while getting sentiment:", e1);
//			throw new Exception("Error while getting sentiment: " + e1, e1);
//		}
//		return map;
//	};
//
//	@GET
//	@Path("/newVolume")
//	@Produces({ "application/json", "application/xml" })
//	// Widget 6 (solo volumen). Dada una lista de datachannels obtener el
//	// volumen para cada uno de ellos
//	public MapResults getVolume(@QueryParam("dataPoolIdL") List<String> dcList,
//			@QueryParam("initDate") Date initDate,
//			@QueryParam("endDate") Date endDate,
//			@QueryParam("per") Periodicity per) throws Exception {
//	
//		MapResults map = new MapResults();
//		try{
//
//			String msg = OP_OK;
//			Map<Date, MapWrapper> dateMapW = new HashMap();
//			if (!dcList.isEmpty() && initDate != null && endDate != null && per!= null){
//				log.info("REST Getting volume for datachannels: " + dcList.toString() + ", for initDate: "+ initDate.toString() + ", for endDate: "+ endDate.toString() + ", for per: "+ per.toString());
//				Map<Date, Map<DataChannel, Integer>> resultMap = realCai.getVolume(dcList,
//						initDate, endDate, per);
//				
//				Iterator<Entry<Date, Map<DataChannel, Integer>>> dateIt = resultMap
//						.entrySet().iterator();
//				while (dateIt.hasNext()) {
//					Entry<Date, Map<DataChannel, Integer>> e = dateIt.next();
//					MapWrapper dcMapWrapper = new MapWrapper();
//					dcMapWrapper.setResultsMap(e.getValue());
//					dateMapW.put(e.getKey(), dcMapWrapper);
//
//				}
//			}else msg = OP_NO_OK_MISSING_PARAMETERS;
//			
//			MapWrapper dateWrapper = new MapWrapper();
//			dateWrapper.setResultsMap(dateMapW);
//
//			map.setMap(dateWrapper);
//			map.setMessage(msg);
//			map.setResults(dateMapW.size());
//
//		}catch (CaptureException e1) {
//			// TODO Auto-generated catch block
//			log.log(Level.SEVERE, "Error while getting volumen:", e1);
//			throw new Exception("Error while getting volume: " + e1, e1);
//		}
//		return map;
//	};
//
//	
//	@GET
//	@Path("/tagCloud")
//	@Produces({ "application/json", "application/xml" })
//	// Widget 9 (solo tag cloud). dado una lista de datachannels te devuelve
//	// tantos tag cloud como datachannels metidos
//	public MapResults getTagCloud(
//			@QueryParam("dataPoolIdL") List<String> dcIdList,
//			@QueryParam("date") Date date) throws Exception {
//
//		log.info("REST Getting tag cloud.... ");
//		log.info("Datachannels: " + dcIdList.toString());
//		log.info("Date: "+ date.toString());
//		MapResults map = new MapResults();
//	
//		
//		
//		try {
//			Map<DataChannel, Map<String, Integer>> resultMap = realCai.getTagCloud(dcIdList, date);
//			Map<DataChannel, MapWrapper> dateMapW = new HashMap();
//			
//			log.info("REST got tag cloud");
//			Iterator<Entry<DataChannel, Map<String, Integer>>> dateIt = resultMap.entrySet().iterator();
//			while (dateIt.hasNext()) {
//				Entry<DataChannel, Map<String, Integer>> e = dateIt.next();
//				MapWrapper dcMapWrapper = new MapWrapper();
//				dcMapWrapper.setResultsMap(e.getValue());
//				dateMapW.put(e.getKey(), dcMapWrapper);
//
//			}
//
//			MapWrapper dateWrapper = new MapWrapper();
//			dateWrapper.setResultsMap(dateMapW);
//
//			map.setMap(dateWrapper);
//			map.setMessage(OP_OK);
//			map.setResults(dateMapW.size());
//
//			return map;
//		} catch (CaptureException e1) {
//			// TODO Auto-generated catch block
//			log.log(Level.SEVERE, "Error while getting tag cloud:", e1);
//			throw new Exception("Error while getting tag cloud: " + e1, e1);
//		}
//
//	};
	

	private class AnalitycMock implements CaptureAnalyticIf {

		@Override
		public Map<Date, Map<DataChannel, Integer>> getTopVolume(
				List<String> dcIdList, Date initDate, Date endDate,
				Periodicity per, int numberOfResults) {

			Map<DataChannel, Integer> channelsMap = new HashMap<DataChannel, Integer>();

			for (int i = 0; i < 3; i++) {
				DataChannel dc = new DataChannel();
				dc.setChannelID("alskdjalsdkj" + i);
				dc.setCreationDate("22-05-2015");
				dc.setDescription("Description " + i);
				dc.setName("Name " + i);
				dc.setType("twitter");
				channelsMap.put(dc, i * 100);

			}

			Map<Date, Map<DataChannel, Integer>> datesMap = new HashMap<Date, Map<DataChannel, Integer>>();

			Calendar calendar = Calendar.getInstance();

			for (int j = 0; j < 3; j++) {

				calendar.add(Calendar.HOUR, j);

				datesMap.put(calendar.getTime(), channelsMap);
			}

			return datesMap;
		}

		@Override
		public Map<Date, Integer> getVolumePer(List<String> dcIdList,
				String dcId, Date initDate, Date endDate, Periodicity per) {
			Map<Date, Integer> datesMap = new HashMap<Date, Integer>();

			Calendar calendar = Calendar.getInstance();

			for (int j = 0; j < 5; j++) {

				calendar.add(Calendar.HOUR, j);

				datesMap.put(calendar.getTime(), j * 50);
			}

			return datesMap;
		}

		@Override
		public Map<Date, Map<DataChannel, Double>> getSentimentDegree(
				List<String> dcList, Date initDate, Date endDate,
				Periodicity per) {
			Map<DataChannel, Double> channelsMap = new HashMap<DataChannel, Double>();

			for (int i = 0; i < 3; i++) {
				DataChannel dc = new DataChannel();
				dc.setChannelID("alskdjalsdkj" + i);
				dc.setCreationDate("22-05-2015");
				dc.setDescription("Description " + i);
				dc.setName("Name " + i);
				dc.setType("twitter");
				channelsMap.put(dc, (double) (i * 50));

			}

			Map<Date, Map<DataChannel, Double>> datesMap = new HashMap<Date, Map<DataChannel, Double>>();

			Calendar calendar = Calendar.getInstance();

			for (int j = 0; j < 3; j++) {

				calendar.add(Calendar.HOUR, j);

				datesMap.put(calendar.getTime(), channelsMap);
			}

			return datesMap;
		}

		@Override
		public Map<Date, Map<DataChannel, Integer[]>> getSentiment(
				List<String> dcIdList, Date initDate, Date endDate,
				Periodicity per) {
			Map<DataChannel, Integer[]> channelsMap = new HashMap<DataChannel, Integer[]>();

			for (int i = 0; i < 3; i++) {
				DataChannel dc = new DataChannel();
				dc.setChannelID("alskdjalsdkj" + i);
				dc.setCreationDate("22-05-2015");
				dc.setDescription("Description " + i);
				dc.setName("Name " + i);
				dc.setType("twitter");
				channelsMap.put(dc, new Integer[] { i * 10, i * 2 });

			}

			Map<Date, Map<DataChannel, Integer[]>> datesMap = new HashMap<Date, Map<DataChannel, Integer[]>>();

			Calendar calendar = Calendar.getInstance();

			for (int j = 0; j < 3; j++) {

				calendar.add(Calendar.HOUR, j);

				datesMap.put(calendar.getTime(), channelsMap);
			}

			return datesMap;
		}

		@Override
		public Map<Date, Map<DataChannel, Integer>> getVolume(
				List<String> dcList, Date initDate, Date endDate,
				Periodicity per) {
			Map<DataChannel, Integer> channelsMap = new HashMap<DataChannel, Integer>();

			for (int i = 0; i < 3; i++) {
				DataChannel dc = new DataChannel();
				dc.setChannelID("alskdjalsdkj" + i);
				dc.setCreationDate("22-05-2015");
				dc.setDescription("Description " + i);
				dc.setName("Name " + i);
				dc.setType("twitter");
				channelsMap.put(dc, i * 100);

			}

			Map<Date, Map<DataChannel, Integer>> datesMap = new HashMap<Date, Map<DataChannel, Integer>>();

			Calendar calendar = Calendar.getInstance();

			for (int j = 0; j < 3; j++) {

				calendar.add(Calendar.HOUR, j);

				datesMap.put(calendar.getTime(), channelsMap);
			}

			return datesMap;
		}

		@Override
		public Map<DataChannel, Map<String, Integer>> getTagCloud(
				@QueryParam("dataPoolIdL") List<String> dcIdList,
				@QueryParam("date") Date date,
				@QueryParam("per") Periodicity per) {

//			Map<String, Integer> termMap = new HashMap<String, Integer>();
//
//			for (int i = 0; i < 3; i++)
//				termMap.put("Tag " + i, i * 10);
//
//			Map<DataChannel, Map<String, Integer>> channelsMap = new HashMap<DataChannel, Map<String, Integer>>();
//
//			for (int i = 0; i < 3; i++) {
//				DataChannel dc = new DataChannel();
//				dc.setChannelID("alskdjalsdkj" + i);
//				dc.setCreationDate("22-05-2015");
//				dc.setDescription("Description " + i);
//				dc.setName("Name " + i);
//				dc.setType("twitter");
//				channelsMap.put(dc, termMap);
//
//			}
//			
//			return channelsMap;
			return null;
		}

		@Override
		public Map<Date, Map<TwitterUser, ReachAnalysis>> getTwitterReach(List<String> userIdList, Date initDate,
				Date endDate, Periodicity per) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<Date, Map<TwitterUser, EngagementAnalysis>> getTwitterEngagement(List<String> userIdList,
				Date initDate, Date endDate, Periodicity per) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, Map<TwitterUser, CompetitorAnalysis>> getTwitterCompetitors(String userId, Date date)
				throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<VolumeOcurrenceAnalysis> getVolumeOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate,
				Date endDate, Periodicity per) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<TermOccurrenceAnalysis> getTermOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate,
				Date endDate, Periodicity per) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<Date, Map<DataChannel, Integer>> getVolumePer(List<String> dataPoolIdList,
				List<String> dpCandidateList, Date initDate, Date endDate, Periodicity per) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<Date, Map<TwitterUser, AmbassadorQuality>> getAmbassadorQuality(List<String> userIdList, String code,
				Date initDate, Date endDate, Periodicity per, List<Double> weightList) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<TwitterUser, AmbassadorQuality> getAmbassadorQualityTop(List<String> userIdList, int top,
				String code, Date initDate, Date endDate, Periodicity per, List<Double> weightList)
						throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<DataChannel, Map<String, Integer>> getTagCloudAccumulated(List<String> dataPoolIdList, Date initDate,
				Date endDate, Periodicity per, int top) throws CaptureException {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
