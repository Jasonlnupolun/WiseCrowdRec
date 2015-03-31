#! /bin/bash
_BACKUP_HOME=/home/ubuntu/WiseCrowdRecBackup/
_RES_HOME=${WISECROWDREC_HOME}/WiseCrowdRec/src/main/resources/
cp ${_BACKUP_HOME}config.properties $_RES_HOME
echo --- copy config.properties from $_BACKUP_HOME to $_RES_HOME 

