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
package atos.knowledgelab.capture.ifaces;

import java.util.Date;
import java.util.List;
import java.util.Map;

import atos.knowledgelab.capture.bean.AmbassadorQuality;
import atos.knowledgelab.capture.bean.CompetitorAnalysis;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.EngagementAnalysis;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.bean.ReachAnalysis;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.exception.CaptureException;

public interface CaptureAnalyticIf {
			
		// Widget 7: Obtener los n (number of result) most popular (el volumen) data channels de entre una serie de datachhanels dados. 
		// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, PORCENTAJE>
		Map<Date, Map<DataChannel, Integer>> getTopVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per, int numberOfResults) throws CaptureException;
		
		// Widget 7: Obtener la popularidad de un datachannels (en cuanto a volomen) de entre una serie de datachhanels dados.
		// Devuelve una lista de <date, porcentaje> de tantos elementos como de la "per"
		Map<Date, Integer> getVolumePer(List<String> dataPoolIdList, String dataPoolId, Date initDate, Date endDate, Periodicity per) throws CaptureException;

		Map<Date, Map<DataChannel, Integer>> getVolumePer(List<String> dataPoolIdList, List<String> dpCandidateList,Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Widget 7: para pintar los velicimetros. Dada una lista de datachannels devolver el sentimiento acumulado (entre initDate and endDate) para cada uno de ellos.
		// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, difPositivos-Negativos>
		Map<Date, Map<DataChannel, Double>> getSentimentDegree(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Widget 8 (grafica superior). Dad una lista de datachannels, te devuelve la resta de positivos - negativos
		// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, [positivos, negativos]>
		Map<Date, Map<DataChannel, Integer[]>> getSentiment(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Widget 6 (solo volumen). Dada una lista de datachannels obtener el volumen para cada uno de ellos
		Map<Date, Map<DataChannel, Integer>> getVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Widget 9 (solo tag cloud). dado una lista de datachannels te devuelve tantos tag cloud como datachannels metidos
		Map<DataChannel, Map<String, Integer>> getTagCloud (List<String> dataPoolIdList, Date date, Periodicity per) throws CaptureException;
		// Same as Widget 9 (solo tag cloud) but with a range of dates. And top N words.
		Map<DataChannel, Map<String, Integer>> getTagCloudAccumulated(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per, int top) throws CaptureException;
		
		// Widget 1 (reach). dado una lista de users te devuelve tantos R
		// Devuelve una lista de tantos elementos como slots (en caso del widget 1: dos dias, el de ayer, y el de antesdeayer), y en cada slot, N elementos de <user, Reach>
		// donde Engagement es de tipo Engagement
		Map<Date, Map<TwitterUser, ReachAnalysis>> getTwitterReach (List<String> userIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Widget 1 (reach). dado una lista de users te devuelve tantos R
		// Devuelve una lista de tantos elementos como slots (en caso del widget 1: dos dias, el de ayer, y el de antesdeayer), y en cada slot, N elementos de <user, Engagement>
		// donde Engagement es de tipo Engagement
		Map<Date, Map<TwitterUser, EngagementAnalysis>> getTwitterEngagement (List<String> userIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
		
		// Dado una lista de users, una lista de pesos para la formula que toque en cuestion, te devuelve tantos elementos como slot y en cada slot, N elementos de <user, AmbassadorQuality>
		Map<Date, Map<TwitterUser, AmbassadorQuality>> getAmbassadorQuality (List<String> userIdList, String code, Date initDate, Date endDate, Periodicity per, List<Double> weightList) throws CaptureException;

		Map<String, Map<TwitterUser, CompetitorAnalysis>> getTwitterCompetitors(String userId, Date date) throws CaptureException;

		List<VolumeOcurrenceAnalysis> getVolumeOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate,
				Date endDate, Periodicity per) throws CaptureException;

		List<TermOccurrenceAnalysis> getTermOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate, Date endDate,
				Periodicity per) throws CaptureException;
		
		// Dado una lista de users, una lista de pesos para la formula que toque en cuestion, te devuelve los TOP N elementos de <user, AmbassadorQuality>
		Map<TwitterUser, AmbassadorQuality> getAmbassadorQualityTop(List<String> userIdList, int top, String code,
				Date initDate, Date endDate, Periodicity per, List<Double> weightList) throws CaptureException;

}
