#! /bin/bash
$WISECROWDREC_HOME/scripts/stopHadoop.sh
$WISECROWDREC_HOME/scripts/stopApacheSpark.sh
$WISECROWDREC_HOME/scripts/stopRabbitmq_server.sh

$WISECROWDREC_HOME/scripts/clearDSStore.sh
