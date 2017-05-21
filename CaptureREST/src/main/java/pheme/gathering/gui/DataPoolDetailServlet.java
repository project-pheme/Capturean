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
package pheme.gathering.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import atos.knowledgelab.capture.bean.DataPool;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class DataPoolDetailServlet extends HttpServlet {

	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	try {
    		
    		if (req.getParameter("id") != null) {
    			Client client = Client.create();
    			//add authentication data.
    			//TODO  This is to be extended.
    			client.addFilter(new HTTPBasicAuthFilter("admin", "66.admin"));
    			
				ServletContext context = getServletContext();
	    		String endpoint = (String) context.getAttribute("endpoint");

    			WebResource webResource = client.resource(endpoint + "/datapool/" + req.getParameter("id"));
    			ClientResponse response = webResource.accept("application/xml").get(ClientResponse.class);

    			if (response.getStatus() != 200) {
    				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
    			}

    			DataPool output = response.getEntity(DataPool.class);
    			
    			req.setAttribute("datapool", output);
    	    	req.getRequestDispatcher("detailsDataPool.jsp").forward(req, resp);
    		} else {
    			
    	    	req.getRequestDispatcher("error.jsp").forward(req, resp);
    		}
			
		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("result", e.getMessage());
			req.getRequestDispatcher("confirmationDataPool.jsp").forward(req, resp);
		}
    	
    	
    	
    }
}
