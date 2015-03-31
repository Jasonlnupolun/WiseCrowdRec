#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
$WISECROWDREC_HOME/scripts/ec2/getConfigProps-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/startRabbitmq_server-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/startHadoop-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/startApacheSpark-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/startCassandra-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/setSchemaCassandra-ec2.sh
#$WISECROWDREC_HOME/scripts/ec2/startElasticsearch-ec2.sh
