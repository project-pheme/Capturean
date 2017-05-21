#!/bin/bash

TARGET_DIR=$HOME/Capture
JETTY_DIR=$TARGET_DIR/jetty-distribution-9.2.2.v20140723
SOLR_DIR=$TARGET_DIR/solr-5.2.1
ZOOKEEPER_DIR=$TARGET_DIR/zookeeper-3.4.6
KAFKA_DIR=$TARGET_DIR/kafka_2.11-0.10.0.0
HBASE_DIR=$TARGET_DIR/hbase-1.1.1


stopjetty()
{
	echo "stopping jetty..."
	cd $JETTY_DIR
	bin/jetty.sh stop
}

stopsolr()
{
	echo "stopping solr..."
	cd $SOLR_DIR
	bash bin/solr stop
	#kill `cat $TARGET_DIR/solr.pid`
	#rm $TARGET_DIR/solr.pid
}

stophbase()
{
	echo "stopping hbase..."
	cd $HBASE_DIR
	bin/stop-hbase.sh
}

stopkafka()
{
	echo "stopping kafka"
	cd $KAFKA_DIR
	#bin/kafka-server-stop.sh
	kill -KILL `cat $TARGET_DIR/kafka.pid`
}

stopzookeeper()
{
	echo "stopping zookeeper"
	cd $ZOOKEEPER_DIR
	bin/zkServer.sh stop
}

if [ "$#" -eq 0 ]; then
	echo "No parameters given, stopping all (hbase, solr, jetty, kafka, zookeeper)..."
    stopjetty
    stopsolr
    stophbase
    stopkafka
    stopzookeeper

else
	#params
	for i in "$@"; do
		case ${i} in
			zookeeper) stopzookeeper;; 
			kafka) stopkafka;;
			hbase) stophbase;;
			solr) stopsolr;;
			jetty) stopjetty;;
		esac
   	 done
fi

