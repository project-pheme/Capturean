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
package atos.knowledgelab.capture.persistence.denormalized.test;

import java.io.IOException;
import java.util.List;

import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;

public class HBaseDeleteTest {

	public static void main(String[] args) throws Throwable {
		
		//String dcId = "9e549112";
		String dcId = args[0];

		System.out.println("Data channel ID: " + dcId);
		
		HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		
		long startTime = System.currentTimeMillis();
		//long count = hbp.wipeDataChannelById(dcId, 100);
		long count = hbp.wipeDc(dcId);
		long endTime = System.currentTimeMillis();
		System.out.println("Removing tweets took: " + (endTime - startTime) + " milliseconds");
		System.out.println("Count: " + count);
		
		startTime = System.currentTimeMillis();
		count = hbp.getDcCountFaster(dcId);
		endTime = System.currentTimeMillis();
		System.out.println("Counting tweets took: " + (endTime - startTime) + " milliseconds");
		System.out.println("Count: " + count);


	}

}
