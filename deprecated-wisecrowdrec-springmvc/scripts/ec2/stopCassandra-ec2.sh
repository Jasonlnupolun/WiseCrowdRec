#! /bin/bash
echo --- stop cassandra 

#ps auwx | grep cassandra
#sudo kill <pid>

sudo pkill -f 'java.*cassandra'
