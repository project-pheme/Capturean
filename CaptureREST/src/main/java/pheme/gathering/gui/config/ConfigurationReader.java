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
package pheme.gathering.gui.config;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.shiro.SecurityUtils;

import pheme.gathering.search.TweetManager;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;


public class ConfigurationReader implements ServletContextListener {

	private final static Logger LOGGER = Logger.getLogger(ConfigurationReader.class.getName());

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Read configuration and make it available in the servlet context
		//String endpoint = "http://95.211.84.96:8080/CaptureREST/rest/";
		
		//Temporary: set default username/pass for accessing RESTful services
		ServletContext context = sce.getServletContext();
		//context.setAttribute("username", "admin");
		//context.setAttribute("password", "66.admin");
		
		Properties prop=new Properties();
		try {
			prop.load(ConfigurationReader.class.getResourceAsStream("/capture.properties"));
			String endpoint = (String) prop.get("restEndpoint");
			
			context.setAttribute("endpoint", endpoint);
			LOGGER.info("Config REST endpoint " + endpoint);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
//		Properties prop=new Properties();
//		try {
//			prop.load(ConfigurationReader.class.getResourceAsStream("/restfulservices.properties"));
//			String username = (String) prop.get("services.adminUsername");
//			String password = (String) prop.get("services.adminPassword");
//			String endpoint = (String) prop.get("services.endpoint");
//			
//			context.setAttribute("endpoint", endpoint);
//			
//		} catch (IOException e) {			
//			e.printStackTrace();
//		}
		
		
	}

	private static String getParameter(ServletContext servletContext,
            String key, String defaultValue) {
        String value = servletContext.getInitParameter(key);
        return value == null ? defaultValue : value;
    }

	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
