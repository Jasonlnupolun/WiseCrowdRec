#! /bin/bash
WISECROWDREC_HOME=$HOME/WiseCrowdRec/
${WISECROWDREC_HOME}scripts/gce/getConfigProps-gce.sh
${WISECROWDREC_HOME}scripts/gce/startRabbitmq_server-gce.sh
${WISECROWDREC_HOME}scripts/gce/startApacheSpark-gce.sh

cd ${WISECROWDREC_HOME}sparkstreaming-rbm
mvn clean install
mvn exec:java -Dexec.mainClass="com.feiyu.starter.Main"
