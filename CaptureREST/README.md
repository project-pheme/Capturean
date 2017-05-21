Capture
=======

TODO: complete project description

Description
-----------

**TODO**

Compilation
------------

Requirements:
* Maven version 3 or later
* Java version 7 or later
* git version 1.7 or later


First make sure you have Java 7 or later, and that your Java path points to JDK executables (**not** JRE).
You can do it for example by putting a following lines in your `.profile`:
```bash
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
PATH=$PATH:$JAVA_HOME/bin
JRE_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre
export JAVA_HOME
export JRE_HOME
export PATH
```

You may have problems with tools.jar library depending of your java version. Try:
```bash
apt-get install openjdk-7-jre-lib
```

To get the source, clone this git repository and do a maven build:

```bash
git clone git@gitlab.atosresearch.eu:ari/capture.git
cd capture/CaptureREST
mvn clean install
```

Now you should have a `CaptureREST.war` file in the target directory of your project.
This file can be deployed in Tomcat or Jetty webapp dir.

**TODO**
Prepare steps for eclipse.


Installation
------------

Capture requires the following dependencies:

* Hbase (version 0.94.21)
* Solr (version 4.2.0)
* Zookeeper (version 3.4.6)
* Kafka (version kafka-0.8.1.1) - for streaming

There is a script that downloads all dependencies locally, establishes execution environment and launch the component.
The purpose of this script is to quickly bootstrap all the environment of the Capture and run a local instance.
It is primarily for testing, and is not aimed at production environment yet.
 
First open the script `install/setup.sh` and edit TARGET_DIR variable to suit your setup.

To run the script execut the following:

```
cd install
bash setup.sh
```

By default it will download and expand all files to `Capture` directory in your home.
It will also configure hbase & solr. After executing the script you should have a basic setup of the Capture instance. :grinning_cat_face_with_smiling_eyes:

The `install` directory also contain two scripts, for starting and shutting down all infrastructure.

Now you can issue command `bash run.sh` which will start hbase, solr and jetty. After that you can open your browser and navigate to:
http://localhost:8080/CaptureREST/rest/datachannel/
which should return an xml, with empty <dataChannels> element. Now you have an instance of Capture running locally :rainbow_solid:


To shutdown all servers, run `bash shutdown.sh` in the install directory.


Configuration
-------------

By default, Capture will connect to Hbase and Solr instances running on localhost.
To change this, edit `src/main/resources/solrCapture.properties` file and point to remote Solr location.



Configuration for streaming
---------------------------

In order to use Capture with kafka streaming, you need to manually start Kafka and zookeeper.

```bash
wget http://apache.rediris.es/zookeeper/zookeeper-3.4.6/zookeeper-3.4.6.tar.gz
wget http://apache.rediris.es/kafka/0.8.1.1/kafka-0.8.1.1-src.tgz

tar -zxvf zookeeper-3.4.6.tar.gz
tar -zxvf kafka-0.8.1.1-src.tgz

#build kafka
cd kafka-0.8.1.1-src
./gradlew jar

```
Now you should configure zookeeper `zookeeper-3.4.6/conf/zoo.cfg` and point to the dataDir to the zookeeper data directory, e.g. `dataDir=/home/matt/zookeeper`.

To run kafka follow the guide from the "Quickstart" chapter on the documentation page: http://kafka.apache.org/documentation.html#quickstart . Basically you nee only to start the zookeeper, the broker and create a new topic.
Currently we use topic "test" with 4 partitions:
```bash
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 4 --topic test
```

You may also setup default partition number in kafka config in `kafka-0.8.1.1-src/config/server.properties` file, 
by setting `num.partitions=4`, but it is not mandatory.

After starting kafka, you need to deploy consumer application. Go to the directory where you have cloned the git repository and build the project `capture-consumer`:
```bash
cd capture/capture-consumer
mvn clean install
```
This will produce `capture-consumer.war` in the target directory. Copy it to the application server webapp dir, and navigate to http://localhost:8080/capture-consumer/

For now you will see only a blank page, because the streaming haven't yet started. 

To start streaming, you should submit a new datachannel with `<type>stream</type>`

Once the streaming start, the webpage will show all the streaming messages.


Production deployment
--------------------

For production deployment instruction, read the [Deployment Documentation](Deployment.md)


How to use
----------

**TODO**
* RESTful services description
* DataChannel xml format



License
-------

**TODO**