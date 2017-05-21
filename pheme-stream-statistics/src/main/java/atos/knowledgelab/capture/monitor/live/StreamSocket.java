package atos.knowledgelab.capture.monitor.live;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;


import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import atos.knowledgelab.capture.monitor.live.beans.Aggregate;
import atos.knowledgelab.capture.monitor.live.beans.Aggregates;
import atos.knowledgelab.capture.monitor.live.beans.Event;
import atos.knowledgelab.capture.monitor.live.beans.Sample;
import atos.knowledgelab.capture.monitor.live.beans.Samples;
import atos.knowledgelab.capture.stream.monitor.Monitor;
import atos.knowledgelab.capture.stream.monitor.MonitorManager;
import atos.knowledgelab.pheme.format.v2.PhemeSource;

@WebSocket(maxTextMessageSize = 64 * 1024)
public class StreamSocket extends WebSocketServlet implements Observer
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5422322764399874772L;
	private Session outbound;
	private int socketid;
	private String topic;
	private String filter; 
	private Monitor monitor;
	private ObjectMapper om = new ObjectMapper();
	
	String message0 = "0{\"sid\":\"tCgbbIoaNEoL9nAPAAAF\",\"upgrades\":[],\"pingInterval\":25000,\"pingTimeout\":60000}";
	String message0a = "40";
	String message0b = "42[\"stats\",{}]";
	String message0c = "42[\"filter\",\"\"]";
	String message0d = "42[\"topic\",\"pheme_en\"]";
	
	String message1 = "42[\"samples\",[{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"Android\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"$direct\",\"$device\":\"iPad\",\"$current_url\":\"https://www.alooma.com\",\"$browser_version\":null,\"$screen_height\":1080,\"$screen_width\":1920,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"44ede12b-5182-2f0a-af84-2a214e711b90\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.850Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"776f11be-8c4b-fa5d-cf5a-cce835b89bda\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.850Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_click\",\"properties\":{\"$os\":\"iOS\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"www.google.com\",\"$device\":\"iPad\",\"$current_url\":\"https://www.alooma.com\",\"$browser_version\":null,\"$screen_height\":900,\"$screen_width\":1920,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"431ab206-30fb-5dbe-44a6-27095c8d374f\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.863Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"f7bd5908-391b-390e-903a-cbe216d8c8b1\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.863Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.869Z\",\"salesforce_object\":\"Lead\",\"event_type\":\"Lead\",\"salesforce_pull_time\":\"2017-05-17T15:23:20.869Z\",\"schema_url\":\"schema?id=abb01c25-e35b-4e40-b9ef-4c8b0e086eb2&schema_object=Lead\",\"deleted\":false,\"updated_at\":\"2017-05-17T15:23:20.869Z\",\"input_type\":\"salesforce\",\"input_label\":\"Salesforces\",\"salesforce_id\":\"00Q240l6t3u9EMFAAC\"}},\"input_label\":\"Salesforce\",\"@timestamp\":\"2017-05-17T15:23:20.869Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"id\":9142114,\"account_id\":28170020,\"total\":932,\"last_update\":\"2017-05-17T15:23:20.873Z\",\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.873Z\",\"log_file\":\"dbslave01-binary-log.000001\",\"log_position\":795604036,\"table\":\"counts\",\"schema\":\"alooma\",\"deleted\":false,\"type\":\"update-delete\",\"event_type\":\"counts\",\"input_type\":\"mysql\",\"@uuid\":\"89494698-0ddf-2e2e-454c-62ac2a7e2741\",\"@version\":1,\"input_label\":\"MySQL\",\"@parent_uuid\":null}},\"input_label\":\"MySQL\",\"@timestamp\":\"2017-05-17T15:23:20.873Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"Android\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"$direct\",\"$device\":\"iPad\",\"$current_url\":\"https://www.alooma.com\",\"$browser_version\":null,\"$screen_height\":800,\"$screen_width\":1920,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"50d48f75-e4de-721d-87cd-6f7e569becd1\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.873Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"ca56c69d-04e7-23e8-f446-0288f1a40bd8\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.873Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"Android\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"www.aloom.com\",\"$device\":\"iPhone\",\"$current_url\":\"https://www.alooma.com/blog\",\"$browser_version\":null,\"$screen_height\":900,\"$screen_width\":1280,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"75ec516b-a76c-7a9f-bded-d3fdb2d1f34c\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.887Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"75a7291d-bfcb-0461-c53b-1993acb18d32\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.887Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_click\",\"properties\":{\"$os\":\"iOS\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"www.quora.com\",\"$device\":\"iPhone\",\"$current_url\":\"https://www.alooma.com/integrations\",\"$browser_version\":null,\"$screen_height\":1080,\"$screen_width\":1280,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"52989c4a-d511-9e04-36ec-6f7b69eb6e20\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.906Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"b7a84d5c-0711-7a03-121b-7b40784ccb29\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.906Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"iOS\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"$direct\",\"$device\":\"Windows Phone\",\"$current_url\":\"https://www.alooma.com/docs\",\"$browser_version\":null,\"$screen_height\":1440,\"$screen_width\":1280,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"b893e179-314f-d6cd-4c6c-24d0b81f6702\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.907Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"c918f6d0-6ce1-ed12-f02d-be26d95d5c41\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.907Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"iOS\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"m.facebook.com\",\"$device\":\"Windows Phone\",\"$current_url\":\"https://www.alooma.com\",\"$browser_version\":null,\"$screen_height\":1080,\"$screen_width\":1280,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"84cfe5e9-e150-cf6a-3bfc-d8517e6a887a\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.908Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"af7931e6-e313-5d1c-4aec-f162b905c132\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.908Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"id\":9608874,\"account_id\":12605843,\"total\":767,\"last_update\":\"2017-05-17T15:23:20.911Z\",\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.911Z\",\"log_file\":\"dbslave01-binary-log.000001\",\"log_position\":795604036,\"table\":\"counts\",\"schema\":\"alooma\",\"deleted\":false,\"type\":\"update-delete\",\"event_type\":\"counts\",\"input_type\":\"mysql\",\"@uuid\":\"57389d77-c368-0e4f-8b6b-8488166732fb\",\"@version\":1,\"input_label\":\"MySQL\",\"@parent_uuid\":null}},\"input_label\":\"MySQL\",\"@timestamp\":\"2017-05-17T15:23:20.911Z\"}},{\"filter\":\"\",\"event\":{\"message\":{\"event\":\"mp_page_view\",\"properties\":{\"$os\":\"iOS\",\"$browser\":\"Facebook Mobile\",\"$referrer\":\"http://m.facebook.com\",\"$referring_domain\":\"www.quora.com\",\"$device\":\"Windows Phone\",\"$current_url\":\"https://www.alooma.com/blog\",\"$browser_version\":null,\"$screen_height\":900,\"$screen_width\":2560,\"mp_lib\":\"web\",\"$lib_version\":\"2.7.5\",\"distinct_id\":\"0c1185c8-2c25-2252-cb61-005215c7717e\"},\"_metadata\":{\"@timestamp\":\"2017-05-17T15:23:20.914Z\",\"event_type\":\"Mobile_SDK\",\"@uuid\":\"f8d01674-f354-8516-cb2e-e3896e8fcca0\",\"@version\":1,\"input_label\":\"Mobile_SDK\",\"@parent_uuid\":null}},\"input_label\":\"Mobile_SDK\",\"@timestamp\":\"2017-05-17T15:23:20.914Z\"}}]]";
	String message2 = "42[\"aggregated\",{\"all\":{\"Mobile_SDK\":64,\"MySQL\":45,\"Salesforce\":3},\"filter\":{\"Mobile_SDK\":64,\"MySQL\":45,\"Salesforce\":3}}]";
	String message3 = "42[\"stats\",{\"all\":[{\"Mobile_SDK\":66,\"MySQL\":51,\"Salesforce\":14},{\"Mobile_SDK\":69,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":68,\"MySQL\":41,\"Salesforce\":14},{\"Mobile_SDK\":71,\"MySQL\":44,\"Salesforce\":3},{\"MySQL\":36,\"Mobile_SDK\":64,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":45,\"Salesforce\":5},{\"Mobile_SDK\":56,\"MySQL\":32,\"Salesforce\":3},{\"Mobile_SDK\":54,\"MySQL\":28,\"Salesforce\":12},{\"Mobile_SDK\":71,\"MySQL\":29,\"Salesforce\":7},{\"Mobile_SDK\":67,\"MySQL\":41,\"Salesforce\":8},{\"Salesforce\":9,\"Mobile_SDK\":69,\"MySQL\":32},{\"MySQL\":32,\"Mobile_SDK\":71,\"Salesforce\":14},{\"Mobile_SDK\":65,\"Salesforce\":14,\"MySQL\":34},{\"Mobile_SDK\":61,\"MySQL\":38,\"Salesforce\":2},{\"Mobile_SDK\":66,\"MySQL\":33,\"Salesforce\":7},{\"MySQL\":36,\"Mobile_SDK\":70,\"Salesforce\":3},{\"MySQL\":39,\"Mobile_SDK\":71,\"Salesforce\":4},{\"MySQL\":39,\"Mobile_SDK\":59,\"Salesforce\":14},{\"Mobile_SDK\":63,\"MySQL\":25,\"Salesforce\":7},{\"Mobile_SDK\":62,\"Salesforce\":8,\"MySQL\":36},{\"Mobile_SDK\":63,\"Salesforce\":13,\"MySQL\":28},{\"Mobile_SDK\":68,\"MySQL\":38,\"Salesforce\":11},{\"Salesforce\":5,\"Mobile_SDK\":63,\"MySQL\":38},{\"Mobile_SDK\":63,\"MySQL\":25,\"Salesforce\":7},{\"Salesforce\":7,\"Mobile_SDK\":68,\"MySQL\":45},{\"Mobile_SDK\":65,\"MySQL\":28,\"Salesforce\":8},{\"Mobile_SDK\":53,\"MySQL\":30,\"Salesforce\":4},{\"Salesforce\":11,\"Mobile_SDK\":64,\"MySQL\":50},{\"Mobile_SDK\":56,\"MySQL\":51,\"Salesforce\":2},{\"Mobile_SDK\":61,\"MySQL\":55,\"Salesforce\":9},{\"MySQL\":38,\"Mobile_SDK\":66,\"Salesforce\":3},{\"Mobile_SDK\":58,\"MySQL\":39,\"Salesforce\":3},{\"Salesforce\":18,\"Mobile_SDK\":72,\"MySQL\":39},{\"MySQL\":28,\"Mobile_SDK\":64,\"Salesforce\":4},{\"Mobile_SDK\":69,\"MySQL\":28,\"Salesforce\":5},{\"Mobile_SDK\":63,\"MySQL\":40,\"Salesforce\":7},{\"Mobile_SDK\":64,\"Salesforce\":10,\"MySQL\":32},{\"MySQL\":34,\"Mobile_SDK\":70,\"Salesforce\":10},{\"Mobile_SDK\":61,\"MySQL\":26,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":25,\"Salesforce\":2},{\"Mobile_SDK\":59,\"Salesforce\":3,\"MySQL\":31},{\"MySQL\":42,\"Mobile_SDK\":68,\"Salesforce\":4},{\"Mobile_SDK\":61,\"MySQL\":42,\"Salesforce\":18},{\"Mobile_SDK\":69,\"Salesforce\":13,\"MySQL\":38},{\"MySQL\":36,\"Salesforce\":3,\"Mobile_SDK\":72},{\"Mobile_SDK\":57,\"MySQL\":41,\"Salesforce\":7},{\"Mobile_SDK\":63,\"MySQL\":28,\"Salesforce\":4},{\"MySQL\":34,\"Mobile_SDK\":60,\"Salesforce\":5},{\"Mobile_SDK\":63,\"MySQL\":38,\"Salesforce\":15},{\"MySQL\":35,\"Mobile_SDK\":64,\"Salesforce\":4},{\"Mobile_SDK\":59,\"MySQL\":43,\"Salesforce\":5},{\"Mobile_SDK\":57,\"MySQL\":38,\"Salesforce\":11},{\"MySQL\":32,\"Mobile_SDK\":55,\"Salesforce\":1},{\"Mobile_SDK\":66,\"MySQL\":25,\"Salesforce\":9},{\"Mobile_SDK\":68,\"Salesforce\":9,\"MySQL\":36},{\"Mobile_SDK\":64,\"MySQL\":28,\"Salesforce\":10},{\"Mobile_SDK\":56,\"MySQL\":44,\"Salesforce\":3},{\"Mobile_SDK\":51,\"MySQL\":30,\"Salesforce\":4},{\"Mobile_SDK\":60,\"MySQL\":45,\"Salesforce\":6},{\"Mobile_SDK\":63,\"MySQL\":40,\"Salesforce\":9},{\"Mobile_SDK\":72,\"MySQL\":35,\"Salesforce\":10},{\"Mobile_SDK\":60,\"MySQL\":32,\"Salesforce\":2},{\"Mobile_SDK\":56,\"MySQL\":33,\"Salesforce\":8},{\"Mobile_SDK\":61,\"MySQL\":27,\"Salesforce\":4},{\"MySQL\":29,\"Mobile_SDK\":62,\"Salesforce\":8},{\"Mobile_SDK\":67,\"MySQL\":32,\"Salesforce\":10},{\"Mobile_SDK\":62,\"MySQL\":27,\"Salesforce\":4},{\"MySQL\":38,\"Mobile_SDK\":63,\"Salesforce\":4},{\"MySQL\":32,\"Mobile_SDK\":73,\"Salesforce\":4},{\"Mobile_SDK\":66,\"Salesforce\":4,\"MySQL\":37},{\"Mobile_SDK\":57,\"MySQL\":22,\"Salesforce\":11},{\"Mobile_SDK\":63,\"MySQL\":39,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":31,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":41,\"Salesforce\":6},{\"MySQL\":28,\"Salesforce\":4,\"Mobile_SDK\":57},{\"Mobile_SDK\":71,\"MySQL\":36,\"Salesforce\":4},{\"Mobile_SDK\":67,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":79,\"MySQL\":38,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":44,\"Salesforce\":6},{\"Mobile_SDK\":59,\"MySQL\":24,\"Salesforce\":8},{\"Mobile_SDK\":70,\"MySQL\":19,\"Salesforce\":1},{\"Mobile_SDK\":66,\"MySQL\":36,\"Salesforce\":9},{\"Salesforce\":6,\"Mobile_SDK\":63,\"MySQL\":43},{\"MySQL\":44,\"Mobile_SDK\":68,\"Salesforce\":3},{\"MySQL\":43,\"Mobile_SDK\":69,\"Salesforce\":9},{\"Mobile_SDK\":59,\"MySQL\":28,\"Salesforce\":4},{\"Mobile_SDK\":66,\"MySQL\":21,\"Salesforce\":5},{\"Mobile_SDK\":64,\"MySQL\":41,\"Salesforce\":13},{\"Mobile_SDK\":63,\"MySQL\":39,\"Salesforce\":7},{\"Mobile_SDK\":64,\"MySQL\":32,\"Salesforce\":8},{\"MySQL\":52,\"Mobile_SDK\":58,\"Salesforce\":3},{\"Mobile_SDK\":67,\"MySQL\":37,\"Salesforce\":8},{\"MySQL\":48,\"Salesforce\":13,\"Mobile_SDK\":62},{\"MySQL\":27,\"Mobile_SDK\":74,\"Salesforce\":3},{\"Mobile_SDK\":72,\"MySQL\":34,\"Salesforce\":4},{\"MySQL\":31,\"Mobile_SDK\":61,\"Salesforce\":9},{\"Mobile_SDK\":65,\"MySQL\":46,\"Salesforce\":5},{\"Mobile_SDK\":61,\"Salesforce\":7,\"MySQL\":32},{\"Salesforce\":6,\"Mobile_SDK\":76,\"MySQL\":31},{\"Mobile_SDK\":69,\"Salesforce\":4,\"MySQL\":39},{\"MySQL\":37,\"Mobile_SDK\":65,\"Salesforce\":6},{\"Mobile_SDK\":64,\"MySQL\":34,\"Salesforce\":6},{\"Mobile_SDK\":61,\"MySQL\":46,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":25,\"Salesforce\":12},{\"Mobile_SDK\":61,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":62,\"MySQL\":22,\"Salesforce\":7},{\"Mobile_SDK\":64,\"MySQL\":32,\"Salesforce\":10},{\"Mobile_SDK\":65,\"MySQL\":34,\"Salesforce\":5},{\"Salesforce\":10,\"Mobile_SDK\":62,\"MySQL\":33},{\"MySQL\":30,\"Mobile_SDK\":65,\"Salesforce\":4},{\"Mobile_SDK\":70,\"MySQL\":45,\"Salesforce\":1},{\"Mobile_SDK\":62,\"MySQL\":32,\"Salesforce\":6},{\"Mobile_SDK\":66,\"MySQL\":40,\"Salesforce\":2},{\"Mobile_SDK\":72,\"MySQL\":39,\"Salesforce\":10},{\"Mobile_SDK\":65,\"MySQL\":37,\"Salesforce\":4},{\"Mobile_SDK\":59,\"MySQL\":21,\"Salesforce\":9},{\"Mobile_SDK\":60,\"MySQL\":48,\"Salesforce\":3},{\"Mobile_SDK\":69,\"MySQL\":36,\"Salesforce\":3},{\"Mobile_SDK\":66,\"Salesforce\":7,\"MySQL\":41},{\"Mobile_SDK\":59,\"MySQL\":36,\"Salesforce\":5},{\"MySQL\":40,\"Mobile_SDK\":54,\"Salesforce\":6},{\"Mobile_SDK\":64,\"MySQL\":35,\"Salesforce\":7},{\"Mobile_SDK\":65,\"Salesforce\":6,\"MySQL\":42},{\"Mobile_SDK\":54,\"MySQL\":44,\"Salesforce\":5},{\"Mobile_SDK\":55,\"MySQL\":39,\"Salesforce\":8},{\"Mobile_SDK\":69,\"MySQL\":53,\"Salesforce\":5},{\"Mobile_SDK\":72,\"MySQL\":26,\"Salesforce\":8},{\"Salesforce\":4,\"Mobile_SDK\":67,\"MySQL\":37},{\"Mobile_SDK\":61,\"MySQL\":34,\"Salesforce\":3},{\"Mobile_SDK\":66,\"MySQL\":41,\"Salesforce\":7},{\"Mobile_SDK\":65,\"MySQL\":25,\"Salesforce\":9},{\"Salesforce\":7,\"Mobile_SDK\":54,\"MySQL\":46},{\"Mobile_SDK\":55,\"MySQL\":23,\"Salesforce\":2},{\"Mobile_SDK\":53,\"MySQL\":37,\"Salesforce\":4},{\"Mobile_SDK\":72,\"Salesforce\":10,\"MySQL\":25},{\"Mobile_SDK\":66,\"MySQL\":47,\"Salesforce\":4},{\"Mobile_SDK\":60,\"MySQL\":17,\"Salesforce\":11},{\"Mobile_SDK\":64,\"Salesforce\":11,\"MySQL\":32},{\"MySQL\":31,\"Mobile_SDK\":63,\"Salesforce\":9},{\"Mobile_SDK\":72,\"Salesforce\":3,\"MySQL\":39},{\"Mobile_SDK\":59,\"MySQL\":39,\"Salesforce\":3},{\"Mobile_SDK\":70,\"MySQL\":38,\"Salesforce\":1},{\"Mobile_SDK\":62,\"MySQL\":36,\"Salesforce\":7},{\"Salesforce\":7,\"Mobile_SDK\":67,\"MySQL\":32},{\"Mobile_SDK\":65,\"MySQL\":31,\"Salesforce\":4},{\"Mobile_SDK\":66,\"MySQL\":35,\"Salesforce\":7},{\"Mobile_SDK\":71,\"MySQL\":23,\"Salesforce\":3},{\"Mobile_SDK\":66,\"MySQL\":44,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":36,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":51,\"Salesforce\":4},{\"Mobile_SDK\":61,\"MySQL\":42,\"Salesforce\":12},{\"Mobile_SDK\":62,\"MySQL\":25,\"Salesforce\":6},{\"MySQL\":49,\"Mobile_SDK\":60,\"Salesforce\":3},{\"Mobile_SDK\":64,\"MySQL\":28,\"Salesforce\":4},{\"MySQL\":46,\"Mobile_SDK\":73,\"Salesforce\":8},{\"Mobile_SDK\":64,\"Salesforce\":12,\"MySQL\":44},{\"Mobile_SDK\":66,\"MySQL\":27,\"Salesforce\":10},{\"Mobile_SDK\":63,\"MySQL\":32,\"Salesforce\":7},{\"MySQL\":31,\"Mobile_SDK\":62,\"Salesforce\":4},{\"Mobile_SDK\":62,\"MySQL\":72,\"Salesforce\":8},{\"Mobile_SDK\":64,\"MySQL\":31,\"Salesforce\":9},{\"MySQL\":26,\"Mobile_SDK\":69,\"Salesforce\":3},{\"Mobile_SDK\":63,\"MySQL\":27,\"Salesforce\":5},{\"Mobile_SDK\":61,\"MySQL\":33,\"Salesforce\":7},{\"Mobile_SDK\":62,\"MySQL\":37,\"Salesforce\":6},{\"Mobile_SDK\":60,\"MySQL\":47,\"Salesforce\":5},{\"MySQL\":40,\"Mobile_SDK\":59,\"Salesforce\":9},{\"Mobile_SDK\":64,\"MySQL\":38,\"Salesforce\":7},{\"Mobile_SDK\":65,\"MySQL\":25,\"Salesforce\":6},{\"Mobile_SDK\":66,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":55,\"MySQL\":35,\"Salesforce\":7},{\"MySQL\":40,\"Mobile_SDK\":59,\"Salesforce\":4},{\"Mobile_SDK\":70,\"MySQL\":42,\"Salesforce\":5},{\"Mobile_SDK\":68,\"MySQL\":23,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":32,\"Salesforce\":5},{\"Mobile_SDK\":54,\"Salesforce\":5,\"MySQL\":30},{\"Mobile_SDK\":63,\"MySQL\":24,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":35,\"Salesforce\":4},{\"Mobile_SDK\":63,\"MySQL\":44,\"Salesforce\":3},{\"Mobile_SDK\":65,\"MySQL\":41,\"Salesforce\":3}],\"filter\":[{\"Mobile_SDK\":66,\"MySQL\":51,\"Salesforce\":14},{\"Mobile_SDK\":69,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":68,\"MySQL\":41,\"Salesforce\":14},{\"Mobile_SDK\":71,\"MySQL\":44,\"Salesforce\":3},{\"MySQL\":36,\"Mobile_SDK\":64,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":45,\"Salesforce\":5},{\"Mobile_SDK\":56,\"MySQL\":32,\"Salesforce\":3},{\"Mobile_SDK\":54,\"MySQL\":28,\"Salesforce\":12},{\"Mobile_SDK\":71,\"MySQL\":29,\"Salesforce\":7},{\"Mobile_SDK\":67,\"MySQL\":41,\"Salesforce\":8},{\"Salesforce\":9,\"Mobile_SDK\":69,\"MySQL\":32},{\"MySQL\":32,\"Mobile_SDK\":71,\"Salesforce\":14},{\"Mobile_SDK\":65,\"Salesforce\":14,\"MySQL\":34},{\"Mobile_SDK\":61,\"MySQL\":38,\"Salesforce\":2},{\"Mobile_SDK\":66,\"MySQL\":33,\"Salesforce\":7},{\"MySQL\":36,\"Mobile_SDK\":70,\"Salesforce\":3},{\"MySQL\":39,\"Mobile_SDK\":71,\"Salesforce\":4},{\"MySQL\":39,\"Mobile_SDK\":59,\"Salesforce\":14},{\"Mobile_SDK\":63,\"MySQL\":25,\"Salesforce\":7},{\"Mobile_SDK\":62,\"Salesforce\":8,\"MySQL\":36},{\"Mobile_SDK\":63,\"Salesforce\":13,\"MySQL\":28},{\"Mobile_SDK\":68,\"MySQL\":38,\"Salesforce\":11},{\"Salesforce\":5,\"Mobile_SDK\":63,\"MySQL\":38},{\"Mobile_SDK\":63,\"MySQL\":25,\"Salesforce\":7},{\"Salesforce\":7,\"Mobile_SDK\":68,\"MySQL\":45},{\"Mobile_SDK\":65,\"MySQL\":28,\"Salesforce\":8},{\"Mobile_SDK\":53,\"MySQL\":30,\"Salesforce\":4},{\"Salesforce\":11,\"Mobile_SDK\":64,\"MySQL\":50},{\"Mobile_SDK\":56,\"MySQL\":51,\"Salesforce\":2},{\"Mobile_SDK\":61,\"MySQL\":55,\"Salesforce\":9},{\"MySQL\":38,\"Mobile_SDK\":66,\"Salesforce\":3},{\"Mobile_SDK\":58,\"MySQL\":39,\"Salesforce\":3},{\"Salesforce\":18,\"Mobile_SDK\":72,\"MySQL\":39},{\"MySQL\":28,\"Mobile_SDK\":64,\"Salesforce\":4},{\"Mobile_SDK\":69,\"MySQL\":28,\"Salesforce\":5},{\"Mobile_SDK\":63,\"MySQL\":40,\"Salesforce\":7},{\"Mobile_SDK\":64,\"Salesforce\":10,\"MySQL\":32},{\"MySQL\":34,\"Mobile_SDK\":70,\"Salesforce\":10},{\"Mobile_SDK\":61,\"MySQL\":26,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":25,\"Salesforce\":2},{\"Mobile_SDK\":59,\"Salesforce\":3,\"MySQL\":31},{\"MySQL\":42,\"Mobile_SDK\":68,\"Salesforce\":4},{\"Mobile_SDK\":61,\"MySQL\":42,\"Salesforce\":18},{\"Mobile_SDK\":69,\"Salesforce\":13,\"MySQL\":38},{\"MySQL\":36,\"Salesforce\":3,\"Mobile_SDK\":72},{\"Mobile_SDK\":57,\"MySQL\":41,\"Salesforce\":7},{\"Mobile_SDK\":63,\"MySQL\":28,\"Salesforce\":4},{\"MySQL\":34,\"Mobile_SDK\":60,\"Salesforce\":5},{\"Mobile_SDK\":63,\"MySQL\":38,\"Salesforce\":15},{\"MySQL\":35,\"Mobile_SDK\":64,\"Salesforce\":4},{\"Mobile_SDK\":59,\"MySQL\":43,\"Salesforce\":5},{\"Mobile_SDK\":57,\"MySQL\":38,\"Salesforce\":11},{\"MySQL\":32,\"Mobile_SDK\":55,\"Salesforce\":1},{\"Mobile_SDK\":66,\"MySQL\":25,\"Salesforce\":9},{\"Mobile_SDK\":68,\"Salesforce\":9,\"MySQL\":36},{\"Mobile_SDK\":64,\"MySQL\":28,\"Salesforce\":10},{\"Mobile_SDK\":56,\"MySQL\":44,\"Salesforce\":3},{\"Mobile_SDK\":51,\"MySQL\":30,\"Salesforce\":4},{\"Mobile_SDK\":60,\"MySQL\":45,\"Salesforce\":6},{\"Mobile_SDK\":63,\"MySQL\":40,\"Salesforce\":9},{\"Mobile_SDK\":72,\"MySQL\":35,\"Salesforce\":10},{\"Mobile_SDK\":60,\"MySQL\":32,\"Salesforce\":2},{\"Mobile_SDK\":56,\"MySQL\":33,\"Salesforce\":8},{\"Mobile_SDK\":61,\"MySQL\":27,\"Salesforce\":4},{\"MySQL\":29,\"Mobile_SDK\":62,\"Salesforce\":8},{\"Mobile_SDK\":67,\"MySQL\":32,\"Salesforce\":10},{\"Mobile_SDK\":62,\"MySQL\":27,\"Salesforce\":4},{\"MySQL\":38,\"Mobile_SDK\":63,\"Salesforce\":4},{\"MySQL\":32,\"Mobile_SDK\":73,\"Salesforce\":4},{\"Mobile_SDK\":66,\"Salesforce\":4,\"MySQL\":37},{\"Mobile_SDK\":57,\"MySQL\":22,\"Salesforce\":11},{\"Mobile_SDK\":63,\"MySQL\":39,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":31,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":41,\"Salesforce\":6},{\"MySQL\":28,\"Salesforce\":4,\"Mobile_SDK\":57},{\"Mobile_SDK\":71,\"MySQL\":36,\"Salesforce\":4},{\"Mobile_SDK\":67,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":79,\"MySQL\":38,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":44,\"Salesforce\":6},{\"Mobile_SDK\":59,\"MySQL\":24,\"Salesforce\":8},{\"Mobile_SDK\":70,\"MySQL\":19,\"Salesforce\":1},{\"Mobile_SDK\":66,\"MySQL\":36,\"Salesforce\":9},{\"Salesforce\":6,\"Mobile_SDK\":63,\"MySQL\":43},{\"MySQL\":44,\"Mobile_SDK\":68,\"Salesforce\":3},{\"MySQL\":43,\"Mobile_SDK\":69,\"Salesforce\":9},{\"Mobile_SDK\":59,\"MySQL\":28,\"Salesforce\":4},{\"Mobile_SDK\":66,\"MySQL\":21,\"Salesforce\":5},{\"Mobile_SDK\":64,\"MySQL\":41,\"Salesforce\":13},{\"Mobile_SDK\":63,\"MySQL\":39,\"Salesforce\":7},{\"Mobile_SDK\":64,\"MySQL\":32,\"Salesforce\":8},{\"MySQL\":52,\"Mobile_SDK\":58,\"Salesforce\":3},{\"Mobile_SDK\":67,\"MySQL\":37,\"Salesforce\":8},{\"MySQL\":48,\"Salesforce\":13,\"Mobile_SDK\":62},{\"MySQL\":27,\"Mobile_SDK\":74,\"Salesforce\":3},{\"Mobile_SDK\":72,\"MySQL\":34,\"Salesforce\":4},{\"MySQL\":31,\"Mobile_SDK\":61,\"Salesforce\":9},{\"Mobile_SDK\":65,\"MySQL\":46,\"Salesforce\":5},{\"Mobile_SDK\":61,\"Salesforce\":7,\"MySQL\":32},{\"Salesforce\":6,\"Mobile_SDK\":76,\"MySQL\":31},{\"Mobile_SDK\":69,\"Salesforce\":4,\"MySQL\":39},{\"MySQL\":37,\"Mobile_SDK\":65,\"Salesforce\":6},{\"Mobile_SDK\":64,\"MySQL\":34,\"Salesforce\":6},{\"Mobile_SDK\":61,\"MySQL\":46,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":25,\"Salesforce\":12},{\"Mobile_SDK\":61,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":62,\"MySQL\":22,\"Salesforce\":7},{\"Mobile_SDK\":64,\"MySQL\":32,\"Salesforce\":10},{\"Mobile_SDK\":65,\"MySQL\":34,\"Salesforce\":5},{\"Salesforce\":10,\"Mobile_SDK\":62,\"MySQL\":33},{\"MySQL\":30,\"Mobile_SDK\":65,\"Salesforce\":4},{\"Mobile_SDK\":70,\"MySQL\":45,\"Salesforce\":1},{\"Mobile_SDK\":62,\"MySQL\":32,\"Salesforce\":6},{\"Mobile_SDK\":66,\"MySQL\":40,\"Salesforce\":2},{\"Mobile_SDK\":72,\"MySQL\":39,\"Salesforce\":10},{\"Mobile_SDK\":65,\"MySQL\":37,\"Salesforce\":4},{\"Mobile_SDK\":59,\"MySQL\":21,\"Salesforce\":9},{\"Mobile_SDK\":60,\"MySQL\":48,\"Salesforce\":3},{\"Mobile_SDK\":69,\"MySQL\":36,\"Salesforce\":3},{\"Mobile_SDK\":66,\"Salesforce\":7,\"MySQL\":41},{\"Mobile_SDK\":59,\"MySQL\":36,\"Salesforce\":5},{\"MySQL\":40,\"Mobile_SDK\":54,\"Salesforce\":6},{\"Mobile_SDK\":64,\"MySQL\":35,\"Salesforce\":7},{\"Mobile_SDK\":65,\"Salesforce\":6,\"MySQL\":42},{\"Mobile_SDK\":54,\"MySQL\":44,\"Salesforce\":5},{\"Mobile_SDK\":55,\"MySQL\":39,\"Salesforce\":8},{\"Mobile_SDK\":69,\"MySQL\":53,\"Salesforce\":5},{\"Mobile_SDK\":72,\"MySQL\":26,\"Salesforce\":8},{\"Salesforce\":4,\"Mobile_SDK\":67,\"MySQL\":37},{\"Mobile_SDK\":61,\"MySQL\":34,\"Salesforce\":3},{\"Mobile_SDK\":66,\"MySQL\":41,\"Salesforce\":7},{\"Mobile_SDK\":65,\"MySQL\":25,\"Salesforce\":9},{\"Salesforce\":7,\"Mobile_SDK\":54,\"MySQL\":46},{\"Mobile_SDK\":55,\"MySQL\":23,\"Salesforce\":2},{\"Mobile_SDK\":53,\"MySQL\":37,\"Salesforce\":4},{\"Mobile_SDK\":72,\"Salesforce\":10,\"MySQL\":25},{\"Mobile_SDK\":66,\"MySQL\":47,\"Salesforce\":4},{\"Mobile_SDK\":60,\"MySQL\":17,\"Salesforce\":11},{\"Mobile_SDK\":64,\"Salesforce\":11,\"MySQL\":32},{\"MySQL\":31,\"Mobile_SDK\":63,\"Salesforce\":9},{\"Mobile_SDK\":72,\"Salesforce\":3,\"MySQL\":39},{\"Mobile_SDK\":59,\"MySQL\":39,\"Salesforce\":3},{\"Mobile_SDK\":70,\"MySQL\":38,\"Salesforce\":1},{\"Mobile_SDK\":62,\"MySQL\":36,\"Salesforce\":7},{\"Salesforce\":7,\"Mobile_SDK\":67,\"MySQL\":32},{\"Mobile_SDK\":65,\"MySQL\":31,\"Salesforce\":4},{\"Mobile_SDK\":66,\"MySQL\":35,\"Salesforce\":7},{\"Mobile_SDK\":71,\"MySQL\":23,\"Salesforce\":3},{\"Mobile_SDK\":66,\"MySQL\":44,\"Salesforce\":5},{\"Mobile_SDK\":65,\"MySQL\":36,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":51,\"Salesforce\":4},{\"Mobile_SDK\":61,\"MySQL\":42,\"Salesforce\":12},{\"Mobile_SDK\":62,\"MySQL\":25,\"Salesforce\":6},{\"MySQL\":49,\"Mobile_SDK\":60,\"Salesforce\":3},{\"Mobile_SDK\":64,\"MySQL\":28,\"Salesforce\":4},{\"MySQL\":46,\"Mobile_SDK\":73,\"Salesforce\":8},{\"Mobile_SDK\":64,\"Salesforce\":12,\"MySQL\":44},{\"Mobile_SDK\":66,\"MySQL\":27,\"Salesforce\":10},{\"Mobile_SDK\":63,\"MySQL\":32,\"Salesforce\":7},{\"MySQL\":31,\"Mobile_SDK\":62,\"Salesforce\":4},{\"Mobile_SDK\":62,\"MySQL\":72,\"Salesforce\":8},{\"Mobile_SDK\":64,\"MySQL\":31,\"Salesforce\":9},{\"MySQL\":26,\"Mobile_SDK\":69,\"Salesforce\":3},{\"Mobile_SDK\":63,\"MySQL\":27,\"Salesforce\":5},{\"Mobile_SDK\":61,\"MySQL\":33,\"Salesforce\":7},{\"Mobile_SDK\":62,\"MySQL\":37,\"Salesforce\":6},{\"Mobile_SDK\":60,\"MySQL\":47,\"Salesforce\":5},{\"MySQL\":40,\"Mobile_SDK\":59,\"Salesforce\":9},{\"Mobile_SDK\":64,\"MySQL\":38,\"Salesforce\":7},{\"Mobile_SDK\":65,\"MySQL\":25,\"Salesforce\":6},{\"Mobile_SDK\":66,\"MySQL\":36,\"Salesforce\":5},{\"Mobile_SDK\":55,\"MySQL\":35,\"Salesforce\":7},{\"MySQL\":40,\"Mobile_SDK\":59,\"Salesforce\":4},{\"Mobile_SDK\":70,\"MySQL\":42,\"Salesforce\":5},{\"Mobile_SDK\":68,\"MySQL\":23,\"Salesforce\":5},{\"Mobile_SDK\":66,\"MySQL\":32,\"Salesforce\":5},{\"Mobile_SDK\":54,\"Salesforce\":5,\"MySQL\":30},{\"Mobile_SDK\":63,\"MySQL\":24,\"Salesforce\":6},{\"Mobile_SDK\":62,\"MySQL\":35,\"Salesforce\":4},{\"Mobile_SDK\":63,\"MySQL\":44,\"Salesforce\":3},{\"Mobile_SDK\":65,\"MySQL\":41,\"Salesforce\":3}]}]";
	
	
	
    @OnWebSocketMessage
    public void onText(Session session, String message)
    {
        if (session.isOpen())
        {
            System.out.printf("%s -- Received message [%s]%n", this.socketid, message);
            // echo the message back
            //session.getRemote().sendString(message0d,null);
            
            try {
                if (message.startsWith("42")) {
                    String[] t = message.split(",");
                    
                    message = message.substring(2);
                    ArrayList<String> request = om.readValue(message, new TypeReference<List<String>>() {});
                    
                    System.out.println(request);
                    
                    if (request != null && request.get(0).equalsIgnoreCase("topic")) {
                        System.out.println("Got topic name: " + request.get(1));
                        this.topic = request.get(1);

                        this.monitor = MonitorManager.getInstance().getMonitors().get(this.topic);
                        if (this.monitor == null) {
                        	System.out.println("No such topic: " + this.topic);
                        } else {
                            this.monitor.getLiveMonitor().addObserver(this);                        	
                        }
                    	
                    }
                    
                    if (request != null && request.get(0).equalsIgnoreCase("filter")) {
                        System.out.println("Got filter: " + request.get(1));
                        this.filter = request.get(1);
                    	
                    }
                	
                    
                    
                }
            	
            } catch (ArrayIndexOutOfBoundsException e) {
            	e.printStackTrace();
            	System.out.println(e);
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
        }
    }
    
    @OnWebSocketConnect
    public void onWebSocketConnect(Session session)
    {
    	this.outbound = session;
    	
    	Random rand = new Random(); 
    	this.socketid = rand.nextInt(500); 
    	
    	System.out.println("Id: " + this.socketid);
    	
    	outbound.getRemote().sendString(message0, null);
    	outbound.getRemote().sendString(message0a, null);
    	outbound.getRemote().sendString(message0b, null);
    	outbound.getRemote().sendString(message0c, null);
    	//outbound.getRemote().sendString(message0d, null);
    	
//    	for (int i = 0; i < 1; i++) {
//        	outbound.getRemote().sendString(message1, null);
//        	outbound.getRemote().sendString(message2, null);
//        	outbound.getRemote().sendString(message3, null);
//    	}

//    	try {
//        	while (session.isOpen()) {
//        		if (this.monitor != null) {
//            		Long count = this.monitor.getTweetCounter().getLastSecondCount();
//            		Aggregates aa = new Aggregates();
//            		
//            		Aggregate a = new Aggregate();
//            		
//            		a.getAll().put("DC1", count);
//            		
//            		aa.setName("aggregated");
//            		aa.setAggregate(a);
//            		
//            		String message = "[42" + om.writeValueAsString(aa) + "]";
//            		outbound.getRemote().sendString(message, null);
//            		
//        		}
//
//        		Thread.sleep(1000);
//        	}
//        	
//    	} catch (Exception e) {
//    		e.printStackTrace();
//    	}
    	
        
    	
    }
    
    @OnWebSocketClose
    public void onWebSocketDisconnect(Session session, int closeCode, String closeReason)
    {
    	this.monitor.getLiveMonitor().deleteObserver(this);
    }

    @OnWebSocketError
    public void onWebSocketError(Session session, Throwable cause)
    {
    	this.monitor.getLiveMonitor().deleteObserver(this);

    }
	@Override
	public void configure(WebSocketServletFactory factory) {
		// set a 10 second timeout
        //factory.getPolicy().setIdleTimeout(10000);

        // register MyEchoSocket as the WebSocket to create on Upgrade
        factory.register(StreamSocket.class);
        
        om.setSerializationInclusion(Include.NON_NULL);
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
		ArrayList<PhemeSource> list = (ArrayList<PhemeSource>) arg;
		System.out.println("Messages!" + list.size());

		if (outbound != null) {
			Aggregates aa = new Aggregates();
			HashMap<String, Long> dcMap = new HashMap<>();
			List<Sample> slist = new ArrayList<>();				
			Samples ss = new Samples();

			
			for (PhemeSource ps : list) {
				//aggregates				
				Long count = dcMap.get(ps.getDcID());
				if (count == null) {
					dcMap.put(ps.getDcID(), 1L);
				} else {
					dcMap.put(ps.getDcID(), count + 1);
				}
				
				//messages (events)
				Sample s = new Sample();
				Event e = new Event();
				ps.setRawJson("\"Not shown\"");
				
				if (ps.getId().charAt(0) > '9') {
					//System.out.println(ps.getId());
					ps.setId("\"" + ps.getId() + "\"");
				}
				
				e.setMessage(ps);
				ps.setId(null);
				e.setInputLabel(ps.getDcID());
				e.setTimestamp(ps.getCreatedAt());
				s.setEvent(e);
				s.setFilter("");
				slist.add(s);
			}
			
			if (dcMap.size() != 0) {

				//aggregates
				Aggregate a = new Aggregate();
				a.setAll(dcMap);
				aa.setAggregate(a);
				
				try {
					outbound.getRemote().sendString("42" + om.writeValueAsString(aa));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//messages
				ss.setSamples(slist);
				

				try {
					outbound.getRemote().sendString("42" + om.writeValueAsString(ss));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
	}

}