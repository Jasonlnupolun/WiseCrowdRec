#! /bin/bash

# $ vim ~/.profile
#WISECROWDREC_HOME=$HOME/WiseCrowdRec/
# $ source ~/.profile

#http://askubuntu.com/questions/497895/permission-denied-for-rootlocalhost-for-ssh-connection
# $ sudo vim /etc/ssh/sshd_config
# change `PermitRootLogin without-password` to `PermitRootLogin yes`

# https://cloud.google.com/compute/docs/troubleshooting#communicatewithinternet
#$ sudo vim /etc/sysctl.conf
#net.ipv4.tcp_keepalive_time=60
#net.ipv4.tcp_keepalive_intvl=60
#net.ipv4.tcp_keepalive_probes=5
#$ sudo sysctl -p

# install screen
sudo apt-get update
sudo apt-get upgrade
sudo apt-get install screen

# install java
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
sudo apt-get install oracle-java7-set-default
# install Redis
# http://tosbourn.com/install-latest-version-redis-ubuntu/
sudo apt-get install -y python-software-properties
sudo add-apt-repository -y ppa:rwky/redis
sudo apt-get update
sudo apt-get install -y redis-server

# install htop
sudo apt-get install htop
# install Maven
sudo apt-get install maven
# install node.js 
sudo apt-get install nodejs
sudo apt-get install npm
sudo ln -s /usr/bin/nodejs /usr/bin/node
# install heroku and use foreman
wget -O- https://toolbelt.heroku.com/install-ubuntu.sh | sh
# install RabbitMQ
sudo apt-get install rabbitmq-server

mkdir $HOME/WiseCrowdRecBackup
mkdir $HOME/tools/
TOOLSHOME=$HOME/tools/
cd $TOOLSHOME

# install scala
#sudo apt-get remove scala-library scala
sudo wget www.scala-lang.org/files/archive/scala-2.10.4.deb
sudo dpkg -i scala-2.10.4.deb
sudo apt-get update
sudo apt-get install scala
wget http://scalasbt.artifactoryonline.com/scalasbt/sbt-native-packages/org/scala-sbt/sbt/0.12.4/sbt.deb
sudo dpkg -i sbt.deb
sudo apt-get update
sudo apt-get install sbt
sudo apt-get -f install
sudo rm sbt.deb
sudo rm scala-2.10.4.deb

# install Apach Spark 
#http://askubuntu.com/questions/497895/permission-denied-for-rootlocalhost-for-ssh-connection
# $ sudo vim /etc/ssh/sshd_config
# change `PermitRootLogin without-password` to `PermitRootLogin yes`
service ssh restart
chmod go-w ~/
chmod 700 ~/.ssh
chmod 600 ~/.ssh/authorized_keys
ssh-keygen -t rsa -P ""
cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys
# $ ssh localhost #check if it works
# $ exit

wget http://d3kbcqa49mib13.cloudfront.net/spark-1.4.0-bin-hadoop2.6.tgz
tar xvf spark-1.4.0-bin-hadoop2.6.tgz
rm spark-1.4.0-bin-hadoop2.6.tgz

# install Redis 
# http://redis.io/topics/quickstart
#wget http://download.redis.io/redis-stable.tar.gz
#tar xvzf redis-stable.tar.gz
#rm redis-stable.tar.gz
#cd $TOOLSHOME/redis-stable
#make
#sudo cp src/redis-server /usr/bin/
#sudo cp src/redis-cli /usr/bin/
