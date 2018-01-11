@echo off
@title BinDumpTest
set CLASSPATH=.;dist\*;
java -server -Xmx8082m -Xrunjdwp:transport=dt_socket,address=9013,server=y,suspend=n -Dwz="wz/" wz.MapleDataFactory
pause