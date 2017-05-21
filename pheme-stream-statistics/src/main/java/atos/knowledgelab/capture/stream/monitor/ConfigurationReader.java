package atos.knowledgelab.capture.stream.monitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ini4j.InvalidFileFormatException;
import org.ini4j.Wini;

import atos.knowledgelab.capture.bean.PipelineConfiguration;
import atos.knowledgelab.capture.bean.PipelineNode;
import atos.knowledgelab.capture.mail.MailSender;

public class ConfigurationReader {

	
	PipelineConfiguration pc = new PipelineConfiguration();
	private final static Logger LOGGER = Logger.getLogger(PipelineConfiguration.class.getName());

	
	public ConfigurationReader() {
		Wini ini;
		
		String configurationString = "Configuration";
		LOGGER.info("Reading pipeline configuration...");
		
		try {
			ini = new Wini(this.getClass().getClassLoader().getResourceAsStream("topics.properties"));
	        
			String appName = ini.get("Configuration", "app.name");
			
			String zooKeeperEndpoint = ini.get("Configuration", "kafka.zookeeper");
	        String groupId = ini.get("Configuration", "kafka.groupid");
	        String threads = ini.get("Configuration", "kafka.threads");

	        String user = ini.get("Configuration", "email.username");
	        String pass = ini.get("Configuration", "email.password");
	        String from = ini.get("Configuration", "email.from");
	        
	        System.out.printf("Config: %s %s\n", zooKeeperEndpoint, groupId);

	        Set<String> keyset = ini.keySet();
	        
	        List<PipelineNode> pipelineNodes = new ArrayList<PipelineNode>();
	        
	        for (String s : keyset) {
	        	if (s.equalsIgnoreCase(configurationString) == false) {
	        		PipelineNode pn = new PipelineNode();
	        		pn.setTopicName(ini.get(s, "topic.kafka.name"));
	        		pn.setNodeName(ini.get(s, "topic.title"));
	        		pn.setNodeDescription(ini.get(s, "topic.description"));
	        		pn.setMonitorEnabled(ini.get(s, "topic.monitor"));
	        		pn.setNotificationEmail(ini.get(s, "topic.alert"));
		        	pn.setNextNodeName(ini.get(s, "next"));
		        	
		        	String log = String.format("Name: %s, "
		        			+ "Title: %s, "
		        			+ "Description: %s, "
		        			+ "Enable monitor: %s, "
		        			+ "Next Node: %s\n", 
		        			pn.getTopicName(), 
		        			pn.getNodeName(), 
		        			pn.getNodeDescription(),
		        			pn.getMonitorEnabled(), 
		        			pn.getNextNodeName());
		        	
	        		LOGGER.info(log);
		        	pipelineNodes.add(pn);
	        	}
	        	
	        }
	        
	        pc.setNodeChain(pipelineNodes);
	        pc.setGroupId(groupId);
	        pc.setZooKeeperEndpoint(zooKeeperEndpoint);
	        try {
	        	pc.setThreadNumber(Integer.valueOf(threads));
	        } catch (Exception e) {
	        	LOGGER.warning("Can't parse number of threads: " + threads);
	        	pc.setThreadNumber(4);
	        }
	        pc.setUserName(user);
	        pc.setPassword(pass);
	        pc.setFromAddress(from);
	        
	        pc.setAppName(appName);
	        
		} catch (InvalidFileFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
	
	public PipelineConfiguration getPipelineConfiguration() {
		return pc;
	}
	
	public static void main(String[] args) {
		ConfigurationReader cr = new ConfigurationReader();
		
		String user = cr.getPipelineConfiguration().getUserName();
		String pass = cr.getPipelineConfiguration().getPassword();
		String from = cr.getPipelineConfiguration().getFromAddress();
		
		System.out.printf("%s %s\n", user, pass);
		
		String s1 = "mateusz.radzimski@atos.net; mateusz.radzimski@gmail.com";
		String s2 = "mateusz.radzimski@atos.net";
		String[] s = s2.split(";");
		System.out.println(s[0]);
		
		
		MailSender ms = new MailSender(from, user, pass);
		ms.sendMail("mateusz.radzimski@atos.net; mateusz.radzimski@gmail.com", "PHEME Pipeline monitor", "Hello world!");
		
	}

}
