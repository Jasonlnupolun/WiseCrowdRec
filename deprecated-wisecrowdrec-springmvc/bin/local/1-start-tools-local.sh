#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
$WISECROWDREC_HOME/scripts/local/getConfigProps-local.sh
$WISECROWDREC_HOME/scripts/local/startRabbitmq_server-local.sh
$WISECROWDREC_HOME/scripts/local/startHadoop-local.sh
$WISECROWDREC_HOME/scripts/local/startApacheSpark-local.sh
#$WISECROWDREC_HOME/scripts/local/startElasticsearch-local.sh
$WISECROWDREC_HOME/scripts/local/startCassandra-local.sh
$WISECROWDREC_HOME/scripts/local/setSchemaCassandra-local.sh
