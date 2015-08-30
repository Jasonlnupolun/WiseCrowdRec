#! /bin/bash
echo --- set schema cassandra 
#/Library/Cassandra/bin/cassandra 

cat $WISECROWDREC_HOME/WiseCrowdRec/src/main/resources/cassandra/schemaCassandra.txt | /home/ubuntu/tools/Cassandra/bin/cassandra-cli -h localhost


