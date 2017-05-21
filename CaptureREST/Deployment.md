Deployment for "Production" Environment
=======================================

Intro
-----
This is a guide for establishing environment for Capture component in the "production" mode.
It consist of steps for preparing all necessary packages, configurations,
permissions and init scripts in order to run Capture webapp among other services.

For establishing simple development environment, you can follow instructions from the README file
and use the setup.sh & run.sh scripts.

This guide is about how to deploy everything on a production server and run Capture as a service.

Prerequisites
-------------

This instruction has been especially prepared for Ubuntu Linux, and should work with
Ubuntu 12 and 14, and probably with any Ubuntu based Linux.
For other distributions, some changes might be required.

There are various packages required in the deployment process. Note that maven and git are not 100% necessary, but it's good to have them.

```bash
sudo apt-get install openjdk-7-jdk
sudo apt-get install maven
sudo apt-get install git
sudo apt-get install vim
sudo apt-get install zookeeper
```

If there are other java versions already installed, use this command to ensure that Java 7 is used by default:
```bash
sudo update-alternatives --config java
```

After installing basic prerequisites, ensure to have the following environment variables in your `.profile`.
While this is not 100% necessary for running services, but it's good to have it.

Put the following lines at the end of the `~/.profile` file.

```bash
JAVA_HOME=/usr/lib/jvm/java-7-openjdk-amd64
PATH=$PATH:$JAVA_HOME/bin
JRE_HOME=/usr/lib/jvm/java-7-openjdk-amd64/jre
export JAVA_HOME
export JRE_HOME
export PATH
```

Main Installation
-----------------

For running Capture we need the following software:
* Tomcat
* Hbase
* Solr

The configuration will be the following:
* Separate Tomcat application server instance for Capture webapp - running on port 8080
* Separate Tomcat application server for Solr - running on port 8983
* Hbase (currently in the standalone mode) 

The layout is the following:
* Binaries in `/opt` (both Tomcat and Hbase)
* Application data in `/var/lib/`:
 * Tomcat (main): `/var/lib/tomcat/`
 * Tomcat (Solr instance): `/var/lib/tomcat_solr/`
 * Tomcat webbapp dir: `/var/lib/tomcat/webapp/`
 * Solr home dir: `/var/lib/solr/`
 * Hbase home: `/var/lib/hbase/0.94.21/`
 * Zookeeper: `/var/lib/zookeeper/`
* Logs:
 * Tomcat logs: `/var/lib/tomcat/logs`
 * HBase logs: `/var/log/hbase/0.94.21/`
 * Zookeeper logs: `/var/log/zookeeper/`
* Configuration files:
 * Tomcat (main): `/var/lib/tomcat/conf/server.xml` (and the whole dir)
 * Tomcat (Solr): `/var/lib/tomcat_solr/conf/server.xml`
 * Hbase: `/var/lib/hbase/0.94.21/conf/`
 * Solr: `/var/lib/solr/solr/solr.xml`
* Init scripts in `/etc/init.d`:
 * Tomcat (main): `/etc/init.d/tomcat`
 * Tomcat (Solr): `/etc/init.d/tomcat_solr`
 * Hbase: `/etc/init.d/hbase`

### Tomcat installation

The following commands will install the latest Tomcat (version 8.0.9). Tomcat will be run as `tomcat` user.

```bash
sudo adduser tomcat

cd /opt
sudo wget http://ftp.cixug.es/apache/tomcat/tomcat-8/v8.0.9/bin/apache-tomcat-8.0.9.tar.gz
sudo tar -zxvf apache-tomcat-8.0.9.tar.gz
sudo rm apache-tomcat-8.0.9.tar.gz 

sudo mkdir /var/lib/tomcat
sudo mv /opt/apache-tomcat-8.0.9/conf /var/lib/tomcat
sudo mv /opt/apache-tomcat-8.0.9/logs /var/lib/tomcat
sudo mv /opt/apache-tomcat-8.0.9/webapps /var/lib/tomcat
sudo mv /opt/apache-tomcat-8.0.9/work /var/lib/tomcat


mkdir /tmp/tomcat-tmp
chmod a+rwx /tmp/tomcat-tmp/

sudo chown -R tomcat.tomcat /var/lib/tomcat/
sudo chmod -R g+rwx /var/lib/tomcat/
```
#### Tag libs

The newest version of Capture contains GUI thatuses JSP and JSTL library. It uses  Servlet 3.0 / JSP 2.2 and expects JSTL 1.2 or 1.2.1. Unfortunately Tomcat doesn't ship with JSTL libs, so manual installation is required.

The simpliest way is to download taglibs from http://tomcat.apache.org/taglibs/
Go to http://tomcat.apache.org/download-taglibs.cgi and download both `taglibs-standard-impl-1.2.1.jar` and ` taglibs-standard-spec-1.2.1.jar`. Put both libraries under `/opt/apache-tomcat-8.0.9/lib`. Restart tomcat if necessary.

More information on JSTL here: http://stackoverflow.com/tags/jstl/info.
(Note: you can also use oracle implementation: javax.servlet.jsp.jstl-api-1.2.1.jar and javax.servlet.jsp.jstl-1.2.1.jar from maven central repository)

### Solr installation

Solr will be deployed on another Tomcat instance, so first we need to replicate Tomcat instance:

```bash
sudo mkdir /var/lib/tomcat_solr
sudo cp -a /var/lib/tomcat/* /var/lib/tomcat_solr
sudo chown -R tomcat.tomcat /var/lib/tomcat_solr/
sudo chmod -R g+rwx /var/lib/tomcat_solr/
```
Please note that Solr will also be run as `tomcat` user.

Now we download latest Solr and create Solr home:
```bash
cd
wget https://archive.apache.org/dist/lucene/solr/4.2.0/solr-4.2.0.tgz
tar -zxvf solr-4.2.0.tgz

sudo mkdir /var/lib/solr
sudo chown -R tomcat.tomcat /var/lib/solr/
sudo chmod -R g+rwx /var/lib/solr/

sudo cp -a ~/solr-4.2.0/example/solr/ /var/lib/solr/
sudo cp ~/solr-4.2.0/example/webapps/solr.war /var/lib/tomcat_solr/webapps/
sudo chown tomcat.tomcat /var/lib/tomcat_solr/webapps/solr.war
sudo rm -rf /var/lib/tomcat_solr/webapps/docs/ /var/lib/tomcat_solr/webapps/examples/
```

### HBase Installation

Download and install hbse in `/opt`

```bash
cd /opt
wget http://apache.rediris.es/hbase/hbase-0.94.21/hbase-0.94.21.tar.gz
tar -zxf hbase-0.94.21.tar.gz
```

Create users for both hbase and zookeeper. Note that hbase will need to write to zookeeper home, so it's adviseable to have them in teh same group. Hbase will be run as hbase user.

```bash
sudo adduser zookeeper
sudo adduser hbase
sudo addgroup hbase zookeeper
```

Create hbase home:

```bash
sudo mkdir /var/lib/hbase/
sudo mkdir /var/lib/hbase/0.94.21/
sudo mkdir /var/log/hbase
sudo mkdir /var/log/hbase/0.94.21/

sudo chown -R hbase.hbase /var/lib/hbase/
sudo chown -R hbase.hbase /var/log/hbase/

sudo chmod g+rwx /var/lib/zookeeper
```

### Configuration

TODO: explain details.

In order to setup the whole configuration for Tomcats, Solr and hbase, you can use prepared package from `install/deplyment/configs.tar.bz`. It contains all configuration and init scripts for tomcat, solr and hbase.
If you want to use it, ensure that you have the same directory structure as outlined above.
To use it, simply invoke the following:

```bash
sudo tar -jxvf configs.tar.bz -C 
sudo chown -R tomcat.tomcat /var/lib/tomcat/conf/
sudo chown -R tomcat.tomcat /var/lib/tomcat_solr/conf/
sudo chown -R tomcat.tomcat /var/lib/solr/
```

In order to setup Hbase tables, first start hbase:

```bash
sudo /etc/init.d/hbase start
```
After Hbase is up, run the follwing commands:

```bash
echo "create 'pheme_data_channel', 'pheme_data_channel'" | $TARGET_DIR/hbase-0.94.21/bin/hbase shell
echo "create 'pheme_data_source', 'common-data', 'pheme_data_source', 'twitter'" | $TARGET_DIR/hbase-0.94.21/bin/hbase shell
echo "create 'pheme_raw_tweet', 'tweet'" | $TARGET_DIR/hbase-0.94.21/bin/hbase shell
echo "create 'pheme_twitter_user', 'user'" | $TARGET_DIR/hbase-0.94.21/bin/hbase shell
sudo /etc/init.d/hbase stop
```

### Starting services


Starting hbase:
```bash
sudo /etc/init.d/hbase start
```
Starting Tomcat:
```bash
sudo /etc/init.d/tomcat start
```
Starting Solr:
```bash
sudo /etc/init.d/tomcat_solr start

```

### Stopping services

Similar to the previous, but with "stop" parameter:
```bash
sudo /etc/init.d/tomcat stop
sudo /etc/init.d/tomcat_solr stop
sudo /etc/init.d/hbase stop
```

### Troubleshooting

Hbase needs access to the /tmp dir and zookeeper home.
If you get error "File.read failed with java.lang.UnsatisfiedLinkError" or similar, then check if the /tmp dir has proper permissions.
execute:
```bash
mount
```
if you find that tmp is mounted with "noexec" mode, for example:
```bash
/dev/sda6 on /tmp type ext4 (rw,noexec,nosuid)

```
Then run this command:
```bash
sudo mount -o remount exec /tmp
```

