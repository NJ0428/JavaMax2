# 📚 Library (라이브러리) 폴더

이 폴더는 **외부 Java 라이브러리 JAR 파일들**을 저장하는 곳입니다.

## 📁 용도

- 외부 JAR 라이브러리 파일 저장
- 게임 기능 확장을 위한 추가 라이브러리
- MP3 오디오 재생, 이미지 처리, UI 개선 등

## 🎵 **추천 라이브러리 (MP3 재생용)**

### **1. BasicPlayer** (MP3 재생)

- **파일명**: `basicplayer-3.0.jar`
- **용도**: MP3, OGG 파일 재생
- **다운로드**: [JavaZOOM BasicPlayer](http://www.javazoom.net/basicplayer/basicplayer.html)

### **2. JLayer** (MP3 디코딩)

- **파일명**: `jlayer-1.0.1.jar`
- **용도**: MP3 파일 디코딩
- **다운로드**: [JLayer](http://www.javazoom.net/javalayer/javalayer.html)

### **3. Tritonus** (오디오 지원 확장)

- **파일명**: `tritonus_share.jar`
- **용도**: 추가 오디오 형식 지원

## 🔧 **라이브러리 사용 방법**

### **1. JAR 파일 추가**

1. 라이브러리 JAR 파일을 `src/main/lib/` 폴더에 복사
2. 예시:
   ```
   src/
   └── main/
       └── lib/
           ├── basicplayer-3.0.jar
           ├── jlayer-1.0.1.jar
           └── tritonus_share.jar
   ```

### **2. 컴파일 시 클래스패스 설정**

빌드 스크립트에서 라이브러리 포함:

```batch
javac -cp "src/main/lib/*;." -d . src/main/*.java src/main/*/*.java
```

### **3. 실행 시 클래스패스 설정**

실행 스크립트에서 라이브러리 포함:

```batch
java -cp "src/main/lib/*;." main.RhythmGame
```

## 📋 **현재 상태**

- ❌ MP3 라이브러리 없음 (Java 기본 라이브러리는 MP3 미지원)
- ✅ WAV, AU 형식은 기본 지원
- ✅ 프로그래밍 생성 사운드 지원

## 💡 **MP3 재생 활성화 단계**

### **1단계: 라이브러리 다운로드**

1. BasicPlayer 라이브러리 다운로드
2. `basicplayer-3.0.jar` 파일을 `src/main/lib/` 폴더에 복사

### **2단계: 빌드 스크립트 수정**

`build.bat` 파일에서 클래스패스에 lib 폴더 추가

### **3단계: AudioManager 수정**

MP3 재생을 위한 BasicPlayer 통합 코드 추가

### **4단계: 테스트**

게임에서 MP3 파일 재생 확인

## 🔗 **유용한 링크**

- [JavaZOOM - 오디오 라이브러리](http://www.javazoom.net/)
- [Maven Repository - 오디오 라이브러리 검색](https://mvnrepository.com/)
- [Java Sound API 가이드](https://docs.oracle.com/javase/tutorial/sound/)

## ⚠️ **주의사항**

- 라이브러리 라이센스 확인 필요
- JAR 파일 크기로 인한 프로젝트 용량 증가
- 라이브러리 버전 호환성 확인
