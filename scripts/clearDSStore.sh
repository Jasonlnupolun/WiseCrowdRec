#! /bin/bash
# clear all the MAC ".DS_Store" in the project dir recursively
find $WISECROWDREC_HOME -name ".DS_Store" -depth -exec rm {} \;
echo ----- Removed all of the ".DS_Store" files from the $WISECROWDREC_HOME dir
