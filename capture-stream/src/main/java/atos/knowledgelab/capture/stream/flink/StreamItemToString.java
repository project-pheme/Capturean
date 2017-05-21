/*******************************************************************************
 * Copyright (C) 2016  ATOS Spain S.A.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
package atos.knowledgelab.capture.stream.flink;

import java.util.logging.Logger;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemSerialize;

public class StreamItemToString implements FlatMapFunction<StreamItem, String> {

	private static final long serialVersionUID = 1L;
	static Logger LOGGER = Logger.getLogger(StringToStreamItem.class.getName());

	public void flatMap(StreamItem arg0, Collector<String> arg1) throws Exception {
		// TODO Auto-generated method stub
		//LOGGER.info("El tweet: "+ arg0.getTweet().getId() + " ,el dp: " + arg0.getDataChannelId());
		arg1.collect(new StreamItemSerialize().serialize (arg0));
	}
}
