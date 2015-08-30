#! /bin/bash

cd $WISECROWDREC_HOME/WiseCrowdRec 
mvn clean install -Dmaven.test.skip=true
