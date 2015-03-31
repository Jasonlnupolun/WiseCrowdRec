#! /bin/bash
$WISECROWDREC_HOME/scripts/ec2/stopRabbitmq_server-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/stopHadoop-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/stopApacheSpark-ec2.sh
$WISECROWDREC_HOME/scripts/ec2/stopCassandra-ec2.sh

$WISECROWDREC_HOME/scripts/clearDSStore.sh
