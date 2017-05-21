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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.persistence.denormalized.HBaseDenormProxy;

public class DataPoolTest {

	public static void main(String[] args) throws Exception {
		HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();

//		System.out.println("============================");
//		System.out.println("Create some data pool");
//		System.out.println("============================");
//		System.out.println();
//		
//		DataPool dp = new DataPool();
//		dp.setDescription("Description");
//		dp.setName("Name");
//		dp.setKeywords(Arrays.asList(new String[] {"keyword1", "keyword2"}));
//		
//		System.out.println("Store data pool");
//		hbp.storeDataPool(dp);
//		
//		System.out.println("Assigned ID: " + dp.getPoolID());
//		
//		System.out.println("Retrieve the same data pool");
//		DataPool dp2 = hbp.getDpById(dp.getPoolID());
//		
//		System.out.println("DP Name: " + dp2.getName());
//		System.out.println("DP Desc: " + dp2.getDescription());
//		System.out.println("DP ID: " + dp2.getPoolID());
//		System.out.println("DP keywords: " + dp2.getKeywords());
//		
//		
		System.out.println();
		System.out.println("============================");
		System.out.println("Retrieve all data pools");
		System.out.println("============================");
		System.out.println();
		
		DataPoolList list = hbp.getDataPools();
		for (DataPool dp3 : list.getDataPools())  {
			System.out.println("Found data pool ->");
			System.out.println(" DP Name: " + dp3.getName());
			System.out.println(" DP Desc: " + dp3.getDescription());
			System.out.println(" DP ID: " + dp3.getPoolID());
			System.out.println(" DP keywords: " + dp3.getKeywords());
			System.out.println();
		}
		
		
		System.out.println();
		System.out.println("============================");
		System.out.println("Retrieve all data pools - second method");
		System.out.println("============================");
		System.out.println();
		
		list = hbp.getDataPools(null, 100);
		for (DataPool dp3 : list.getDataPools())  {
			System.out.println("Found data pool ->");
			System.out.println(" DP Name: " + dp3.getName());
			System.out.println(" DP Desc: " + dp3.getDescription());
			System.out.println(" DP ID: " + dp3.getPoolID());
			System.out.println(" DP keywords: " + dp3.getKeywords());
			System.out.println();
		}
	}

}
