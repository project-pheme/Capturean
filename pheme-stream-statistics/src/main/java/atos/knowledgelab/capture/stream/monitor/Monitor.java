package atos.knowledgelab.capture.stream.monitor;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import atos.knowledgelab.capture.bean.NotificationAlert;
import atos.knowledgelab.capture.bean.PipelineConfiguration;
import atos.knowledgelab.capture.counter.TweetCounter;
import atos.knowledgelab.capture.stream.config.ConsumerClientConfig;
import atos.knowledgelab.capture.stream.consumer.ConsumerClient;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;
import atos.knowledgelab.capture.stream.serializers.impl.GenericDeserialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemDeserialize;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

public class Monitor extends Observable implements Observer {
	MessageReceiver mr;
	ConsumerClient client;
	TweetCounter tc;
	LiveMonitor liveMonitor;
	String topicName;
	PipelineConfiguration pc;
	private static Logger LOGGER = Logger.getLogger(Monitor.class.getName());

	NotificationAlert alert = null;
	
	public Monitor(String topic, PipelineConfiguration pc) {
		try {
			this.topicName = topic;
			this.pc = pc;
			init();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	

	public NotificationAlert getAlert() {
		return alert;
	}
	public void clearAlert() {
		this.alert = null;
	}

	public void init() throws JAXBException {
		
		LOGGER.info("Setting po monitor for " + this.topicName);
		
		ConsumerClientConfig config = new ConsumerClientConfig();
		config.setZooKeeperEndPoint(pc.getZooKeeperEndpoint());
		config.setGroupId(pc.getGroupId());
		config.setThreadNum(pc.getThreadNumber());
		config.setTopicName(topicName);

		IDeserialize<PhemeSource> deser = new GenericDeserialize<PhemeSource>(PhemeSource.class);

		client = new ConsumerClient(config, deser);

		tc = new TweetCounter();
		liveMonitor = new LiveMonitor();
		
		// instantiate the message receiver class (Observer)
		mr = new MessageReceiver(tc, liveMonitor);

		Timer timer = new Timer();

		//Setup a clock "tick" every 1 second.
		//this is to ensure measurment every 1 second, even if there is no data.
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				
				mr.update(null, null);
				
			}
		}, 5000, 1000);

	}

	public void start() {

		// register observer to receive all messages from kafka
		client.subscribeObserver(mr);
		
		mr.addObserver(this);
		
		// connect clients to the kafka broker and start receiving messages
		client.start();

		// wait for 10 seconds while we receive messages

	}

	public void stop() {
		// disconnect from broker and remove the client(s)
		client.stop();

		// unsubscribe from observer
		client.unsubscribeObserver(mr);
	}

	public TweetCounter getTweetCounter() {
		return this.tc;
	}

	public LiveMonitor getLiveMonitor() {
		return liveMonitor;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Configuration of the client

		//Monitor.getInstance().start();

	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		//System.out.printf("Notification on topic %s, no messages seen since: %s %n", this.topicName, arg.toString() );
		NotificationAlert alert = new NotificationAlert();
		
		Date date = new Date();
		alert.setMessage("No messages seen in last 1 hour on topic " + this.topicName + date);
		alert.setReceived(date);
		
		this.alert = alert;
		
		this.setChanged();
		this.notifyObservers(tc.getLastMinuteTimestamp());
		
	}




}
