@echo off
echo "=== Java 리듬 게임 빌드 ==="

REM build 폴더가 없으면 생성
if not exist build mkdir build

REM 기존 .class 파일 삭제
if exist build\main rmdir /s /q build\main

echo "소스 파일 컴파일 중..."

REM lib 폴더가 존재하고 JAR 파일이 있는지 확인
if exist src\main\lib\*.jar (
    echo "외부 라이브러리와 함께 컴파일..."
    javac -cp "src/main/lib/*" -sourcepath src -d build src/main/*.java src/main/game/*.java src/main/ui/*.java src/main/audio/*.java src/main/utils/*.java
) else (
    echo "기본 라이브러리로 컴파일..."
    javac -sourcepath src -d build src/main/*.java src/main/game/*.java src/main/ui/*.java src/main/audio/*.java src/main/utils/*.java
)

if %errorlevel% equ 0 (
    echo "컴파일 성공!"
) else (
    echo "컴파일 실패!"
    pause
    exit /b 1
)

echo "=== 빌드 완료 ==="
pause 