@echo off
setlocal
cd /d "%~dp0"
if not exist "dist\PotionAtelier-v2.jar" (
    call "%~dp0compile_and_run.bat"
    exit /b
)
java -Dfile.encoding=UTF-8 -jar "dist\PotionAtelier-v2.jar"
if errorlevel 1 (
    echo Cannot start the game. Java 17 or newer is required.
    pause
)
