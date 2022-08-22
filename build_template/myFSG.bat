@echo off
REM ############################################
REM # MyMSG Batch File
REM #############################################

setlocal
set JAVA_OPTS=-Xms64m -Xmx1024m -XX:+UseParallelGC
java.exe ^
-Dfile.encoding=UTF8 ^
-DLOG_DIR=log ^
-Dlog4j.configurationFile=config\log4j2.xml ^
-cp .\config;.\myFSG-@build.version@.jar ^
-splash:splash.gif ^
symbolthree.calla.MAIN -config config\myFSG.properties %* 
endlocal

