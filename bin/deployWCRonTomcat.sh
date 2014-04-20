#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
/Library/Tomcat/bin/startup.sh
cd $WISECROWDREC_HOME/WiseCrowdRec
mvn tomcat7:undeploy
mvn clean package tomcat7:deploy
