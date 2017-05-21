package atos.knowledgelab.capture.monitor.live;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LiveTopic extends HttpServlet {

	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String topic = "pheme_capture";
		if (req.getParameter("topic") != null) {
			topic = req.getParameter("topic");
			
		} else {
			
		}
		
		resp.setCharacterEncoding("UTF-8");

		req.setAttribute("topic", topic);
		req.getRequestDispatcher("live.jsp").forward(req, resp);
	}
}
