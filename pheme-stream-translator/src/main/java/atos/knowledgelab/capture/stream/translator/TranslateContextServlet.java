package atos.knowledgelab.capture.stream.translator;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;



public class TranslateContextServlet implements ServletContextListener {

	private final static Logger LOGGER = Logger.getLogger(TranslateContextServlet.class.getName());
	
	public void contextDestroyed(ServletContextEvent arg0) {
		 
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			Translate.getInstance().start();
			LOGGER.info("Stream translation service started!");
		} catch (Exception e) {
			LOGGER.severe(" " + e.getMessage());
			e.printStackTrace();
		}
		
	}

}
