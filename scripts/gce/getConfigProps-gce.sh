#! /bin/bash
WISECROWDREC_HOME=$HOME/WiseCrowdRec/
_BACKUP_HOME=$HOME/WiseCrowdRecBackup/
_RES_HOME=${WISECROWDREC_HOME}sparkstreaming-rbm/src/main/resources/
cp ${_BACKUP_HOME}config.properties $_RES_HOME
echo --- copy config.properties from $_BACKUP_HOME to $_RES_HOME 

