#! /bin/bash
echo --- stop cassandra 

#ps auwx | grep cassandra
#sudo kill <pid>

pkill -f 'java.*cassandra'
