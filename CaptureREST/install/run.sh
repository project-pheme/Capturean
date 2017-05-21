#!/bin/bash

TARGET_DIR=$HOME/Capture
JETTY_DIR=$TARGET_DIR/jetty-distribution-9.2.2.v20140723
SOLR_DIR=$TARGET_DIR/solr-5.2.1
ZOOKEEPER_DIR=$TARGET_DIR/zookeeper-3.4.6
KAFKA_DIR=$TARGET_DIR/kafka_2.11-0.10.0.0
HBASE_DIR=$TARGET_DIR/hbase-1.1.1



runzookeeper()
{
	cd $ZOOKEEPER_DIR
	echo "Starting zookeeper..."
	bin/zkServer.sh start
}

runkafka()
{
	cd $KAFKA_DIR
	echo "Starting kafka..."
	bin/kafka-server-start.sh config/server.properties &> logs/all-`date +%G-%m-%d-%H-%M`.txt &
	echo $! > $TARGET_DIR/kafka.pid
	sleep 1
}


runhbase()
{
	cd $HBASE_DIR
	echo "Starting HBase..."
	bin/start-hbase.sh &
	sleep 5
}

runsolr() 
{
	cd $SOLR_DIR
	echo "Starting solr..."
	# java -jar start.jar &> logs/all-`date +%G-%m-%d-%H-%M`.txt &
	#echo $! > $TARGET_DIR/solr.pid
	bash bin/solr start
	sleep 5
}

runjetty()
{
	cd $JETTY_DIR
	echo "Starting jetty..."
	# obsolete
	#java -jar start.jar &> logs/`date +%G-%m-%d-%H-%M`.txt &
	#echo $! > $TARGET_DIR/jetty.pid
	
	bin/jetty.sh start
}


if [ "$#" -eq 0 ]; then
	echo "No parameters given, starting all (hbase, solr, jetty)..."
    runzookeeper
    runkafka
    runhbase
    runsolr
    runjetty

else
	#params
	for i in "$@"; do
		case ${i} in
			zookeeper) runzookeeper;; 
			kafka) runkafka;;
			hbase) runhbase;;
			solr) runsolr;;
			jetty) runjetty;;
		esac
   	 done
fi
