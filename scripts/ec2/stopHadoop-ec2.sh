#! /bin/bash
echo --- stop hadoop-2.6.0 
THOME=/home/ubuntu/tools/
sudo ${THOME}hadoop-2.6.0/sbin/stop-dfs.sh
sudo ${THOME}hadoop-2.6.0/sbin/stop-yarn.sh



