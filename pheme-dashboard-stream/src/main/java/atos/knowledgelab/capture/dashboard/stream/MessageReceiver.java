package atos.knowledgelab.capture.dashboard.stream;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.bean.stream.StreamItem;
import atos.knowledgelab.capture.importer.beans.DashboardDocument;
import atos.knowledgelab.capture.importer.beans.DashboardDocumentFeatures;
import atos.knowledgelab.capture.importer.beans.DashboardDocumentMetadata;
import atos.knowledgelab.capture.importer.beans.DashboardDocumentRelations;
import atos.knowledgelab.capture.importer.beans.SDQC;
import atos.knowledgelab.capture.importer.beans.SDQCKV;
import atos.knowledgelab.capture.importer.beans.Veracity;
import atos.knowledgelab.capture.importer.beans.VeracityKV;
import atos.knowledgelab.capture.stream.config.StreamProducerConfig;
import atos.knowledgelab.pheme.format.v2.PhemeSource;
import atos.knowledgelab.pheme.format.v2.PhemeSourceFeatures;
import atos.knowledgelab.pheme.format.v2.PhemeSourceRelations;
import atos.knowledgelab.pheme.format.v2.PhemeSourceUser;
import atos.knowledgelab.pheme.format.v2.PhemeVeracity;
import atos.knowledgelab.pheme.format.v2.StigmaAdvertFeatures;

public class MessageReceiver implements Observer {

	private static Logger LOGGER = Logger.getLogger(MessageReceiver.class.getName());

	//this is the first producer (for Stream)
//	StreamProducerConfig conf1 = new StreamProducerConfig();
//	StreamProducer sp1;
//	
//	//this is the second producer (for particular Search topic)
//	StreamProducerConfig conf2 = new StreamProducerConfig();
//	StreamProducer sp2;
	
	//String trumpChannelID = "f09f426e";
	//String mentahHealthDisordersChannelID = "00e6ed09";
	
	PhemeSource ps = new PhemeSource();
	//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	ObjectMapper mapper = new ObjectMapper();

	APICaller api = null;

	private String sourceDcId = "";
	private String repositoryId = "";
	
	public MessageReceiver() throws JAXBException, IOException {
		

		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("stream.properties"));
		
		this.sourceDcId = props.getProperty("source.datachannel.id");
		this.repositoryId = props.getProperty("target.repository.id");

		//formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		mapper.setSerializationInclusion(Include.NON_NULL);

		//String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJwZXJtaXNzaW9ucyI6WyJjb20ud2VibHl6YXJkLmFwaS5kb2N1bWVudC5hZGQ6cGhlbWUud2VibHl6YXJkLmNvbS9hcGkiLCJjb20ud2VibHl6YXJkLmFwaS5kb2N1bWVudC51cGRhdGU6cGhlbWUud2VibHl6YXJkLmNvbS9hcGkiLCJjb20ud2VibHl6YXJkLmFwaS5kb2N1bWVudC5kZWxldGU6cGhlbWUud2VibHl6YXJkLmNvbS9hcGkiXSwiaWF0IjoxNDU1NTY2OTA1LCJleHAiOjE0NTU2MDUzMDUsImF1ZCI6WyJjb20ud2VibHl6YXJkLmFwaSJdLCJpc3MiOiJjb20ud2VibHl6YXJkLmFwaSIsInN1YiI6ImFwaUBwaGVtZS53ZWJseXphcmQuY29tIn0=.9_iRofIO9kNCRqqUK0kgnue2EKvfz7sHKON2S1e2Lln-DK_yUDmFSVh9s7GnEaMupEIIjmHmo9KrTTDuXYCw0Q==";
		//String uri = "https://api.weblyzard.com/0.1/documents/pheme.weblyzard.com/api";
		api = new APICaller();
	}
	
	
	public void update(Observable o, Object arg) {
		//The object we receive here is of class StreamItem 
		PhemeSource tweet = (PhemeSource) arg;
		
		if (tweet.getDcID().equalsIgnoreCase(this.sourceDcId)) {

			try {
				//PhemeSource tweet = mapper.readValue(json, PhemeSource.class);
				String userScreenName = "unknown";
				
				String source = "twitter";
				String titlePrefix = "Tweet";
				String sourceUrl = "";
				if (tweet.getSourceType() != null) {
					source = tweet.getSourceType();
				}
				if (source.equalsIgnoreCase("reddit")) {
					titlePrefix = "Reddit post";
					sourceUrl = tweet.getSourceUrl();
				}
				
				
				if (tweet.getUserScreenName() != null ) {
					userScreenName = tweet.getUserScreenName();
				} else {
					PhemeSourceUser user = tweet.getUser();
					if (user != null) {
						userScreenName = user.getScreenName();
					}
				}
				
				//let's check the date format. 
				String incomingDate = tweet.getCreatedAt();
				String isoDate = "";

				
				if (tweet.getCreatedAt() != null) {
					
					isoDate = parseDateToIsoUtcString(incomingDate);
					
				}

				
				DashboardDocument dd = new DashboardDocument();
				dd.setContent(tweet.getText());
				dd.setContentType("text/plain");
				dd.setRepositoryId(this.repositoryId);
				dd.setTitle(titlePrefix + " by " + userScreenName);
				if (source.equalsIgnoreCase("reddit")) {
					dd.setUri(sourceUrl);
				} else {
					dd.setUri("https://twitter.com/" + userScreenName + "/status/" + tweet.getId());
				}
				
				//dd.setCreatedAt(tweet.getCreatedAt());
				//dd.setUserScreenName(tweet.getUserScreenName());
				//dd.setTweetID(tweet.getTweetID());
				
				//System.out.println("Text: " + tweet.getText());
				
				DashboardDocumentMetadata ddmeta = new DashboardDocumentMetadata();
				ddmeta.setTweetID(tweet.getId());
				ddmeta.setUserScreenName(tweet.getUserScreenName());
				ddmeta.setPublishedDate(isoDate);
				ddmeta.setUserID(tweet.getUserID());
				ddmeta.setTwitterLanguageID(tweet.getLang());
				ddmeta.setUserScreenName(userScreenName);

				dd.setMetadata(ddmeta);
				
				DashboardDocumentRelations ddRelations = new DashboardDocumentRelations();
				PhemeSourceRelations trelations = tweet.getRelations();
				if (trelations != null) {
					ddRelations.setParentDoc(trelations.getParentDoc());
					ddRelations.setReplyTo(trelations.getReplyTo());
					ddRelations.setRetweetedFrom(trelations.getRetweetedFrom());
				}
				dd.setRelations(ddRelations);
				
				
				DashboardDocumentFeatures ddFeatures = new DashboardDocumentFeatures();
//				PhemeSourceFeatures tFeatures = tweet.getFeatures();
//				if (tFeatures != null) {
//					ddFeatures.setAdvert(tFeatures.getAdvert());
//					ddFeatures.setAntiStigma(tFeatures.getAntiStigma());
//					ddFeatures.setSentiment(tFeatures.getSentiment());
//					ddFeatures.setCluster(tFeatures.getCluster());
//				}
				StigmaAdvertFeatures saFeatures = tweet.getSAFeatures();
				if (saFeatures != null) {
					ddFeatures.setAdvert(saFeatures.getAdvert());
					ddFeatures.setAntiStigma(saFeatures.getStigma());
					ddFeatures.setSuicideRelevant(saFeatures.getSuicideRelevant());
				}
				
				//set event cluster id
				if (tweet.getEventCluster() != null) {
					List<Long> eventCluster = new ArrayList<>();
					eventCluster.add(Long.parseLong(tweet.getEventCluster()));
					ddFeatures.setCluster(eventCluster);
				}
				//set SDQC
				if (tweet.getSdqc() != null) {
					atos.knowledgelab.pheme.format.v2.SDQC phemeSDQC = tweet.getSdqc();
					atos.knowledgelab.capture.importer.beans.SDQCKV modulSDQC = new SDQCKV();
					modulSDQC.setLabel(phemeSDQC.getLabel());
					modulSDQC.setProbability(phemeSDQC.getProbability());
					
					ddFeatures.setSdqc(modulSDQC);
				}
				
				//set veracity 
				if (tweet.getPhemeVeracity() != null) {
					PhemeVeracity inputVeracity = tweet.getPhemeVeracity();
					
					VeracityKV veracity = new VeracityKV();
					if (inputVeracity.getLabel().equalsIgnoreCase("true")) {
						veracity.setScore(1);
					} else {
						veracity.setScore(0);						
					}
					veracity.setConfidence(inputVeracity.getConfidence());
					
					
					ddFeatures.setVeracity(veracity);
				}

				
				
				
				dd.setFeatures(ddFeatures);
				
				//LOGGER.info(mapper.writeValueAsString(dd));
				
				api.sendDocument(dd);
				
				
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			//filter messages based on QueryData -> dcID

			
		}
		
		
		
	}
	
	/**
	 * 
	 * Accepts date string in one of accepted formats, and converts it into a UTC ISO String
	 * e.g.:
	 *  input: Fri Dec 09 09:58:51 +0000 2016
	 *  output: 2016-12-09T09:58:51Z
	 * 
	 * @param date
	 * @return
	 */
	String parseDateToIsoUtcString(String inputDate) {
		//our target format
		SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		targetFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		//a list of accepted date formats
		List<SimpleDateFormat> dateFormats = new ArrayList<SimpleDateFormat>();
		dateFormats.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX"));
		dateFormats.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX"));


		for (SimpleDateFormat pattern : dateFormats) {
		    try {
		    	Date d = pattern.parse(inputDate);
		    	return targetFormat.format(d);
		    			
		    } catch (ParseException e) {
		        //ignore
		    }
		}
		LOGGER.info("Date in strange format. Passing what we've got: " + inputDate);
		return inputDate;
	}

}
