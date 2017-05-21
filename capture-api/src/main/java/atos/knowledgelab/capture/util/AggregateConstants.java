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
package atos.knowledgelab.capture.util;

public class AggregateConstants {
	public static final String daily = "daily";
	public static final String hourly = "hourly";
	public static final String minutes30 = "minutes30";
	public static final String minutes15 = "minutes15";
	public static final String minutes5 = "minutes5";
	public static final String minutes1 = "minutes1";
	
	
	public static final String agg_desc_tableName = "capture_aggregator_fast";
	public static final String agg_desc_columnFamily = "capture_aggregator";
	public static final byte[] agg_desc_bColFamily = agg_desc_columnFamily.getBytes();
	public static final String agg_desc_colInitDate = "initdate";
	public static final String agg_desc_colEndDate = "enddate";
	public static final String agg_desc_colDatachannels = "datachannels";
	public static final String agg_desc_colAnalysisType = "analysisType";
	public static final String agg_desc_colPeriodicity = "periodicity";
	public static final String agg_desc_colDescription = "description";
	public static final String agg_desc_colPeriodically = "periodically";
	public static final String agg_desc_colStatus = "status";
	public static final String agg_desc_colType = "type";
	
	public static final String aggregateTagCloud = "TC";
	public static final String aggregateTotalVolume = "VT";
	public static final String aggregatePositiveVolume = "VP";
	public static final String aggregateNegativeVolume = "VN";
	public static final String aggregateNeutroVolume = "VO";
	public static final String aggregateDaily = "DAY";
	public static final String aggregateHourly = "HOU";
	public static final String aggregateMinutes30 = "M30";
	public static final String aggregateMinutes15 = "M15";
	public static final String aggregateMinutes5 = "M05";
	public static final String aggregateMinutes1 = "M01";
	public static final String aggregateOther = "OTH";
	
	public static final String tweets_tableName = "capture_tweet_fast";
	public static final String tweets_columnFamily = "capture_tweet";
	public static final String tweets_columnText = "text";
	public static final String tweets_columnSentiment = "sentiment";
	public static final String tweets_columnLanguage = "lang";
	
	public static final String agg_res_tableName = "capture_aggregate_fast";
	public static final String agg_res_columnFamily = "capture_aggregate";
	public static final byte[] agg_res_bColFamily = agg_res_columnFamily.getBytes();
	public static final String TMP_DIR = "/tmp";

}
