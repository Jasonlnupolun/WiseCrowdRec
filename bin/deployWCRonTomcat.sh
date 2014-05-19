#! /bin/bash
$WISECROWDREC_HOME/scripts/clearDSStore.sh
/Library/Tomcat/bin/startup.sh
cd $WISECROWDREC_HOME/WiseCrowdRec
mvn tomcat7:undeploy -Dmaven.test.skip=true
mvn clean package tomcat7:deploy -Dmaven.test.skip=true
