#! /bin/bash
echo --- set schema cassandra 
#/Library/Cassandra/bin/cassandra 

cat $WISECROWDREC_HOME/WiseCrowdRec/src/main/resources/cassandra/schemaCassandra.txt | /Library/Cassandra/bin/cassandra-cli -h localhost


