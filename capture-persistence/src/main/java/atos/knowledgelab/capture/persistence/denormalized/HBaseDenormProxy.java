/*******************************************************************************
 * 	Copyright (C) 2017  ATOS Spain S.A.
 *
 * 	This file is part of the Capturean software.
 *
 * 	This program is dual licensed under the terms of GNU Affero General
 * 	Public License and proprietary for commercial usage.
 *
 *
 * 	This program is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU Affero General Public License as
 * 	published by the Free Software Foundation, either version 3 of the
 * 	License, or (at your option) any later version.
 *
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU Affero General Public License for more details.
 *
 * 	You should have received a copy of the GNU Affero General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 	You can be released from the requirements of the license by purchasing
 * 	a commercial license or negotiating an agreement with Atos Spain S.A.
 * 	Buying such a license is mandatory as soon as you develop commercial
 * 	activities involving the Capturean software without disclosing the source 
 * 	code of your own applications. 
 *
 * 	
 * Contributors:
 *      Mateusz Radzimski (ATOS, ARI, Knowledge Lab)
 *      Iván Martínez Rodriguez (ATOS, ARI, Knowledge Lab)
 *      María Angeles Sanguino Gonzalez (ATOS, ARI, Knowledge Lab)
 *      Jose María Fuentes López (ATOS, ARI, Knowledge Lab)
 *      Jorge Montero Gómez (ATOS, ARI, Knowledge Lab)
 *      Ana Luiza Pontual Costa E Silva (ATOS, ARI, Knowledge Lab)
 *      Miguel Angel Tinte García (ATOS, ARI, Knowledge Lab)
 *      
 *******************************************************************************/
package atos.knowledgelab.capture.persistence.denormalized;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.Random;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Row;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.Batch;
import org.apache.hadoop.hbase.coprocessor.example.generated.BulkDeleteProtos.BulkDeleteRequest;
import org.apache.hadoop.hbase.coprocessor.example.generated.BulkDeleteProtos.BulkDeleteRequest.Builder;
import org.apache.hadoop.hbase.coprocessor.example.generated.BulkDeleteProtos.BulkDeleteRequest.DeleteType;
import org.apache.hadoop.hbase.coprocessor.example.generated.BulkDeleteProtos.BulkDeleteResponse;
import org.apache.hadoop.hbase.coprocessor.example.generated.BulkDeleteProtos.BulkDeleteService;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.MultiRowRangeFilter;
import org.apache.hadoop.hbase.filter.MultiRowRangeFilter.RowRange;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.ipc.BlockingRpcCallback;
import org.apache.hadoop.hbase.ipc.ServerRpcController;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.util.Bytes;

import atos.knowledgelab.capture.bean.Aggregator;
import atos.knowledgelab.capture.bean.AggregatorList;
import atos.knowledgelab.capture.bean.AmbassadorQuality;
import atos.knowledgelab.capture.bean.AmbassadorQualityFactor;
import atos.knowledgelab.capture.bean.Brand;
import atos.knowledgelab.capture.bean.BrandList;
import atos.knowledgelab.capture.bean.CompetitorAnalysis;
import atos.knowledgelab.capture.bean.DataChannel;
import atos.knowledgelab.capture.bean.DataChannelList;
import atos.knowledgelab.capture.bean.DataPool;
import atos.knowledgelab.capture.bean.DataPoolList;
import atos.knowledgelab.capture.bean.DataSource;
import atos.knowledgelab.capture.bean.DcTweetList;
import atos.knowledgelab.capture.bean.EngagementAnalysis;
import atos.knowledgelab.capture.bean.InfluentialAnalysis;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.AnalysisType;
import atos.knowledgelab.capture.bean.PeriodicAnalysisResult.Periodicity;
import atos.knowledgelab.capture.bean.QueryData;
import atos.knowledgelab.capture.bean.ReachAnalysis;
import atos.knowledgelab.capture.bean.RedditDataSource;
import atos.knowledgelab.capture.bean.TermOccurrenceAnalysis;
import atos.knowledgelab.capture.bean.Tweet;
import atos.knowledgelab.capture.bean.TwitterDataSource;
import atos.knowledgelab.capture.bean.TwitterUser;
import atos.knowledgelab.capture.bean.VolumeOcurrenceAnalysis;
import atos.knowledgelab.capture.exception.CaptureException;
import atos.knowledgelab.capture.util.AggregateConstants;
import atos.knowledgelab.capture.util.AggregateType;
import atos.knowledgelab.capture.util.BrandManagementConstants;
import atos.knowledgelab.capture.util.CaptureConstants;
import atos.knowledgelab.capture.util.DataChannelState;
import atos.knowledgelab.capture.util.TopEntryComparator;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.protobuf.ServiceException;
import com.thoughtworks.xstream.XStream;

/*
 * API for interacting directly with HBase tables.
 * This is a set of low-level methods to be invoked
 * by any class interfacing with Capture Storage
 * (implementing CaptureStorageIf)
 * 
 * This version needs HBase client >= 1.0
 * 
 * 
 * 
 * Features of 1.0:
 * - MultiRowRangeFilter (in method: scanTableAggergates) 
 * - get tables from org.apache.hadoop.hbase.client.Connection instead of HTable. 
 * 
 * 
 * 
 */
public class HBaseDenormProxy extends Configured {

	private static HBaseDenormProxy instance;
	
	//TODO: make table prefix name configurable from external conf file
	private String tableNamePrefix = "capture_";
		
	
	private String captureTweetsTable = tableNamePrefix + "tweet_fast";
	private String captureTweetsColFamily = "capture_tweet";
	
	private String captureDcTable = tableNamePrefix + "dc_fast";
	private String captureDcColFamily = "capture_dc";

	private String captureAggregateTable = tableNamePrefix + "aggregate_fast";
	private String captureAggregateColFamily = "capture_aggregate";
	
	private String captureAggregatorTable = tableNamePrefix + "aggregator_fast";
	private String captureAggregatorColFamily = "capture_aggregator";
	
	private String captureProcessTable = tableNamePrefix + "process_fast";
	private String captureProcessColFamily = "capture_process";
	
	private String captureUserAggregateTable = tableNamePrefix + "useraggregate_fast";
	
	private String captureUserAggregateColFamilyReach = "capture_reach";
	private String captureUserAggregateColFamilyEngagement = "capture_engagement";
	private String captureUserAggregateColFamilyInfluential = "capture_influential";
	private String captureUserAggregateColFamilyAmbassador = "capture_ambassador";

	private String captureBrandTable = tableNamePrefix + "brand_fast";
	private String captureBrandColFamily = "capture_brand";
	
	private String dataSourcePrefix = "DATASOURCE";
	private String dcPropertyPrefix = "DCPROPERTY";
	
	private final int dcRowKeyType = 0;
	private final int dcRowKeyIdPart = 1;
	private final int dcRowKeyCaptureCreationDatePart = 2;
	private final int dcRowKeyDCTypePart = 3;
	
	private final int dsColNameSize = 4;
	private final int dsTypeKeyPart = 1;
	private final int dsIdKeyPart = 2;
	private final int dsFieldNameKeyPart = 3;

	private final int dcColNameSize = 2;
	private final int dcFieldNameKeyPart = 1;

	private final int dpColNameSize = 2;
	private final int dpFieldNameKeyPart = 1;
	
	/*
	 * capture-tweet-fast Row Key design:
	 * 
	 * 12345678901234567890123456789012345678901
	 * DDDDDDDDtYYYYMMDDHHmmssIIIIIIIIIIIIIIIIII
	 * 
	 * DDDDDDDD	- DC/DP Id,							length: 8	offset: 0
	 * t 		- type (T - tweet, R - RSS, etc)	length: 1	offset: 8
	 * YYYY 	- year								length: 4	offset: 9
	 * MM 		- month                             length: 2	offset: 13
	 * DD 		- day                               length: 2 	offset: 15
	 * HH 		- hour                              length: 2	offset: 17
	 * mm 		- minute                            length: 2	offset: 19
	 * ss 		- second                            length: 2 	offset: 21
	 * IIIIIIIIIIIIIIIIII - Tweet ID                length: 18	offset: 23
	 * 
	 * total length: 41 characters
	 * 
	 */
	
	private final int[] dcTweetsTableRowKeyOffset = {0,8};
	private final int[] typeTweetsTableRowKeyOffset = {8,9};
	private final int[] yearTweetsTableRowKeyOffset = {9,13};
	private final int[] monthTweetsTableRowKeyOffset = {13,15};
	private final int[] dayTweetsTableRowKeyOffset = {15,17};
	private final int[] hourTweetsTableRowKeyOffset = {17,19};
	private final int[] minuteTweetsTableRowKeyOffset = {19,21};
	private final int[] secondTweetsTableRowKeyOffset = {21,23};
	private final int[] tweetIdTweetsTableRowKeyOffset = {23,41};
	
	
	private TableName captureTweetsTableName = null;
	private TableName captureDcTableName = null;
	private TableName captureAggregateTableName = null;
	private TableName captureAggregatorTableName = null;
	private TableName captureProcessTableName = null;
	private TableName captureUserAggregateTableName = null;
	private TableName captureBrandTableName = null;
	
	private Configuration conf = HBaseConfiguration.create();
	private Connection connection = null;
	//private HBaseConnectionPool
	
	private final String lastLetter = "Z";

	private String dpPropertyPrefix = "DPPROPERTY";
	private String dataPoolKeywordsPrefix = "DPKEYWORD";
	
	
	private final static Logger LOGGER = Logger.getLogger(
			HBaseDenormProxy.class.getName());
	
	

	public static HBaseDenormProxy getInstance() throws IOException {
		if (instance == null) {
			instance = new HBaseDenormProxy();
		}
		return instance;
	}
	
	public HBaseDenormProxy() throws IOException {
		//init code
		
		LOGGER.info("*********************************************Init");
		// Create a Properties to charge capture configuration
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("capture.properties"));
		tableNamePrefix = properties.getProperty("tableNamePrefix");
		LOGGER.info("Table prefix: " + tableNamePrefix);
		
		
		captureTweetsTable = tableNamePrefix + "tweet_fast";
		captureDcTable = tableNamePrefix + "dc_fast";
		captureAggregateTable = tableNamePrefix + "aggregate_fast";
		captureAggregatorTable = tableNamePrefix + "aggregator_fast";
		captureProcessTable = tableNamePrefix + "process_fast";
		captureUserAggregateTable = tableNamePrefix + "useraggregate_fast";
		captureBrandTable = tableNamePrefix + "brand_fast";
		
		captureTweetsTableName = TableName.valueOf(captureTweetsTable);
		captureDcTableName = TableName.valueOf(captureDcTable);
		captureAggregateTableName = TableName.valueOf(captureAggregateTable);
		captureAggregatorTableName = TableName.valueOf(captureAggregatorTable);
		captureProcessTableName = TableName.valueOf(captureProcessTable);
		captureUserAggregateTableName = TableName.valueOf(captureUserAggregateTable);
		captureBrandTableName = TableName.valueOf(captureBrandTable);
		
		Configuration config = HBaseConfiguration.create();
		config.addResource("hbase-site.xml");
		
		//you can use either "hbase-site.xml" file or
		//specify all the settings here.
		//here we will use hbase-site.xml, together 
		//with some fine-tuning options.
		
		//config.set("hbase.zookeeper.quorum", "localhost");
		config.set("hbase.client.retries.number", "2");
		config.set("zookeeper.session.timeout", "60000");
		config.set("zookeeper.recovery.retry", "2");
	    //config.setLong("hbase.client.scanner.caching", 300000);
		
		//en mi caso esta en el puerto 2182, comentar linea para dejarlo por defecto
		//config.set("hbase.zookeeper.property.clientPort","2182");


		setConf(HBaseConfiguration.create(config));
			    
		try {
			String diagnosticLog = "Starting HBase Client for the first time. Performing some diagnostics...\n";
			
			diagnosticLog += "HBase conf file path: " + config.getResource("") + "\n";
			diagnosticLog += "HBase final configuration params: " + config.toString() + "\n";
			diagnosticLog += "HBase Zookeeper quorum: " + config.get("hbase.zookeeper.quorum") + "\n";
			LOGGER.info(diagnosticLog);
			
			LOGGER.info("Connecting...");
			connection = ConnectionFactory.createConnection(getConf());
			//table = connection.getTable(captureDcTweetsTableName);
			LOGGER.info("Connected. Checking tables...");

			LOGGER.info("Table prefix antes de llamar: " + captureTweetsTableName);
			
			createTable(captureTweetsTableName, new String[]{captureTweetsColFamily});
			createTable(captureDcTableName, new String[]{captureDcColFamily});		
			createTable(captureAggregateTableName, new String[]{captureAggregateColFamily});
			createTable(captureAggregatorTableName, new String[]{captureAggregatorColFamily});
			createTable(captureProcessTableName, new String[]{captureProcessColFamily});
			createTable(captureUserAggregateTableName, new String[]{captureUserAggregateColFamilyReach,captureUserAggregateColFamilyEngagement,captureUserAggregateColFamilyInfluential,captureUserAggregateColFamilyAmbassador});
			createTable(captureBrandTableName, new String[]{captureBrandColFamily});

			LOGGER.info("HBase successfully initialized!");
		} catch (IOException e) {
			//e.printStackTrace();
			//LOGGER.severe("Can't start!");
			LOGGER.log(Level.SEVERE, "Can't initialize HBase!", e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Can't initialize HBase!", e);
			
		} finally {
//			if (table != null) {
//				table.close();
//			}
//		    if (connection != null) {
//		    	connection.close();
//		    }

		}
		
		
		
		
	}
	
	public String generateCrc32Id() {
		UUID uuid = UUID.randomUUID();
		CRC32 crc = new CRC32();
		crc.update(Bytes.toBytes(uuid.toString()));
		//provide necessary padding in order to have always 8 characters.
		String key = String.format("%8s", Long.toHexString(crc.getValue())).replace(" ", "0");
		return key;
	}
	
	public void consistencyCheckAndFix() {
		try {
			
			LOGGER.log(Level.INFO, "DC consistency check & auto fix!");
			DataChannelList dcList = getDataChannels();
			
			LOGGER.log(Level.INFO, "Cleaning up DC!");
			LOGGER.log(Level.INFO, "dc list: " + dcList);
			
			for (DataChannel dc : dcList.getDataChannels()) {
				LOGGER.log(Level.INFO, "dc id" + dc);
				removeDcById(dc.getChannelID());
			}

			DataPoolList dpList = getDataPools();
			for (DataPool dp : dpList.getDataPools()) {
				LOGGER.log(Level.INFO, "dc id" + dp);
				removeDcById(dp.getPoolID());
			}
			
			LOGGER.log(Level.INFO, "Restoring DC!");
			for (DataChannel dc : dcList.getDataChannels()) {
				storeDataChannel(dc, true);
			}
			LOGGER.log(Level.INFO, "Restoring DP!");
			for (DataPool dp : dpList.getDataPools()) {
				storeDataPool(dp, true);
			}

			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Can't initialize HBase!", e);
			
		} finally {
//			if (table != null) {
//				table.close();
//			}
//		    if (connection != null) {
//		    	connection.close();
//		    }

		}
	}
	
	
	
	/**
	 * 
	 * Check if the generated ID is unique among
	 * data channels
	 * 
	 * 
	 * 
	 * @param dcId
	 * @return
	 * @throws IOException
	 */
	private boolean isDcIdUnique(String dcId) throws IOException {
		Table table;

		table = connection.getTable(captureDcTableName);
		//byte[] prefix=Bytes.toBytes(keyPrefix);
		
		String key = "DC" + "|" + dcId;
		
		Scan scan = new Scan(Bytes.toBytes(key));
		
		PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(key));
		scan.setFilter(prefixFilter);
		ResultScanner resultScanner = table.getScanner(scan);
		
		int count = 0;
		for (Result r : resultScanner) {
			count++;
		}
	
		table.close();
		if (count == 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private boolean isDpIdUnique(String dcId) throws IOException {
		Table table;

		table = connection.getTable(captureDcTableName);
		//byte[] prefix=Bytes.toBytes(keyPrefix);
		
		String key = "DP" + "|" + dcId;
		
		Scan scan = new Scan(Bytes.toBytes(key));
		
		PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(key));
		scan.setFilter(prefixFilter);
		ResultScanner resultScanner = table.getScanner(scan);
		
		int count = 0;
		for (Result r : resultScanner) {
			count++;
		}
	
		table.close();
		if (count == 0) {
			return true;
		} else {
			return false;
		}
		
	}
	
	private DataPool createDefaultDataPoolFromDataChannel(DataChannel dc) {
		DataPool dp = new DataPool();
		dp.setPoolID(dc.getChannelID());
		dp.setName(dc.getName());
		dp.setDescription(dc.getDescription());
		dp.setKeywords(Arrays.asList("DEFAULT DATA POOL"));
		
		return dp;
	}

	public void updateDataChannel(DataChannel dc) {
		try {
			//removeDcById(dc.getChannelID());
			storeDataChannel(dc, true);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "updateDataChannel failed!", e);
			LOGGER.severe("Problem updating data channel!");
			LOGGER.severe("TODO this needs some serious fix");
		}
				
	}
	
	public void storeDataChannel(DataChannel dc) throws Exception {
		storeDataChannel(dc, false);

	}

	private long invokeBulkDeleteProtocol(TableName tableName, final Scan scan, final int rowBatchSize, final DeleteType deleteType,
			final Long timeStamp) throws Throwable {
		Table ht = connection.getTable(tableName);
		long noOfDeletedRows = 0L;
		Batch.Call<BulkDeleteService, BulkDeleteResponse> callable = new Batch.Call<BulkDeleteService, BulkDeleteResponse>() {
			ServerRpcController controller = new ServerRpcController();
			BlockingRpcCallback<BulkDeleteResponse> rpcCallback = new BlockingRpcCallback<BulkDeleteResponse>();

			public BulkDeleteResponse call(BulkDeleteService service) throws IOException {
				Builder builder = BulkDeleteRequest.newBuilder();
				builder.setScan(ProtobufUtil.toScan(scan));
				builder.setDeleteType(deleteType);
				builder.setRowBatchSize(rowBatchSize);
				if (timeStamp != null) {
					builder.setTimestamp(timeStamp);
				}
				service.delete(controller, builder.build(), rpcCallback);
				return rpcCallback.get();
			}
		};
		Map<byte[], BulkDeleteResponse> result = ht.coprocessorService(BulkDeleteService.class, scan.getStartRow(), scan.getStopRow(),
				callable);
		for (BulkDeleteResponse response : result.values()) {
			noOfDeletedRows += response.getRowsDeleted();
		}
		ht.close();
		return noOfDeletedRows;
	}
	
	public long wipeDc(String dcId) throws Throwable {
		return invokeBulkDeleteProtocol(
				captureTweetsTableName, 
				new Scan(Bytes.toBytes(dcId), Bytes.toBytes(dcId + "Z")), 
				10, 
				DeleteType.ROW, 
				null);
	}
	
	/**
	 * 
	 * Remove all tweets from data channel.
	 * This method can be applied to data pools. 
	 * 
	 * 
	 * @param dcId
	 * @return Number of deleted rows
	 * @throws Throwable 
	 * 
	 */
	public long deleteTweetsFromDC(String dcId) throws Throwable {
		
		String dcId_end = dcId+"Z";

		long count = 0;
		
		System.out.println("Data channel ID: " + dcId);
		
		long startTime = System.currentTimeMillis();
					    
		Table table = connection.getTable(captureTweetsTableName);

		ArrayList<Delete> deleteList = new ArrayList<Delete>();
		int maxDeletesPerBatch       = 1000;
		Scan scan                    = new Scan( dcId.getBytes(), dcId_end.getBytes());
		scan.setCaching(maxDeletesPerBatch); // Get the scanner results in batches
		ResultScanner scanner        = table.getScanner(scan);
		try {
		    for (Result result : scanner) {
		        deleteList.add(new Delete(result.getRow()));
		        count++;
		        if (deleteList.size() == maxDeletesPerBatch) {
		            // Max deletes reached, flush deletes and clear the list
		            table.delete(deleteList);
		            deleteList.clear();
		        }
		    }
		} finally {
		    scanner.close();
		    if (deleteList.size() > 0) {
		        // Flush remaining deletes
		        table.delete(deleteList);
		    }
		    table.close();
		    		
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Removing tweets took: " + (endTime - startTime) + " milliseconds");
		System.out.println("Count: " + count);
		
		return count;
		
	}
	

	/**
	 * 
	 * Remove all data from data channel without removing the data channel itself.
	 * This method can be applied to data pools. 
	 * 
	 * 
	 * @param dcId
	 * @return Number of deleted rows
	 * @throws Throwable 
	 * @throws ServiceException
	 * 
	 */
	public long wipeDataChannelById(String dcId, final int batchSize) throws ServiceException, Throwable {
		//final Scan scan = new Scan(Bytes.toBytes(dcId), Bytes.toBytes(dcId + "Z"));
		final Scan scan = new Scan();
		// set scan properties(rowkey range, filters, timerange etc).
		Table ht = connection.getTable(captureTweetsTableName);
		long noOfDeletedRows = 0L;
		Batch.Call<BulkDeleteService, BulkDeleteResponse> callable = new Batch.Call<BulkDeleteService, BulkDeleteResponse>() {
			ServerRpcController controller = new ServerRpcController();
			BlockingRpcCallback<BulkDeleteResponse> rpcCallback = new BlockingRpcCallback<BulkDeleteResponse>();

			@Override
			public BulkDeleteResponse call(BulkDeleteService instance) throws IOException {
				Builder builder = BulkDeleteRequest.newBuilder();
				builder.setScan(ProtobufUtil.toScan(scan));
				builder.setDeleteType(DeleteType.ROW);
				builder.setRowBatchSize(batchSize);
				// Set optional timestamp if needed
				//builder.setTimestamp(timeStamp);
				instance.delete(controller, builder.build(), rpcCallback);
				return rpcCallback.get();
			}
		};
		Map<byte[], BulkDeleteResponse> result = ht.coprocessorService(BulkDeleteService.class, scan.getStartRow(), scan.getStopRow(),
				callable);
		for (BulkDeleteResponse response : result.values()) {
			noOfDeletedRows += response.getRowsDeleted();
		}
		
		ht.close();
		return noOfDeletedRows;
	}
	
	/**
	 * 
	 * This is private method for adding a new DataPool.
	 * It recognizes if we *add* or *modify* the Data Pool.
	 * 
	 * 
	 * @param dc
	 * @param update
	 * @throws Exception
	 */
	private void storeDataChannel(DataChannel dc, boolean update) throws Exception {
		try {
			// when we add new data channel, we have to: 
			// 1) generate a new and unique data channel id
			// 2) create default data pool for new data channel 
			
			if (update == false) {
				dc.setChannelID(generateCrc32Id());
				int count = 0;
				boolean unique = false;
				do {
					unique = isDcIdUnique(dc.getChannelID());
					String newId = generateCrc32Id();
					dc.setChannelID(newId);
					count++;
					if (count > 100) {
						//algo pasa
						throw(new Exception("Can't generate unique key!"));
					}
				} while (unique == false);
				
				//handle Data Pools: create mirror DataPool for a new Data Channel.
				DataPool dp = createDefaultDataPoolFromDataChannel(dc);
				storeDataPool(dp, true);
				
				//set DC create date (in UTC) and automatically set update date to the same.
				dc.setCreationDate(getUTCDateString(new Date()));
				dc.setUpdateDate(dc.getCreationDate());
			} else {
				dc.setUpdateDate(getUTCDateString(new Date()));
			}
			
			Table table = connection.getTable(captureDcTableName);
			
			/* create ID for datachannel
  			 *
  			 * RowKey structure:
  			 * DATACHANNELID | ENDCAPTUREDATE | TYPE 
  			 *
  			 * - DataChannelID: UUID
  			 * - EndCaptureDate: YYYYMMDDHHMMSS
  			 * - Type: string (CaptureConstants.SEARCH, STREAM...
  			 *
  			 */
			
			//Map<String, String> endDate = analyseDateInUTC(dc.getEndCaptureDate());
			
			String rowKey = 
					"DC"
					+ "|"
					+ dc.getChannelID() 
					+ "|"
					+ "NONE"
//					+ endDate.get("YEAR")
//					+ endDate.get("MONTH")
//					+ endDate.get("DAY_OF_MONTH")
//					+ endDate.get("HOUR_OF_DAY")
//					+ endDate.get("MINUTE")
//					+ endDate.get("SECOND")
					+ "|"
					+ dc.getType().toUpperCase();
			
			LOGGER.info(update ? "Update DC: " + rowKey : "Add new DC: " + rowKey);
			
			Put put = new Put(Bytes.toBytes(rowKey));
			
			
			//Data Channel ID
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|" + "channelID"), 
					Bytes.toBytes(dc.getChannelID()));

			//Data Channel Name
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "name"), 
					Bytes.toBytes(dc.getName()));
			
			//Data Channel type
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "type"), 
					Bytes.toBytes(dc.getType()));

			//Data Channel description
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "description"), 
					Bytes.toBytes(dc.getDescription() == null ? "" : dc.getDescription()));

			//Data Channel creation date
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "creationDate"), 
					Bytes.toBytes(dc.getCreationDate() == null ? "" : dc.getCreationDate()));

			//Data Channel update date
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "updateDate"), 
					Bytes.toBytes(dc.getUpdateDate()  == null ? "" : dc.getUpdateDate()));

			//Data Channel start capture date
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "startCaptureDate"), 
					Bytes.toBytes(dc.getStartCaptureDate()));

			//Data Channel end capture date
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "endCaptureDate"), 
					Bytes.toBytes(dc.getEndCaptureDate()));

			//Data Channel status
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dcPropertyPrefix + "|"  + "status"), 
					Bytes.toBytes(dc.getStatus() == null ? "" : dc.getStatus()));

			//Data sources
			for (DataSource ds : dc.getDataSources()) {
				//Filter Reddit data source for now
				//if (ds instanceof RedditDataSource) {
				//	continue;
				//}
				
				String dsId = ds.getSourceID();
				
				String dsType = "-";
				
				if (ds instanceof TwitterDataSource) {
					dsType = "T";
				}

				if (ds instanceof RedditDataSource) {
					dsType = "R";
				}
				
				//Data Source ID
				put.addColumn(Bytes.toBytes(captureDcColFamily), 
						Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|sourceID"), 
						Bytes.toBytes(ds.getSourceID()));

				//Data Source Type
				put.addColumn(Bytes.toBytes(captureDcColFamily), 
						Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId  + "|dstype"), 
						Bytes.toBytes(ds.getDstype() == null ? "" : ds.getDstype()));

				//For other subclasses: (in case of TwitterDataSource)
				if (ds instanceof TwitterDataSource) {
					TwitterDataSource tds = (TwitterDataSource) ds;
					//Data Source keywords
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|keywords"), 
							Bytes.toBytes(tds.getKeywords() == null ? "" : tds.getKeywords()));
					
					//last tweet id
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|lastTweetId"), 
							Bytes.toBytes(tds.getLastTweetId()));
					
					//fromLastTweetId (boolean to indicate if we should check lastTweetId) 
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|fromLastTweetId"), 
							Bytes.toBytes(String.valueOf(tds.getFromLastTweetId())));
					
					//chronologicalOrder (boolean to indicate if we should get in chronological order) 
					
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|chronologicalOrder"), 
							Bytes.toBytes(String.valueOf(tds.getChronologicalOrder())));

					//historical limit
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|historicalLimit"), 
							Bytes.toBytes(tds.getHistoricalLimit()));

					//total tweet count
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|totalTweetCount"), 
							Bytes.toBytes(tds.getTotalTweetCount()));


					//data source state
					if (tds.getState() != null) {
						put.addColumn(Bytes.toBytes(captureDcColFamily), 
								Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|state"), 
								Bytes.toBytes(tds.getState().toString()));
						
					}
				}
				
				if (ds instanceof RedditDataSource) {
					RedditDataSource rds = (RedditDataSource) ds;
					//Data Source keywords
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|redditKeywords"), 
							Bytes.toBytes(rds.getKeywords() == null ? "" : rds.getKeywords()));
					//Data Source subreddit
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|subreddits"), 
							Bytes.toBytes(rds.getSubreddits() == null ? "" : rds.getSubreddits().toString()));
					//Data Source redditType
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|redditType"), 
							Bytes.toBytes(rds.getRedditType() == null ? "" : rds.getRedditType()));
					//Data Source post
					put.addColumn(Bytes.toBytes(captureDcColFamily), 
							Bytes.toBytes(dataSourcePrefix + "|" + dsType + "|" + dsId + "|post"), 
							Bytes.toBytes(rds.getPost() == null ? "" : rds.getPost()));
				}
				
			}

			table.put(put);
			table.close();
			
			//createAggregateCounters(dc.getChannelID());
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "DataChannel couldn't be saved :(.", e);
			//e.printStackTrace();
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "DataChannel date parsing error!", e);			
			//e.printStackTrace();
		}
	}
	
	public void updateDataPool(DataPool dp) {
		try {
			storeDataPool(dp, true);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "updateDataPool failed!", e);
			LOGGER.severe("Problem updating data pool!");
			LOGGER.severe("TODO this needs some serious fix");
		}
				
	}
	
	public void storeDataPool(DataPool dp) throws Exception {
		storeDataPool(dp, false);
	}
	
	/**
	 * This is private method for adding a new DataPool.
	 * It recognizes if we *add* or *modify* the Data Pool.
	 * 
	 * 
	 * @param dp
	 * @param update
	 * @throws Exception
	 */
	private void storeDataPool(DataPool dp, boolean update) throws Exception {
		try {
			if (update == false) {
				dp.setPoolID(generateCrc32Id());
				int count = 0;
				boolean unique = false;
				do {
					unique = isDpIdUnique(dp.getPoolID());
					String newId = generateCrc32Id();
					dp.setPoolID(newId);
					count++;
					if (count > 100) {
						//algo pasa
						throw(new Exception("Can't generate unique key!"));
					}
				} while (unique == false);
				
			}
			
			Table table = connection.getTable(captureDcTableName);
			
			String rowKey = 
					"DP"
					+ "|"
					+ dp.getPoolID() 
					;
			
			LOGGER.info("Adding new DP with row key: " + rowKey);
			
			Map<String, Object> m = getFieldsGenericForClass(DataPool.class, dp);
			List<String> list = Arrays.asList(new String[] {"poolID", "name", "description", "lang"});
			
			Put put = new Put(Bytes.toBytes(rowKey));
			
			for (String field : m.keySet()) {
				if (list.contains(field) == true) {
					//System.out.println(" * " + field + " " );
					if (m.get(field) != null) {
						put.addColumn(Bytes.toBytes(captureDcColFamily), 
								Bytes.toBytes(dpPropertyPrefix + "|" + field), 
								Bytes.toBytes(m.get(field).toString()));						
					}
				}
			}
			
			//add dcsAllowed
			List<String> dcIDsAllowed = new ArrayList<String>();
			for (DataChannel dc : dp.getDcsAllowed()) {
				dcIDsAllowed.add(dc.getChannelID());
			}
			put.addColumn(Bytes.toBytes(captureDcColFamily), 
					Bytes.toBytes(dpPropertyPrefix + "|" + "dcsAllowed"), 
					Bytes.toBytes(dcIDsAllowed.toString()));
			
			
			//remove old keywords
			Delete del = new Delete(Bytes.toBytes(rowKey));
			
			Scan scan = new Scan(Bytes.toBytes(rowKey));
		    ResultScanner resultScanner = table.getScanner(scan);
		    
		    for (Result r : resultScanner) {	
		    	NavigableMap<byte[], byte[]> columnKeywords = r.getFamilyMap(Bytes.toBytes(captureDcColFamily));
		    	for (Entry<byte[], byte[]> a : columnKeywords.entrySet()) {
					if (new String(a.getKey()).startsWith(dataPoolKeywordsPrefix)) {
						del.addColumns(Bytes.toBytes(captureDcColFamily), 
								Bytes.toBytes(new String(a.getKey())));
					}
		    	}
		    }
		    
		    table.delete(del);
			
		    
			//add new keywords
			int count = 0; 
			for (String keyword : dp.getKeywords()) {
				count ++;
				put.addColumn(Bytes.toBytes(captureDcColFamily), 
						Bytes.toBytes(dataPoolKeywordsPrefix + "|" + count), 
						Bytes.toBytes(keyword));
			}
						
			table.put(put);
			
			table.close();
			
			//createAggregateCounters(dc.getChannelID());
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "DataChannel couldn't be saved :(.", e);
			//e.printStackTrace();
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "DataChannel date parsing error!", e);			
			//e.printStackTrace();
		}
	}
	private DataPool getDataPoolFromRawHBaseResult(Result r) throws InstantiationException, SecurityException, ParseException {
		
		DataPool dp = new DataPool();
		
		String[] dcRowKeyParts = new String(r.getRow()).split("\\|");
		if (dcRowKeyParts != null && dcRowKeyParts.length == 2) {
//			DataChannel dc = dataChannelMap.get(dcRowKeyParts[dcRowKeyIdPart]);
//			if (dc == null) {
//				dc = new DataChannel();
//				dataChannelMap.put(new String(r.getRow()), dc);
//			}		
			
			List<String> keywords = new ArrayList<String>();
			
			List<String> dcIDsAllowed = new ArrayList<String>();
								
			NavigableMap<byte[], byte[]> map = r.getFamilyMap(Bytes.toBytes(captureDcColFamily));

			List<String> allowedList = Arrays.asList(new String[] {"poolID", "name", "description", "lang"});
			Map<String, byte[]> fieldMap = new HashMap<String, byte[]>();

			
			for (Entry<byte[], byte[]> a : map.entrySet()) {
				//System.out.println(new String(a.getKey()) + " -> " + new String(a.getValue()));
				
				//Iterate columns inside data channel.
				//Detect Data Channel properties and Data Source properties:
				if (new String(a.getKey()).startsWith(dpPropertyPrefix)) {
					//this is Data Channel property
					
					String[] dcColNameParts = new String(a.getKey()).split("\\|");
					
					// detect and process DataPool fields
					// use only predefined list of fields (Strings)
					// the list will be treated separately
					
					if (dcColNameParts != null 
							&& dcColNameParts.length == dpColNameSize
							&& dcColNameParts[dpFieldNameKeyPart] != null) {
						
						
						
						if (allowedList.contains(dcColNameParts[dpFieldNameKeyPart])) {
							//System.out.println(dcColNameParts[dpFieldNameKeyPart] + " -> " + new String(a.getValue()));
							fieldMap.put(dcColNameParts[dpFieldNameKeyPart], a.getValue());
							
						} 
						else if ("dcsAllowed".equals(dcColNameParts[dpFieldNameKeyPart])) {
							String sDcIDsAllowed = Bytes.toString(a.getValue());
							sDcIDsAllowed = sDcIDsAllowed.replace("[", "").replace("]", "").replace(" ", "");
							for (String dcID: sDcIDsAllowed.split(",")){
								if (!"".equals(dcID))
									dcIDsAllowed.add(dcID);
							}
						}
						
					}
				}
				
				if (new String(a.getKey()).startsWith(dataPoolKeywordsPrefix)) {
					//this is Data Source property
					
					String[] dpColNameParts = new String(a.getKey()).split("\\|");
					
					
					// detect and process DataSource fields
					if (dpColNameParts != null && dpColNameParts.length == dpColNameSize) {
						
						
						if (dpColNameParts[dpFieldNameKeyPart] != null) {
							keywords.add(new String(a.getValue()));
						}

					}
				}
			}

			populateFieldsGenericForClass(fieldMap, DataPool.class, dp);
			
			List<DataChannel> dcsAllowed = new ArrayList<DataChannel>();
			for (String dcID : dcIDsAllowed){
				DataChannel dc = new DataChannel();
				dc.setChannelID(dcID);
				dcsAllowed.add(dc);
			}
			dp.setDcsAllowed(dcsAllowed);	
			
			dp.setKeywords(keywords);
			
			// put dcsAllowed/keywords/lang initialized in case no value.
			if (dp.getLang() == null) dp.setLang("");

		} else {
			//in case the row doesn't contain data channel, return null.
			dp = null;
		}
		
		return dp;

		
	}
	
	private DataChannel getDataChannelFromRawHBaseResult(Result r) {
		//every row is a data channel.
		//inside we can find data channel properties & data sources
		//keyRow identified datachannel & dc id and type:
		// The structure is the following:
		// DataChannelID|DC Capture End Date|Type
		// Inside DC, the column names identify properties of DC
		// and the list of Data Sources it contains.
		// DC properties columns have the following structure:
		// PROP|propertyName
		//
		// data sources are identified by the name of the column:
		// DATASOURCE|TYPE|DSID|property_name
		
		//System.out.println(">");
		//System.out.println(" " + new String(r.getRow()));
		
		DataChannel dc = new DataChannel();
		
		String[] dcRowKeyParts = new String(r.getRow()).split("\\|");
		if (dcRowKeyParts != null && dcRowKeyParts.length == 4) {
//			DataChannel dc = dataChannelMap.get(dcRowKeyParts[dcRowKeyIdPart]);
//			if (dc == null) {
//				dc = new DataChannel();
//				dataChannelMap.put(new String(r.getRow()), dc);
//			}		
			
			Map<String, DataSource> dataSourceMap = new HashMap<>();
								
			NavigableMap<byte[], byte[]> map = r.getFamilyMap(Bytes.toBytes(captureDcColFamily));
			
			for (Entry<byte[], byte[]> a : map.entrySet()) {
				//System.out.println(new String(a.getKey()) + " -> " + new String(a.getValue()));
				
				//Iterate columns inside data channel.
				//Detect Data Channel properties and Data Source properties:
				if (new String(a.getKey()).startsWith(dcPropertyPrefix)) {
					//this is Data Channel property

					String[] dcColNameParts = new String(a.getKey()).split("\\|");
					
					// detect and process DataChannel fields
					if (dcColNameParts != null 
							&& dcColNameParts.length == dcColNameSize
							&& dcColNameParts[dcFieldNameKeyPart] != null) {
						
						
						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("channelID")) {
							dc.setChannelID(new String(a.getValue()));
						}

						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("creationDate")) {
							dc.setCreationDate(new String(a.getValue()));
						}
						
						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("description")) {
							dc.setDescription(new String(a.getValue()));
						}

						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("endCaptureDate")) {
							dc.setEndCaptureDate(new String(a.getValue()));
						}
						
						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("name")) {
							dc.setName(new String(a.getValue()));
						}
						
						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("startCaptureDate")) {
							dc.setStartCaptureDate(new String(a.getValue()));
						}

						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("status")) {
							dc.setStatus(new String(a.getValue()));
						}
						
						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("type")) {
							dc.setType(new String(a.getValue()));
						}

						if (dcColNameParts[dcFieldNameKeyPart].equalsIgnoreCase("updateDate")) {
							dc.setUpdateDate(new String(a.getValue()));
						}
						

					}
				}
				
				if (new String(a.getKey()).startsWith(dataSourcePrefix)) {
					//this is Data Source property
					
					String[] dsColNameParts = new String(a.getKey()).split("\\|");
					
					// detect and process DataSource fields
					if (dsColNameParts != null && dsColNameParts.length == dsColNameSize) {
						
						DataSource ds = dataSourceMap.get(dsColNameParts[dsIdKeyPart]);
						if (ds == null) {
							//check the type of Data Source
							if (dsColNameParts[dsTypeKeyPart].equalsIgnoreCase("T")) {
								ds = new TwitterDataSource();
								dataSourceMap.put(dsColNameParts[dsIdKeyPart], ds);
							} 
							else if (dsColNameParts[dsTypeKeyPart].equalsIgnoreCase("R")) {
								ds = new RedditDataSource();
								dataSourceMap.put(dsColNameParts[dsIdKeyPart], ds);
							} 
							else {
								ds = new DataSource();
								dataSourceMap.put(dsColNameParts[dsIdKeyPart], ds);
							}
							
						}
						
						ds = dataSourceMap.get(dsColNameParts[dsIdKeyPart]);

						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("dstype")) {
							ds.setDstype(new String(a.getValue()));
						}

						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("sourceID")) {
							ds.setSourceID(new String(a.getValue()));
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("keywords")) {
							((TwitterDataSource) ds).setKeywords(new String(a.getValue()));
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("lastTweetId")) {
							((TwitterDataSource) ds).setLastTweetId(ByteBuffer.wrap(a.getValue()).getLong());
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("fromLastTweetId")) {
							((TwitterDataSource) ds).setFromLastTweetId(new Boolean(new String(a.getValue())));
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("chronologicalOrder")) {
							((TwitterDataSource) ds).setChronologicalOrder(new Boolean(new String(a.getValue())));
						}

						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("historicalLimit")) {
							((TwitterDataSource) ds).setHistoricalLimit(ByteBuffer.wrap(a.getValue()).getLong());
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("totalTweetCount")) {
							((TwitterDataSource) ds).setTotalTweetCount(ByteBuffer.wrap(a.getValue()).getLong());
						}
						
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("state")) {
							try {
								((TwitterDataSource) ds).setState(DataChannelState.fromString(new String(a.getValue())));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						//reddit fields
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("redditKeywords")) {
							((RedditDataSource) ds).setKeywords(new String(a.getValue()));
						}
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("redditType")) {
							((RedditDataSource) ds).setRedditType(new String(a.getValue()));
						}
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("subreddits")) {
							String data = new String(a.getValue());
							data = data.replace("[","").replace("]","");
							List<String> subreddits = Arrays.asList(data.split(","));
							((RedditDataSource) ds).setSubreddits(subreddits);
						}
						if (dsColNameParts[dsFieldNameKeyPart] != null && dsColNameParts[dsFieldNameKeyPart].equalsIgnoreCase("post")) {
							((RedditDataSource) ds).setPost(new String(a.getValue()));
						}

					}
				}
			}

			List<DataSource> dsList = new ArrayList<DataSource>();
			for (Entry<String, DataSource> d : dataSourceMap.entrySet()) {
				dsList.add(d.getValue());
			}
			dc.setDataSources(dsList);	
		} else {
			//in case the row doesn't contain data channel, return null.
			dc = null;
		}
		
		return dc;
	}
	
	/**
	 * 
	 * Method for retrieving all Data Channels from HBase. 
	 * 
	 * 
	 * @return DataChannelList containing all found Data Channels. 
	 */
	public DataChannelList getDataChannels() {
	
		try {
			int count = 0;
			Table table = connection.getTable(captureDcTableName);
			
			//byte[] prefix=Bytes.toBytes(keyPrefix);
			
			Scan scan = new Scan();
			
			//PrefixFilter prefixFilter = new PrefixFilter(prefix);
			//scan.setFilter(prefixFilter);
			//ColumnRangeFilter crf = new ColumnRangeFilter(Bytes.toBytes("PROP"), true, Bytes.toBytes("PROZ"), true);
			//scan.setFilter(crf);
			ResultScanner resultScanner = table.getScanner(scan);

			Map<String, DataChannel> dataChannelMap = new HashMap<>();

			
			for (Result r : resultScanner) {		
				DataChannel dc = getDataChannelFromRawHBaseResult(r);
				if (dc != null && dc.getChannelID() != null) {
					dataChannelMap.put(new String(r.getRow()), dc);	
				}
				
				
			}
			
			DataChannelList dcl = new DataChannelList();
			for (Entry<String, DataChannel> d : dataChannelMap.entrySet()) {
				dcl.addDataChannel(d.getValue());
			}
			
			table.close();
			return dcl;
			
		} catch (IOException e) {
			//LOGGER.severe("Can't retrieve DataChannel list.");
			//e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);

			return null;
		}
	}
	
	/**
	 * 
	 * Method for retrieving all Data Pools.
	 * 
	 * 
	 * @return List of DataPool objects.
	 */
	public DataPoolList getDataPools() {
		
		try {
			int count = 0;
			Table table = connection.getTable(captureDcTableName);
			
			//byte[] prefix=Bytes.toBytes(keyPrefix);
			
			Scan scan = new Scan();
			
			//PrefixFilter prefixFilter = new PrefixFilter(prefix);
			//scan.setFilter(prefixFilter);
			//ColumnRangeFilter crf = new ColumnRangeFilter(Bytes.toBytes("PROP"), true, Bytes.toBytes("PROZ"), true);
			//scan.setFilter(crf);
			ResultScanner resultScanner = table.getScanner(scan);
			
			List<DataPool> dataPools = new ArrayList<>();
			
			for (Result r : resultScanner) {		
				DataPool dp = getDataPoolFromRawHBaseResult(r);
				if (dp != null) {
					dataPools.add(dp);	
				}
			}
			DataPoolList dcl = new DataPoolList();
			dcl.setDataPools(dataPools);
			
			table.close();
			return dcl;
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (InstantiationException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (SecurityException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		}
	}

	/**
	 * 
	 * This is the preferred method for retrieving Data Pools.
	 * It provides limiting the number of results, and pagination.
	 *  
	 * 
	 * @param fromKey is used to give a hint for pagination. Next page
	 * will start from the "fromKey" parameter
	 * @param numResults limit the number of results given
	 * @return returns the list of DataPool objects.
	 */
	public DataPoolList getDataPools(String fromKey, int numResults) {
		
		try {
			int count = 0;
			Table table = connection.getTable(captureDcTableName);
			
			
			Scan scan;
			if (fromKey != null && fromKey.isEmpty() == false) {
				fromKey = "DP" + "|" + fromKey;
				scan = new Scan(Bytes.toBytes(fromKey));	
			} else {
				fromKey = "DP" + "|";
				scan = new Scan();
			}
			
			FilterList filterList = new FilterList();
			
			if (fromKey != null) {
				byte[] prefix = Bytes.toBytes(fromKey);
				Filter prefixFilter = new PrefixFilter(prefix);
				filterList.addFilter(prefixFilter);
			}
						
			Filter countFilter = new PageFilter(numResults);
			filterList.addFilter(countFilter);

			scan.setFilter(filterList);
			
			
			ResultScanner resultScanner = table.getScanner(scan);

			List<DataPool> dataPools = new ArrayList<>();

			
			for (Result r : resultScanner) {		
				DataPool dp = getDataPoolFromRawHBaseResult(r);
				if (dp != null) {
					dataPools.add(dp);	
				}
			}
			DataPoolList dcl = new DataPoolList();
			dcl.setDataPools(dataPools);
			
			table.close();

			
			return dcl;
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (InstantiationException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (SecurityException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		} catch (ParseException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			return null;
		}

	}

	/**
	 * 
	 * This is the preferred method for retrieving Data Channels.
	 * It provides limiting the number of results, and pagination.
	 *  
	 * 
	 * @param fromKey is used to give a hint for pagination. Next page
	 * will start from the "fromKey" parameter
	 * @param numResults limit the number of results given
	 * @return returns the DataChannelList object.
	 */
	public DataChannelList getDataChannels(String fromKey, int numResults) {
		
		try {
			int count = 0;
			Table table = connection.getTable(captureDcTableName);
			
			
			Scan scan;
			if (fromKey != null && fromKey.isEmpty() == false) {
				fromKey = "DC" + "|" + fromKey;
				scan = new Scan(Bytes.toBytes(fromKey));	
			} else {
				fromKey = "DC" + "|";
				scan = new Scan();
			}
			
			FilterList filterList = new FilterList();
			
			if (fromKey != null) {
				byte[] prefix = Bytes.toBytes(fromKey);
				Filter prefixFilter = new PrefixFilter(prefix);
				filterList.addFilter(prefixFilter);
			}
						
			Filter countFilter = new PageFilter(numResults);
			filterList.addFilter(countFilter);

			scan.setFilter(filterList);
			
			
			//ColumnRangeFilter crf = new ColumnRangeFilter(Bytes.toBytes("PROP"), true, Bytes.toBytes("PROZ"), true);
			//scan.setFilter(crf);
			ResultScanner resultScanner = table.getScanner(scan);

			Map<String, DataChannel> dataChannelMap = new HashMap<>();

			
			for (Result r : resultScanner) {		
				DataChannel dc = getDataChannelFromRawHBaseResult(r);
				if (dc != null && dc.getChannelID() != null) {
					dataChannelMap.put(new String(r.getRow()), dc);
				}
			}
			
			table.close();

			DataChannelList dcl = new DataChannelList();
			for (Entry<String, DataChannel> d : dataChannelMap.entrySet()) {
				dcl.addDataChannel(d.getValue());
			}
			
			return dcl;
			
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			//e.printStackTrace();
			return null;
		}

	}
	
	
	public DataChannel getDcById(String dcId) {
		try {
			Table table = connection.getTable(captureDcTableName);
			
			String key = "DC" + "|" + dcId;
			
			byte[] prefix=Bytes.toBytes(key);
			
			Scan scan = new Scan(prefix);
			PrefixFilter prefixFilter = new PrefixFilter(prefix);
			scan.setFilter(prefixFilter);
			ResultScanner resultScanner = table.getScanner(scan);
			
			Map<String, DataChannel> dataChannelMap = new HashMap<>();
			
			for (Result r : resultScanner) {		
				DataChannel dc = getDataChannelFromRawHBaseResult(r);
				dataChannelMap.put(new String(r.getRow()), dc);
				
			}
			
			DataChannelList dcl = new DataChannelList();
			for (Entry<String, DataChannel> d : dataChannelMap.entrySet()) {
				dcl.addDataChannel(d.getValue());
			}
			
			table.close();
			
			if (dcl.getDataChannels() != null && dcl.getDataChannels().size() > 0) {
				return dcl.getDataChannels().get(0);				
			} else {
				return null;
			}
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);
			//e.printStackTrace();
			return null;
		}

	}
	
	public DataPool getDpById(String dpId) {
		try {
			Table table = connection.getTable(captureDcTableName);
			
			String key = "DP" + "|" + dpId;
			
			byte[] prefix=Bytes.toBytes(key);
			
			Scan scan = new Scan(prefix);
			PrefixFilter prefixFilter = new PrefixFilter(prefix);
			scan.setFilter(prefixFilter);
			ResultScanner resultScanner = table.getScanner(scan);
			
			List<DataPool> dataPools = new ArrayList<>();
			
			for (Result r : resultScanner) {		
				DataPool dp = getDataPoolFromRawHBaseResult(r);
				dataPools.add(dp);
				
			}
			
			
			table.close();
			
			if (dataPools.size() > 0) {
				return dataPools.get(0);				
			} else {
				return null;
			}
						
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't retrieve DataPool list.", e);
			//e.printStackTrace();
			return null;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't retrieve DataPool list.", e);
			//e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't retrieve DataPool list.", e);			
			//e.printStackTrace();
			return null;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't retrieve DataPool list.", e);			
			//e.printStackTrace();
			return null;
		}

	}

	public void removeDcById(String dcId) {
		try {
			Table table = connection.getTable(captureDcTableName);
			
			String key = "DC" + "|" + dcId;
			
			byte[] prefix=Bytes.toBytes(key);
			
			Scan scan = new Scan(prefix);
			PrefixFilter prefixFilter = new PrefixFilter(prefix);
			scan.setFilter(prefixFilter);
			ResultScanner resultScanner = table.getScanner(scan);
			
			Map<String, DataChannel> dataChannelMap = new HashMap<>();
			
			for (Result r : resultScanner) {		
				LOGGER.info("Removing DC: " + new String(r.getRow()));
				Delete delete = new Delete(r.getRow());
				
				table.delete(delete);
			}
			
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't remove DC: " + dcId, e);
			e.printStackTrace();

		}
	}
	
	
	public void removeDpById(String dpId) {
		try {
			Table table = connection.getTable(captureDcTableName);
			
			String key = "DP" + "|" + dpId;
			
			byte[] prefix=Bytes.toBytes(key);
			
			Scan scan = new Scan(prefix);
			PrefixFilter prefixFilter = new PrefixFilter(prefix);
			scan.setFilter(prefixFilter);
			ResultScanner resultScanner = table.getScanner(scan);
			
			Map<String, DataChannel> dataChannelMap = new HashMap<>();
			
			for (Result r : resultScanner) {		
				LOGGER.info("Removing DP: " + new String(r.getRow()));
				Delete delete = new Delete(r.getRow());
				
				table.delete(delete);
			}
			
			table.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Can't remove DC: " + dpId, e);
			e.printStackTrace();

		}
	}
	/**
	 * 
	 * Method for getting volume aggregates from data channel (or data pool)
	 * 
	 * 
	 * 
	 * @param dcId Id of data channel (or data pool) to take data from
	 * @param type type od fata source (Tweet, RSS) CURRENTLY ONLY TWEETS
	 * @param startDate Start Date in ISO format: "yyyy-MM-dd'T'HH:mm:ssXXX", time zone: UTC!
	 * @param endDate End Date in ISO format: "yyyy-MM-dd'T'HH:mm:ssXXX", time zone: UTC!
	 * @param aggregation Aggregation type of class AggregateType (eg. Yearly, Monthly, Daily, etc...)
	 * @return Returns map of Date -> Count
	 * @throws IOException
	 * @throws ParseException
	 */
	public Map<String, Integer> getVolumeFromDC(String dcId, String type, String startDate, String endDate, AggregateType aggregation) throws IOException, ParseException {
		int count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix=Bytes.toBytes(dcId);
		
		Scan scan = new Scan(prefix);
		List<RowRange> ranges = new ArrayList<RowRange>();
		
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		Date dStartDate = dt.parse(startDate);
		Date dEndDate = dt.parse(endDate);
		
		Map<String, String> startDateComponents = analyseDateInUTC(dStartDate);
		Map<String, String> endDateComponents = analyseDateInUTC(dEndDate);
		
		
		String startDateYear = startDateComponents.get("YEAR");
		String startDateMonth = startDateComponents.get("MONTH"); 
		String startDateDay = startDateComponents.get("DAY_OF_MONTH");
		String startDateHours = startDateComponents.get("HOUR_OF_DAY");
		String startDateMinutes = startDateComponents.get("MINUTE");
		String startDateSeconds = startDateComponents.get("SECOND");
		
		String endDateYear = endDateComponents.get("YEAR");        
		String endDateMonth = endDateComponents.get("MONTH");      
		String endDateDay = endDateComponents.get("DAY_OF_MONTH"); 
		String endDateHours = endDateComponents.get("HOUR_OF_DAY");
		String endDateMinutes = endDateComponents.get("MINUTE");   
		String endDateSeconds = endDateComponents.get("SECOND");   
		
		Map<String, Integer> aggregates = new TreeMap<String, Integer>();
		
		if (aggregation == AggregateType.MONTHLY) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth;
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				//String second = new String(r.getRow()).substring(51,53);
				String yearMonth = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						monthTweetsTableRowKeyOffset[1]);
				
				if (aggregates.containsKey(yearMonth) == true) {
					aggregates.put(yearMonth, aggregates.get(yearMonth) + 1);
				} else {
					aggregates.put(yearMonth, 1);
				}				
				
				count++;
			}
			
			LOGGER.info("Found " + count);
		}
		
		
		if (aggregation == AggregateType.DAILY) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth
					+ startDateDay;
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ endDateDay
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				String timeSpan = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						dayTweetsTableRowKeyOffset[1]);
				
				if (aggregates.containsKey(timeSpan) == true) {
					aggregates.put(timeSpan, aggregates.get(timeSpan) + 1);
				} else {
					aggregates.put(timeSpan, 1);
				}
								
				count++;
			}
			
			LOGGER.info("Found " + count);
		}

		if (aggregation == AggregateType.HOURLY) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth
					+ startDateDay
					+ startDateHours;
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ endDateDay
					+ endDateHours
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				String timeSpan = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						hourTweetsTableRowKeyOffset[1]);
				
				if (aggregates.containsKey(timeSpan) == true) {
					aggregates.put(timeSpan, aggregates.get(timeSpan) + 1);
				} else {
					aggregates.put(timeSpan, 1);
				}
				
				
				count++;
			}
			
			LOGGER.info("Found " + count);
		}
		
		if (aggregation == AggregateType.TEN_MINUTES) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth
					+ startDateDay
					+ startDateHours
					+ startDateMinutes.substring(0, 1);
			
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ endDateDay
					+ endDateHours
					+ endDateMinutes.substring(0, 1)
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				String timeSpan = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						minuteTweetsTableRowKeyOffset[1]-1);
				
				if (aggregates.containsKey(timeSpan) == true) {
					aggregates.put(timeSpan, aggregates.get(timeSpan) + 1);
				} else {
					aggregates.put(timeSpan, 1);
				}				
				
				count++;
			}
			
			LOGGER.info("Found " + count);
		}
		
		if (aggregation == AggregateType.PER_MINUTE) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth
					+ startDateDay
					+ startDateHours
					+ startDateMinutes;
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ endDateDay
					+ endDateHours
					+ endDateMinutes
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				String timeSpan = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						minuteTweetsTableRowKeyOffset[1]);
				
				if (aggregates.containsKey(timeSpan) == true) {
					aggregates.put(timeSpan, aggregates.get(timeSpan) + 1);
				} else {
					aggregates.put(timeSpan, 1);
				}				
				
				count++;
			}
			
			LOGGER.info("Found " + count);
		}
		
		if (aggregation == AggregateType.PER_SECOND) {
			String startRowKey = dcId 	
					+ "T" 								
					+ startDateYear 							
					+ startDateMonth
					+ startDateDay
					+ startDateHours
					+ startDateMinutes
					+ startDateSeconds;
					
			String endRowKey = dcId 						
					+ "T" 								
					+ endDateYear 								
					+ endDateMonth
					+ endDateDay
					+ endDateHours
					+ endDateMinutes
					+ endDateSeconds
					+ lastLetter;				// the last letter is to include all rows after the startDate		 
			
			ranges.add(new RowRange(
					Bytes.toBytes(startRowKey), 
					true, 
					Bytes.toBytes(endRowKey),
					true));
			
			Filter filter = new MultiRowRangeFilter(ranges);
			scan.setFilter(filter);
			//scan = new Scan(Bytes.toBytes(startRowKey), Bytes.toBytes(endRowKey));
			
			
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {

				String timeSpan = new String(r.getRow()).substring(
						yearTweetsTableRowKeyOffset[0],
						secondTweetsTableRowKeyOffset[1]);
				
				if (aggregates.containsKey(timeSpan) == true) {
					aggregates.put(timeSpan, aggregates.get(timeSpan) + 1);
				} else {
					aggregates.put(timeSpan, 1);
				}				
				
				count++;
			}
			
			LOGGER.info("Found " + count);
		}
		
		return aggregates;
	}
	
	
//	DcTweetList getTweetsFromDC(String dcId, int page,
//			int numResults) {
//		
//		try {
//			int count = 0;
//			Table table = connection.getTable(captureTweetsTableName);
//			byte[] prefix=Bytes.toBytes(dcId);
//			
//			Scan scan = new Scan(prefix);
//			PrefixFilter prefixFilter = new PrefixFilter(prefix);
//			Filter filter = new PageFilter(page);
//			scan.setFilter(prefixFilter);
//			scan.setFilter(filter);
//			ResultScanner resultScanner = table.getScanner(scan);
//			
//			List<Tweet> tweetList = new ArrayList<Tweet>();
//			for (Result r : resultScanner) {
//				count++;
//				Tweet t = getTweetFromRawHBaseResult(r);
//				tweetList.add(t);
//			}
//
//			DcTweetList dctl = new DcTweetList();
//			dctl.setTweets(tweetList);
//
//			
//			return dctl;
//		} catch (IOException e) {
//			LOGGER.severe("Can't retrieve all tweets from Data Channel!");
//			e.printStackTrace();
//			return null;
//		}
//		
//		
//	}
	
	


	private Map<String, byte[]> getFieldsFromTweet(Tweet t) throws InstantiationException {
		
		
		
		Map<String, byte[]> objectFields = findAndExecuteAnnotatedMethodsForClass(Tweet.class, javax.xml.bind.annotation.XmlElement.class, "name", t);
//		for (byte[] b : objectFields.keySet()) {
//			System.out.println(b + " " + objectFields.get(b));
//		}
			
		return objectFields;
	}
	
	
	public void addTweetsToDCBatch(List<Tweet> tweets, String dcId) {

		try {
			
			throw new Exception("Not available in the open source version.");

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Can't add tweets batch to DC!", e);
		}
						
	}
	
	public boolean createTable(TableName tableName, String[] families) throws IOException {
		boolean tableAlreadyExists = false;
		Admin admin = connection.getAdmin();
		
		if (admin.tableExists(tableName)) {
			LOGGER.info("Table " + tableName + " already exists. This is good!");
		} else {
			Table table = connection.getTable(tableName);	
			HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
			
			for (int i = 0; i < families.length; i++) {
				tableDescriptor.addFamily(new HColumnDescriptor(families[i]));
			}
			admin.createTable(tableDescriptor);
			LOGGER.info("Creating table " + tableName + " ok.");
			table.close();
		}
		
		admin.close();
		return tableAlreadyExists;
	}
	
	@Deprecated
	public void createAggregateCounters(String dcId) throws IOException {
		
		//create few counters:
		for (int i=1; i<=5; i++) {
			Table table = connection.getTable(captureAggregateTableName);	
			Put put = new Put(Bytes.toBytes(dcId + "|" + "COUNTER" + "|" + i));
			put.addColumn(Bytes.toBytes(captureAggregateColFamily), 
					Bytes.toBytes("counter"), 
					Bytes.toBytes(0L));
			table.put(put);
			table.close();	
		}
	}
	
	
	public List<Tweet> generateTweets(int count) {
		String text = "This is some text of the tweet"; 
		//UUID dcId = UUID.randomUUID();
		
		//System.out.println("DC UUID: " + dataChannel);
		List<Tweet> list = new ArrayList<Tweet>();
		
		for (int i=0; i<count; i++) {
			Tweet t = new Tweet();
			t.setCreatedAt(new Date());
			t.setText(text + " " + i);
			t.setId(String.valueOf(randInt(10_000_000, 99_999_999)) + String.valueOf(randInt(10_000_000, 99_999_999)));
			t.setUserID(String.valueOf(randInt(1_000_000, 9_999_999)));
			t.setUserName("Name Surname" + randInt(100, 999));
			t.setUserScreenName("Name" + randInt(100, 999));
			list.add(t);
			
//			try {
//				Thread.sleep(randInt(5, 20));
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		return list;
	}
	
	private Map<String, byte[]> findAndExecuteAnnotatedMethodsForClass(
			Class<?> lookedUpClass, 
			Class<? extends Annotation> annotationClass,
			String annotationMethodName,
			Object obj) throws InstantiationException {
		
		Map<String, byte[]> result = new HashMap<>();
		
		Method[] methods = lookedUpClass.getMethods();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		//System.out.println("\nClass annotations:");
		for (Annotation annotation : lookedUpClass.getAnnotations()) {
			Class<? extends Annotation> type = annotation.annotationType();
			//System.out.println("Annotation: " + type);
			for (Method m : type.getDeclaredMethods()) {
				try {
					value = m.invoke(annotation, (Object[]) null);
					//System.out.println(m + " -> " + value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}
		//System.out.println("\nMethod annotations:");
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				annotatedMethods.add(method);
				
				try {
					
					for (Annotation annotation : method.getAnnotations()) {
						Class<? extends Annotation> type = annotation.annotationType();
						for (Method m : type.getDeclaredMethods()) {
							if (m.getName().toString().equalsIgnoreCase(annotationMethodName)) {
								Class<?> rType = method.getReturnType();
								value = m.invoke(annotation, (Object[]) null);
								//System.out.println(method + " :: " + m + " -> " + value);
								
								Object response = method.invoke(obj, (Object[]) null);
								rType.cast(response);
								//System.out.println(" >> " + rType.cast(response));

								//Be careful here. We assume that all fields are mappable to String,
								//which they are. In case a new field is added (such as list),
								//this might break the following code!
								if (rType.cast(response) instanceof String) {
									result.put((String)value, Bytes.toBytes((String)rType.cast(response)));
								}
								if (rType.cast(response) instanceof Date) {
									Date d = (Date) rType.cast(response);
									SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
									result.put((String)value, Bytes.toBytes(dt.format(d)));
								}
								if (rType.cast(response) instanceof Integer) {
									Integer i = (Integer) rType.cast(response);									
									result.put((String)value, Bytes.toBytes(i));
								}
								if (rType.cast(response) instanceof Double) {
									Double d = (Double) rType.cast(response);									
									result.put((String)value, Bytes.toBytes(d));
								}
								if (rType.cast(response) instanceof Long) {
									Long l = (Long) rType.cast(response);									
									result.put((String)value, Bytes.toBytes(l));
								}
							}
						}
					}
					
					//Annotation a = method.getAnnotation(annotationClass);
					//value = method.invoke(a, (Object[]) null);
					
				} catch (IllegalAccessException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);

				} catch (IllegalArgumentException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);

				} catch (InvocationTargetException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);

				}
				
			}
		}
		return result;
	}


	
	
	private Object populateFieldsWithAnnotatedMethodsForClass(
			Map<String, byte[]> fields,
			Class<?> lookedUpClass, 
			Class<? extends Annotation> annotationClass,
			String annotationMethodName,
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Method[] methods = lookedUpClass.getMethods();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Method method : methods) {
			if (method.isAnnotationPresent(annotationClass)) {
				annotatedMethods.add(method);
				
				try {
					
					for (Annotation annotation : method.getAnnotations()) {
						Class<? extends Annotation> type = annotation.annotationType();
						for (Method m : type.getDeclaredMethods()) {
							if (m.getName().toString().equalsIgnoreCase(annotationMethodName)) {
								//Classm.getReturnType();
								Class<?> rType = method.getReturnType();
								value = m.invoke(annotation, (Object[]) null);
								//System.out.println(method + " :: " + m + " -> " + value);							
								
//								Object response = method.invoke(obj, (Object[]) null);
//								rType.cast(response);
//								System.out.println(" >> " + rType.cast(response));

								//Be careful here. We assume that all fields are mappable either
								//to String, to Date, to Integer or to Double. 
								//In case a new field type is added,
								//it will probably be ignored!
								//TODO use generic types <T>
								//System.out.println(lookedUpClass);
								//String typeString = method.getReturnType().getTypeName();
								Class<?> returnTypeClass = method.getReturnType();
								//System.out.println(typeString);
								//Object o = rType.newInstance();
								//Object o = rType.getConstructor(parameterTypes);

						
								Field field = null;
								try {
									field = lookedUpClass.getDeclaredField(value.toString());														
									field.setAccessible(true);									
									
									//if (typeString.equalsIgnoreCase("java.lang.String")) {
									if (returnTypeClass.isAssignableFrom(String.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											field.set(obj, new String(b));
										}
										
									}
									//if (typeString.equalsIgnoreCase("java.lang.Integer")) {
									if (returnTypeClass.isAssignableFrom(Integer.class)) {
										byte[] b = fields.get((String)value);
										if (b != null && b.length != 0) {
											field.set(obj, ByteBuffer.wrap(b).getInt());
										} 
									}
									//if (typeString.equalsIgnoreCase("java.lang.Double")) {
									if (returnTypeClass.isAssignableFrom(Double.class)) {										
										byte[] b = fields.get((String)value);
										if (b != null && b.length != 0) {
											field.set(obj, ByteBuffer.wrap(b).getDouble());
										}
										
									}
									//if (typeString.equalsIgnoreCase("java.util.Date")) {
									if (returnTypeClass.isAssignableFrom(Date.class)) {
										byte[] b = fields.get((String)value);
										if (b != null) {
											
											SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
											Date d = dt.parse(new String(b));
											
											field.set(obj, (Date) d);
										}										
									}
									
								} catch (NoSuchFieldException e) {
									
									if (field != null && field.getDeclaringClass() == lookedUpClass) {
										LOGGER.log(Level.SEVERE, "Error! Field " + value.toString() + " couldn't be read!", e);
									} else {
										//System.out.println(value.toString() + " is a superclass method. Skipping.");
									}
								}
								
							}
						}
					}
					
					//Annotation a = method.getAnnotation(annotationClass);
					//value = method.invoke(a, (Object[]) null);
					
				} catch (IllegalAccessException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
				} catch (IllegalArgumentException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
				} catch (InvocationTargetException e) {
					LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
				}
				
			}
		}
		
		return obj;
	}
	
	private Map<String, Object> getFieldsGenericForClass(
			Class<?> lookedUpClass, 
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Method[] methods = lookedUpClass.getMethods();
		Field[] classFields = lookedUpClass.getDeclaredFields();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Field field : classFields) {
//			System.out.println("Field: " + field.getName());
//			if (method.isAnnotationPresent(annotationClass)) {
//				annotatedMethods.add(method);
				
//			System.out.println(lookedUpClass);
			//String typeString1 = field.getType().getTypeName();
			Class<?> fieldTypeClass = field.getType();
//			System.out.println(fieldTypeClass);
			//System.out.println(typeString1);
			
			try {
				//field2 = lookedUpClass.getDeclaredField(value.toString());

				field.setAccessible(true);
				Object o = field.get(obj);
				
				map.put(field.getName(), o);
				
				
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
			}
			
		}
		
		return map;
	}

	private Object populateFieldsGenericForClass(
			Map<String, byte[]> fields,
			Class<?> lookedUpClass, 
			Object obj) throws InstantiationException, SecurityException, ParseException {
		
		Method[] methods = lookedUpClass.getMethods();
		Field[] classFields = lookedUpClass.getDeclaredFields();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		Object value;
		
		for (Field field : classFields) {
//			System.out.println("Field: " + field.getName());
//			if (method.isAnnotationPresent(annotationClass)) {
//				annotatedMethods.add(method);
				
//			System.out.println(lookedUpClass);
			//String typeString1 = field.getType().getTypeName();
			Class<?> fieldTypeClass = field.getType();
			//System.out.println(typeString1);
			
			try {
				//field2 = lookedUpClass.getDeclaredField(value.toString());
		
				field.setAccessible(true);
				Object o = field.get(obj);
				//System.out.println(o);
				
				
				//if (typeString1.equalsIgnoreCase("java.lang.String")) {
				if (fieldTypeClass.isAssignableFrom(String.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						//field.set(obj, new String(b, "utf-8"));
						field.set(obj, new String(b));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.lang.Integer")) {
				if (fieldTypeClass.isAssignableFrom(Integer.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						field.set(obj, new Integer(new String(b)));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.lang.Double")) {
				if (fieldTypeClass.isAssignableFrom(Double.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						field.set(obj, new Double(new String(b)));
					}
					
				}
				//if (typeString1.equalsIgnoreCase("java.util.Date")) {
				if (fieldTypeClass.isAssignableFrom(Date.class)) {
					byte[] b = fields.get((String)field.getName());
					if (b != null) {
						
						SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
						Date d = dt.parse(new String(b));
						
						field.set(obj, (Date) d);
					}
					
				}
				
			} catch (IllegalArgumentException e) {
				LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
			} catch (IllegalAccessException e) {
				LOGGER.log(Level.SEVERE, "Error reading Tweet annotations! Did you modify beans???", e);
//			} catch (UnsupportedEncodingException e) {
//				LOGGER.log(Level.SEVERE, "Error in DC field characters encoding!", e);
			}
			
			
				
				
			//}
		}
		
		return null;
	}
	
	public void getOneRecord(String tableName, String rowKey) throws IOException {
		Table table = connection.getTable(captureTweetsTableName);
		Get get = new Get(rowKey.getBytes());
		Result rs = table.get(get);
//		for (Cell kv : rs.rawCells()) {
//			System.out.print(new String(kv.getRow()) + " ");
//			System.out.print(new String(kv.getFamily()) + ":");
//			System.out.print(new String(kv.getQualifier()) + " ");
//			System.out.print(kv.getTimestamp() + " ");
//			System.out.println(new String(kv.getValue()));
//		}
		
		//simplier method for reading columns:
		NavigableMap<byte[], byte[]> map = rs.getFamilyMap(Bytes.toBytes(captureTweetsColFamily));
		
		if (map == null) {
			LOGGER.info("No records found!");
		} else {
			for (Entry<byte[], byte[]> a : map.entrySet()) {
				LOGGER.fine(new String(a.getKey()) + " -> " + new String(a.getValue()));
			}

		}
		
	}
	
	public int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	/**
	 * This method will return ALL the tweets from a given datachannel
	 * probably choking itself to death.
	 * 
	 * Please don't use it.
	 * 
	 * @param dcId
	 * @return
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws ParseException
	 */
	@Deprecated
	public DcTweetList getTweetsFromDc(String dcId) throws IOException, InstantiationException, SecurityException, ParseException {
		DcTweetList dcTweetList = new DcTweetList();
		
		long count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix=Bytes.toBytes(dcId);
		
		Scan scan = new Scan();
		PrefixFilter prefixFilter = new PrefixFilter(prefix);
		scan.setFilter(prefixFilter);
		ResultScanner resultScanner = table.getScanner(scan);
		
		List<Tweet> tweetList = new ArrayList<Tweet>(50000);
		
		for (Result r : resultScanner) {
			//get only entries of type Twitter:
			String rowKey = new String(r.getRow());
			Map<String, String> keyMap = analyseDcTweetRowKey(rowKey);
			
			if (keyMap != null) {
				count++;
				
				NavigableMap<byte[], byte[]> map = r.getFamilyMap(Bytes.toBytes(captureTweetsColFamily));
				
				if (map == null) {
					LOGGER.info("No records found!");
				} else {
					Tweet t = new Tweet();
					Map<String, byte[]> fieldsMap = new HashMap<String, byte[]>();
					for (Entry<byte[], byte[]> a : map.entrySet()) {
						fieldsMap.put(new String(a.getKey()), a.getValue());
					}
					populateFieldsWithAnnotatedMethodsForClass(fieldsMap, Tweet.class, javax.xml.bind.annotation.XmlElement.class, "name", t);
					tweetList.add(t);
				}
			}
			
			
			
		}
		
		dcTweetList.setTotalTweets(count);
		dcTweetList.setTweets(tweetList);
		
		return dcTweetList;
	}
	
	/**
	 * This method will return all Tweets from Data Channel.
	 * Parameters provide pagination support, restricting the result
	 * size to a manageable amount of data.
	 * 
	 * Note on parameters:
	 * 
	 * 
	 * reverseOrder has impact on the "fromKey" parameter:
	 * - when reverseOrder is false, the method will return rows that are newer than fromKey (exclusive) 
	 * - when reverseOrder is true, the method will return rows that are older than fromKey (exclusive)

	 * 
	 * Method Parameters:
	 *
	 * @param dcId - data channel ID
	 * @param fromKey - get data starting from this key. fromKey will NOT be included in the results.
	 * @param numResults - max number of results
	 * @param reverseOrder - when true it returns data starting from newest to oldest (descending order)
	 * @return
	 * 
	 * 
	 * 
	 */
	public DcTweetList getTweetsFromDc(String dcId, String fromKey, int numResults, boolean reverseOrder) throws IOException, InstantiationException, SecurityException, ParseException {
		//Important warning!
		if (reverseOrder == true) {
			LOGGER.warning("Please note that 'reverse' parameter might give unexpected errors, such as 'Could not seekToPreviousRow StoreFileScanner'."
					+ " This is a unresolved HBase bug. See: https://issues.apache.org/jira/browse/HBASE-14283 ");
		}
		
		
		DcTweetList dcTweetList = new DcTweetList();
		String originalFromKey = fromKey;
		
		int count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix = Bytes.toBytes(dcId);
		
//		Scan scan;
//		if (fromKey != null) {
//			scan = new Scan(Bytes.toBytes(fromKey));
//		} else {
//			scan = new Scan(prefix);
//		}
		Scan scan;
		if (fromKey != null && fromKey.isEmpty() == false) {
			//here we have two options:
			if (reverseOrder == false) {
				// When we go in the ascending order (from oldest to newest)
				//we need to retrieve data that are AFTER the "fromKey" parameter.
				//we get last character of the key and increase it by 1. 
				
				//LOGGER.finer("Before: " + fromKey);
				
				char lastChar = fromKey.charAt(fromKey.length() - 1);
				lastChar++;
				fromKey = fromKey.substring(0, fromKey.length() - 1) + lastChar;
				
				//LOGGER.finer("After: " + fromKey);
				
			} else {
				// When we go in the descending order (from newest to oldest)
				// we need to retrieve data that are BEFORE the "fromKey" parameter.
				// we get last character of the key and decrease it by 1. 
				
				//LOGGER.finer("Before: " + fromKey);
				
				char lastChar = fromKey.charAt(fromKey.length() - 1);
				lastChar--;
				fromKey = fromKey.substring(0, fromKey.length() - 1) + lastChar;
				
				//LOGGER.finer("After: " + fromKey);
			}
			
			scan = new Scan(Bytes.toBytes(fromKey));
		} else {
			scan = new Scan(Bytes.toBytes(dcId));
		}
		if (reverseOrder == true) {
			scan.setReversed(true);
		}
		
		FilterList filterList = new FilterList();
		Filter prefixFilter = new PrefixFilter(prefix);
		Filter countFilter = new PageFilter(numResults);
		filterList.addFilter(prefixFilter);
		filterList.addFilter(countFilter);
		
		scan.setFilter(filterList);
		
		ResultScanner resultScanner = table.getScanner(scan);
		
		List<Tweet> tweetList = new ArrayList<Tweet>(50000);
		
		Tweet t;
		byte[] lastRowKey = Bytes.toBytes(new String(""));
		for (Result r : resultScanner) {
			count++;
			
			NavigableMap<byte[], byte[]> map = r.getFamilyMap(Bytes.toBytes(captureTweetsColFamily));
			
			if (map == null) {
				LOGGER.info("No records found!");
			} else {
				t = new Tweet();
				Map<String, byte[]> fieldsMap = new HashMap<String, byte[]>();
				for (Entry<byte[], byte[]> a : map.entrySet()) {
					fieldsMap.put(new String(a.getKey()), a.getValue());
				}
				populateFieldsWithAnnotatedMethodsForClass(fieldsMap, Tweet.class, javax.xml.bind.annotation.XmlElement.class, "name", t);
				tweetList.add(t);
				lastRowKey = r.getRow();
			}
//			System.out.print("."); 
//			if (count % 100 == 0) {
//				System.out.println();
//			}
			
			//There is a bug in HBase client, with ReverseScanner that after
			//the given amount of results, the resultScanner stalls
			//waiting for more data, but the data is never coming
			//and a timeout occurs.
			//We may get around it by simply breaking the loop, when
			//numResults is reached.
			//TODO report it on the hbase mailing list.
			if (count >= numResults) {
				break;
			}
		}
		
		dcTweetList.setTotalTweets(getDcCountById(dcId));
		dcTweetList.setTweets(tweetList);
		dcTweetList.setLastTweetId(new String(lastRowKey));
		dcTweetList.setFromId(originalFromKey);
		dcTweetList.setPageSize(count);

		//temporary, to be removed later
		//currently the tweet counter in hbase will count 
		dcTweetList.setTotalTweets(-1);
		dcTweetList.setPage(-1);

		
		return dcTweetList;
	}
	
	public long scanTableAndCountRows(String tableName, String keyPrefix) throws IOException {
		long count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix = Bytes.toBytes(keyPrefix);
		
		Scan scan = new Scan(prefix);
		PrefixFilter prefixFilter = new PrefixFilter(prefix);
		scan.setFilter(prefixFilter);
		ResultScanner resultScanner = table.getScanner(scan);
		for (Result r : resultScanner) {
			count++;
		}
		
		return count;
	}
	
	@Deprecated
	public long scanTableAndCountRowKeysOnly(String tableName, String keyPrefix) throws IOException {
		long count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix=Bytes.toBytes(keyPrefix);
		
		Scan scan = new Scan(prefix);
		FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
		
		PrefixFilter prefixFilter = new PrefixFilter(prefix);
		KeyOnlyFilter keyOnlyFilter = new KeyOnlyFilter();
		
		filterList.addFilter(prefixFilter);
		filterList.addFilter(keyOnlyFilter);
		
		scan.setFilter(filterList);
		ResultScanner resultScanner = table.getScanner(scan);
		for (Result r : resultScanner) {
			count++;
		}
		
		return count;
	}
	
	public long scanTableAndCountRows(String tableName, String keyPrefix, int max) throws IOException {
		long count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix=Bytes.toBytes(keyPrefix);
		
		Scan scan = new Scan(prefix);
		Filter prefixFilter = new PrefixFilter(prefix);
		Filter countFilter = new PageFilter(max);
		FilterList filterList = new FilterList();
		filterList.addFilter(prefixFilter);
		filterList.addFilter(countFilter);
		scan.setFilter(filterList);
		ResultScanner resultScanner = table.getScanner(scan);
		for (Result r : resultScanner) {
			count++;
//			for (KeyValue kv : r.raw()) {
//				System.out.print(new String(kv.getRow()) + " ");
//				System.out.print(new String(kv.getFamily()) + ":");
//				System.out.print(new String(kv.getQualifier()) + " ");
//				System.out.print(kv.getTimestamp() + " ");
//				System.out.println(new String(kv.getValue()));
//			}
		}
		
		return count;
	}
	
	public long getDcCountSlow(String keyPrefix) {
		try {
			return scanTableAndCountRows(captureDcTable, keyPrefix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
	
	public long getDcCountSlow1(String keyPrefix) {
		try {
			return scanTableAndCountRowKeysOnly(captureDcTable, keyPrefix);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}

	
	public long getDcCountFaster(String keyPrefix) throws Throwable {
		
	    conf.setLong("hbase.rpc.timeout", 600000);
	    conf.setLong("hbase.client.scanner.caching", 30000);
	    AggregationClient aggregationClient = new AggregationClient(conf);
	    Scan scan = new Scan(Bytes.toBytes(keyPrefix), Bytes.toBytes(keyPrefix + lastLetter));
	    //scan.addFamily(Bytes.toBytes(captureTweetsColFamily));
	    scan.addColumn(Bytes.toBytes(captureTweetsColFamily), Bytes.toBytes("text"));
	    long rowCount = aggregationClient.rowCount(captureTweetsTableName, new CountColumnInterpreter(), scan);
	    //System.out.println("row count is " + rowCount);
	    aggregationClient.close();
	    return rowCount;
	}

	
	public long getDcCountById(String dcId) {
		
		try {
			long count = 0;
			Table table = connection.getTable(captureAggregateTableName);
			String rowKey = dcId + "|" + "COUNTER" + "|";
			Scan scan = new Scan(Bytes.toBytes(dcId));
			PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes(rowKey));
			scan.setFilter(prefixFilter);
			ResultScanner resultScanner = table.getScanner(scan);
			for (Result r : resultScanner) {
				NavigableMap<byte[], byte[]> map = r.getFamilyMap(Bytes.toBytes(captureAggregateColFamily));
				byte[] counterShard = map.get(Bytes.toBytes("counter"));
				count += ByteBuffer.wrap(counterShard).getLong();
			}

			table.close();
			return count;
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error getting DC count! ", e);
			return 0;
		}
	}
	
	public void incrementTweetCount(String dcId, long count) {
		int counter = randInt(1, 5);
		try {
			Table table = connection.getTable(captureAggregateTableName);
			String rowKey = dcId + "|" + "COUNTER" + "|" + counter;
			//Get g = new Get(Bytes.toBytes(rowKey));
			//Result rs = table.get(get);
			Increment i = new Increment(Bytes.toBytes(rowKey));
			i = i.addColumn(Bytes.toBytes(captureAggregateColFamily), Bytes.toBytes("counter"), count);
			table.increment(i);
			
			if (i.getRow() == null) {
				LOGGER.log(Level.INFO, "Possible DC counter increment problem!");
			} else {
				LOGGER.log(Level.FINER, "Increment " + new String(i.getRow()) + count);
			}
		
			table.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Error incrementing counters! ", e);
		}
	}
	
	
	
	/*
	 * This method requires hbase-client >1.0 
	 * 
	 * 
	 */
	@Deprecated
	public Map<String, Integer> scanTableAggregates(String tableName, String keyPrefix, int max) throws IOException {
		int count = 0;
		Table table = connection.getTable(captureTweetsTableName);
		byte[] prefix=Bytes.toBytes(keyPrefix);
		
		Scan scan = new Scan();
		List<RowRange> ranges = new ArrayList<RowRange>();
		

		for (int i=0; i<59; i++) {
			ranges.add(new RowRange(
					Bytes.toBytes("81bf4579-24fc-4ced-a773-a5443580518f0000T2015072419" + String.format("%02d", i)), 
					true, 
					Bytes.toBytes("81bf4579-24fc-4ced-a773-a5443580518f0000T2015072419" + String.format("%02d", i+1)),
					false));
		}
		ranges.add(new RowRange(
				Bytes.toBytes("81bf4579-24fc-4ced-a773-a5443580518f0000T2015072419" + "59"), 
				true, 
				Bytes.toBytes("81bf4579-24fc-4ced-a773-a5443580518f0000T2015072420"),
				false));

		
		Filter filter = new MultiRowRangeFilter(ranges);
		scan.setFilter(filter);
		
		Map<String, Integer> aggregates = new HashMap<String, Integer>();
		
		ResultScanner resultScanner = table.getScanner(scan);
		for (Result r : resultScanner) {
			
			//String second = new String(r.getRow()).substring(53,55);
			//String hours = new String(r.getRow()).substring(49,51);
			String second = new String(r.getRow()).substring(51,53);
			
			if (aggregates.containsKey(second) == true) {
				aggregates.put(second, aggregates.get(second) + 1);
			} else {
				aggregates.put(second, 1);
			}
			
			
			count++;
//			for (KeyValue kv : r.raw()) {
//				System.out.print(new String(kv.getRow()) + " ");
//				System.out.print(new String(kv.getFamily()) + ":");
//				System.out.print(new String(kv.getQualifier()) + " ");
//				System.out.print(kv.getTimestamp() + " ");
//				System.out.println(new String(kv.getValue()));
//			}
		}
		
		return aggregates;
	}

	/**
	 * Utility method for dissecting date into the following parts:
	 * YEAR
	 * MONTH
	 * DAY_OF_MONTH
	 * HOUR_OF_DAY
	 * MINUTE
	 * SECOND
	 * 
	 * This method will accept Date object as an input.
	 * 
	 * The date will be converted to UTC and
	 * each date part will be returned in UTC time zone.	 * 
	 * 
	 * @param date
	 * @return Map of string names of each data part and the corresponding value.
	 */
	public static Map<String, String> analyseDateInUTC(Date date) {
		Map<String, String> result = new HashMap<>();
	
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		result.put("YEAR", 
				String.format("%04d", calendar.get(Calendar.YEAR)));
		
		result.put("MONTH",
				String.format("%02d", (calendar.get(Calendar.MONTH) + 1)));

		result.put("DAY_OF_MONTH", 
				String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
		
		result.put("HOUR_OF_DAY", 
				String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY)));
		
		result.put("MINUTE", 
				String.format("%02d", calendar.get(Calendar.MINUTE)));
		
		result.put("SECOND", 
				String.format("%02d", calendar.get(Calendar.SECOND)));
		
		
		return result;
	}
	
	public static Map<String, String> analyseDcTweetRowKey(String rowKey) {
		Map<String, String> result = new HashMap<>();

		if (rowKey != null && rowKey.length() >= 43) {
			result.put("DCID", rowKey.substring(0,36));
			
			result.put("DATAPOOL", rowKey.substring(36,40));

			result.put("TYPE", rowKey.substring(40,41));
			
			result.put("DATE", rowKey.substring(41,55));
			
			result.put("TWEETID", rowKey.substring(56));
			
			
			return result;
			
		} else {
			return null;
		}
		 
	}
	
	public static String getUTCDateString(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		parser.setTimeZone(calendar.getTimeZone());
		return parser.format(calendar.getTime());
	}
	

	/**
	 * Utility method for dissecting date into the following parts:
	 * YEAR
	 * MONTH
	 * DAY_OF_MONTH
	 * HOUR_OF_DAY
	 * MINUTE
	 * SECOND
	 * 
	 * Input Date format: YYYY-MM-DD HH:MM:SS.sss
	 * Input Time zone: local to machine (doesn't matter)
	 * example: 2015-07-30 17:37:08.000
	 * 
	 * The date will be converted to UTC and
	 * each date part will be returned in UTC time zone.
	 * 
	 * @param date Date string in format: YYYY-MM-DD HH:MM:SS.sss
	 * @return Map of string names of each data part and the corresponding value.
	 * @throws ParseException
	 */
	public static Map<String, String> analyseDateInUTC(String date) throws ParseException {
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		Date d = parser.parse(date);
		return analyseDateInUTC(d);
	}
	
	// **********************************************************************************************************
	// ************************************ Methods for aggregators *********************************************
	// **********************************************************************************************************
	
	// Methods auxiliars for add/update aggregator
	
	public List<String> getAggregatorRowkeys() throws IOException {
		
		List<String> rowkeys = new ArrayList<String>();
		
		Table table = connection.getTable(captureAggregatorTableName);
		Scan scan = new Scan();
		ResultScanner resultScanner = table.getScanner(scan);
		
		for (Result r : resultScanner) {
			String rowkey = new String(r.getRow());
			rowkeys.add(rowkey);
		}
		
		return rowkeys;
	}

	public String saveAggregator(Aggregator aggregator, String aggID) throws IOException {
		
		// HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		// org.apache.hadoop.conf.Configuration config = hbp.getConf();
	
	    //HTable table = new HTable(config, AggregateConstants.agg_desc_tableName);
		Table table = connection.getTable(captureAggregatorTableName);
			
		Put put = new Put(Bytes.toBytes(aggID));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colInitDate),Bytes.toBytes(aggregator.getInitDate()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colEndDate),Bytes.toBytes(aggregator.getEndDate()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colDatachannels),Bytes.toBytes(aggregator.getDatachannels()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colAnalysisType),Bytes.toBytes(aggregator.getAnalysisType().toString()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colPeriodicity),Bytes.toBytes(aggregator.getPeriodicity().toString()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colDescription),Bytes.toBytes(aggregator.getDescription()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colPeriodically),Bytes.toBytes(aggregator.getPeriodically()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colStatus),Bytes.toBytes(aggregator.getStatus().toString()));
		put.addColumn(AggregateConstants.agg_desc_bColFamily, Bytes.toBytes(AggregateConstants.agg_desc_colType),Bytes.toBytes(aggregator.getType().toString()));
		
		table.put(put);
		
		table.close();
		
		return aggID;
		
	}
	
	public String addAggregator(Aggregator aggregator) throws IOException {

		String aggID = "porahorameloinventoparaquecompile";
		
		List<String> rowkeys = getAggregatorRowkeys();
		
		do{
			aggID = generateCrc32Id();
		}
		while (rowkeys.contains(aggID));
		
		saveAggregator(aggregator, aggID);	
		return aggID;
	}
	
	public void updateAggregator(Aggregator aggregator, String aggID) throws IOException {
		saveAggregator(aggregator, aggID);	
	}
	
	
	@SuppressWarnings("null")
	public Aggregator getAggregator(String aggID) throws CaptureException, IOException {
			 
		//HBaseDenormProxy hbp = HBaseDenormProxy.getInstance();
		//org.apache.hadoop.conf.Configuration config = hbp.getConf();
		
		// HTable table = new HTable(config, AggregateConstants.agg_desc_tableName);
		Aggregator agg = new Aggregator();
		Table table = connection.getTable(captureAggregatorTableName);
		
		Get g=new Get(aggID.getBytes());
		      
		    Result r=table.get(g);
		    
		    //System.out.println("existe: "+ r.size() + "  --  " +r.isEmpty());
		    if (r.isEmpty()){
		    	return null;
		    }
		    
		    agg.setAggID(aggID);
		    
		    byte[] v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colInitDate.getBytes());
		    String initDate=Bytes.toString(v);
		    agg.setInitDate(initDate);
		      
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colEndDate.getBytes());
		    String endDate=Bytes.toString(v);
		    agg.setEndDate(endDate);
		
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colDatachannels.getBytes());
		    String datachannel=Bytes.toString(v);
		    agg.setDatachannels(datachannel);
		
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colAnalysisType.getBytes());
		    String sAnalysisType=Bytes.toString(v);
		    
		    List<AnalysisType> lAnalysisTypes = null;
		    if (!"".equals(sAnalysisType) && sAnalysisType != null){
		    	lAnalysisTypes = new ArrayList<AnalysisType>();
		    	for (String item: sAnalysisType.split(",")){
		    		lAnalysisTypes.add(PeriodicAnalysisResult.convertAnalysisType(item.trim().replace("[", "").replace("]", "")));
		    		}
		    }
		    agg.setAnalysisType(lAnalysisTypes);
		
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colPeriodicity.getBytes());
		    String periodicity=Bytes.toString(v);
		    agg.setPeriodicity(PeriodicAnalysisResult.convertPeriodicity(periodicity));
		    
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colDescription.getBytes());
		    String description=Bytes.toString(v);
		    agg.setDescription(description);
		    
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colPeriodically.getBytes());
		    String periodically=Bytes.toString(v);
		    agg.setPeriodically(periodically);
		    
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colStatus.getBytes());
		    String status=Bytes.toString(v);
		    agg.setStatus(Aggregator.convertStatus(status));
		    
		    v=r.getValue(AggregateConstants.agg_desc_bColFamily,AggregateConstants.agg_desc_colType.getBytes());
		    String type=Bytes.toString(v);
		    agg.setType(type);
		
		    table.close();
		    return agg;
	}
	
	public void deleteAggregator(String aggID) throws IOException {
		
		Table table = connection.getTable(captureAggregatorTableName);
		Delete dlt = new Delete(aggID.getBytes());
		table.delete(dlt);
		table.close();
	}
	
	
	public AggregatorList getAggregators(){
		
		
		try {
			int count = 0;
			Table table = connection.getTable(captureAggregatorTableName);
			
			Scan scan = new Scan();
			ResultScanner resultScanner = table.getScanner(scan);

			Map<String, Aggregator> aggregatorMap = new HashMap<>();

			for (Result r : resultScanner) {		
				Aggregator agg = getAggregator(new String(r.getRow()));
				aggregatorMap.put(new String(r.getRow()), agg);
				
			}
			
			AggregatorList aggl = new AggregatorList();
			for (Entry<String, Aggregator> d : aggregatorMap.entrySet()) {
				aggl.addAggregator(d.getValue());
			}
			
			table.close();
			return aggl;
			
		} catch (IOException | CaptureException e) {
			//LOGGER.severe("Can't retrieve DataChannel list.");
			//e.printStackTrace();
			LOGGER.log(Level.SEVERE, "Can't retrieve DataChannel list.", e);

			return null;
		}
	}
	
	public void storeTermOccurrenceAnalysis(List<TermOccurrenceAnalysis> ocurrenceList) {
		
		LOGGER.log(Level.INFO, "Entramos a guardar en hbase...");
		for (TermOccurrenceAnalysis to: ocurrenceList){
			
			LOGGER.log(Level.INFO, "");
			Date storeDate = to.getaDate();
			
			DataChannel dc = to.getDataChannel();
			DataPool dp = to.getDataPool();
			String id = "";
			if (dc != null) id = dc.getChannelID();
			if (dp != null) id = dp.getPoolID();
			   
			Periodicity per = to.getPeriodicity();
			String periodicity = per.toString();
			HashMap<String, Integer> toMap = to.getTermOcc();
			
			String rowPeriodicity = getRowPeriodicity(periodicity);
			
			if (toMap != null){
				try {
					
					Table table = connection.getTable(TableName.valueOf(AggregateConstants.agg_res_tableName));
				
					String rowKey = rowPeriodicity+AggregateConstants.aggregateTagCloud+id+stringDate(storeDate);
					
					Put put = new Put(Bytes.toBytes(rowKey));
					
					for (String key : toMap.keySet()){
						put.addColumn(Bytes.toBytes(AggregateConstants.agg_res_columnFamily), Bytes.toBytes(key),Bytes.toBytes(toMap.get(key).toString()));
					}			
					
					table.put(put);
					table.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void storeVolumeOcurrenceAnalysis(List<VolumeOcurrenceAnalysis> ocurrenceList) {
		
		for (VolumeOcurrenceAnalysis vo: ocurrenceList){
			
			Date storeDate = vo.getaDate();
			DataChannel dc = vo.getDataChannel();
			DataPool dp = vo.getDataPool();
			String id = "";
			if (dc != null) id = dc.getChannelID();
			if (dp != null) id = dp.getPoolID();
			
			Periodicity per = vo.getPeriodicity();
			String periodicity = per.toString();
			AnalysisType type = vo.getAnalysisType();
			
			String sType = AggregateConstants.aggregateOther;
			if (type.toString().equals(CaptureConstants.VOLUME)){
				sType = AggregateConstants.aggregateTotalVolume;
			}
			else if (type.toString().equals(CaptureConstants.SENTIMENT_VOLUME_POSITIVE)){
				sType = AggregateConstants.aggregatePositiveVolume;
			}
			else if (type.toString().equals(CaptureConstants.SENTIMENT_VOLUME_NEGATIVE)){
				sType = AggregateConstants.aggregateNegativeVolume;
			}
			else if (type.toString().equals(CaptureConstants.SENTIMENT_VOLUME_NEUTRO)){
				sType = AggregateConstants.aggregateNeutroVolume;
			}
			
			Integer volume = (int) vo.getMeasurement();
			
			String rowPeriodicity = getRowPeriodicity(periodicity);
			
			try {
				
				Table table = connection.getTable(TableName.valueOf(AggregateConstants.agg_res_tableName));
				
				String rowKey = rowPeriodicity+sType+id+stringDate(storeDate);
				
				Put put = new Put(Bytes.toBytes(rowKey));
				
				put.addColumn(Bytes.toBytes(AggregateConstants.agg_res_columnFamily), Bytes.toBytes(sType),Bytes.toBytes(volume.toString()));			
				
				table.put(put);
				table.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	// **********************************************************************************************************
	// ******************** Methods for aggregators (DASHBOARD) *************************************************
	// **********************************************************************************************************
	
	
	// Widget 7: Obtener los n (number of result) most popular (el volumen) data channels de entre una serie de datachhanels dados. 
	// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, PORCENTAJE>
	// Map<Date, Map<DataChannel, Integer>> getTopVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per, int numberOfResults) throws CaptureException;
	public Map<Date, Map<DataChannel, Integer>> getTopVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per, int numberOfResults){
		LOGGER.info("Getting populars in HBase...");
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		Integer total=0;
		Map<Date, Map<DataChannel, Integer>> topVolume = new HashMap<Date, Map<DataChannel, Integer>>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
		
			String type_measure = AggregateConstants.aggregateTotalVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dataPoolId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dataPoolId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dataPoolId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {		
					 String sDc = Bytes.toString(r.getRow()).substring(5,13);
					 Integer iMeasurement = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())));
	
					 if (ranking.get(sDc) == null){
						 ranking.put(sDc, iMeasurement);
					 }else{
						 ranking.put(sDc, ranking.get(sDc) + iMeasurement);
					 }
					 total = total + iMeasurement;
				}
			}
				
				LOGGER.info("Ranking: "+ ranking.toString());	
	    		ArrayList<Entry<String, Integer>> losValores = new ArrayList<Entry<String, Integer>>(ranking.entrySet());
	    		try{
		    		Collections.sort(losValores, new TopEntryComparator());
	    		}catch (Exception e) {
	    			LOGGER.info("Error comparing: ");
	    		}
	  
	            // creamos el bean que se va a enviar
	    		
	    		if (losValores.size() < numberOfResults) numberOfResults=losValores.size();
	    		HashMap <DataChannel, Integer> rankedMap = new HashMap<DataChannel, Integer>();
	    		for (Entry<String, Integer> it : losValores.subList(0, numberOfResults)) {
	    			DataChannel newDC = new DataChannel();
					newDC.setChannelID(it.getKey());
					int percentaje = it.getValue()*100/total;
	    			//rankedMap.put(newDC, it.getValue());
					rankedMap.put(newDC, percentaje);
	    		}
	    		LOGGER.info("Ranked: "+ rankedMap.toString());
	    		topVolume.put(endDate, rankedMap);
	    		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the volume", e);
		}
		return topVolume;
		
	}
	
	
	// Widget 7: Obtener la popularidad de un datachannels (en cuanto a volomen) de entre una serie de datachhanels dados.
	// Devuelve una lista de <date, porcentaje> de tantos elementos como de la "per"
	// Map<Date, Integer> getVolumePer(List<String> dataPoolIdList, String dataPoolId, Date initDate, Date endDate, Periodicity per) throws CaptureException;
	public Map<Date, Integer> getVolumePer(List<String> dataPoolIdList, String dataPoolId, Date initDate, Date endDate, Periodicity per){
		
		LOGGER.info("Getting popularity in HBase...");
		Map<String, Integer> ranking = new HashMap<String, Integer>();
		Integer total=0;
		Map<Date, Integer> popularity = new HashMap<Date, Integer>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure = AggregateConstants.aggregateTotalVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String sDc = Bytes.toString(r.getRow()).substring(5,13);
					 Integer iMeasurement = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())));
					 
					 if (ranking.get(sDc) == null){
						 ranking.put(sDc, iMeasurement);
					 }else{
						 ranking.put(sDc, ranking.get(sDc) + iMeasurement);
					 }
					 total = total+iMeasurement;
				}
			}
				
			LOGGER.info("Ranking: "+ ranking.toString() + ", total:" + total);	
	        // creamos el bean que se va a enviar
			if (ranking!=null && !ranking.isEmpty() && ranking.containsKey(dataPoolId)) popularity.put(endDate, ranking.get(dataPoolId)*100/total);
	    		 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the popularity", e);
		}
		return popularity;
		
	}
	
	public Map<Date, Map<DataChannel, Integer>> getVolumePer(List<String> dataPoolIdList, List<String> dataPoolIdCandidates, Date initDate, Date endDate, Periodicity per){
		
		LOGGER.info("Getting popularity in HBase...");
		Map<String, Integer> volume = new HashMap<String, Integer>();
		Map<Date, Map<DataChannel, Integer>> volumeFinal = new HashMap<Date, Map<DataChannel, Integer>>();
		
		long total=0;
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure = AggregateConstants.aggregateTotalVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String sDc = Bytes.toString(r.getRow()).substring(5,13);
					 Integer iMeasurement = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())));
					 
					 if (volume.get(sDc) == null){
						 volume.put(sDc, iMeasurement);
					 }else{
						 volume.put(sDc, volume.get(sDc) + iMeasurement);
					 }
					 total = total+iMeasurement;
				}
			}
				
			LOGGER.info("Ranking: "+ volume.toString() + ", total:" + total);	
	        
			// creamos el bean que se va a enviar
			Map<DataChannel, Integer> volumeDate = new HashMap<DataChannel, Integer>();
			for (String dpIdCandidate : dataPoolIdCandidates){
				
				long dcVolume = 0;
				if (volume.get(dpIdCandidate) != null){
					dcVolume = volume.get(dpIdCandidate);
				}
				
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(dpIdCandidate);
				
				if (total > 0){
					volumeDate.put(newDC, (int) (dcVolume*100/total));
				}
				else {
					volumeDate.put(newDC, 0);					
				}
			}
			
			volumeFinal.put(endDate, volumeDate);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the popularity", e);
		}
		
		return volumeFinal;
	}
	
	
	// Widget 7: para pintar los velicimetros. Dada una lista de datachannels devolver el sentimiento acumulado (entre initDate and endDate) para cada uno de ellos.
	// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, difPositivos-Negativos>
	// Map<Date, Map<DataChannel, Double>> getSentimentDegree(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
	public Map<Date, Map<DataChannel, Double>> getSentimentDegree(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) {
		
		LOGGER.info("Getting sentiment degree in HBase...");
		
		Map<String, Integer[]> sentiment = new HashMap<String, Integer[]>();
		Map<Date, Map<DataChannel, Double>> sentimentFinal = new HashMap<Date, Map<DataChannel, Double>>();

		Map<Date, Map<DataChannel, Integer[]>> allSentiments = getSentiment(dataPoolIdList, initDate, endDate, per);
		for (Map<DataChannel, Integer[]> dcSentiments : allSentiments.values()){
			for (DataChannel dc : dcSentiments.keySet()){
				String dcID = dc.getChannelID();
				Integer[] dcValues = dcSentiments.get(dc);
				
				Integer sentiments[] = sentiment.get(dcID);
				if (sentiments == null){
					 // este datachannel no tenia todavia acumulados
					 sentiments = dcValues;
				}
				else{
					 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
					 sentiments[0] = sentiments[0] + dcValues[0];
					 sentiments[1] = sentiments[1] + dcValues[1];
					 sentiments[2] = sentiments[2] + dcValues[2];
				}
				sentiment.put(dcID, sentiments);
			}
		}
		
		Map<DataChannel, Double> singleDate = new HashMap<DataChannel, Double>();
		Iterator<String> itDc = (Iterator<String>) sentiment.keySet().iterator();
		while (itDc.hasNext()) {
			// Vamos sacando por datachannel
			String dcId = itDc.next();
			//LOGGER.info("dcId: "+ dcId);
			Integer[] dcSentiments = sentiment.get(dcId);
			
			DataChannel newDC = new DataChannel();
			newDC.setChannelID(dcId);
			double degree = 0.0;
			if (dcSentiments.length>1){
				//LOGGER.info("pos: "+ dcSentiments[0].toString());
				//LOGGER.info("neg: "+ dcSentiments[1].toString());
				//LOGGER.info("neu: "+ dcSentiments[2].toString());
				degree = (((dcSentiments[0]-dcSentiments[1]) * 1.0)/((dcSentiments[0]+dcSentiments[1]+dcSentiments[2]) * 1.0));
			}
			//LOGGER.info("Degree: "+ degree);
			singleDate.put(newDC, degree);
		}
		
		//just in case there aren't rows in some datachannel
		for (String dpId : dataPoolIdList){
			if ( !(sentiment.keySet()).contains(dpId) ){
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(dpId);
				double degree = 0.0;
				singleDate.put(newDC, degree);
			}
		}
		
		sentimentFinal.put(endDate, singleDate);
		
		/*
		Map<String, Integer[]> sentiment = new HashMap<String, Integer[]>();
		Map<Date, Map<DataChannel, Double>> sentimentFinal = new HashMap<Date, Map<DataChannel, Double>>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure_positive = AggregateConstants.aggregatePositiveVolume;
			String type_measure_negative = AggregateConstants.aggregateNegativeVolume;
			String type_measure_neutro = AggregateConstants.aggregateNeutroVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_positive,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_positive,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_negative,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_negative,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_neutro,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_neutro,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
			
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					int positives=0;
					int negatives=0;
					int neutros=0;
					
					if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 positives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 negatives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())) {
						neutros = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())));
					}
					 
	
					Integer sentiments[] = sentiment.get(dc);
					if (sentiments == null){
							 // este datachannel no tenia todavia acumulados
							 sentiments = new Integer[3];
							 sentiments[0] = positives;
							 sentiments[1] = negatives;
							 sentiments[2] = neutros;
					}
					else{
							 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
							 if (positives > 0) sentiments[0] = sentiments[0] + positives;
							 if (negatives > 0) sentiments[1] = sentiments[1] + negatives;
							 if (neutros > 0) sentiments[2] = sentiments[2] + neutros;
					}
					sentiment.put(dc, sentiments);
				}
			}
				
			//LOGGER.info("El acumulado: "+ sentiment.toString());

			Map<DataChannel, Double> singleDate = new HashMap<DataChannel, Double>();
			Iterator<String> itDc = (Iterator<String>) sentiment.keySet().iterator();
			while (itDc.hasNext()) {
					// Vamos sacando por datachannel
					String dcId = itDc.next();
					Integer[] dcSentiments = sentiment.get(dcId);
					
					DataChannel newDC = new DataChannel();
					newDC.setChannelID(dcId);
					double degree = 0.0;
					if (dcSentiments.length>1){
						//LOGGER.info("pos: "+ dcSentiments[0].toString());
						//LOGGER.info("neg: "+ dcSentiments[1].toString());
						//LOGGER.info("neu: "+ dcSentiments[2].toString());
						degree = (((dcSentiments[0]-dcSentiments[1]) * 1.0)/((dcSentiments[0]+dcSentiments[1]+dcSentiments[2]) * 1.0));
					}
					//LOGGER.info("Degree: "+ degree);
					singleDate.put(newDC, degree);
			}
			
			//just in case there aren't rows in some datachannel
			for (String dpId : dataPoolIdList){
				if ( !(sentiment.keySet()).contains(dpId) ){
					DataChannel newDC = new DataChannel();
					newDC.setChannelID(dpId);
					double degree = 0.0;
					singleDate.put(newDC, degree);
				}
			}
			
			sentimentFinal.put(endDate, singleDate);
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the sentiment", e);
		}
		*/
		return sentimentFinal;
		
	}

	
	
	
	// Widget 8 (grafica superior). Dad una lista de datachannels, te devuelve la resta de positivos - negativos
	// Devuelve una lista de tantos elementos como slots (en caso del widget 7, 1 slot), y en cada slot, N elementos de <datachanelId, [positivos, negativos]>
	// Map<Date, Map<DataChannel, Integer[]>> getSentiment(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
	public Map<Date, Map<DataChannel, Integer[]>> getSentiment(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) {
		
		LOGGER.info("Getting sentiment in HBase...");
		Map<Date, Map<String, Integer[]>> sentiment = new HashMap<Date, Map<String, Integer[]>>();
		Map<Date, Map<DataChannel, Integer[]>> sentimentFinal = new HashMap<Date, Map<DataChannel, Integer[]>>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure_positive = AggregateConstants.aggregatePositiveVolume;
			String type_measure_negative = AggregateConstants.aggregateNegativeVolume;
			String type_measure_neutro = AggregateConstants.aggregateNeutroVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			//Scan scan = new Scan();
			//List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_positive,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_positive,dpId,endDate);
				
				Scan scanp = new Scan();
		        scanp.setStartRow(rowkeyinit.getBytes());
				scanp.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScannerp = table.getScanner(scanp);
				
				for (Result r : resultScannerp) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 int positives=0;
					 int negatives=0;
					 int neutros=0;
					 if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 positives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
					}
					 else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 negatives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
					}
					 else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())) {
						 neutros = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())));
					}
					
					 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
					 if (dateSentiment == null){
						 dateSentiment = new HashMap<String, Integer[]>();
						 Integer sentiments[] = {positives, negatives, neutros};
						 dateSentiment.put(dc, sentiments);
					 }else{
						 Integer sentiments[] = dateSentiment.get(dc);
						 if (sentiments == null){
							 // ya estaba la fecha registrada pero no ese datachannel
							 sentiments = new Integer[3];
							 sentiments[0] = positives;
							 sentiments[1] = negatives;
							 sentiments[2] = neutros;
						 }
						 else{
							 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
							 if (positives > 0) sentiments[0] = positives;
							 if (negatives > 0) sentiments[1] = negatives;
							 if (neutros > 0) sentiments[2] = neutros;
						 }
						 dateSentiment.put(dc, sentiments);
						 //LOGGER.info("dc: "+dc+"sentiments (pos - neg): "+sentiments[0]+" - "+sentiments[1]);
					 }
					 sentiment.put(date, dateSentiment);
					 for (String a : dateSentiment.keySet()){
						 //LOGGER.info("date: "+date+" dc: "+a+"sentiments (pos - neg): "+dateSentiment.get(a)[0]+" - "+dateSentiment.get(a)[1]);
					 }
				}
				
				//totalResultScanner.add(resultScanner);
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_negative,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_negative,dpId,endDate);

				Scan scann = new Scan();
				scann.setStartRow(rowkeyinit.getBytes());
				scann.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScannern = table.getScanner(scann);
				
				for (Result r : resultScannern) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 int positives=0;
					 int negatives=0;
					 int neutros=0;
					 if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 positives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 negatives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())) {
						 neutros = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())));
					}
					
					 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
					 if (dateSentiment == null){
						 dateSentiment = new HashMap<String, Integer[]>();
						 Integer sentiments[] = {positives, negatives, neutros};
						 dateSentiment.put(dc, sentiments);
					 }else{
						 Integer sentiments[] = dateSentiment.get(dc);
						 if (sentiments == null){
							 // ya estaba la fecha registrada pero no ese datachannel
							 sentiments = new Integer[3];
							 sentiments[0] = positives;
							 sentiments[1] = negatives;
							 sentiments[2] = neutros;
						 }
						 else{
							 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
							 if (positives > 0) sentiments[0] = positives;
							 if (negatives > 0) sentiments[1] = negatives;
							 if (neutros > 0) sentiments[2] = neutros;
						 }
						 dateSentiment.put(dc, sentiments);
						 //LOGGER.info("dc: "+dc+"sentiments (pos - neg): "+sentiments[0]+" - "+sentiments[1]);
					 }
					 sentiment.put(date, dateSentiment);
					 for (String a : dateSentiment.keySet()){
						 //LOGGER.info("date: "+date+" dc: "+a+"sentiments (pos - neg): "+dateSentiment.get(a)[0]+" - "+dateSentiment.get(a)[1]);
					 }
				}
				
				//totalResultScanner.add(resultScanner);
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_neutro,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_neutro,dpId,endDate);

				Scan scanneu = new Scan();
				scanneu.setStartRow(rowkeyinit.getBytes());
				scanneu.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScannerneu = table.getScanner(scanneu);
				
				for (Result r : resultScannerneu) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 int positives=0;
					 int negatives=0;
					 int neutros=0;
					 if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 positives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 negatives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())) {
						 neutros = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())));
					}
					
					 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
					 if (dateSentiment == null){
						 dateSentiment = new HashMap<String, Integer[]>();
						 Integer sentiments[] = {positives, negatives, neutros};
						 dateSentiment.put(dc, sentiments);
					 }else{
						 Integer sentiments[] = dateSentiment.get(dc);
						 if (sentiments == null){
							 // ya estaba la fecha registrada pero no ese datachannel
							 sentiments = new Integer[3];
							 sentiments[0] = positives;
							 sentiments[1] = negatives;
							 sentiments[2] = neutros;
						 }
						 else{
							 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
							 if (positives > 0) sentiments[0] = positives;
							 if (negatives > 0) sentiments[1] = negatives;
							 if (neutros > 0) sentiments[2] = neutros;
						 }
						 dateSentiment.put(dc, sentiments);
						 //LOGGER.info("dc: "+dc+"sentiments (pos - neg): "+sentiments[0]+" - "+sentiments[1]);
					 }
					 sentiment.put(date, dateSentiment);
					 for (String a : dateSentiment.keySet()){
						 //LOGGER.info("date: "+date+" dc: "+a+"sentiments (pos - neg): "+dateSentiment.get(a)[0]+" - "+dateSentiment.get(a)[1]);
					 }
				}
				
				//totalResultScanner.add(resultScanner);
			}
			
			/*for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 int positives=0;
					 int negatives=0;
					 if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 positives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
					}
					else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 negatives = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
					}
					
					 Map<String, Integer[]> dateSentiment  = sentiment.get(date);
					 if (dateSentiment == null){
						 dateSentiment = new HashMap<String, Integer[]>();
						 Integer sentiments[] = {positives, negatives};
						 dateSentiment.put(dc, sentiments);
					 }else{
						 Integer sentiments[] = dateSentiment.get(dc);
						 if (sentiments == null){
							 // ya estaba la fecha registrada pero no ese datachannel
							 sentiments = new Integer[2];
							 sentiments[0] = positives;
							 sentiments[1] = negatives;
						 }
						 else{
							 // ya estaba la fecha registrada y ese datachannel, asi que actualizamos el sentimiento
							 if (positives > 0) sentiments[0] = positives;
							 if (negatives > 0) sentiments[1] = negatives;
						 }
						 dateSentiment.put(dc, sentiments);
						 //LOGGER.info("dc: "+dc+"sentiments (pos - neg): "+sentiments[0]+" - "+sentiments[1]);
					 }
					 sentiment.put(date, dateSentiment);
					 for (String a : dateSentiment.keySet()){
						 //LOGGER.info("date: "+date+" dc: "+a+"sentiments (pos - neg): "+dateSentiment.get(a)[0]+" - "+dateSentiment.get(a)[1]);
					 }
				}
			}*/
		
			Iterator<Date> itDates = (Iterator<Date>) sentiment.keySet().iterator();
			while (itDates.hasNext()) {
				// Vamos sacando por cada fecha
				Date dateFinal = itDates.next();
				Map<DataChannel, Integer[]> newDate = new HashMap<DataChannel, Integer[]>();
				Map<String, Integer[]> res = sentiment.get(dateFinal);
				Iterator<String> dcKey = res.keySet().iterator();
				while (dcKey.hasNext()) {
					// Vamos sacando por datachannel
					String dcFinal = dcKey.next();
					DataChannel newDC = new DataChannel();
					newDC.setChannelID(dcFinal);
					newDate.put(newDC, res.get(dcFinal));
				}
				sentimentFinal.put(dateFinal, newDate);
			}
				 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the sentiment", e);
		}
		return sentimentFinal;
		
	}

	
	
	
	// Widget 6 (solo volumen). Dada una lista de datachannels obtener el volumen para cada uno de ellos
	// Map<Date, Map<DataChannel, Integer>> getVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per) throws CaptureException;
	public Map<Date, Map<DataChannel, Integer>> getVolume(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		
		LOGGER.info("Getting volume in HBase...");
		Map<Date, Map<DataChannel, Integer>> volume = new HashMap<Date, Map<DataChannel, Integer>>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure = AggregateConstants.aggregateTotalVolume;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 DataChannel newDC = new DataChannel();
					 newDC.setChannelID(dc);
					
					 Integer medida = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())));
					 Map<DataChannel, Integer> dateVolume  = volume.get(date);
					 if (dateVolume== null){
						 dateVolume = new HashMap<DataChannel, Integer>();
						 dateVolume.put(newDC, medida);
					 }else{
						 dateVolume.put(newDC, medida);
					 }
					 volume.put(date, dateVolume);
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the volume", e);
		}
		LOGGER.info("Got volume in HBase");
		return volume;
		
	}

	
	
	
	// Widget 9 (solo tag cloud). dado una lista de datachannels te devuelve tantos tag cloud como datachannels metidos
	// Map<DataChannel, Map<String, Integer>> getTagCloud (List<String> dataPoolIdList, Date date) throws CaptureException;
	public Map<DataChannel, Map<String, Integer>> getTagCloud (List<String> dataPoolIdList, Date date, Periodicity per){
		
		LOGGER.info("Getting tag cloud for: "+ date.toString() +" y los datachannels: " + dataPoolIdList.toString()+" y per: " + per.toString());
		Map<DataChannel, Map<String, Integer>> tagCloud = new HashMap<DataChannel, Map<String, Integer>>();
		Map<String, Integer> dcTagCloud = new HashMap<String, Integer>();
		XStream xstream = new XStream();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String periodicity = getRowPeriodicity(per.toString());
			
			// Creamos la fecha del fin del dia
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			calendar.setTime(date); // Configuramos la fecha que se recibe
			
			if (periodicity.equals(Periodicity.daily.toString())){
				calendar.set(Calendar.HOUR_OF_DAY,0); 
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.add(Calendar.DAY_OF_MONTH, +1);
			}
			else if (periodicity.equals(Periodicity.hourly.toString())){
				calendar.add(Calendar.HOUR_OF_DAY,+1); 
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
			}
			else if (periodicity.equals(Periodicity.minutes30.toString())){
				int minute = calendar.get(Calendar.MINUTE);
	    		int setMinute = (minute/30)*30;
	    		calendar.set(Calendar.MINUTE, setMinute);
	    		calendar.set(Calendar.SECOND, 0);
	    		calendar.add(Calendar.MINUTE, +30);
			}
			else if (periodicity.equals(Periodicity.minutes15.toString())){
				int minute = calendar.get(Calendar.MINUTE);
	    		int setMinute = (minute/15)*15;
	    		calendar.set(Calendar.MINUTE, setMinute);
	    		calendar.set(Calendar.SECOND, 0);
	    		calendar.add(Calendar.MINUTE, +15);
			}
			else if (periodicity.equals(Periodicity.minutes5.toString())){
				int minute = calendar.get(Calendar.MINUTE);
	    		int setMinute = (minute/5)*5;
	    		calendar.set(Calendar.MINUTE, setMinute);
	    		calendar.set(Calendar.SECOND, 0);
	    		calendar.add(Calendar.MINUTE, +5);
			}
			else if (periodicity.equals(Periodicity.minutes1.toString())){
	    		calendar.set(Calendar.SECOND, 0);
	    		calendar.add(Calendar.MINUTE, +1);
			}
			Date endDate = calendar.getTime();
			
			String type_measure = AggregateConstants.aggregateTagCloud;
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<String> dcTagCloudValues = new ArrayList<String>();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,date);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 NavigableMap<byte[], byte[]> res = r.getFamilyMap(AggregateConstants.agg_res_bColFamily);

					 for (byte[] key : res.keySet()) {
						 String word = Bytes.toString(key);
						 String value = Bytes.toString(res.get(key));
						 dcTagCloudValues.add(dc+":"+word+":"+value);
					 }
				}
			}
						
			if (!dcTagCloudValues.isEmpty()){
				
				String[] dcWordValue;
				
				String globalDC = dcTagCloudValues.get(0).split(":")[0];
				HashMap <String, Integer> wordValues = new HashMap <String, Integer>();
				
				for (String pair : dcTagCloudValues){
					
					dcWordValue = pair.split(":");
					String actualDC = dcWordValue[0];
					
					if (!actualDC.equals(globalDC)){
						DataChannel newDC = new DataChannel();
						newDC.setChannelID(globalDC);
						tagCloud.put (newDC, wordValues);
						
						globalDC = actualDC;
						wordValues = new HashMap <String, Integer>();
					}
					
					wordValues.put(dcWordValue[1], Integer.parseInt(dcWordValue[2]));	
					
				}
				
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(globalDC);
				tagCloud.put (newDC, wordValues);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating tag cloud", e);
		}

		LOGGER.info("Got tag cloud");

		return tagCloud;
	
	}
	
	
	// Widget 9 (solo tag cloud). dado una lista de datachannels te devuelve tantos tag cloud como datachannels metidos
	public Map<DataChannel, Map<String, Integer>> getTagCloudAccumulated (List<String> dataPoolIdList, Date initDate,Date endDate, Periodicity per, int top){
		
		LOGGER.info("Getting top "+ top + " tag cloud for: "+ initDate.toString() +" to " + endDate.toString() +" y los datachannels: " + dataPoolIdList.toString()+" y per: " + per.toString());
		Map<DataChannel, Map<String, Integer>> tagCloud = new HashMap<DataChannel, Map<String, Integer>>();
		Map<String, Integer> dcTagCloud = new HashMap<String, Integer>();
		XStream xstream = new XStream();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String periodicity = getRowPeriodicity(per.toString());
			
			String type_measure = AggregateConstants.aggregateTagCloud;
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<String> dcTagCloudValues = new ArrayList<String>();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dc = Bytes.toString(r.getRow()).substring(5,13);
					 NavigableMap<byte[], byte[]> res = r.getFamilyMap(AggregateConstants.agg_res_bColFamily);

					 for (byte[] key : res.keySet()) {
						 String word = Bytes.toString(key);
						 String value = Bytes.toString(res.get(key));
						 dcTagCloudValues.add(dc+":"+word+":"+value);
					 }
				}
			}
						
			if (!dcTagCloudValues.isEmpty()){
				
				String[] dcWordValue;
				
				String globalDC = dcTagCloudValues.get(0).split(":")[0];
				HashMap <String, Integer> wordValues = new HashMap <String, Integer>();
				
				for (String pair : dcTagCloudValues){
					
					dcWordValue = pair.split(":");
					String actualDC = dcWordValue[0];
					
					if (!actualDC.equals(globalDC)){
						DataChannel newDC = new DataChannel();
						newDC.setChannelID(globalDC);
						
						if (wordValues.containsKey(dcWordValue[1])){
							int newDcWordValue = wordValues.get(dcWordValue[1])+Integer.parseInt(dcWordValue[2]);
							wordValues.put(dcWordValue[1], newDcWordValue);
						}
						else{
							wordValues.put(dcWordValue[1], Integer.parseInt(dcWordValue[2]));
						}
						
						//get top N words
						HashMap <String, Integer> finalWordValues = new HashMap <String, Integer>();
						
						List list = new LinkedList(wordValues.entrySet());
						 
						Collections.sort(list, new Comparator() {
							public int compare(Object o1, Object o2) {
								return ((Comparable) ((Map.Entry) (o2)).getValue())
											.compareTo(((Map.Entry) (o1)).getValue());
							}
						});
					 
						Map<String, Integer> wordValues_sorted = new LinkedHashMap();
						for (Iterator it = list.iterator(); it.hasNext();) {
							Map.Entry entry = (Map.Entry) it.next();
							wordValues_sorted.put((String)entry.getKey(), (Integer)entry.getValue());
							//LOGGER.info("wordValues_sorted: " + entry.getKey() + " : " + entry.getValue());
						}
						
						//get TOP N
						int n = top;
						if (wordValues_sorted.keySet().size()<n){
							n = wordValues_sorted.keySet().size();
						}
						//LOGGER.info("N final value: " + n);
						
						int i = 0;
						for (String sorted_word : wordValues_sorted.keySet()){
							if (i==n) break;
							//LOGGER.info("sorted_word: " + sorted_word);
							finalWordValues.put(sorted_word, wordValues_sorted.get(sorted_word));
							i++;
							
						}
						
						tagCloud.put (newDC, finalWordValues);
						
						globalDC = actualDC;
						wordValues = new HashMap <String, Integer>();
					}
					
					if (wordValues.containsKey(dcWordValue[1])){
						int newDcWordValue = wordValues.get(dcWordValue[1])+Integer.parseInt(dcWordValue[2]);
						wordValues.put(dcWordValue[1], newDcWordValue);
					}
					else{
						wordValues.put(dcWordValue[1], Integer.parseInt(dcWordValue[2]));
					}
					
				}
				
				//get top N words
				HashMap <String, Integer> finalWordValues = new HashMap <String, Integer>();
				
				List list = new LinkedList(wordValues.entrySet());
				 
				Collections.sort(list, new Comparator() {
					public int compare(Object o1, Object o2) {
						return ((Comparable) ((Map.Entry) (o2)).getValue())
									.compareTo(((Map.Entry) (o1)).getValue());
					}
				});
			 
				Map<String, Integer> wordValues_sorted = new LinkedHashMap();
				for (Iterator it = list.iterator(); it.hasNext();) {
					Map.Entry entry = (Map.Entry) it.next();
					wordValues_sorted.put((String)entry.getKey(), (Integer)entry.getValue());
					//LOGGER.info("wordValues_sorted: " + entry.getKey() + " : " + entry.getValue());
				}
				
				//get TOP N
				int n = top;
				if (wordValues_sorted.keySet().size()<n){
					n = wordValues_sorted.keySet().size();
				}
				//LOGGER.info("N final value: " + n);
				
				int i = 0;
				for (String sorted_word : wordValues_sorted.keySet()){
					if (i==n) break;
					//LOGGER.info("sorted_word: " + sorted_word);
					finalWordValues.put(sorted_word, wordValues_sorted.get(sorted_word));
					i++;
					
				}
				
				DataChannel newDC = new DataChannel();
				newDC.setChannelID(globalDC);
				tagCloud.put (newDC, finalWordValues);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating tag cloud", e);
		}

		LOGGER.info("Got tag cloud");

		return tagCloud;
	
	}

	
	
	private String getAggregateRowkey(String periodicity, String volume, String dcID, Date date){
		
		String stringDate = stringDate(date);
		
		String rowkey = periodicity+volume+dcID+stringDate;
		
		return rowkey;
	}

	private String getRowPeriodicity(String periodicity){
		
		if (periodicity.equals(AggregateConstants.daily)) {
			return AggregateConstants.aggregateDaily;
		}
		else if (periodicity.equals(AggregateConstants.hourly)) {
			return AggregateConstants.aggregateHourly;
		}
		else if (periodicity.equals(AggregateConstants.minutes30)) {
			return AggregateConstants.aggregateMinutes30;
		}
		else if (periodicity.equals(AggregateConstants.minutes15)) {
			return AggregateConstants.aggregateMinutes15;
		}
		else if (periodicity.equals(AggregateConstants.minutes5)) {
			return AggregateConstants.aggregateMinutes5;
		}
		else if (periodicity.equals(AggregateConstants.minutes1)){
			return AggregateConstants.aggregateMinutes1;
		}
		return AggregateConstants.aggregateOther;
	}
	
	private String stringDate(Date date){
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		calendar.setTime(date);
		String year = String.format("%04d", calendar.get(Calendar.YEAR));
		String month = String.format("%02d",calendar.get(Calendar.MONTH)+1);
		String day = String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
		String hour = String.format("%02d",calendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.format("%02d",calendar.get(Calendar.MINUTE));
		String second = String.format("%02d",calendar.get(Calendar.SECOND));
		
		String stringDate = year+month+day+hour+minute+second;
		
		return stringDate;
	}
	
	private String stringDateDaily(Date date){
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		calendar.setTime(date);
		String year = String.format("%04d", calendar.get(Calendar.YEAR));
		String month = String.format("%02d",calendar.get(Calendar.MONTH)+1);
		String day = String.format("%02d",calendar.get(Calendar.DAY_OF_MONTH));
		
		String stringDate = year+month+day+"000000";
		
		return stringDate;
	}
	
	public Date rowkeyToDate(String sDate) throws ParseException{
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		calendar.setTime(new Date());
		int year = Integer.parseInt(sDate.substring(0,4));
		int month = Integer.parseInt(sDate.substring(4,6));
		int day = Integer.parseInt(sDate.substring(6,8));
		int hour = Integer.parseInt(sDate.substring(8,10));
		int minute = Integer.parseInt(sDate.substring(10,12));
		int second = Integer.parseInt(sDate.substring(12,14));
		
		calendar.set(Calendar.YEAR,year); 
		calendar.set(Calendar.MONTH,month-1); 
		calendar.set(Calendar.DAY_OF_MONTH,day); 
		calendar.set(Calendar.HOUR_OF_DAY,hour); 
		calendar.set(Calendar.MINUTE,minute); 
		calendar.set(Calendar.SECOND,second); 
		calendar.set(Calendar.MILLISECOND,0); 
		
		return calendar.getTime();
	}

	
	// **********************************************************************************************************
	// ******************** Methods for processes *************************************************
	// **********************************************************************************************************
	
	public void storeAGPid(String aggID, String pid) throws IOException {
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "AG"+aggID;
		
		Put put = new Put(Bytes.toBytes(rowkey));
		put.addColumn(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid"),Bytes.toBytes(pid));
		table.put(put);
		
		table.close();
	}
	
	public void deleteAGPid(String aggID) throws IOException {
		
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "AG"+aggID;
		
		Delete dlt = new Delete(rowkey.getBytes());
		table.delete(dlt);
		
		table.close();
	}
	
	public String getAGPid(String aggID) throws IOException {
		
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "AG"+aggID;
		
		Get g=new Get(rowkey.getBytes());
	      
	    Result r=table.get(g);
	    		    
	    byte[] v=r.getValue(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid"));
	    String pid=Bytes.toString(v);
		
		return pid;
	}
	
	public Map<String, String> getAGPids() throws IOException {
		
		Map<String,String> pids = new HashMap<String,String>();
		
		Table table = connection.getTable(captureProcessTableName);
		Scan scan = new Scan();
		scan.setStartRow("AG".getBytes());
		scan.setStopRow("AH".getBytes());
		ResultScanner resultScanner = table.getScanner(scan);
		
		for (Result r : resultScanner) {
			String rowkey = new String(r.getRow());
			String aggID = rowkey.substring(2);
			String pid = new String(r.getValue(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid")));
			pids.put(pid, aggID);
		}
		
		return pids;
		
	}

	public void updateDataSource(QueryData query) throws Exception {
		//LOGGER.info("Update DS: " + query.getDcID());
		DataChannel dc = getDcById(query.getDcID());
		for (DataSource ds : dc.getDataSources()) {
			if (ds.getSourceID().equalsIgnoreCase(query.getDsID())) {
				if (ds instanceof TwitterDataSource) {
					((TwitterDataSource) ds).setFromLastTweetId(query.isFromLastID());
					((TwitterDataSource) ds).setLastTweetId(query.getLastID());
					((TwitterDataSource) ds).setState(query.getState());
					((TwitterDataSource) ds).setTotalTweetCount(query.getTotalTweetCount());
					((TwitterDataSource) ds).setFromLastTweetId(query.isFromLastID());
					((TwitterDataSource) ds).setChronologicalOrder(query.isChronologicalOrder());
				}
				
			}
		}
		
		//removeDcById(dc.getChannelID());
		storeDataChannel(dc, true);
	}

	
	// **********************************************************************************************************
	// ******************** Methods for brand management metrics *************************************************
	// **********************************************************************************************************
		
	
	public Map<Date, Map<TwitterUser, AmbassadorQuality>> getAmbassadorQuality(List<String> userIdList, String code,
			Date initDate, Date endDate, Periodicity per, List<Double> weightList) {
				
		LOGGER.info("Getting AmbassadorQuality for: "+ initDate.toString() +" and: "+ endDate.toString() +" and users: " + userIdList.toString() +" and factors: " + weightList.toString()+" and code: " + code);
		
		Map<Date, Map<TwitterUser, AmbassadorQuality>> twitterAmbassadorQuality = new HashMap<Date, Map<TwitterUser, AmbassadorQuality>>();
		
		Map<Date,Map<String,List<Double>>> date_quality_map = new HashMap<Date,Map<String,List<Double>>>();
		
		Map<Date,List<Integer>> date_tweets_list =  new HashMap<Date,List<Integer>>();
		Map<Date,List<Integer>> date_followers_list =  new HashMap<Date,List<Integer>>();
		Map<Date,List<Integer>> date_rts_list =  new HashMap<Date,List<Integer>>();
		Map<Date,List<Integer>> date_favs_list =  new HashMap<Date,List<Integer>>();
		Map<Date,List<Integer>> date_replies_list =  new HashMap<Date,List<Integer>>();
		
		double tweets_factor = 0.0;
		double followers_factor = 0.0;
		double rts_factor = 0.0;
		double favs_factor = 0.0;
		double replies_factor = 0.0;
		
		if (weightList.isEmpty()){
			tweets_factor = 0.25;
			followers_factor = 0.125;
			rts_factor = 0.25;
			favs_factor = 0.25;
			replies_factor = 0.125;
		}
		if (weightList.size() > 0) tweets_factor = weightList.get(0);
		if (weightList.size() > 1) followers_factor = weightList.get(1);
		if (weightList.size() > 2) rts_factor = weightList.get(2);
		if (weightList.size() > 3) favs_factor = weightList.get(3);
		if (weightList.size() > 4) replies_factor = weightList.get(4);
			
		try{
			
			Table table = connection.getTable(captureUserAggregateTableName);
			
			String type = BrandManagementConstants.userAggregateAmbassador;
			if (code.equals(BrandManagementConstants.userAggregateAmbassador_ATOS)){
				type = BrandManagementConstants.userAggregateAmbassador_ATOS;
			}
			
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
						
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
									
			//List<Result> totalResultGet = new ArrayList<Result>();

			for (String userID : userIdList){
				rowkeyinit = getUserAggregateRowkey(type, userID, initDate);
				rowkeyend = getUserAggregateRowkey(type, userID, endDate);
				rowkeyend = rowkeyend.substring(0, rowkeyend.length()-1) + "2";
				
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				/*
				Get g=new Get(rowkeyinit.getBytes());
			    Result r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    g=new Get(rowkeyend.getBytes());
			    r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    */
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					//for (Result r : totalResultGet) {	
					String user = Bytes.toString(r.getRow()).substring(2,r.getRow().length-14);
					String date = Bytes.toString(r.getRow()).substring(r.getRow().length-14);
					String tweets_hb = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_ambassador.getBytes(), BrandManagementConstants.userAggregate_col_ambassador_tweets.getBytes()));
					String followers_hb = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_ambassador.getBytes(), BrandManagementConstants.userAggregate_col_ambassador_followers.getBytes()));
					String rts_hb = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_ambassador.getBytes(), BrandManagementConstants.userAggregate_col_ambassador_retweets.getBytes()));
					String favs_hb = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_ambassador.getBytes(), BrandManagementConstants.userAggregate_col_ambassador_favorites.getBytes()));
					String replies_hb = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_ambassador.getBytes(), BrandManagementConstants.userAggregate_col_ambassador_replies.getBytes()));
					
					int tweets = 0;
					int followers = 0;
					int rts = 0;
					int favs = 0;
					int replies = 0;
					
					try{
						tweets = Integer.parseInt(tweets_hb);
					}catch (NumberFormatException nfe){
						LOGGER.info("Tweets NumberFormatException: "+nfe.getMessage());
					}
					
					try{
						followers = Integer.parseInt(followers_hb);
					}catch (NumberFormatException nfe){
						LOGGER.info("Followers NumberFormatException: "+nfe.getMessage());
					}
					
					try{
						rts = Integer.parseInt(rts_hb);
					}catch (NumberFormatException nfe){
						LOGGER.info("RTS NumberFormatException: "+nfe.getMessage());
					}
					
					try{
						favs = Integer.parseInt(favs_hb);
					}catch (NumberFormatException nfe){
						LOGGER.info("FAVS NumberFormatException: "+nfe.getMessage());
					}
					
					try{
						replies = Integer.parseInt(replies_hb);
					}catch (NumberFormatException nfe){
						LOGGER.info("Replies NumberFormatException: "+nfe.getMessage());
					}
					
					
					Map<String,List<Double>> quality_map = new HashMap<String,List<Double>>();
					List<Double> quality_values = new ArrayList<Double>();
					
					quality_values.add((double) tweets);
					quality_values.add((double) followers);
					quality_values.add((double) rts);
					quality_values.add((double) favs);
					quality_values.add((double) replies);
					
					if (date_quality_map.containsKey(rowkeyToDate(date))){
						quality_map = date_quality_map.get(rowkeyToDate(date));
					}
					quality_map.put(user, quality_values);
					date_quality_map.put(rowkeyToDate(date), quality_map);
					
					
					List<Integer> actual_data = new ArrayList<Integer>();
					if (date_tweets_list.containsKey(rowkeyToDate(date))){
						actual_data = date_tweets_list.get(rowkeyToDate(date));			
					}
					actual_data.add(tweets);
					date_tweets_list.put(rowkeyToDate(date), actual_data);
					
					actual_data = new ArrayList<Integer>();
					if (date_followers_list.containsKey(rowkeyToDate(date))){
						actual_data = date_followers_list.get(rowkeyToDate(date));			
					}
					actual_data.add(followers);
					date_followers_list.put(rowkeyToDate(date), actual_data);
					
					actual_data = new ArrayList<Integer>();
					if (date_rts_list.containsKey(rowkeyToDate(date))){
						actual_data = date_rts_list.get(rowkeyToDate(date));
					}
					actual_data.add(rts);
					date_rts_list.put(rowkeyToDate(date), actual_data);
					
					actual_data = new ArrayList<Integer>();
					if (date_favs_list.containsKey(rowkeyToDate(date))){
						actual_data = date_favs_list.get(rowkeyToDate(date));		
					}
					actual_data.add(favs);
					date_favs_list.put(rowkeyToDate(date), actual_data);
					
					actual_data = new ArrayList<Integer>();
					if (date_replies_list.containsKey(rowkeyToDate(date))){
						actual_data = date_replies_list.get(rowkeyToDate(date));
					}
					actual_data.add(replies);
					date_replies_list.put(rowkeyToDate(date), actual_data);
					
				}
			}
			
			
			for (Date date : date_quality_map.keySet()){
				
				Integer max_tweets = Collections.max(date_tweets_list.get(date));
				Integer max_followers = Collections.max(date_followers_list.get(date));
				Integer max_rts = Collections.max(date_rts_list.get(date));
				Integer max_favs = Collections.max(date_favs_list.get(date));
				Integer max_replies = Collections.max(date_replies_list.get(date));
				
				Map<String, List<Double>> quality_map = date_quality_map.get(date);
				
				for (String embid : quality_map.keySet()){
					
					double quality = 0;
					
					List<Double> parcial = quality_map.get(embid);
					Double parcial_tweets = parcial.get(0);
					Double parcial_followers = parcial.get(1);
					Double parcial_rts = parcial.get(2);
					Double parcial_favs = parcial.get(3);
					Double parcial_replies = parcial.get(4);
					
					double quality_tweets = 0.0;
					double quality_followers = 0.0;
					double quality_rts = 0.0;
					double quality_favs = 0.0;
					double quality_replies = 0.0;
					
					if (max_tweets > 0) quality_tweets = parcial_tweets / max_tweets;
					if (max_followers > 0) quality_followers = parcial_followers / max_followers;
					if (max_rts > 0) quality_rts = parcial_rts / max_rts;
					if (max_favs > 0) quality_favs = parcial_favs / max_favs;
					if (max_replies > 0) quality_replies = parcial_replies / max_replies;
					
					quality = quality_tweets*tweets_factor + quality_followers*followers_factor + 
							quality_rts*rts_factor + quality_favs*favs_factor + 
							quality_replies*replies_factor;
					
					
					Map<TwitterUser, AmbassadorQuality> parcial_twitterAmbassadorQuality = new HashMap<TwitterUser, AmbassadorQuality>();
					
					TwitterUser tu = new TwitterUser();
					tu.setId(embid);
					
					AmbassadorQuality aq = new AmbassadorQuality();
					aq.setQuality(quality);
					
					List<AmbassadorQualityFactor> list_aqf = new ArrayList<AmbassadorQualityFactor>();
					
					AmbassadorQualityFactor aqf = new AmbassadorQualityFactor();
					aqf.setName("tweets");
					aqf.setValue(parcial_tweets.intValue());
					aqf.setWeight(tweets_factor);
					list_aqf.add(aqf);
					aqf = new AmbassadorQualityFactor();
					aqf.setName("followers");
					aqf.setValue(parcial_followers.intValue());
					aqf.setWeight(followers_factor);
					list_aqf.add(aqf);
					aqf = new AmbassadorQualityFactor();
					aqf.setName("retweets");
					aqf.setValue(parcial_rts.intValue());
					aqf.setWeight(rts_factor);
					list_aqf.add(aqf);
					aqf = new AmbassadorQualityFactor();
					aqf.setName("favorites");
					aqf.setValue(parcial_favs.intValue());
					aqf.setWeight(favs_factor);
					list_aqf.add(aqf);
					aqf = new AmbassadorQualityFactor();
					aqf.setName("replies");
					aqf.setValue(parcial_replies.intValue());
					aqf.setWeight(replies_factor);
					list_aqf.add(aqf);
					
					aq.setFactors(list_aqf);
					
					if (twitterAmbassadorQuality.containsKey(date)){
						parcial_twitterAmbassadorQuality = twitterAmbassadorQuality.get(date);
					}
					
					parcial_twitterAmbassadorQuality.put(tu, aq);
			
					twitterAmbassadorQuality.put(date, parcial_twitterAmbassadorQuality);
									
				}
				
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating AmbassadorQuality", e);
		}

		LOGGER.info("Got AmbassadorQuality");

		return twitterAmbassadorQuality;
	}
	
	
	public Map<TwitterUser, AmbassadorQuality> getAmbassadorQualityTop(List<String> userIdList, int top, String code,
			Date initDate, Date endDate, Periodicity per, List<Double> weightList) {
				
		LOGGER.info("Getting Top " + top + " AmbassadorQuality for: "+ initDate.toString() +" and: "+ endDate.toString() +" and users: " + userIdList.toString() +" and factors: " + weightList.toString()+" and code: " + code);
		
		double tweets_factor = 0.0;
		double followers_factor = 0.0;
		double rts_factor = 0.0;
		double favs_factor = 0.0;
		double replies_factor = 0.0;
		
		if (weightList.isEmpty()){
			tweets_factor = 0.25;
			followers_factor = 0.125;
			rts_factor = 0.25;
			favs_factor = 0.25;
			replies_factor = 0.125;
		}
		if (weightList.size() > 0) tweets_factor = weightList.get(0);
		if (weightList.size() > 1) followers_factor = weightList.get(1);
		if (weightList.size() > 2) rts_factor = weightList.get(2);
		if (weightList.size() > 3) favs_factor = weightList.get(3);
		if (weightList.size() > 4) replies_factor = weightList.get(4);
		
		//get Map with all values from init to end date
		Map<Date, Map<TwitterUser, AmbassadorQuality>> twitterAmbassadorQuality = getAmbassadorQuality(userIdList, code, initDate, endDate, per, weightList);

		//get total days
		int totalDates = twitterAmbassadorQuality.size();
		LOGGER.info("totalDates: " + totalDates);
		
		//save intermediate results
		Map<TwitterUser, AmbassadorQuality> userAmbassadorQuality_total = new HashMap<TwitterUser, AmbassadorQuality>();
		Map<String, Double> userAmbassadorQuality_quality = new HashMap<String, Double>();
		Map<String, Double> userAmbassadorQuality_tweets = new HashMap<String, Double>();
		Map<String, Double> userAmbassadorQuality_followers = new HashMap<String, Double>();
		Map<String, Double> userAmbassadorQuality_retweets = new HashMap<String, Double>();
		Map<String, Double> userAmbassadorQuality_favorites = new HashMap<String, Double>();
		Map<String, Double> userAmbassadorQuality_replies = new HashMap<String, Double>();
		
		//get each ambassador values per day
		Iterator it = twitterAmbassadorQuality.entrySet().iterator();		
		while (it.hasNext()) {
			Map.Entry eI = (Map.Entry)it.next();
			Map<TwitterUser, AmbassadorQuality> userAmbassadorQuality = (Map<TwitterUser, AmbassadorQuality>)eI.getValue();
			Iterator it2 = userAmbassadorQuality.entrySet().iterator();		
			while (it2.hasNext()) {
				Map.Entry eIn = (Map.Entry)it2.next();
				
				String userID = (String)(((TwitterUser)eIn.getKey()).getId());
				AmbassadorQuality userValue = (AmbassadorQuality)eIn.getValue();
				
				double quality = userValue.getQuality();
				double tweets = 0;
				double followers = 0;
				double retweets = 0;
				double favorites = 0;
				double replies = 0;
				
				//get actual values
				List<AmbassadorQualityFactor> list_aqf = userValue.getFactors();
				for (AmbassadorQualityFactor aqf : list_aqf){
					if (aqf.getName().equals("tweets")){
						tweets = aqf.getValue();
					}
					if (aqf.getName().equals("followers")){
						followers = aqf.getValue();
					}
					if (aqf.getName().equals("retweets")){
						retweets = aqf.getValue();
					}
					if (aqf.getName().equals("favorites")){
						favorites = aqf.getValue();
					}
					if (aqf.getName().equals("replies")){
						replies = aqf.getValue();
					}
				}
				
				//sum actual days to intermediate results
				if (userAmbassadorQuality_quality.containsKey(userID)){
					double oldQuality = userAmbassadorQuality_quality.get(userID);
					userAmbassadorQuality_quality.put(userID, oldQuality+quality);
				}
				else {
					userAmbassadorQuality_quality.put(userID, quality);
				}
				
				if (userAmbassadorQuality_tweets.containsKey(userID)){
					double oldTweets = userAmbassadorQuality_tweets.get(userID);
					userAmbassadorQuality_tweets.put(userID, oldTweets+tweets);
				}
				else {
					userAmbassadorQuality_tweets.put(userID, tweets);
				}
				
				if (userAmbassadorQuality_followers.containsKey(userID)){
					double oldFollowers = userAmbassadorQuality_followers.get(userID);
					userAmbassadorQuality_followers.put(userID, oldFollowers+followers);
				}
				else {
					userAmbassadorQuality_followers.put(userID, followers);
				}
				
				if (userAmbassadorQuality_retweets.containsKey(userID)){
					double oldRetweets = userAmbassadorQuality_retweets.get(userID);
					userAmbassadorQuality_retweets.put(userID, oldRetweets+retweets);
				}
				else {
					userAmbassadorQuality_retweets.put(userID, retweets);
				}
				
				if (userAmbassadorQuality_favorites.containsKey(userID)){
					double oldFavorites = userAmbassadorQuality_favorites.get(userID);
					userAmbassadorQuality_favorites.put(userID, oldFavorites+favorites);
				}
				else {
					userAmbassadorQuality_favorites.put(userID, favorites);
				}
				
				if (userAmbassadorQuality_replies.containsKey(userID)){
					double oldReplies = userAmbassadorQuality_replies.get(userID);
					userAmbassadorQuality_replies.put(userID, oldReplies+replies);
				}
				else {
					userAmbassadorQuality_replies.put(userID, replies);
				}

			}
		}
		
		//calculate ambassador quality average
		for (String thisUserID : userAmbassadorQuality_quality.keySet()){
			userAmbassadorQuality_quality.put(thisUserID, userAmbassadorQuality_quality.get(thisUserID)/totalDates);
		}
		for (String thisUserID : userAmbassadorQuality_tweets.keySet()){
			userAmbassadorQuality_tweets.put(thisUserID, userAmbassadorQuality_tweets.get(thisUserID)/totalDates);
		}
		for (String thisUserID : userAmbassadorQuality_followers.keySet()){
			userAmbassadorQuality_followers.put(thisUserID, userAmbassadorQuality_followers.get(thisUserID)/totalDates);
		}
		for (String thisUserID : userAmbassadorQuality_retweets.keySet()){
			userAmbassadorQuality_retweets.put(thisUserID, userAmbassadorQuality_retweets.get(thisUserID)/totalDates);
		}
		for (String thisUserID : userAmbassadorQuality_favorites.keySet()){
			userAmbassadorQuality_favorites.put(thisUserID, userAmbassadorQuality_favorites.get(thisUserID)/totalDates);
		}
		for (String thisUserID : userAmbassadorQuality_replies.keySet()){
			userAmbassadorQuality_replies.put(thisUserID, userAmbassadorQuality_replies.get(thisUserID)/totalDates);
		}
		
		LOGGER.info("userAmbassadorQuality_quality: " + userAmbassadorQuality_quality.toString());
		LOGGER.info("userAmbassadorQuality_quality size: " + userAmbassadorQuality_quality.size());
		
		//order quality values
		List list = new LinkedList(userAmbassadorQuality_quality.entrySet());
		 
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
							.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
	 
		Map<String, Double> userAmbassadorQuality_quality_sorted = new LinkedHashMap();
		for (Iterator it3 = list.iterator(); it3.hasNext();) {
			Map.Entry entry = (Map.Entry) it3.next();
			userAmbassadorQuality_quality_sorted.put((String)entry.getKey(), (Double)entry.getValue());
			LOGGER.info("userAmbassadorQuality_quality_sorted: " + entry.getKey() + " : " + entry.getValue());
		}
		
		//get TOP N
		int n = top;
		if (userAmbassadorQuality_quality_sorted.keySet().size()<n){
			n = userAmbassadorQuality_quality_sorted.keySet().size();
		}
		LOGGER.info("N final value: " + n);
		
		int i = 0;
		for (String sorted_userID : userAmbassadorQuality_quality_sorted.keySet()){
			if (i==n) break;
			
			LOGGER.info("sorted_userID: " + sorted_userID);
							
			TwitterUser tu = new TwitterUser();
			tu.setId(sorted_userID);
			
			AmbassadorQuality aq = new AmbassadorQuality();
			aq.setQuality(userAmbassadorQuality_quality.get(sorted_userID));
			LOGGER.info("sorted_quality: " + userAmbassadorQuality_quality.get(sorted_userID));
			
			List<AmbassadorQualityFactor> list_aqf = new ArrayList<AmbassadorQualityFactor>();
			
			AmbassadorQualityFactor aqf = new AmbassadorQualityFactor();
			aqf.setName("tweets");
			aqf.setValue(userAmbassadorQuality_tweets.get(sorted_userID).intValue());
			aqf.setWeight(tweets_factor);
			list_aqf.add(aqf);
			aqf = new AmbassadorQualityFactor();
			aqf.setName("followers");
			aqf.setValue(userAmbassadorQuality_followers.get(sorted_userID).intValue());
			aqf.setWeight(followers_factor);
			list_aqf.add(aqf);
			aqf = new AmbassadorQualityFactor();
			aqf.setName("retweets");
			aqf.setValue(userAmbassadorQuality_retweets.get(sorted_userID).intValue());
			aqf.setWeight(rts_factor);
			list_aqf.add(aqf);
			aqf = new AmbassadorQualityFactor();
			aqf.setName("favorites");
			aqf.setValue(userAmbassadorQuality_favorites.get(sorted_userID).intValue());
			aqf.setWeight(favs_factor);
			list_aqf.add(aqf);
			aqf = new AmbassadorQualityFactor();
			aqf.setName("replies");
			aqf.setValue(userAmbassadorQuality_replies.get(sorted_userID).intValue());
			aqf.setWeight(replies_factor);
			list_aqf.add(aqf);
			
			aq.setFactors(list_aqf);
			
			userAmbassadorQuality_total.put(tu, aq);
			
			i++;
			
		}
		
		LOGGER.info("userAmbassadorQuality_total size: " + userAmbassadorQuality_total.size());
		LOGGER.info("Got Top " + top + " AmbassadorQuality");

		return userAmbassadorQuality_total;
	}
	
	
	// Widget 1 (reach). dado una lista de users te devuelve tantos R
	// Devuelve una lista de tantos elementos como slots (en caso del widget 1: dos dias, el de ayer, y el de antesdeayer), y en cada slot, N elementos de <user, Reach>
	// donde Reach es de tipo Reach			
	public Map<Date, Map<TwitterUser, ReachAnalysis>> getTwitterReach(List<String> userIdList, Date initDate,
			Date endDate, Periodicity per) {
		
		LOGGER.info("Getting twitter reach for: "+ initDate.toString() +" and: "+ endDate.toString() +" y los users: " + userIdList.toString());
		Map<Date, Map<TwitterUser, ReachAnalysis>> twitterReach = new HashMap<Date, Map<TwitterUser, ReachAnalysis>>();
		try{
			
			Table table = connection.getTable(captureUserAggregateTableName);
			
			String type = BrandManagementConstants.userAggregateReach;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
						
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
									
			//List<Result> totalResultGet = new ArrayList<Result>();

			for (String userID : userIdList){
				rowkeyinit = getUserAggregateRowkey(type, userID, initDate);
				rowkeyend = getUserAggregateRowkey(type, userID, endDate);
				rowkeyend = rowkeyend.substring(0, rowkeyend.length()-1) + "2";
				
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				/*
				Get g=new Get(rowkeyinit.getBytes());
			    Result r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    g=new Get(rowkeyend.getBytes());
			    r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    */
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					//for (Result r : totalResultGet) {	
					String user = Bytes.toString(r.getRow()).substring(2,r.getRow().length-14);
					String date = Bytes.toString(r.getRow()).substring(r.getRow().length-14);
					String followers = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_followers.getBytes()));
					String mentions = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_mentions.getBytes()));
					String retweets = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_retweets.getBytes()));
					String subfollowers = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_subfollowers.getBytes()));
					String reach = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_total.getBytes()));
					
					Map<TwitterUser, ReachAnalysis> parcial_twitterReach = new HashMap<TwitterUser, ReachAnalysis>();
					
					TwitterUser tu = new TwitterUser();
					tu.setId(user);
					
					ReachAnalysis ra = new ReachAnalysis();
					ra.setFollowers(Integer.parseInt(followers));
					ra.setMentioners(Integer.parseInt(mentions));
					ra.setRetweeters(Integer.parseInt(retweets));
					ra.setSubFollowers(Integer.parseInt(subfollowers));
					ra.setReach(Integer.parseInt(reach));
					
					parcial_twitterReach.put(tu, ra);
					
					if (twitterReach.containsKey(rowkeyToDate(date))){
						parcial_twitterReach = twitterReach.get(rowkeyToDate(date));
						parcial_twitterReach.put(tu, ra);
					}
					
					twitterReach.put(rowkeyToDate(date), parcial_twitterReach);
					}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating reach", e);
		}

		LOGGER.info("Got reach");

		return twitterReach;
	}
	
	// Widget 1 (engagement). dado una lista de users te devuelve tantos R
	// Devuelve una lista de tantos elementos como slots (en caso del widget 1: dos dias, el de ayer, y el de antesdeayer), y en cada slot, N elementos de <user, Engagement>
	// donde Engagement es de tipo Engagement
	public Map<Date, Map<TwitterUser, EngagementAnalysis>> getTwitterEngagement(List<String> userIdList, Date initDate,
			Date endDate, Periodicity per) {
		
		LOGGER.info("Getting twitter engagement for: "+ initDate.toString() +" and: "+ endDate.toString() +" y los users: " + userIdList.toString());
		Map<Date, Map<TwitterUser, EngagementAnalysis>> twitterEngagement = new HashMap<Date, Map<TwitterUser, EngagementAnalysis>>();
		try{
			
			Table table = connection.getTable(captureUserAggregateTableName);
			
			String type = BrandManagementConstants.userAggregateEngagement;
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
					
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
					
			//List<Result> totalResultGet = new ArrayList<Result>();
			
			for (String userID : userIdList){
				rowkeyinit = getUserAggregateRowkey(type, userID, initDate);
				rowkeyend = getUserAggregateRowkey(type, userID, endDate);
				rowkeyend = rowkeyend.substring(0, rowkeyend.length()-1) + "2";
				
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				/*
				Get g=new Get(rowkeyinit.getBytes());
			    Result r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    g=new Get(rowkeyend.getBytes());
			    r=table.get(g);
			    if (!r.isEmpty()){
			    	totalResultGet.add(r);
			    }
			    */
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					//for (Result r : totalResultGet) {
					String user = Bytes.toString(r.getRow()).substring(2,r.getRow().length-14);
					String date = Bytes.toString(r.getRow()).substring(r.getRow().length-14);
					String retweets = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_retweets.getBytes()));
					String replies = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_replies.getBytes()));
					String mentions = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_mentions.getBytes()));
					String favorites = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_favorites.getBytes()));
					String engagement = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_total.getBytes()));
					//String mostRetweeted = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_influential_retweets.getBytes()));
					//String mostReplied = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_influential_replies.getBytes()));
					
					
					Map<TwitterUser, EngagementAnalysis> parcial_twitterEngagement = new HashMap<TwitterUser, EngagementAnalysis>();
					
					TwitterUser tu = new TwitterUser();
					tu.setId(user);
					
					EngagementAnalysis ea = new EngagementAnalysis();
					ea.setRetweets(Integer.parseInt(retweets));
					ea.setReplies(Integer.parseInt(replies));
					ea.setMentions(Integer.parseInt(mentions));
					ea.setFavorites(Integer.parseInt(favorites));
					ea.setEngagement(Integer.parseInt(engagement));
					
					/*
					List<String> mostRetweetedList = null;
				    if (!"".equals(mostRetweeted) && mostRetweeted != null){
				    	mostRetweetedList = new ArrayList<String>();
				    	for (String item: mostRetweeted.split(",")){
				    		mostRetweetedList.add(item.trim());
				    	}
				    }
				    ea.setMostRetweeted(mostRetweetedList);
				    
				    List<String> mostRepliedList = null;
				    if (!"".equals(mostReplied) && mostReplied != null){
				    	mostRepliedList = new ArrayList<String>();
				    	for (String item: mostReplied.split(",")){
				    		mostRepliedList.add(item.trim());
				    	}
				    }
					ea.setMostReplied(mostRepliedList);
					*/
					parcial_twitterEngagement.put(tu, ea);
					
					if (twitterEngagement.containsKey(rowkeyToDate(date))){
						parcial_twitterEngagement = twitterEngagement.get(rowkeyToDate(date));
						parcial_twitterEngagement.put(tu,ea);
					}
					
					twitterEngagement.put(rowkeyToDate(date), parcial_twitterEngagement);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating engagement", e);
		}

		LOGGER.info("Got engagement");

		return twitterEngagement;
		
	}
	
	
	public Map<String,Integer> getTwitterInfluential(String userID, Date date, String top) {
		
		LOGGER.info("Getting twitter influential ("+top+") for: "+ date.toString() +" y el user: " + userID.toString());

		Map<String,Integer> returnMap = new HashMap<String,Integer>();
		
		try{
			
			Table table = connection.getTable(captureUserAggregateTableName);
			
			String type = BrandManagementConstants.userAggregateInfluential;
								
			String rowkey = getUserAggregateRowkey(type, userID, date);
				
			Get g=new Get(rowkey.getBytes());
			
		    Result r=table.get(g);
		    
		    if (!r.isEmpty()){
		    			    	
				if (top.equals("retweeted")){
					String mostRetweeted = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_retweets.getBytes()));
		    		Map<String,Integer> mostRetweetedList = new HashMap<String,Integer>();
				    if (!"".equals(mostRetweeted) && mostRetweeted != null){
				    	for (String item: mostRetweeted.split("[|]")){
				    		String[] itemSplit = item.split(",");
				    		if (itemSplit.length == 2){
				    			mostRetweetedList.put(itemSplit[0],Integer.parseInt(itemSplit[1]));
				    		}
				    	}
				    }
			    	returnMap = mostRetweetedList;
			    }
			    else if (top.equals("replied")){
					String mostReplied = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_replies.getBytes()));
			    	Map<String,Integer> mostRepliedList = new HashMap<String,Integer>();
				    if (!"".equals(mostReplied) && mostReplied != null){
				    	for (String item: mostReplied.split("[|]")){
				    		String[] itemSplit = item.split(",");
				    		if (itemSplit.length == 2){
				    			mostRepliedList.put(itemSplit[0],Integer.parseInt(itemSplit[1]));
				    		}
				    	}
				    }
				    returnMap = mostRepliedList;
			    }
			    else if (top.equals("mentioned")){
					String mostMentioned = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_mentions.getBytes()));
					Map<String,Integer> mostMentionedList = new HashMap<String,Integer>();
				    if (!"".equals(mostMentioned) && mostMentioned != null){
				    	for (String item: mostMentioned.split("[|]")){
				    		String[] itemSplit = item.split(",");
				    		if (itemSplit.length == 2){
					    		mostMentionedList.put(itemSplit[0],Integer.parseInt(itemSplit[1]));
				    		}
				    	}
				    }
			    	returnMap = mostMentionedList;
			    }
			    else if (top.equals("influencers")){
					String mostInfluencers = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_influencers.getBytes()));
			    	Map<String,Integer> mostInfluencersList = new HashMap<String,Integer>();
				    if (!"".equals(mostInfluencers) && mostInfluencers != null){
				    	for (String item: mostInfluencers.split("[|]")){
				    		String[] itemSplit = item.split(",");
				    		if (itemSplit.length == 2){
				    			mostInfluencersList.put(itemSplit[0],Integer.parseInt(itemSplit[1]));
				    		}
				    	}
				    }
			    	returnMap = mostInfluencersList;
			    }
		    }
		    
		    table.close();
		    
		    LOGGER.info("Got influential");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating influential", e);
		}
		
		return returnMap;

	}
	
	
	public Map<String, Map<TwitterUser, CompetitorAnalysis>>  getTwitterCompetitors(String userID, Date date) {
		
		LOGGER.info("Getting twitter competitors for: "+ date.toString() +" y el user: " + userID.toString());

		Map<String, Map<TwitterUser, CompetitorAnalysis>> twitterCompetitors = new HashMap<String, Map<TwitterUser, CompetitorAnalysis>>();
		
		try{

			Table table = connection.getTable(captureUserAggregateTableName);
			String type;
			String rowkey;
												
			List<Result> totalResultGet = new ArrayList<Result>();
			
			Brand br = getBrand(userID);
			List<String> competitors = br.getCompetitors();
			
			if (competitors != null){
				
				for (String competitor : competitors){
					
					type = BrandManagementConstants.userAggregateReach;
					rowkey = getUserAggregateRowkey(type, competitor, date);
					
					Get g=new Get(rowkey.getBytes());
				    Result r=table.get(g);
				    if (!r.isEmpty()){
				    	totalResultGet.add(r);
				    }
				    
				    type = BrandManagementConstants.userAggregateEngagement;
					rowkey = getUserAggregateRowkey(type, competitor, date);
					
					g=new Get(rowkey.getBytes());
				    r=table.get(g);
				    if (!r.isEmpty()){
				    	totalResultGet.add(r);
				    }
				    
				    type = BrandManagementConstants.userAggregateInfluential;
					rowkey = getUserAggregateRowkey(type, competitor, date);
					
					g=new Get(rowkey.getBytes());
				    r=table.get(g);
				    if (!r.isEmpty()){
				    	totalResultGet.add(r);
				    }

				}
			}
			
			Map<TwitterUser, CompetitorAnalysis> parcial_twitterCompetitors = new HashMap<TwitterUser, CompetitorAnalysis>();
			
			for (Result r : totalResultGet) {	
				String rtype = Bytes.toString(r.getRow()).substring(0,2);
				String ruser = Bytes.toString(r.getRow()).substring(2,r.getRow().length-14);
				String rdate = Bytes.toString(r.getRow()).substring(r.getRow().length-14);
					
				TwitterUser tu = new TwitterUser();
				tu.setId(ruser);
				
				CompetitorAnalysis ca = new CompetitorAnalysis();
				
				TwitterUser hbase_tu = null;
				for (TwitterUser ttu : parcial_twitterCompetitors.keySet()){
					if (ttu.getId().equals(tu.getId())){
						ca = parcial_twitterCompetitors.get(ttu);
						hbase_tu = ttu;
					}
				}
				if (hbase_tu != null) {
					parcial_twitterCompetitors.remove(hbase_tu);
				}
				
				if (rtype.equals(BrandManagementConstants.userAggregateReach)){
					String followers =  Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_reach.getBytes(), BrandManagementConstants.userAggregate_col_reach_followers.getBytes()));
					ca.setFollowers(Integer.parseInt(followers));
				}
				else if (rtype.equals(BrandManagementConstants.userAggregateEngagement)){
					String retweets =  Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_retweets.getBytes()));
					String replies =  Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_replies.getBytes()));
					String mentions =  Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_mentions.getBytes()));
					String favorites =  Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_engagement.getBytes(), BrandManagementConstants.userAggregate_col_engagement_favorites.getBytes()));
					
					ca.setFavorites(Integer.parseInt(favorites));
					ca.setMentions(Integer.parseInt(mentions));
					ca.setReplies(Integer.parseInt(replies));
					ca.setRetweets(Integer.parseInt(retweets));
				}
				else if (rtype.equals(BrandManagementConstants.userAggregateInfluential)){
					String mostInfluencers = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_influencers.getBytes()));
			    	Map<String,Integer> mostInfluencersList = new HashMap<String,Integer>();
				    if (!"".equals(mostInfluencers) && mostInfluencers != null){
				    	for (String item: mostInfluencers.split("[|]")){
				    		String[] itemSplit = item.split(",");
				    		if (itemSplit.length == 2){
				    			mostInfluencersList.put(itemSplit[0],Integer.parseInt(itemSplit[1]));
				    		}
				    	}
				    }
					ca.setInfluencers(mostInfluencersList);
					
					String tweets = Bytes.toString(r.getValue(BrandManagementConstants.userAggregate_columnFamily_influential.getBytes(), BrandManagementConstants.userAggregate_col_influential_tweets.getBytes()));	
					List<String> tweets_posted = new ArrayList<String>();
					if (!"".equals(tweets) && tweets != null){
						for (String tweet: tweets.split(",")){
							tweets_posted.add(tweet);
						}
					}
					ca.setTweets(tweets_posted);
				}
				
				parcial_twitterCompetitors.put(tu, ca);
					
			}
			
			twitterCompetitors.put(userID, parcial_twitterCompetitors);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR generating competitors", e);
		}

		LOGGER.info("Got competitors");

		return twitterCompetitors;

	}
	
	
	private String getUserAggregateRowkey(String type, String userID, Date date){
		
		String stringDate = stringDateDaily(date);
		
		//manage ambassador rowkey
		if (type.equals(BrandManagementConstants.userAggregateAmbassador) ||
			type.equals(BrandManagementConstants.userAggregateAmbassador_ATOS)){
			stringDate = stringDate(date);
		}
		
		String rowkey = type+userID+stringDate;
		
		return rowkey;
	}
	
	public String addBrand(Brand brand) throws IOException {
		
		Table table = connection.getTable(captureBrandTableName);
			
		String brandID = brand.getBrandID();
		
		Put put = new Put(Bytes.toBytes(brandID));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_brandID),Bytes.toBytes(brand.getBrandID()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_twitterID),Bytes.toBytes(brand.getTwitterID()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_screenName),Bytes.toBytes(brand.getScreenName()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_name),Bytes.toBytes(brand.getName()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_alternativeNames),Bytes.toBytes(brand.getAlternativeNames().toString()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_dcIDs),Bytes.toBytes(brand.getDcIDs().toString()));
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_competitors),Bytes.toBytes(brand.getCompetitors().toString()));
		
		put.addColumn(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_status),Bytes.toBytes(brand.getStatus().toString()));
		
		table.put(put);
		
		table.close();
		
		return brandID;
		
	}
	
	public void updateBrand(Brand brand, String brandID) throws IOException {
		addBrand(brand);	
	}
	
	
	@SuppressWarnings("null")
	public Brand getBrand(String brandID) throws CaptureException, IOException {
			 
		Brand brand = new Brand();
		Table table = connection.getTable(captureBrandTableName);
		
		Get g=new Get(brandID.getBytes());
		      
	    Result r=table.get(g);
	    		
	    byte[] v = r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_brandID));
	    String brandid =Bytes.toString(v);
	    brand.setBrandID(brandid);
	    
	    v = r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_twitterID));
	    String twitterid =Bytes.toString(v);
	    brand.setTwitterID(twitterid);
		
	    v = r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_screenName));
	    String screenName=Bytes.toString(v);
	    brand.setScreenName(screenName);
	
	    v=r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_name));
	    String name=Bytes.toString(v);
	    brand.setName(name);
	
	    v=r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_alternativeNames));
	    String alternativeNames=Bytes.toString(v);
	    List<String> lalternativeNames = null;
	    if (!"".equals(alternativeNames) && alternativeNames != null){
	    	lalternativeNames = new ArrayList<String>();
	    	for (String item: alternativeNames.split(",")){
	    		lalternativeNames.add(item.trim().replace("[", "").replace("]", ""));
	    	}
	    }
	    brand.setAlternativeNames(lalternativeNames);
	
	    v=r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_dcIDs));
	    String dcIDs=Bytes.toString(v);
	    List<String> ldcIDs = null;
	    if (!"".equals(dcIDs) && dcIDs != null){
	    	ldcIDs = new ArrayList<String>();
	    	for (String item: dcIDs.split(",")){
	    		ldcIDs.add(item.trim().replace("[", "").replace("]", ""));
	    	}
	    }
	    brand.setDcIDs(ldcIDs);
	    
	    v=r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_competitors));
	    String competitors=Bytes.toString(v);
	    List<String> lcompetitors = new ArrayList<String>();
	    if (!"".equals(competitors) && competitors != null){
	    	lcompetitors = new ArrayList<String>();
	    	for (String item: competitors.split(",")){
	    		String itemre = item.trim().replace("[", "").replace("]", "");
	    		if (!"".equals(itemre)){
	    			lcompetitors.add(itemre);
	    		}
	    	}
	    }
	    brand.setCompetitors(lcompetitors);
	    
	    v=r.getValue(captureBrandColFamily.getBytes(), Bytes.toBytes(BrandManagementConstants.captureBrand_col_status));
	    String status=Bytes.toString(v);
	    brand.setStatus(Brand.convertStatus(status));
	    
	    table.close();
	    return brand;
	}
	
	public void deleteBrand(String brandID) throws IOException {
		
		Table table = connection.getTable(captureBrandTableName);
		Delete dlt = new Delete(brandID.getBytes());
		table.delete(dlt);
		table.close();
	}

	public BrandList getBrands() {
		
		try {
			int count = 0;
			Table table = connection.getTable(captureBrandTableName);
			
			Scan scan = new Scan();
			ResultScanner resultScanner = table.getScanner(scan);

			Map<String, Brand> brandMap = new HashMap<>();

			for (Result r : resultScanner) {		
				Brand brand = getBrand(new String(r.getRow()));
				brandMap.put(new String(r.getRow()), brand);
				
			}
			
			BrandList brandl = new BrandList();
			for (Entry<String, Brand> d : brandMap.entrySet()) {
				brandl.addBrand(d.getValue());
			}
			
			table.close();
			return brandl;
			
		} catch (IOException | CaptureException e) {

			LOGGER.log(Level.SEVERE, "Can't retrieve brands list.", e);

			return null;
		}
	}
	
	
	
	public void storeReachAnalysis(List<ReachAnalysis> ocurrenceList) {
		
		for (ReachAnalysis ra: ocurrenceList){
			
			Date storeDate = ra.getaDate();
			String twitterID = ra.getUser().getId();
						
			try {
				
				Table table = connection.getTable(TableName.valueOf(BrandManagementConstants.userAggregate_tablename));
				
				String rowKey = BrandManagementConstants.userAggregateReach+twitterID+stringDateDaily(storeDate);
				
				Put put = new Put(Bytes.toBytes(rowKey));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_followers),Bytes.toBytes(Integer.toString(ra.getFollowers())));
				//put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_followerslist),Bytes.toBytes(reach_followers_list));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_mentions),Bytes.toBytes(Integer.toString(ra.getMentioners())));	
				//put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_mentionslist),Bytes.toBytes(reach_mentions_list));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_retweets),Bytes.toBytes(Integer.toString(ra.getRetweeters())));
				//put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_retweetslist),Bytes.toBytes(reach_retweets_list));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_subfollowers),Bytes.toBytes(Integer.toString(ra.getSubFollowers())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_total),Bytes.toBytes(Integer.toString(ra.getReach())));
				table.put(put);
				
				table.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void storeEngagementAnalysis(List<EngagementAnalysis> ocurrenceList) {
		
		for (EngagementAnalysis ea: ocurrenceList){
			
			Date storeDate = ea.getaDate();
			String twitterID = ea.getUser().getId();
						
			try {
				
				Table table = connection.getTable(TableName.valueOf(BrandManagementConstants.userAggregate_tablename));
				
				String rowKey = BrandManagementConstants.userAggregateEngagement+twitterID+stringDateDaily(storeDate);
				
				Put put = new Put(Bytes.toBytes(rowKey));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_retweets),Bytes.toBytes(Integer.toString(ea.getRetweets())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_replies),Bytes.toBytes(Integer.toString(ea.getReplies())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_mentions),Bytes.toBytes(Integer.toString(ea.getMentions())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_favorites),Bytes.toBytes(Integer.toString(ea.getFavorites())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_total),Bytes.toBytes(Integer.toString(ea.getEngagement())));
				table.put(put);	
				
				table.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void storeInfluentialAnalysis(List<InfluentialAnalysis> ocurrenceList) {
		
		for (InfluentialAnalysis ia: ocurrenceList){
			
			Date storeDate = ia.getaDate();
			String twitterID = ia.getUser().getId();
						
			try {
				
				Table table = connection.getTable(TableName.valueOf(BrandManagementConstants.userAggregate_tablename));
				
				String rowKey = BrandManagementConstants.userAggregateInfluential+twitterID+stringDateDaily(storeDate);
				
				Put put = new Put(Bytes.toBytes(rowKey));
				
				String influential_retweets = "";
				Map<String, Integer> iaRetweets = ia.getRetweets();
				if (!iaRetweets.isEmpty()){
					String influential_retweet_clean = ia.getRetweets().toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
					String[] influential_retweet_pares = influential_retweet_clean.split(",");
					for (String par: influential_retweet_pares){
						String[] values = par.split("=");
						influential_retweets += values[0] + "," + values[1] + "|";
					}
					if (influential_retweets!=""){
						influential_retweets = influential_retweets.substring(0, influential_retweets.length() - 1);
					}	
				}
				LOGGER.info("--"+influential_retweets);
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_retweets),Bytes.toBytes(influential_retweets));
				
				String influential_replies = "";
				Map<String, Integer> iaReplies = ia.getReplies();
				if (!iaReplies.isEmpty()){
					String influential_replies_clean = ia.getReplies().toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
					String[] influential_replies_pares = influential_replies_clean.split(",");
					for (String par: influential_replies_pares){
						String[] values = par.split("=");
						influential_replies += values[0] + "," + values[1] + "|";
					}
					if (influential_replies!=""){
						influential_replies = influential_replies.substring(0, influential_replies.length() - 1);
					}	
				}
				LOGGER.info("--"+influential_replies);
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_replies),Bytes.toBytes(influential_replies));
				
				String influential_mentions = "";
				Map<String, Integer> iaMentions = ia.getMentions();
				if (!iaMentions.isEmpty()){
					String influential_mentions_clean = ia.getMentions().toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
					String[] influential_mentions_pares = influential_mentions_clean.split(",");
					for (String par: influential_mentions_pares){
						String[] values = par.split("=");
						influential_mentions += values[0] + "," + values[1] + "|";
					}
					if (influential_mentions!=""){
						influential_mentions = influential_mentions.substring(0, influential_mentions.length() - 1);
					}	
				}
				LOGGER.info("--"+influential_mentions);
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_mentions),Bytes.toBytes(influential_mentions));
				
				String influential_influencers = "";
				Map<String, Integer> iaInfluencers = ia.getInfluencers();
				if (!iaInfluencers.isEmpty()){
					String influential_influencers_clean = ia.getInfluencers().toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
					String[] influential_influencers_pares = influential_influencers_clean.split(",");
					for (String par: influential_influencers_pares){
						String[] values = par.split("=");
						influential_influencers += values[0] + "," + values[1] + "|";
					}
					if (influential_influencers!=""){
						influential_influencers = influential_influencers.substring(0, influential_influencers.length() - 1);
					}	
				}
				LOGGER.info("--"+influential_influencers);
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_influencers),Bytes.toBytes(influential_influencers));
				
				String personal_tweets = ia.getTweets().toString().replace("[", "").replace("]", "").replace(" ", "");
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_tweets),Bytes.toBytes(personal_tweets));			
				table.put(put);
				
				table.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void storeCompetitorAnalysis(List<CompetitorAnalysis> ocurrenceList) {
		
		for (CompetitorAnalysis ca: ocurrenceList){
			
			Date storeDate = ca.getaDate();
			String twitterID = ca.getUser().getId();
						
			try {
				List<Put> puts = new ArrayList<Put>();
				
				Table table = connection.getTable(TableName.valueOf(BrandManagementConstants.userAggregate_tablename));
				
				//Reach
				String rowKey = BrandManagementConstants.userAggregateReach+twitterID+stringDateDaily(storeDate);
				Put put = new Put(Bytes.toBytes(rowKey));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_reach), Bytes.toBytes(BrandManagementConstants.userAggregate_col_reach_followers),Bytes.toBytes(Integer.toString(ca.getFollowers())));
				//table.put(put);
				puts.add(put);
				
				//Engagement
				rowKey = BrandManagementConstants.userAggregateEngagement+twitterID+stringDateDaily(storeDate);
				put = new Put(Bytes.toBytes(rowKey));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_retweets),Bytes.toBytes(Integer.toString(ca.getRetweets())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_replies),Bytes.toBytes(Integer.toString(ca.getReplies())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_mentions),Bytes.toBytes(Integer.toString(ca.getMentions())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_favorites),Bytes.toBytes(Integer.toString(ca.getFavorites())));
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_engagement), Bytes.toBytes(BrandManagementConstants.userAggregate_col_engagement_total),Bytes.toBytes(Integer.toString(ca.getEngagement())));
				//table.put(put);
				puts.add(put);
				
				//Influential
				rowKey = BrandManagementConstants.userAggregateInfluential+twitterID+stringDateDaily(storeDate);
				put = new Put(Bytes.toBytes(rowKey));
				
				String influential_influencers = "";
				Map<String, Integer> iaInfluencers = ca.getInfluencers();
				if (!iaInfluencers.isEmpty()){
					String influential_influencers_clean = ca.getInfluencers().toString().replace("[", "").replace("]", "").replace("{", "").replace("}", "").replace(" ", "");
					String[] influential_influencers_pares = influential_influencers_clean.split(",");
					for (String par: influential_influencers_pares){
						String[] values = par.split("=");
						influential_influencers += values[0] + "," + values[1] + "|";
					}
					if (influential_influencers!=""){
						influential_influencers = influential_influencers.substring(0, influential_influencers.length() - 1);
					}	
				}
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_influencers),Bytes.toBytes(influential_influencers));
				
				String personal_tweets = ca.getTweets().toString().replace("[", "").replace("]", "").replace(" ", "");
				put.addColumn(Bytes.toBytes(BrandManagementConstants.userAggregate_columnFamily_influential), Bytes.toBytes(BrandManagementConstants.userAggregate_col_influential_tweets),Bytes.toBytes(personal_tweets));			
				//table.put(put);
				puts.add(put);
				
				table.put(puts);
				
				table.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	// **********************************************************************************************************
	// ******************** Methods for brand management processes *************************************************
	// **********************************************************************************************************
		
	public void storeBMPid(String brandID, String pid) throws IOException {
		
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "BM"+brandID;
		
		Put put = new Put(Bytes.toBytes(rowkey));
		put.addColumn(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid"),Bytes.toBytes(pid));
		table.put(put);
		
		table.close();
	}

	public void deleteBMPid(String brandID) throws IOException {
		
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "BM"+brandID;
		
		Delete dlt = new Delete(rowkey.getBytes());
		table.delete(dlt);
		
		table.close();
		
	}

	public String getBMPid(String brandID) throws IOException {
		
		Table table = connection.getTable(captureProcessTableName);
		
		String rowkey = "BM"+brandID;
		
		Get g=new Get(rowkey.getBytes());
	      
	    Result r=table.get(g);
	    		    
	    byte[] v=r.getValue(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid"));
	    String pid=Bytes.toString(v);
		
		return pid;
	}

	public Map<String, String> getBMPids() throws IOException {
		
		Map<String,String> pids = new HashMap<String,String>();
		
		Table table = connection.getTable(captureProcessTableName);
		Scan scan = new Scan();
		scan.setStartRow("BM".getBytes());
		scan.setStopRow("BN".getBytes());
		ResultScanner resultScanner = table.getScanner(scan);
		
		for (Result r : resultScanner) {
			String rowkey = new String(r.getRow());
			String aggID = rowkey.substring(2);
			String pid = new String(r.getValue(Bytes.toBytes(captureProcessColFamily), Bytes.toBytes("pid")));
			pids.put(pid, aggID);
		}
		
		return pids;
	}

	
	
	// **********************************************************************************************************
	// ******************** Auxiliar Methods to get aggregate objects *************************************************
	// **********************************************************************************************************
				
	public List<VolumeOcurrenceAnalysis> getVolumeOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
			
		LOGGER.info("Getting volume ocurrences analysis in HBase...");
		List<VolumeOcurrenceAnalysis> volumeOcurrences = new ArrayList<VolumeOcurrenceAnalysis>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure_total = AggregateConstants.aggregateTotalVolume;
			String type_measure_positive = AggregateConstants.aggregatePositiveVolume;
			String type_measure_negative = AggregateConstants.aggregateNegativeVolume;
			String type_measure_neutro = AggregateConstants.aggregateNeutroVolume;
			
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				//total
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_total,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_total,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				//positive
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_positive,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_positive,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				//negative
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_negative,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_negative,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
				//neutro
				rowkeyinit = getAggregateRowkey(periodicity,type_measure_neutro,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure_neutro,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dcID = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 int value = 0;
					 String type = "";
					 if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())) {
						 value = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateTotalVolume.getBytes())));
						 type = "volume";
					 }
					 else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())) {
						 value = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregatePositiveVolume.getBytes())));
						 type = "sentiment_volume_positive";
					 }
					 else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())) {
						 value = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNegativeVolume.getBytes())));
						 type = "sentiment_volume_negative";
					 }
					 else if (r.containsColumn(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())) {
						 value = Integer.parseInt(Bytes.toString(r.getValue(AggregateConstants.agg_res_bColFamily, AggregateConstants.aggregateNeutroVolume.getBytes())));
						 type = "sentiment_volume_neutro";
					 }
					 
					VolumeOcurrenceAnalysis newVolumeOcurrence = null;
					DataChannel dc = new DataChannel ();
					dc.setChannelID(dcID);
					newVolumeOcurrence = new VolumeOcurrenceAnalysis();
					newVolumeOcurrence.setDataChannel(dc);
					newVolumeOcurrence.setPeriodicity(per);
					newVolumeOcurrence.setaDate(date);
					newVolumeOcurrence.setAnalysisType(PeriodicAnalysisResult.convertAnalysisType(type));
					newVolumeOcurrence.setMeasurement(value);
					
					volumeOcurrences.add(newVolumeOcurrence);
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the volume ocurrences analysis", e);
		}
		LOGGER.info("Got volume ocurrences analysis in HBase");
		return volumeOcurrences;
		
	}

	
	
	public List<TermOccurrenceAnalysis> getTermOcurrenceAnalysis(List<String> dataPoolIdList, Date initDate, Date endDate, Periodicity per){
		
		LOGGER.info("Getting term ocurrences analysis in HBase...");
		List<TermOccurrenceAnalysis> termOcurrences = new ArrayList<TermOccurrenceAnalysis>();
		
		try{
			
			Table table = connection.getTable(captureAggregateTableName);
			
			String type_measure = AggregateConstants.aggregateTagCloud;
			
			String periodicity = getRowPeriodicity(per.toString());
			
			String rowkeyinit;
			String rowkeyend;
			
			Scan scan = new Scan();
			
			List<ResultScanner> totalResultScanner = new ArrayList<ResultScanner>();
			
			for (String dpId : dataPoolIdList){
				rowkeyinit = getAggregateRowkey(periodicity,type_measure,dpId,initDate);
				rowkeyend = getAggregateRowkey(periodicity,type_measure,dpId,endDate);
				scan.setStartRow(rowkeyinit.getBytes());
				scan.setStopRow(rowkeyend.getBytes());
				ResultScanner resultScanner = table.getScanner(scan);
				totalResultScanner.add(resultScanner);
			}
			
			for (ResultScanner rs : totalResultScanner){
				for (Result r : rs) {	
					 String dcID = Bytes.toString(r.getRow()).substring(5,13);
					 Date date = rowkeyToDate(Bytes.toString(r.getRow()).substring(13,27));
					 
					 HashMap <String, Integer> termOcc = new HashMap <String, Integer> ();

					 NavigableMap<byte[], byte[]> res = r.getFamilyMap(AggregateConstants.agg_res_bColFamily);
					 for (byte[] key : res.keySet()) {
						 String word = Bytes.toString(key);
						 int value = Integer.parseInt(Bytes.toString(res.get(key)));
						 termOcc.put(word,value);
					 }
					 
					 TermOccurrenceAnalysis newtermOcurrence = null;
					DataChannel dc = new DataChannel ();
					dc.setChannelID(dcID);
					newtermOcurrence = new TermOccurrenceAnalysis();
					newtermOcurrence.setDataChannel(dc);
					newtermOcurrence.setPeriodicity(per);
					newtermOcurrence.setaDate(date);
					newtermOcurrence.setTermOcc(termOcc);					
					termOcurrences.add(newtermOcurrence);
				}
			}
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, "ERROR while getting the term ocurrences analysis", e);
		}
		LOGGER.info("Got term ocurrences analysis in HBase");
		return termOcurrences;
		
	}

}
