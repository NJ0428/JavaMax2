@echo off
echo "=== Java 리듬 게임 실행 ==="

REM build 폴더가 있는지 확인
if not exist build\main (
    echo "컴파일된 파일이 없습니다. 먼저 build.bat을 실행하세요."
    pause
    exit /b 1
)

echo "게임 실행 중..."

REM lib 폴더가 존재하고 JAR 파일이 있는지 확인
if exist src\main\lib\*.jar (
    echo "외부 라이브러리와 함께 실행..."
    java -cp "build;src/main/lib/*" main.RhythmGame
) else (
    echo "기본 라이브러리로 실행..."
    java -cp build main.RhythmGame
)

echo "게임 종료"
pause 