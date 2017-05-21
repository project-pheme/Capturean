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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.hadoop.hbase.util.Bytes;

public class CRC32Test {

	public static void main(String[] args) {

		
		Set<String> crcLlist = new HashSet<String>();
		Set<Integer> javaHashList = new HashSet<Integer>();

		int crcCollisionCount = 0;
		int javaHashCollisionCount = 0;
		
		for (int i=0; i<1_000_000; i++) {
			UUID uuid = UUID.randomUUID();
			//System.out.println(uuid.toString());
			
			CRC32 crc = new CRC32();
			crc.update(Bytes.toBytes(uuid.toString()));
			
//			System.out.println(crc.toString());
//			System.out.println(crc.getValue());
//			System.out.println(crc.hashCode());
			String k = Long.toHexString(crc.getValue());
			
			
					
			System.out.println(String.format("%8s", k).replace(" ", "0"));
			
			boolean crcExists = crcLlist.add(Long.toHexString(crc.getValue()));
			boolean javaHashExists = javaHashList.add(uuid.hashCode());
			
			if (crcExists == false) {
				System.out.println("\nCRC COLLISION!");
				crcCollisionCount += 1;
			}
			
			if (javaHashExists == false) {
				System.out.println("\nJAVA HASH COLLISION!");
				javaHashCollisionCount += 1;
			}
			
//			try {
//				Thread.sleep(0, 30);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			if (i % 1000 == 0) {
				System.out.print(".");
			
			}
		}
		
		System.out.println();
		System.out.println("CRC Collisions: " + crcCollisionCount);
		System.out.println("Java Hash Collisions: " + javaHashCollisionCount);
		 
		
		
	}

	
	
	
	
	
	
	
	
	
	
}
