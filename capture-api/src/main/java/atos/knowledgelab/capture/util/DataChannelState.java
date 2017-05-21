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



/**
 * This is an enum that describes the state of the DataChannel.
 * It is not to be confused with DataChannel.state, that is used for
 * tagging active/deleted datachannels and has nothing to do with actual
 * state of the datachannel.
 * 
 * The state is based on the actual activity of the QueryQueue and
 * the DataChannel that corresponds to the active query is notified
 * on its status. 
 * 
 * The DC status is to enable polling for DS status, in order to
 * - monitor DC status
 * - change DC type (historical -> real time)
 * 
 * @author matt
 *
 *
 */
public enum DataChannelState {

	/* 
	 * 
	 * the order of the DC/DS states are the following:
	 * READY -> RUNNING -> FINISHED
	 * 
	 * 
	 * The states are atomic for Data Source. For Data Channel it is 
	 * an aggregation of all DS in DC, so that: 
	 * 
	 * 
	 */
	
	
	READY,
	RUNNING,
	FINISHED,
	
	//not implemented:
	SCHEDULED,
	API_WAITING;
	
	
	
	
	
	public String value() {
        return name();
    }
    
    public static DataChannelState fromString(String param) throws Exception {
        String toUpper = param.toUpperCase();
        try {
            return valueOf(toUpper);
        } catch (Exception e) {
        	//send 400 error in this case, as the search mode is obligatory
        	//Thread.dumpStack();
        	throw new Exception("No such state!");
        }
    }
	
}
