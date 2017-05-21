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
package atos.knowledgelab.capture.persistence;

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
import atos.knowledgelab.capture.ifaces.CaptureAnalyticIf;

public class CaptureAnalytic implements CaptureAnalyticIf{

	@Override
	public Map<Date, Map<DataChannel, Integer>> getTopVolume(
			List<String> dcIdList, Date initDate, Date endDate,
			Periodicity per, int numberOfResults) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getTopVolume(dcIdList, initDate, endDate, per, numberOfResults);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting most popular:"
					+ ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public Map<Date, Integer> getVolumePer(List<String> dcIdList, String dcId,
			Date initDate, Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getVolumePer(dcIdList, dcId, initDate, endDate, per);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting popularity:"
					+ ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public Map<Date, Map<DataChannel, Integer>> getVolumePer(List<String> dataPoolIdList,
			List<String> dpCandidateList, Date initDate, Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getVolumePer(dataPoolIdList, dpCandidateList, initDate, endDate, per);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting popularity:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Map<Date, Map<DataChannel, Double>> getSentimentDegree(
			List<String> dcIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getSentimentDegree(dcIdList, initDate, endDate, per);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting sentiment degree:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Map<Date, Map<DataChannel, Integer[]>> getSentiment(
			List<String> dcIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getSentiment(dcIdList, initDate, endDate, per);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting sentiment:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Map<Date, Map<DataChannel, Integer>> getVolume(List<String> dcIdList,
			Date initDate, Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getVolume(dcIdList, initDate, endDate, per);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting volume:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Map<DataChannel, Map<String, Integer>> getTagCloud(
			List<String> dcIdList, Date date, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getTagCloud(dcIdList, date);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting tag cloud:"
					+ ": " + e.getMessage(), e);
		}
	}
	
	@Override
	public Map<DataChannel, Map<String, Integer>> getTagCloudAccumulated(
			List<String> dcIdList, Date initDate, Date endDate, Periodicity per, int top) throws CaptureException {
		// TODO Auto-generated method stub
		try {
			return SolrManager.getInstance().getTagCloudAccumulated(dcIdList, initDate, endDate, top);
		} catch (Exception e) {
			throw new CaptureException(" ERROR while getting tag cloud:"
					+ ": " + e.getMessage(), e);
		}
	}

	@Override
	public Map<Date, Map<TwitterUser, ReachAnalysis>> getTwitterReach(List<String> userIdList, Date initDate,
			Date endDate, Periodicity per) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Date, Map<TwitterUser, EngagementAnalysis>> getTwitterEngagement(List<String> userIdList, Date initDate,
			Date endDate, Periodicity per) throws CaptureException {
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
	public Map<Date, Map<TwitterUser, AmbassadorQuality>> getAmbassadorQuality(List<String> userIdList, String code,
			Date initDate,Date endDate, Periodicity per, List<Double> weightList) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<TwitterUser, AmbassadorQuality> getAmbassadorQualityTop(List<String> userIdList, int top, String code,
			Date initDate, Date endDate, Periodicity per, List<Double> weightList) throws CaptureException {
		// TODO Auto-generated method stub
		return null;
	}

}
