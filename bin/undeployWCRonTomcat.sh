#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
cd $WISECROWDREC_HOME/WiseCrowdRec
mvn tomcat7:undeploy -Dmaven.test.skip=true
/Library/Tomcat/bin/shutdown.sh
