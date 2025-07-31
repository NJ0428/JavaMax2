@echo off
echo "=== Java Rhythm Game Execution ==="

REM build 폴더가 있는지 확인
if not exist build\main (
    echo "No compiled files found. Please run build.bat first."
    pause
    exit /b 1
)

echo "Running game..."

REM lib 폴더가 존재하고 JAR 파일이 있는지 확인
if exist src\main\lib\*.jar (
    echo "Running with external libraries..."
    java -cp "build;src/main/lib/*" main.RhythmGame
) else (
    echo "Running with default libraries..."
    java -cp build main.RhythmGame
)

echo "Game terminated"
pause 