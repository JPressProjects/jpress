@echo off
setlocal

if not exist "%JAVA_HOME%\bin\jps.exe" echo 请先设置您的环境变量 & EXIT /B 1

set MAIN_CLASS=io.jpress.Starter
set "PATH=%JAVA_HOME%\bin;%PATH%"
echo killing jpress application
for /f "tokens=1" %%i in ('jps -l ^| find %MAIN_CLASS%') do ( taskkill /F /PID %%i )
echo Done!