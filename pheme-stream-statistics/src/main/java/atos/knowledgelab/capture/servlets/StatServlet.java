package atos.knowledgelab.capture.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

public class StatServlet extends HttpServlet {

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
			
			if (paramValue != null && paramValue.length() != 0) {
				String topicName = paramValue;
				Monitor monitor = MonitorManager.getInstance().getMonitors().get(topicName);	
				if (monitor == null) {
					new Throwable("No such topic");
				}
				
        		TweetCounter tc = monitor.getTweetCounter();
        		

				//System.out.println(topics);
	    		
				req.setAttribute("menutopics", topics);

				List<String> topicsToGraph = Arrays.asList(paramValue);
				req.setAttribute("topics", topicsToGraph);
				req.setAttribute("topicsjs", mapper.writeValueAsString(topicsToGraph));
				 
				
				
		    	req.getRequestDispatcher("chart.jsp").forward(req, resp);
			} else {
				req.setAttribute("menutopics", topics);
				
				req.setAttribute("topics", topics);
				req.getRequestDispatcher("select.jsp").forward(req, resp);
				
			}
		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("errormsg", e.getMessage());
			req.getRequestDispatcher("error.jsp").forward(req, resp);


		}
    	
    	
    	
    }
}
