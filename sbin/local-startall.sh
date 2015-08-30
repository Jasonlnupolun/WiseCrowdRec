#! /bin/bash
WISECROWDREC_NODEJS=/Users/feiyu/workspace/WiseCrowdRec
${WISECROWDREC_NODEJS}/scripts/local/startRabbitmq_server-local.sh
${WISECROWDREC_NODEJS}/scripts/local/startApacheSpark-local.sh
#${WISECROWDREC_NODEJS}/scripts/local/startRedis-local.sh

cd ${WISECROWDREC_NODEJS}/sparkstreaming-rbm
mvn clean install
mvn exec:java -Dexec.mainClass="com.feiyu.starter.Main"
