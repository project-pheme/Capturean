package atos.knowledgelab.capture.stream.monitor;

import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import atos.knowledgelab.capture.bean.PipelineConfiguration;
import atos.knowledgelab.capture.bean.PipelineNode;
import atos.knowledgelab.capture.mail.MailSender;



public class MonitorContextServlet implements ServletContextListener {

	private final static Logger LOGGER = Logger.getLogger(MonitorContextServlet.class.getName());
	
	public void contextDestroyed(ServletContextEvent arg0) {
		 
		
	}

	public void contextInitialized(ServletContextEvent arg0) {
		try {
			
			ConfigurationReader cr = new ConfigurationReader();
			MonitorManager manager = MonitorManager.getInstance();
			PipelineConfiguration pc = cr.getPipelineConfiguration();
			
			manager.setPipelineConfiguration(pc);
			
			//setup mailer
			String user = cr.getPipelineConfiguration().getUserName();
			String pass = cr.getPipelineConfiguration().getPassword();
			String from = cr.getPipelineConfiguration().getFromAddress();
						
			MailSender ms = new MailSender(from, user, pass);
			manager.setMailer(ms);
			
			//Instantiate monitors
			for (PipelineNode node : pc.getNodeChain()) {
				if (node.getMonitorEnabled().equalsIgnoreCase("true")) {
					Monitor m = new Monitor(node.getTopicName(), pc);
					System.out.printf("Register monitor for topic: %s %n", node.getTopicName());
					
					//register this monitor to report to the monitor manager
					m.addObserver(manager);
					m.start();
					manager.getMonitors().put(node.getTopicName(), m);
				}
			}
			
//			Monitor phemeCapture = new Monitor("pheme_capture");			
//			phemeCapture.start();
//			
//			Monitor phemeEventsEn = new Monitor("pheme_en_events");
//			phemeEventsEn.start();
//			
//			Monitor phemeEntitiesEn = new Monitor("pheme_en_entities");			
//			phemeEntitiesEn.start();
//			
//			Monitor phemeProcessedEn = new Monitor("pheme_en_processed");
//			phemeProcessedEn.start();
//			
//			Monitor phemePreprocessedEn = new Monitor("pheme_en_preprocessed");
//			phemePreprocessedEn.start();
//			
//			Monitor phemeConceptsEn = new Monitor("pheme_en_concepts");
//			phemeConceptsEn.start();
//			
//			Monitor phemeGraphDbEn = new Monitor("pheme_en_graphdb");
//			phemeGraphDbEn.start();
//			
//			//Monitor test = new Monitor("test");
//			//test.start();
//			
//			mm.getMonitors().put("pheme_capture", phemeCapture);
//			mm.getMonitors().put("pheme_en_events", phemeEventsEn);
//			mm.getMonitors().put("pheme_en_entities", phemeEntitiesEn);
//			mm.getMonitors().put("pheme_en_concepts", phemeConceptsEn);
//			mm.getMonitors().put("pheme_en_graphdb", phemeGraphDbEn);
//			mm.getMonitors().put("pheme_en_processed", phemeProcessedEn);
//			mm.getMonitors().put("pheme_en_preprocessed", phemePreprocessedEn);

			//mm.getMonitors().put("test", test);
			//---------------------------------------------------
			
			
//			Monitor medCapture = new Monitor("med_capture");			
//			medCapture.start();
//			
//			Monitor medEvents = new Monitor("med_events");
//			medEvents.start();
//			
//			Monitor medEn = new Monitor("med_en");			
//			medEn.start();
//			
//			Monitor medEntities = new Monitor("med_entities");
//			medEntities.start();
//			
//			Monitor medConcepts = new Monitor("med_concepts");
//			medConcepts.start();
//			
//			Monitor medAdvert = new Monitor("med_advert");
//			medAdvert.start();
//			
////			Monitor medStigma = new Monitor("med_stigma");
////			medStigma.start();
//			
//			Monitor medProcessed = new Monitor("med_processed");
//			medProcessed.start();
//			
//			//Monitor test = new Monitor("test");
//			//test.start();
//			
//			mm.getMonitors().put("med_capture", medCapture);
//			mm.getMonitors().put("med_events", medEvents);
//			mm.getMonitors().put("med_en", medEn);
//			mm.getMonitors().put("med_entities", medEntities);
//			mm.getMonitors().put("med_concepts", medConcepts);
//			mm.getMonitors().put("med_advert", medAdvert);
//			//mm.getMonitors().put("med_stigma", medStigma);
//			mm.getMonitors().put("med_processed", medProcessed);

			
			
			LOGGER.info("Stream statistics service started!");
		} catch (Exception e) {
			LOGGER.severe(" " + e.getMessage());
			e.printStackTrace();
		}
		
	}

}
