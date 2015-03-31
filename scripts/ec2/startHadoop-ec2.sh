#! /bin/bash
echo --- start hadoop-2.6.0
THOME=/home/ubuntu/tools/
sudo ${THOME}hadoop-2.6.0/sbin/start-dfs.sh
sudo ${THOME}hadoop-2.6.0/sbin/start-yarn.sh



