package atos.knowledgelab.capture.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.counter.TweetCounter;
import atos.knowledgelab.capture.stream.monitor.Monitor;
import atos.knowledgelab.capture.stream.monitor.MonitorManager;

public class OverviewServlet extends HttpServlet {

	ObjectMapper mapper = new ObjectMapper();

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	try {
    		
    		String paramValue = req.getParameter("topic");
			
    		MonitorManager mm = MonitorManager.getInstance();			
			List<String> topics = new ArrayList<String>(mm.getMonitors().keySet());
			HashMap<String, Long> lastCounts = new HashMap<String, Long>();
			HashMap<String, String> lastMessages = new HashMap<String, String>();
			String title = mm.getPipelineConfiguration().getAppName();
			
			for (String topic : topics) {
				Monitor monitor = MonitorManager.getInstance().getMonitors().get(topic);
				Long count = monitor.getTweetCounter().getLastHourCount();
				String msg = monitor.getTweetCounter().getLastMessage();
				
				lastCounts.put(topic, count);
				lastMessages.put(topic, msg);
			}
			
			
			 
			
			req.setAttribute("title", title);				
			req.setAttribute("menutopics", topics);				
			req.setAttribute("lastcounts", lastCounts);				
			req.setAttribute("lastmsgs", lastMessages);				

			req.getRequestDispatcher("overview.jsp").forward(req, resp);

		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("errormsg", e.getMessage());
			req.getRequestDispatcher("error.jsp").forward(req, resp);


		}
    	
    	
    	
    }
}
