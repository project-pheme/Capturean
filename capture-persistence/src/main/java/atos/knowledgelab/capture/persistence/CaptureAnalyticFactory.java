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

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import atos.knowledgelab.capture.ifaces.CaptureAnalyticIf;

public class CaptureAnalyticFactory {
	private final static String capturAnalyticImpl = "captureAnalyticImpl";
	
	private static CaptureAnalyticIf instance = null;
	
	private final static Logger LOGGER = Logger
			.getLogger(CaptureAnalyticFactory.class.getName());
	
	public static CaptureAnalyticIf getInstance(){
		if(instance == null){
			Properties props = new Properties();
			try {
				props.load(CaptureAnalyticFactory.class.getClassLoader().getResourceAsStream("capture.properties"));
				instance = (CaptureAnalyticIf) CaptureAnalyticFactory.class.getClassLoader().loadClass(props.getProperty(capturAnalyticImpl)).newInstance();
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Cannot load Capture Analytic: " + props.getProperty(capturAnalyticImpl), e);
			}																						
		}
		
		return instance;
	}

}
