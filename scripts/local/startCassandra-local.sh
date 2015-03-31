#! /bin/bash
echo --- start cassandra 
/Library/Cassandra/bin/cassandra #> cassandra-log.txt

#cat $WISECROWDREC_HOME/WiseCrowdRec/src/main/resources/cassandra/schemaCassandra.txt | /Library/Cassandra/bin/cassandra-cli -h localhost

