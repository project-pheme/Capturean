package atos.knowledgelab.capture.dashboard.stream;

import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.config.ConsumerClientConfig;
import atos.knowledgelab.capture.stream.consumer.ConsumerClient;
import atos.knowledgelab.capture.stream.serializers.IDeserialize;
import atos.knowledgelab.capture.stream.serializers.impl.GenericDeserialize;
import atos.knowledgelab.capture.stream.serializers.impl.StreamItemDeserialize;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

public class Translate {
	MessageReceiver mr;
	ConsumerClient<PhemeSource> client;
	
	private Translate() {
		try {
			init();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static class SingletonHolder {
        private static final Translate INSTANCE = new Translate();
	}
	
	public static Translate getInstance() {
        return SingletonHolder.INSTANCE;
	}
	
	public void init() throws JAXBException, IOException {
		
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("stream.properties"));
		
		ConsumerClientConfig config = new ConsumerClientConfig();

		config.setZooKeeperEndPoint(props.getProperty("source.zookeeper.endpoint"));
		config.setGroupId(props.getProperty("source.group.id"));
		config.setTopicName(props.getProperty("source.topic"));
		config.setThreadNum(Integer.decode(props.getProperty("source.threads.number")));
				
		IDeserialize<PhemeSource> deser = new GenericDeserialize<PhemeSource>(PhemeSource.class);
		client = new ConsumerClient<PhemeSource>(config, deser);
		
		//instantiate the message receiver class (Observer)
		mr = new MessageReceiver();
	}
	
	public void start() {
		
			
		
		//register observer to receive all messages from kafka
		client.subscribeObserver(mr);
		
		//connect clients to the kafka broker and start receiving messages
		client.start();
		
		//wait for 10 seconds while we receive messages
				
			

	}
	
	public void stop() {
		//disconnect from broker and remove the client(s)
		client.stop();
		
		//unsubscribe from observer
		client.unsubscribeObserver(mr);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//Configuration of the client
		
		Translate.getInstance().start();
		
		
	}

}
