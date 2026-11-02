@echo off
setlocal
cd /d "%~dp0"
echo Building Potion Atelier...
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build.ps1"
if errorlevel 1 (
    echo Build failed. Install Java JDK 17 or newer and try again.
    pause
    exit /b 1
)
java -Dfile.encoding=UTF-8 -jar "dist\PotionAtelier-v2.jar"
if errorlevel 1 pause
