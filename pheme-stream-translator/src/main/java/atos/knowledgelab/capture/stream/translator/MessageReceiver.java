package atos.knowledgelab.capture.stream.translator;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.apache.kafka.clients.producer.ProducerConfig;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

public class MessageReceiver implements Observer {

	private static Logger LOGGER = Logger.getLogger(MessageReceiver.class.getName());

	//this is the first producer
	StreamProducerConfig conf1 = new StreamProducerConfig();
	StreamProducer sp1;
	
	PhemeSource ps = new PhemeSource();
	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
	
	LinkedBlockingQueue<Map<String, Integer>> rollingIndex = new LinkedBlockingQueue<>();
	Map<String, Integer> currentDcCount = new HashMap<>();
	Map<String, Integer> allDcCount = new HashMap<>();

	//for comparison, keep track of original message stream count
	LinkedBlockingQueue<Map<String, Integer>> rollingIndexUnbalanced = new LinkedBlockingQueue<>();
	Map<String, Integer> currentDcCountUnbalanced = new HashMap<>();
	Map<String, Integer> allDcCountUnbalanced = new HashMap<>();
	
	Timer updateRollingIndex = new Timer();
	
	/*
	 * Important parameters
	 * 
	 * rollingIndexSize 	this is how many 1-second buckets we want to track 
	 * 						When calculating total message count we summ all buckets here.
	 * 						By default we use 60 buckets, so we look into last 60 seconds of traffic.
	 * loadBalanceThreshold this parameter establishes the limit after which we start to load balance messages
	 * 						This affects "local probability", that is a probability when load balancing
	 * 						between datachannels. "Global probability" is defined by a sigmoid function
	 * 						and its parameters. 
	 * loadBalanceMinThreshold this parameter establish the lower limit, below which we prefer the messages
	 * 						to pass always, without balancing. This threshold works on the DC level, so it will
	 * 						allow a DC with number of messages lower than X to pass. 
	 * steepnessFactor		this is the "k" parameter of the sigmoid function
	 * xValue				this is the x_0 value of the sigmoid function
	 * 
	 * Total probability for passing a message of a particular DC is calculated as follows:
	 * 	P(DC_a) = Local_probability(DC_a) * Global_probability(all DCs)
	 * 
	 * When calculating local probability we use a formula:
	 * 						,
	 * 						| 1, in range (0, loadBalanceThreshold)
	 * Local_prob (DC_a) = 	|
	 * 						| log(DC_a) / sum ( log(DC) ), in range (loadBalanceThreshold, infinity)
	 * 						`
	 * 
	 * Total probability is defined as a function of all messages (from our time window).
	 * We use sigmoid function, that is aligned with loadBalanceThreshold parameter. 
	 * For low total amount of messages, the P ~= 1, and is decreasing sharply when reaching
	 * the message amount threshold. The value is P ~= 0 when close to the total limit.
	 * 
	 * 	
	 * Total_Prob (DC) =  (1.0 / (1.0 + Math.exp(steepnessFactor * (totalCount - xValue))));
	 * 
	 * 
	 * In the example below we have configured 30 buckets, we estimate that the pipeline
	 * can process max ~4 messages/s, so the max threshold is set to 120.
	 * The loadBalanceThreshold to 80, and the global probability function is as below
	 * with xVal = 100 and steepness factor of 1/4. 
	 * 
	 * 
	 * 
  1.2 ++----------------+-----------------+------------------+-----------------+------------------+----------------++
      |                 +                 +                (1.0 - 1.0 / (1.0 + exp(-(1.0/4.0) * (x - 100)))) ****** +
      |                                                                                 ^             ^             |
      |                                                                             steepness      x value          |
      |                                                                                                             |
    1 ****************************************************************************                                 ++
      |                                                                           *****                             |
      |                                                                                **                           |
      |                                                                                  ***                        |
      |                                                                                     *                       |
  0.8 ++                                                                                     *                     ++
      |                                                                                      *                      |
      |                                                                                       *                     |
      |                                                                                        *                    |
      |                                                                                        *                    |
  0.6 ++                                                                                        *                  ++
      |                                                                                          *                  |
      |                                                                                          *                  |
      |                                                                                           *                 |
      |                                                                                           *                 |
      |                                                                                            *                |
  0.4 ++                                                                                           *               ++
      |                                                                                             *               |
      |                                                                                             *               |
      |                                                                                              *              |
      |                                                                                               *             |
  0.2 ++                                                                                               *           ++
      |                                                                                                 *           |
      |                                                                                                  *          |
      |                                                                                                   **        |
      |                 +                 +                  +                 +                  +         *****   +
    0 ++----------------+-----------------+------------------+-----------------+------------------+--------------****
                        20                40                 60                80                100               120

	 * 
	 * 
	 */
	boolean loadbalancingEnabled = false;
	double rollingIndexSize = 60;
	double loadBalanceThreshold = 80; //we permit max of 2 messages per second before doing load balancing
	double loadBalanceMinThreshold = 10;
	double steepnessFactor = 0.125;
	double xValue = 200;
	
	Random random = new Random();
	
	long count = 0;
	long statsPrintEvery = 120; //in seconds 
	
	long msgCount = 0;
	
	public MessageReceiver() throws JAXBException, IOException {
		
		//Load configuration from *.properties file
		//note that source.* properties refer to the consumer
		//and target.* properties refer to the producer
		LOGGER.info("Loading configuration.");
		
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("translator.properties"));
		
		//conf1 = new StreamProducerConfig(props);
		conf1.put("metadata.broker.list", props.getProperty("target.metadata.broker.list"));
		conf1.put("serializer.class", props.getProperty("target.serializer.class"));
		conf1.put("request.required.acks", props.getProperty("target.request.required.acks"));
		conf1.put("kafka.topic", props.getProperty("target.kafka.topic"));
		
		//read load balancing parameters 
		try {
			//default vals => OFF
			String loadbalancingEnabled = props.getProperty("loadbalancing", "0");
			String rollingIndexSize = props.getProperty("loadbalancing.rolling.index.size", "0");
			String loadBalanceThreshold = props.getProperty("loadbalancing.local.threshold", "0");
			String loadBalanceMinThreshold = props.getProperty("loadbalancing.local.threshold.min", "0");
			String steepnessFactor = props.getProperty("loadbalancing.global.steepness", "0");
			String xValue = props.getProperty("loadbalancing.global.xvalue", "0");
		
			
			
			if (loadbalancingEnabled.equalsIgnoreCase("1")) {
				this.loadbalancingEnabled = true;
				
				StringBuilder sb = new StringBuilder();
				
				sb.append("Load balancing ENABLED with the following params:\n");
				
				this.rollingIndexSize = Double.parseDouble(rollingIndexSize);
				this.loadBalanceThreshold = Double.parseDouble(loadBalanceThreshold);
				this.loadBalanceMinThreshold = Double.parseDouble(loadBalanceMinThreshold);
				this.steepnessFactor = Double.parseDouble(steepnessFactor);
				this.xValue = Double.parseDouble(xValue);
				
				sb.append("Rolling index size: " + this.rollingIndexSize + "\n");
				sb.append("Load balance threshold: " + this.loadBalanceThreshold + "\n");
				sb.append("Load balance min (local) threshold: " + this.loadBalanceMinThreshold + "\n");
				sb.append("Steepness factor: " + this.steepnessFactor + "\n");
				sb.append("X value: " + this.xValue + "\n");
				
				LOGGER.info(sb.toString());
			} else {
				LOGGER.info("Load balancing is DISABLED.");
			}
			
			
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Failed to parse configuration for load balancing.", e);
		}
			
		
		//THIS IS A BUGFIX IN KAFKA BROKER v0.10.0.0, see:
		//https://github.com/dpkp/kafka-python/issues/718
		//https://issues.apache.org/jira/browse/KAFKA-3789
		//https://github.com/apache/kafka/pull/1467
		
		conf1.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
				
		//Here it is important to specify the broker (kafka node or nodes)
		//more details here: http://kafka.apache.org/08/configuration.html
//		conf1.put("metadata.broker.list", "gatezkt1:9092");
//		//conf1.put("zookeeper.connect", "gatezkt1:2181");
//		conf1.put("serializer.class", "kafka.serializer.StringEncoder");
//		conf1.put("request.required.acks", "1");
//		conf1.put("kafka.topic", "pheme_capture");	

//		conf1.put("metadata.broker.list", "gatezkt1:9092");
//		//conf1.put("zookeeper.connect", "gatestorm2:2181");
//		conf1.put("serializer.class", "kafka.serializer.StringEncoder");
//		conf1.put("request.required.acks", "1");
//		conf1.put("kafka.topic", "med_capture");

		sp1 = new StreamProducer(conf1);
		
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		//schedule timer in 5 seconds
		updateRollingIndex.scheduleAtFixedRate(new TimerTask() {
			@Override
			public synchronized void run() {
				update(null, null);

				
			}
			
		}, 5000, 1000);
		
	}

	
	public static double calculateLocalProbability(Integer localCount, Map<String, Integer> totalCount, double localThreshold, double minThreshold) {
		//internal param: bottom threshold for applying local prob. 
		//double minThreshold = 10;
		
		double logSum = 0;
		double sum = 0;
		double result = 0;
		
		if (localCount == null) {
			localCount = 0;
		}
		
		for (String key : totalCount.keySet()) {
			logSum += Math.log(totalCount.get(key));
			sum += totalCount.get(key);
		}
		
		if (sum < localThreshold || localCount == 0) {
			result = 1;
		} else {
			if (localCount < minThreshold && totalCount.size() * minThreshold < localThreshold) {
				//System.out.printf(">> loc count: %d  totalCount.size: %d \n ", localCount, totalCount.size());
				result = 1;
			} else {
				//System.out.println("  => sum: " + sum + " log: " + logSum + " local: " + localCount + " log loc: " + Math.log(localCount));
				
				result = Math.min(Math.log(minThreshold + localCount) / logSum, 0.999999);
				//System.out.println("  => result: " + result);
				//System.out.printf(">> loc count: %d  totalCount.size: %d  result: %f\n ", localCount, totalCount.size(), result);
			}
		}
		return result;
		
	}
	
	public static double calculateGlobalProbability(long totalCount, double steepness, double xValue) {
		double result =  (1.0 / (1.0 + Math.exp(steepness * (totalCount - xValue))));
		
		return result;
	}
	
	public synchronized void update(Observable o, Object arg) {
		//
		
		
		//timer produces events for updating rolling indices
		if (arg == null) {
			try {
				if (rollingIndex.size() < rollingIndexSize) {
					
					rollingIndex.put(currentDcCount);					
					currentDcCount = new HashMap<>();	
					
					//unbalanced stats
					rollingIndexUnbalanced.put(currentDcCountUnbalanced);
					currentDcCountUnbalanced = new HashMap<>();
					
				} else {
					
					
					Map<String, Integer> oldDcCount = rollingIndex.take();

					for (String key : oldDcCount.keySet()) {
						Integer oldCount = oldDcCount.get(key);
						Integer allCount = allDcCount.get(key);
						
						//do some housekeeping
						if (allCount - oldCount == 0) {
							allDcCount.remove(key);	
						} else {
							allDcCount.put(key, allCount - oldCount);
						}	
					}
					currentDcCount = new HashMap<>();
					rollingIndex.put(currentDcCount);
					
					//unbalanced stats
					Map<String, Integer> oldDcCountUnbalanced = rollingIndexUnbalanced.take();

					for (String key : oldDcCountUnbalanced.keySet()) {
						Integer oldCountUnbalanced = oldDcCountUnbalanced.get(key);
						Integer allCountUnbalanced = allDcCountUnbalanced.get(key);
						
						//do some housekeeping
						if (allCountUnbalanced - oldCountUnbalanced == 0) {
							allDcCountUnbalanced.remove(key);	
						} else {
							allDcCountUnbalanced.put(key, allCountUnbalanced - oldCountUnbalanced);
						}	
					}
					currentDcCountUnbalanced = new HashMap<>();
					rollingIndexUnbalanced.put(currentDcCountUnbalanced);
					

				}
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

			//print some stats every 2 min
			if (this.count % statsPrintEvery == 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Printing DC stats for the last " + rollingIndex.size() + " seconds:\n");
				sb.append("DC stats:\n");
				for (String key: allDcCountUnbalanced.keySet()) {
					sb.append(" -> " + key + " : " + allDcCountUnbalanced.get(key) + "\n");
				}
				if (loadbalancingEnabled == true) {
					sb.append("After balancing:\n");
					for (String key: allDcCount.keySet()) {
						sb.append(" -> " + key + " : " + allDcCount.get(key) + "\n");
					}
				}
				
				sb.append("Done!");
				LOGGER.info(sb.toString());

			}
//			
			this.count = (this.count % statsPrintEvery) + 1;

			
		} else {
			
			
			//The object we receive here is of class StreamItem 
			StreamItem si = (StreamItem) arg;
			
			String dcId = si.getDataChannelId();
			
			long sum = 0;
			for (String key : allDcCount.keySet()) {
				sum += allDcCount.get(key);
			}
			
			//do the load balancing
			boolean skipThisMessage = false;
			
			double l = MessageReceiver.calculateLocalProbability(allDcCount.get(dcId), allDcCount, this.loadBalanceThreshold, this.loadBalanceMinThreshold);
			double g = MessageReceiver.calculateGlobalProbability(sum, this.steepnessFactor, this.xValue);
			
			double finalProbability = 0;
			
			//we throttle traffic based on both probabilities.
			//with one exception: if there is already a lot of messages, but we have a message with "local probability" == 1
			//we want this message to pass. Any other case falls to the general formula of P = l * g.
			//This way we avoid cases when some DC are saturating the whole pipeline all the time
			//and some silent DCs can never get past the low global probability.
			//Note that this way we can sometimes have more than the total maximum number of messages, but should never
			//exceed ~ (xValue + localThreshold) value.
			
			if (l == 1.0) {
				finalProbability = 1;
			} else {
				finalProbability = l * g;
			}
			
			double r = random.nextInt(1000);
			
			msgCount++;
//			if (msgCount % 100 == 0) {
//				msgCount = 0;
//				
//				StringBuilder sb = new StringBuilder();
//				sb.append("Printing DC stats for the last " + rollingIndex.size() + " seconds:\n");
//				sb.append("DC stats:\n");
//				for (String key: allDcCountUnbalanced.keySet()) {
//					sb.append(" -> " + key + " : " + allDcCountUnbalanced.get(key) + "\n");
//				}
//				if (loadbalancingEnabled == true) {
//					sb.append("After balancing:\n");
//					for (String key: allDcCount.keySet()) {
//						sb.append(" -> " + key + " : " + allDcCount.get(key) + "\n");
//					}
//				}
//				
//				sb.append("Done!");
//				LOGGER.info(sb.toString());
//				
//				String dc = si.getDataChannelId();
//				System.out.printf("Local: %f  Global: %f  Dc: %s \n", l, g, dc);
//				System.out.printf("		%f %s \n", finalProbability, (r < (finalProbability * 1000)));
//				
//				
//			}
			
			if (r < (finalProbability * 1000)) {
				//pass
				
				
			} else {
				//do not pass
				skipThisMessage = true;
			}
			
			//System.out.println("Final probability for DC " + si.getDataChannelId() + " : " + finalProbability + " Result: " + skipThisMessage);
			
			//keep track of the "original" (unbalanced) message stream for comparison purposes
			//do the rolling index duties
			Integer currCountUnbalanced = currentDcCountUnbalanced.get(si.getDataChannelId());
			if (currCountUnbalanced == null) {
				currentDcCountUnbalanced.put(si.getDataChannelId(), 1);
			} else {
				currentDcCountUnbalanced.put(si.getDataChannelId(), currCountUnbalanced + 1);
			}
			
			Integer allCountUnbalanced = allDcCountUnbalanced.get(si.getDataChannelId());
			if (allCountUnbalanced == null) {
				allDcCountUnbalanced.put(si.getDataChannelId(), 1);
			} else {
				allDcCountUnbalanced.put(si.getDataChannelId(), allCountUnbalanced + 1);
			}
			//end rolling index duties
			
			
			
			
			if (skipThisMessage == true && loadbalancingEnabled == true) {
				//skipping message
				//System.out.println("### Skipping message of dcid: " + si.getDataChannelId());
				
				
			} else {

				//do the rolling index duties
				Integer currCount = currentDcCount.get(si.getDataChannelId());
				if (currCount == null) {
					currentDcCount.put(si.getDataChannelId(), 1);
				} else {
					currentDcCount.put(si.getDataChannelId(), currCount + 1);
				}
				
				Integer allCount = allDcCount.get(si.getDataChannelId());
				if (allCount == null) {
					allDcCount.put(si.getDataChannelId(), 1);
				} else {
					allDcCount.put(si.getDataChannelId(), allCount + 1);
				}
				//end rolling index duties
				
				
				ps.setText(si.getTweet().getText());
				ps.setSourceType("twitter");
					
				try {
						Date d = si.getTweet().getCreatedAt();
						ps.setCreatedAt(formatter.format(d));
				} catch (ParseException e) {
					
					LOGGER.log(Level.WARNING, "JSON Parsing error (createdAt field)", e);
				}
					
				ps.setUserID(si.getTweet().getUserID());
				ps.setUserScreenName(si.getTweet().getUserScreenName());
				ps.setRawJson(si.getTweet().getRawJson());

					
				ObjectMapper mapper = new ObjectMapper();
				try {
						JsonNode rawJsonNode = mapper.readTree(si.getTweet().getRawJson());
						String lang = rawJsonNode.get("lang").asText();
						ps.setLang(lang);
						
				} catch (JsonProcessingException e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (language field)", e);
				} catch (IOException e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (language field)", e);
				} catch (Exception e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (language field)", e);
				}
					
				try {
						JsonNode rawJsonNode = mapper.readTree(si.getTweet().getRawJson());
						String id_str = rawJsonNode.get("id_str").asText();
						ps.setId(id_str);
						
				} catch (JsonProcessingException e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (id_str field)", e);
				} catch (IOException e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (id_str field)", e);
				} catch (Exception e) {
						LOGGER.log(Level.WARNING, "JSON Parsing error (id_str field)", e);
				}

//				try {
//					JsonNode rawJsonNode = mapper.readTree(si.getTweet().getRawJson());
//					String createdAt = rawJsonNode.get("created_at").asText();
//					ps.setCreatedAt(createdAt);
//					
//				} catch (JsonProcessingException e) {
//						LOGGER.log(Level.WARNING, "JSON Parsing error (createdAt field)", e);
//				} catch (IOException e) {
//						LOGGER.log(Level.WARNING, "JSON Parsing error (createdAt field)", e);
//				} catch (Exception e) {
//						LOGGER.log(Level.WARNING, "JSON Parsing error (createdAt field)", e);
//				}
				
				ps.setDcID(si.getQueryData().getDcID());
					
				atos.knowledgelab.pheme.format.v2.StreamItem newsi = new atos.knowledgelab.pheme.format.v2.StreamItem();
				newsi.setPhemeSource(ps);
				sp1.send(newsi);

				//LOGGER.warning("Sent this: " + newsi.getPhemeSource().getText());
				//LOGGER.warning()

				
			}
			

			
			
		}
		
		

		
	}
	
	public static void main(String args[]) {
		int t = 80;
		
		int loc = 20;
		
		for (int i = 0; i < 200; i++) {
			Map<String, Integer> map = new HashMap<>();
			map.put("sdss", loc);
			map.put("other", i + 20 - loc);
			
			int sum = 0;
			for (String s : map.keySet()) {
				sum += map.get(s);
			}
			//System.out.println("Global prob. for i = " + i + " : " + MessageReceiver.calculateGlobalProbability(i));
			
			//ñSystem.out.println("Loc prob. for i = " + (loc) + " : " + MessageReceiver.calculateLocalProbability(loc, map, t));
			for (String s : map.keySet()) {
				double l = MessageReceiver.calculateLocalProbability(map.get(s), map, t, 10);
				double g = MessageReceiver.calculateGlobalProbability(sum, 1.0/4.0, 100);
				System.out.println("Count: " + map.get(s) + " Loc: " + l + " glob: " + g + " | Total: " + (l * g));
				
				//System.out.println(" -> " + map.get(s) + "  log: " + Math.log(map.get(s)) + "  prob: " + l);
				
			}
			
			System.out.println();
		}

	}

}
