#!/bin/sh
#
#
# This script creates the entire environment for running Capture component.
# Invoke this script from the install/ directory.
# By default everything will be installed to ~/Capture directory
# You can change this behaviour by modyfying TARGET_DIR variable.
#
# Prerequisites: 
# 1) java 7
# 
# 2) following environment variables should be set:
# JAVA_HOME
#
# 3) Java JDK binaries should be added in PATH
#
#
# example:
# JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
# PATH=$PATH:$JAVA_HOME/bin
# JRE_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre
# export JAVA_HOME
# export JRE_HOME
# export PATH
#

#target directory
LOCAL_DIR=`pwd`
TARGET_DIR=$HOME/Capture

#hbase directories
HBASEROOT=$TARGET_DIR/hbase
ZOOKEEPERROOT=$TARGET_DIR/zookeeper

mkdir $HOME/Capture
cd $TARGET_DIR

#download all prerequisite packages
wget -nc http://download.eclipse.org/jetty/stable-9/dist/jetty-distribution-9.2.9.v20150224.zip
wget -nc http://archive.apache.org/dist/lucene/solr/4.10.4/solr-4.10.4.tgz
wget -nc http://apache.rediris.es/hbase/hbase-0.98.11/hbase-0.98.11-hadoop2-bin.tar.gz


#extract to local dir
echo "Extracting hbase..."
tar -zxf hbase-0.94.23.tar.gz
echo "Extracting solr..."
tar -zxf solr-4.2.0.tgz
echo "Extracting jetty..."
tar -zxf jetty-distribution-9.2.2.v20140723.tar.gz

#configure hbase:
#add conf/hbase-site.xml
echo "Configuring HBase..."
cp -f $LOCAL_DIR/hbase-conf/hbase-site.xml $LOCAL_DIR/hbase-conf/hbase-site.tmp
sed -i "s|{HBASEROOTDIR}|$HBASEROOT|g" $LOCAL_DIR/hbase-conf/hbase-site.tmp
sed -i "s|{ZOOKEEPERROOTDIR}|$ZOOKEEPERROOT|g" $LOCAL_DIR/hbase-conf/hbase-site.tmp
cp -f $LOCAL_DIR/hbase-conf/hbase-site.tmp $TARGET_DIR/hbase-0.94.23/conf/hbase-site.xml

cp -f $LOCAL_DIR/hbase-conf/hbase-env.sh $LOCAL_DIR/hbase-conf/hbase-env.tmp
sed -i "s|{HBASEROOTDIR}|$HBASEROOT|g" $LOCAL_DIR/hbase-conf/hbase-env.tmp
sed -i "s|{ZOOKEEPERROOTDIR}|$ZOOKEEPERROOT|g" $LOCAL_DIR/hbase-conf/hbase-env.tmp
cp -f $LOCAL_DIR/hbase-conf/hbase-env.tmp $TARGET_DIR/hbase-0.94.23/conf/hbase-env.sh

cp -f $LOCAL_DIR/hbase-conf/regionservers $LOCAL_DIR/hbase-conf/regionservers.tmp
sed -i "s|{HBASEROOTDIR}|$HBASEROOT|g" $LOCAL_DIR/hbase-conf/regionservers.tmp
sed -i "s|{ZOOKEEPERROOTDIR}|$ZOOKEEPERROOT|g" $LOCAL_DIR/hbase-conf/regionservers.tmp
cp -f $LOCAL_DIR/hbase-conf/regionservers.tmp $TARGET_DIR/hbase-0.94.23/conf/regionservers

mkdir $HBASEROOT
mkdir $ZOOKEEPERROOT

#start hbase
cd $TARGET_DIR/hbase-0.94.23
echo "Starting HBase..."
bin/start-hbase.sh &
sleep 30

#create tables
echo "create 'pheme_data_channel', 'pheme_data_channel'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'pheme_data_source', 'common-data', 'pheme_data_source', 'twitter'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'pheme_raw_tweet', 'tweet'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'pheme_twitter_user', 'user'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
# new schema:
echo "create 'capture_data', 'capture_data'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'capture_tweet', 'capture_tweet'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'data_channel', 'data_channel'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'data_pool', 'data_pool'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'data_source', 'data_source'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell
echo "create 'twitter_user', 'twitter_user'" | $TARGET_DIR/hbase-0.94.23/bin/hbase shell


echo "Shutting down hbase..."
bin/stop-hbase.sh &

#configure Solr
#bootstrap solr configuration from predefined config files
#and copy predefined cores (dc-capture & dc-twitts)
echo "Configuring Solr..."
cp -fa $LOCAL_DIR/cores/* $TARGET_DIR/solr-4.2.0/example/solr

#run solr
#cd $TARGET_DIR/solr-4.2.0/example/
#echo "Starting solr..."
#java -jar start.jar &
#echo $! > $TARGET_DIR/solr.pid
#sleep 5



#put war in jetty webapp dir and run the server
echo "Configuring webapp..."
cp $LOCAL_DIR/../target/CaptureREST.war $TARGET_DIR/jetty-distribution-9.2.2.v20140723/webapps/

#cd $TARGET_DIR/jetty-distribution-9.2.2.v20140723/
#echo "Starting jetty..."
#java -jar start.jar &
#echo $! > $TARGET_DIR/jetty.pid