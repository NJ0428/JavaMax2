# 효과음 폴더

이 폴더에는 게임에서 사용되는 효과음 파일들을 저장합니다.

## 파일 명명 규칙

- `hit.wav` - 노트 타격 성공 효과음 (GOOD 판정)
- `perfect.wav` - 완벽한 타격 효과음 (PERFECT 판정)
- `miss.wav` - 노트 놓침 효과음 (MISS 판정)
- `click.wav` - 버튼 클릭 효과음

## 권장 사양

- **형식**: WAV (빠른 로딩과 재생)
- **샘플레이트**: 44.1kHz
- **비트레이트**: 16bit
- **채널**: 모노 또는 스테레오
- **길이**: 0.1-1초 (짧고 명확한 소리)

## 효과음 가이드

- **hit.wav**: 만족스러운 타격감을 주는 소리
- **perfect.wav**: 특별함을 나타내는 높은 톤의 소리
- **miss.wav**: 실망감을 주지 않는 부드러운 소리
- **click.wav**: 명확하고 깔끔한 클릭 소리

## 사용 예시

```java
// 판정에 따른 효과음 재생
audioManager.playJudgmentSound("PERFECT");  // perfect.wav 재생
audioManager.playSoundEffect("click");      // click.wav 재생
```

## 볼륨 가이드

- 배경음악보다 살짝 작은 볼륨으로 설정
- 게임 플레이에 방해가 되지 않는 적절한 크기
- 각 효과음 간의 볼륨 균형 유지
