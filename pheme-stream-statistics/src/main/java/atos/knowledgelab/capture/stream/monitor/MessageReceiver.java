package atos.knowledgelab.capture.stream.monitor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.counter.Tick;
import atos.knowledgelab.capture.counter.TweetCounter;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

public class MessageReceiver extends Observable implements Observer {

	private static Logger LOGGER = Logger.getLogger(MessageReceiver.class.getName());
	
	private int maxSeconds = 4000; //around 1 hour
	private int maxMinutes = 10000; //around 7 days
	private int maxHours = 1000; //	around 42 days


	
	private int miliseconds = 1000; //	around 83 days

	private ObjectMapper mapper;
	
	PhemeSource ps = new PhemeSource();
	TweetCounter tc;
	LiveMonitor liveMonitor;
	
	public MessageReceiver(TweetCounter tc, LiveMonitor liveMonitor) throws JAXBException {
		
		this.tc = tc;
		this.liveMonitor = liveMonitor;
		System.out.println("Init...");
		
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		
	}

	
	
	public synchronized void update(Observable o, Object arg) {
		//do some debug
		if (arg != null) {
			
			
			try {
				tc.setLastMessage(mapper.writeValueAsString(arg));
				
				
			} catch (JsonProcessingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
		long timestamp = System.currentTimeMillis();
		
		long timestampSeconds = timestamp / miliseconds;
		long timestampMinutes = timestamp / miliseconds / 60;
		long timestampHours = timestamp / miliseconds / 60 / 60;
				
		if (timestampSeconds > tc.getLastSecondTimestamp()) {			
			
			//save old counter and create a new one
			Tick t = new Tick(tc.getLastSecondTimestamp() * 1000, tc.getLastSecondCount());
			tc.getSeconds().add(t);
			if (tc.getSeconds().size() > maxSeconds || tc.getLastSecondTimestamp() == 0) {
				tc.getSeconds().poll();
			}
			
			tc.setLastSecondTimestamp(timestampSeconds);
			
			//null arg are clock ticks.
			if (arg != null) {
				tc.setLastSecondCount(1L);	
				tc.getMessages().add(arg);
			} else {
				tc.setLastSecondCount(0L);
				this.liveMonitor.tick(tc.getMessages());
				tc.setMessages(new ArrayList<>());
			}
		} else {
			//System.out.println("Enter old tick");
			if (arg != null) {
				tc.incrementLastSecondCount();
				tc.getMessages().add(arg);
			} 
		}
				
		if (timestampMinutes > tc.getLastMinuteTimestamp()) {

			
			//save old counter and create a new one
			Tick t = new Tick(tc.getLastMinuteTimestamp() * 1000 * 60, tc.getLastMinuteCount());
			tc.getMinutes().add(t);
			if (tc.getMinutes().size() > maxMinutes || tc.getLastMinuteTimestamp() == 0) {
				tc.getMinutes().poll();
			}
			
			tc.setLastMinuteTimestamp(timestampMinutes);
			
			//null arg are clock ticks.
			if (arg != null) {
				tc.setLastMinuteCount(1L);				
			} else {
				tc.setLastMinuteCount(0L);
			}
		} else {
			if (arg != null) {
				tc.incrementLastMinuteCount();
			} 
		}
		
		if (timestampHours > tc.getLastHourTimestamp()) {
			//check messages
			if (tc.getLastHourTimestamp() != 0) {
				if (tc.getLastHourCount() == 0) {
//					System.out.printf("Observers: %d, last minute count: %d, last timestamp: %d %n", 
//							this.countObservers(),
//							tc.getLastMinuteCount(),
//							tc.getLastMinuteTimestamp());
					this.setChanged();
					this.notifyObservers(tc.getLastMinuteTimestamp());
				}
			}
			

			//debug: print all queue sizes:
			MonitorManager mm = MonitorManager.getInstance();
//			if (mm.getMonitors().get("test2") != null) {
//				LOGGER.info("Hours  : " + mm.getMonitors().get("test2").getTweetCounter().getHours().size());
//				LOGGER.info("Minutes: " + mm.getMonitors().get("test2").getTweetCounter().getMinutes().size());
//				LOGGER.info("Seconds: " + mm.getMonitors().get("test2").getTweetCounter().getSeconds().size());
//			}
			
			//check messages, send email if no messages are received during last hour
			
			
			//save old counter and create a new one
			Tick t = new Tick(tc.getLastHourTimestamp() * 1000 * 60 * 60, tc.getLastHourCount());
			tc.getHours().add(t);
			if (tc.getHours().size() > maxHours || tc.getLastHourTimestamp() == 0) {
				tc.getHours().poll();
			}
			
			tc.setLastHourTimestamp(timestampHours);
			//null arg are clock ticks.
			if (arg != null) {
				tc.setLastHourCount(1L);		
			} else {
				tc.setLastHourCount(0L);
			}
			
		} else {
			if (arg != null) {
				 tc.incrementLastHourCount();
			}
		}
		
	}



	public LiveMonitor getLiveMonitor() {
		return liveMonitor;
	}



	public void setLiveMonitor(LiveMonitor liveMonitor) {
		this.liveMonitor = liveMonitor;
	}

}
