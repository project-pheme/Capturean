Capture Consumer Client
=======================

Purpose
-------

The purpose of this module is to provide a simple API
for fast and easy implementation of kafka data stream consumers.


Architecture
------------

TODO


Quick Start
-----------

Here are instructions for quickly setup a running kafka client:

1) Download the source code if you haven't already

```bash
git clone git@gitlab.atosresearch.eu:ari/capture.git
```

2) compile the "capture-consumer-client" module

```bash
cd capture/capture-consumer-client
mvn clean install
```

3) Setup kafka message broker (if you haven't already)
```

```


4) in the pom of your project put the following dependency:
```xml
<dependency>
	<groupId>atos.knowledgelab.capture</groupId>
	<artifactId>capture-consumer-client</artifactId>
	<version>0.0.1-SNAPSHOT</version>
</dependency>
```

5) create class "MessageReceiver" 

```java
import java.util.Observable;
import java.util.Observer;

import atos.knowledgelab.capture.api.stream.StreamItem;

public class MessageReceiver implements Observer {

	public void update(Observable o, Object arg) {
		//The object we receive here is of class StreamItem 
		StreamItem si = (StreamItem) arg;

		//print the text of the tweet
		System.out.println(si.getTweet().getText());
		
		
	}

}
```

6) create class "ExampleApp"

```java
import atos.knowledgelab.capture.client.manager.ConsumerClient;
import atos.knowledgelab.capture.client.manager.ConsumerClientConfig;

public class ExampleApp {

	public static void main(String[] args) throws InterruptedException {
		
		//Configuration of the client
		ConsumerClientConfig config = new ConsumerClientConfig();
		config.setZooKeeperEndPoint("localhost:2181");
		config.setGroupId("myGroup");
		config.setTopicName("test");
		
		ConsumerClient client = new ConsumerClient(config);
		
		//instantiate the message receiver class (Observer)
		MessageReceiver mr = new MessageReceiver();
		
		//register observer to receive all messages from kafka
		client.subscribeObserver(mr);
		
		//connect clients to the kafka broker and start receiving messages
		client.start();
		
		//wait for 10 seconds while we receive messages
		Thread.sleep(10000);
		
		//disconnect from broker and remove the client(s)
		client.stop();
		
		//unsubscribe from observer
		client.unsubscribeObserver(mr);

	}

}

```

7) Now you can run the ExampleApp class and you should receive messages from the "test" topic. 





