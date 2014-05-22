#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
$WISECROWDREC_HOME/scripts/getConfigProps.sh
$WISECROWDREC_HOME/scripts/startCassandra.sh

cd $WISECROWDREC_HOME/WiseCrowdRec 
mvn clean install -Dmaven.test.skip=true
