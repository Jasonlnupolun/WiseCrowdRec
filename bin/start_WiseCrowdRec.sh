#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
$WISECROWDREC_HOME/scripts/getConfigProps.sh
$WISECROWDREC_HOME/scripts/startCassandra.sh
$WISECROWDREC_HOME/scripts/startElasticsearch.sh
$WISECROWDREC_HOME/scripts/startHadoop.sh
$WISECROWDREC_HOME/scripts/startApacheSpark.sh
$WISECROWDREC_HOME/scripts/setSchemaCassandra.sh
$WISECROWDREC_HOME/scripts/startRabbitmq_server.sh

cd $WISECROWDREC_HOME/WiseCrowdRec 
mvn clean install -Dmaven.test.skip=true
