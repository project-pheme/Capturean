package atos.knowledgelab.capture.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.counter.Tick;
import atos.knowledgelab.capture.counter.TweetCounter;
import atos.knowledgelab.capture.stream.monitor.Monitor;
import atos.knowledgelab.capture.stream.monitor.MonitorManager;

public class TimelineMinutesServlet extends HttpServlet {

	ObjectMapper mapper = new ObjectMapper();
	private static Logger LOGGER = Logger.getLogger(TimelineMinutesServlet.class.getName());

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	try {

    		//String topicName = req.getParameter("topic");
    		
    		String pathInfo = req.getPathInfo();
    		
    		if (pathInfo != null && pathInfo.length() != 0) {
    			String topicName = pathInfo.substring(1);
    			Monitor monitor = MonitorManager.getInstance().getMonitors().get(topicName);
    			
        		TweetCounter tc = monitor.getTweetCounter();

    			String s = mapper.writeValueAsString(tc.getMinutes());
    			resp.setContentType("application/json");
    			resp.getWriter().print(s);
        		
    		} else {
    			resp.setContentType("application/json");
    			resp.getWriter().print("{\"error\": \"No topic given\"}");
    			
    			LOGGER.info("No topic given");
    		}
    		
			
		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("result", e.getMessage());
			req.getRequestDispatcher("confirmation.jsp").forward(req, resp);


		}
    	
    	
    	
    }
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		String pathInfo = req.getPathInfo();
		
		if (pathInfo != null && pathInfo.length() != 0) {
			String topicName = pathInfo.substring(1);
			Monitor monitor = MonitorManager.getInstance().getMonitors().get(topicName);
			
			if (monitor != null) {
				try {
					BufferedReader reader = req.getReader();
					while ((line = reader.readLine()) != null)
						jb.append(line);
				} catch (Exception e) {
					LOGGER.log(Level.SEVERE, "Error reading POST request", e); 
				}

				List<Tick> list = new ArrayList<Tick>();

				list = mapper.readValue(jb.toString(),  new TypeReference<List<Tick>>(){});
				
				TweetCounter tc = monitor.getTweetCounter();
				tc.setMinutes(new ConcurrentLinkedQueue<Tick>(list));
				
				LOGGER.info("Read " + list.size() + " statements.");
			} else {
				LOGGER.info("No such topic!");

			}

		}
	}
}
