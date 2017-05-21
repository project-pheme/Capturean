package atos.knowledgelab.capture.stream.monitor;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import atos.knowledgelab.capture.bean.PipelineConfiguration;
import atos.knowledgelab.capture.bean.PipelineNode;
import atos.knowledgelab.capture.mail.MailSender;

public class MonitorManager implements Observer {

	private static Logger LOGGER = Logger.getLogger(MonitorManager.class.getName());


	// list of all active monitors. map: topic_name -> corresponding monitor 
	HashMap<String, Monitor> monitors = new HashMap<String, Monitor>();
	PipelineConfiguration pc;
	Timer timer = new Timer();
	
	//this is tricky: once we receive alert that some topic has no data
	//we want to wait a while and receive other alerts (if any)
	//after waiting (~30sec) we look what topics are in the alert state.
	//this flag is indicating that we receive an alert and now we are waiting
	//to have a complete list of alerts.
	boolean waitFlag = false;
	
	MailSender mailer;
	
	public MailSender getMailer() {
		return mailer;
	}

	public void setMailer(MailSender mailer) {
		this.mailer = mailer;
	}

	public HashMap<String, Monitor> getMonitors() {
		return monitors;
	}
	
	public PipelineConfiguration getPipelineConfiguration() {
		return pc;
	}
	
	public void setPipelineConfiguration(PipelineConfiguration pc) {
		this.pc = pc;
	}

	private MonitorManager() {

	}

	private static class SingletonHolder {
		private static final MonitorManager INSTANCE = new MonitorManager();
	}

	public static MonitorManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private void clearAllAlerts() {
		for (String topic : monitors.keySet()) {
			monitors.get(topic).clearAlert();
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		//LOGGER.info("Received alert notification!");

		//start waiting procedure and after that analyse all topics and notify the fist in the pipeline
		//wait around 15 seconds.
		if (waitFlag == false) {
			waitFlag = true;
			timer.schedule(new SingleTimer() {
				@Override
				public void run() {
					analysePipelineStatusAndSendEMail();
					//System.out.println("Done! unsetting wait flag");
					waitFlag = false;
				}
			}, 1*15*1000);
			

		}
	}
	
	private void analysePipelineStatusAndSendEMail() {
		PipelineNode firstBrokenNode = null;
		LOGGER.info("Analysing piepline...");
		for (PipelineNode node : pc.getNodeChain()) {
			String topicName = node.getTopicName();
			//System.out.printf("Detected: %s", topicName);
			LOGGER.info("Empty topic: " +  topicName);
			if (node.getMonitorEnabled().equalsIgnoreCase("true")) {
				if (monitors.get(topicName).getAlert() != null) {
					firstBrokenNode = node;
					break;
				}	
			}
		}
		
		try {
			if (firstBrokenNode != null) {
				LOGGER.info("Broken component: " + firstBrokenNode.getNodeName() + ", affected topic: " + firstBrokenNode.getTopicName());
				LOGGER.info("Sending mail to: " + firstBrokenNode.getNotificationEmail());
				//send email to proper component owner
				if (mailer != null) {
					mailer.sendMail(firstBrokenNode.getNotificationEmail(), "PHEME Pipeline monitor", ""
							+ "Hello! \n"
							+ "\n"
							+ "The pipeline monitor has detected that your component is not producing data for at least 1 hour.\n"
							+ "The component: \"" + firstBrokenNode.getNodeName() + "\", affected topic: " + firstBrokenNode.getTopicName()
							+ ", at: " + monitors.get(firstBrokenNode.getTopicName()).getAlert().getReceived() + "\n"
							+ "\n"
							+ "Please check if everything is fine and restart your component if needed. \n"
							+ "This message should stop arriving once the problem is solved.\n"
							+ "Sorry for incovenience!\n"
							+ "\n"
							+ "Best Regards,\n"
							+ "The Pheme Pipeline\n");
				}

				//clear alerts
				clearAllAlerts();
			} else {
				LOGGER.info("Stranger Things!");
			}

			
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Erron on notification!", e);
			LOGGER.warning("Not sure if message was delivered! ");
		}
		
	}
	
}
