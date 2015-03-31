#! /bin/bash
echo --- start cassandra 
/home/ubuntu/tools/Cassandra/bin/cassandra #> cassandra-log.txt

#cat $WISECROWDREC_HOME/WiseCrowdRec/src/main/resources/cassandra/schemaCassandra.txt | /Library/Cassandra/bin/cassandra-cli -h localhost

#ps auwx | grep cassandra
#sudo kill <pid>

#sudo pkill -f 'java.*cassandra'
