#! /bin/bash

#vim ~/.profile
#export SCALA_HOME=/home/ubuntu/tools/scala-2.11.5
#export PATH=$SCALA_HOME/bin:$PATH
#export WISECROWDREC_HOME=/home/ubuntu/WiseCrowdRec
#export MAVEN_OPTS="-Xmx2048M -XX:MaxPermSize=2048M -XX:ReservedCodeCacheSize=2048M"
#or export MAVEN_OPTS="-Xmx2g -XX:MaxPermSize=1024M -XX:ReservedCodeCacheSize=512m -Xms1024m -Xmx1024m"
#export HADOOP_HOME=/home/ubuntu/tools/hadoop-2.6.0
#export PATH=$PATH:$HADOOP_HOME/bin
#source ~/.profile

# install java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
sudo apt-get install oracle-java7-set-default
# install ant
sudo apt-get install ant

cd /
sudo mkdir Library

HOME=/home/ubuntu/
mkdir ${HOME}tools/
TOOLSHOME=${HOME}tools/

cd $TOOLSHOME

# install RabbitMQ
sudo apt-get install rabbitmq-server

# install Cassandra
wget "http://mirrors.gigenet.com/apache/cassandra/2.0.13/apache-cassandra-2.0.13-bin.tar.gz"
tar -zxvf apache-cassandra-2.0.13-bin.tar.gz
rm apache-cassandra-2.0.13-bin.tar.gz
mv ${TOOLSHOME}apache-cassandra-2.0.13 ${TOOLSHOME}Cassandra
#sudo ln -s ${TOOLSHOME}Cassandra /Library/ 
sudo mkdir -p /var/log/cassandra
sudo chown -R `whoami` /var/log/cassandra
sudo mkdir -p /var/lib/cassandra
sudo chown -R `whoami` /var/lib/cassandra

# install Elasticsearch
#wget "https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.0.1.tar.gz"
#tar -zxvf elasticsearch-1.0.1.tar.gz
#rm elasticsearch-1.0.1.tar.gz
#mv ${TOOLSHOME}elasticsearch-1.0.1 ${TOOLSHOME}elasticsearch
##sudo ln -s ${TOOLSHOME}elasticsearch /Library/

# install Apach Spark 
#http://askubuntu.com/questions/497895/permission-denied-for-rootlocalhost-for-ssh-connection
#sudo vim /etc/ssh/sshd_config
# change `PermitRootLogin without-password` to `PermitRootLogin yes`
#service ssh restart
#sudo su
#ssh-keygen -t rsa -P ""
#cat /root/.ssh/id_rsa.pub >> /root/.ssh/authorized_keys
# ssh localhost #check if it works
#exit

wget http://www.scala-lang.org/files/archive/scala-2.11.5.tgz
sudo tar xvf scala-2.11.5.tgz
rm scala-2.11.5.tgz
wget "http://apache.mesi.com.ar/spark/spark-1.3.0/spark-1.3.0-bin-hadoop2.3.tgz"
tar xvf spark-1.3.0-bin-hadoop2.3.tgz
rm spark-1.3.0-bin-hadoop2.3.tgz
# bin/run-example SparkPi 10
mv ${TOOLSHOME}spark-1.3.0-bin-hadoop2.3 ${TOOLSHOME}ApacheSpark
#sudo ln -s ${TOOLSHOME}ApacheSpark /Library/

# install Tomcat
sudo apt-get install tomcat7
sudo apt-get install tomcat7-docs tomcat7-admin tomcat7-examples
# https://help.ubuntu.com/lts/serverguide/tomcat.html
sudo chgrp -R tomcat7 /etc/tomcat7
sudo chmod -R g+w /etc/tomcat7
#tomcat log dir /var/log/tomcat7/

#sudo vim /etc/tomcat7/tomcat-users.xml
# add configration into <tomcat-users></tomcat-users>
#<role rolename="manager-gui"/>
#<role rolename="manager-script"/>
#<role rolename="manager-jmx"/>
#<role rolename="manager-status"/>
#<role rolename="admin-gui"/>
#<role rolename="admin-script"/>
#<user username="admin" password=""  roles="manager-gui,manager-script,manager-jmx,manager-status,admin-gui,admin-script"/>

# change Tomcat's port from 8080 to 9999, since Spark uses part 8080
#sudo vim /etc/tomcat7/server.xml
# find `Connector port="8080"`, and change to Connector port="9999"

#sudo service tomcat7 restart

#sudo vim /var/lib/tomcat7/webapps/ROOT/META-INF/context.xml
#<Context antiJARLocking="true" antiResourceLocking="true">

# http://openwares.net/java/jenkens_deploy_to_tomcat_error_of_outofmemoryerror.html
#sudo vim /etc/default/tomcat7
#JAVA_OPTS="-Djava.awt.headless=true -Xmx5120m -Xms5120m -Xmn1024m -XX:PermSize=1024m -XX:MaxPermSize=1024m -XX:+UseConcMarkSweepGC"

#FAIL - Context /WiseCrowdRecApp is defined in server.xml and may not be undeployed
#sudo service tomcat7 stop
#sudo rm -rf /var/lib/tomcat7/webapps/WiseCrowdRecApp*
#sudo service tomcat7 start 

# install Maven
sudo apt-get install maven
#for deploying app onto Tomcat by Maven
mkdir ~/.m2
#cp /etc/maven/settings.xml ~/.m2/
#vim ~/.m2/settings.xml
#add in <servers> </servers>
#<server>
#       <id>TomcatServer</id>
#      <username>admin</username>
#      <password></password>
#</server>

mkdir /home/ubuntu/WiseCrowdRecBackup
# copy from local
#scp -i ~/.ssh/wcr-kp-useast.cer config.properties ubuntu@ec2-xx-x-xx-xx.compute-1.amazonaws.com:~/WiseCrowdRecBackup
#vim ~/WiseCrowdRecBackup/config.properties #set confg info
##tar -xvzf ~/WiseCrowdRecBackup/ec2tools.tar.gz -C ~/tools
##rm ~/WiseCrowdRecBackup/ec2tools.tar.gz

# install hadoop
wget http://apache.mirrors.pair.com/hadoop/common/stable/hadoop-2.6.0.tar.gz
tar -zxvf hadoop-2.6.0.tar.gz 
rm hadoop-2.6.0.tar.gz
# follow hadoop configuration http://faustineinsun.blogspot.com/2014/01/setup-hadoop-220-yarn-on-single-node.html
#cd $HADOOP_HOME/etc/hadoop
#vim hadoop-env.sh
#export JAVA_HOME=/usr/lib/jvm/java-7-oracle/jre/






