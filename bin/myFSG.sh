#!/bin/sh

############################################
# MyFSG Unix script
# $Header: /TOOL/myMSG/build_template/myFSG.sh 2     3/31/17 9:23p Christopher Ho $
#############################################

export JAVA_OPTS="-Xms64m -Xmx1024m -XX:+UseParallelGC"
java \
-Dfile.encoding=UTF8 \
-DLOG_DIR=$HOME/symbolthree/myFSG/log \
-Dlog4j.configurationFile=lib/log4j2.xml \
-cp ./bin:\
./lib/myFSG-@build.version@.jar:\
./lib/CALLA-1.3.jar:\
./lib/fndext.jar:\
./lib/log4j-api-2.3.jar:\
./lib/log4j-core-2.3.jar:\
./lib/jaxen-1.1.6.jar:\
./lib/jdom-2.0.6.jar:\
./lib/commons-io-2.5.jar:\
./lib/commons-codec-1.10.jar:\
./lib/commons-collections4-4.1.jar:\
./lib/netcfg.jar:\
./lib/ojdbc8.jar:\
./lib/poi-3.17.jar:\
./lib/poi-ooxml-3.17.jar:\
./lib/poi-ooxml-schemas-3.17.jar:\
./lib/xmlbeans-2.6.0.jar:\
./lib/stax-api-1.0.1.jar \
symbolthree.flower.CALLA $*
