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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.TopicDetailBean;
import atos.knowledgelab.capture.counter.TweetCounter;
import atos.knowledgelab.capture.stream.monitor.Monitor;
import atos.knowledgelab.capture.stream.monitor.MonitorManager;

public class OverviewDetailsServlet extends HttpServlet {

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
			TopicDetailBean topicDetail = new TopicDetailBean();
			
			
			for (String topic : topics) {
				Monitor monitor = MonitorManager.getInstance().getMonitors().get(topic);
				Long count = monitor.getTweetCounter().getLastHourCount();
				String msg = monitor.getTweetCounter().getLastMessage();
				
				lastCounts.put(topic, count);
				lastMessages.put(topic, msg);
			}
			
			topicDetail.setLastCounts(lastCounts);
			topicDetail.setLastMessages(lastMessages);
		

			String s = mapper.writeValueAsString(topicDetail);
			resp.setContentType("application/json");
			resp.getWriter().print(s);
			

		} catch (Exception e) {

			e.printStackTrace();
			req.setAttribute("errormsg", e.getMessage());
			req.getRequestDispatcher("error.jsp").forward(req, resp);


		}
    	
    	
    	
    }
}
