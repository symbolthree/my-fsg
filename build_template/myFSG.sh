#!/bin/sh

############################################
# MyFSG Unix script
#############################################

export JAVA_OPTS="-Xms64m -Xmx1024m -XX:+UseParallelGC"
java \
-Dfile.encoding=UTF8 \
-DLOG_DIR=log \
-Dlog4j.configurationFile=config/log4j2.xml \
-cp ./config:\
./lib/myFSG-@build.version@.jar:\
symbolthree.calla.MAIN -config config\myFSG.properties $*
