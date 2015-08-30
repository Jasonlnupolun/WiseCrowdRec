#! /bin/bash
$WISECROWDREC_HOME/scripts/local/stopHadoop-local.sh
$WISECROWDREC_HOME/scripts/local/stopApacheSpark-local.sh
$WISECROWDREC_HOME/scripts/local/stopRabbitmq_server-local.sh
$WISECROWDREC_HOME/scripts/local/stopCassandra-local.sh

$WISECROWDREC_HOME/scripts/clearDSStore.sh
