#! /bin/bash
cd $WISECROWDREC_HOME/WiseCrowdRec
mvn exec:java -Dexec.mainClass="com.feiyu.storm.streamingdatacollection.Topology"
