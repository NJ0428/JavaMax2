@echo off
echo "=== Java Rhythm Game Build ==="

REM build 폴더가 없으면 생성
if not exist build mkdir build

REM 기존 .class 파일 삭제
if exist build\main rmdir /s /q build\main

echo "Compiling source files..."

REM lib 폴더가 존재하고 JAR 파일이 있는지 확인
if exist src\main\lib\*.jar (
    echo "Compiling with external libraries..."
    javac -cp "src/main/lib/*" -sourcepath src -d build src/main/*.java src/main/game/*.java src/main/ui/*.java src/main/audio/*.java src/main/utils/*.java
) else (
    echo "Compiling with default libraries..."
    javac -sourcepath src -d build src/main/*.java src/main/game/*.java src/main/ui/*.java src/main/audio/*.java src/main/utils/*.java
)

if %errorlevel% equ 0 (
    echo "Compilation successful!"
) else (
    echo "Compilation failed!"
    pause
    exit /b 1
)

echo "=== Build Complete ==="
pause 