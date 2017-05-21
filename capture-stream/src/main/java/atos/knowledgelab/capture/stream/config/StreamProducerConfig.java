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
package atos.knowledgelab.capture.stream.config;

import java.util.Properties;

/*
 * 
 * Kafka producer will accept a standard Java property class,
 * for configuration parameters. 
 * In the future we can specify here:
 * - default options, 
 * - reading configuration from file here.
 * - etc... 
 * 
 */
public class StreamProducerConfig extends Properties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StreamProducerConfig(Properties props) {
		super(props);
	}

	public StreamProducerConfig() {		
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Object k : this.keySet()) {
			sb.append((String) k + ": " + (String) this.getProperty((String) k) + "\n");
		}
		
		return sb.toString();
	}
	
}
