#! /bin/bash
_BACKUP_HOME=$(dirname $WISECROWDREC_HOME)/WiseCrowdRecBackup
mkdir $_BACKUP_HOME
cp $WISECROWDREC_HOME/WiseCrowdRec/src/main/resources/config.properties $_BACKUP_HOME 
echo --- config.properties is saved in $_BACKUP_HOME
