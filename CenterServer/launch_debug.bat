@echo off
@title CenterServer
set CLASSPATH=.;dist\*;
java -server -Xmx2048m -Xrunjdwp:transport=dt_socket,address=9004,server=y,suspend=n -Dwz="wz/" server.Client
pause