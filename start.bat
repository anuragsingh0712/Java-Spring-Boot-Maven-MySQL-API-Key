@echo off
if not defined SERVER_PORT set SERVER_PORT=29586

echo Building application...
call mvn package -DskipTests -q
if errorlevel 1 exit /b %errorlevel%

echo Starting application on port %SERVER_PORT%...
java -jar target\app.jar --server.port=%SERVER_PORT%
