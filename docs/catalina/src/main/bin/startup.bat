@echo off
title dongnao-vip-netty
SET MAIN_CLASS="com.dongnao.chat.server.ChatServer"

SET APP_HOME="%~dp0.."
SET LOG_DIR="%APP_HOME%/logs"

echo JAVA_HOME = "%JAVA_HOME%"
SET JAVA_EXE="%JAVA_HOME%\bin\java.exe"

SET CLASSPATH=.;../classes;
FOR %%F IN (..\lib\*.jar) DO call :ADDCP %%F
goto RUN

:ADDCP
set CLASSPATH=%CLASSPATH%;%1
goto :EOF 

:RUN
echo %CLASSPATH%
%JAVA_EXE% -DlogDir=%LOG_DIR% %MAIN_CLASS% -classpath %CLASSPATH% 

CMD
