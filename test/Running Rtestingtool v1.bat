REM Start the RTestingtool
@echo on
java -jar RTestingTools.jar
cd LOG
@echo off
for /f "tokens=*" %%a in ('dir /b /od') do set newest=%%a
@echo on
%newest%
PAUSE