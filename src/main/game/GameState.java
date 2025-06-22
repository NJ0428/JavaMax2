package main.game;

/**
 * 게임의 현재 상태를 나타내는 열거형
 */
public enum GameState {
    MENU, // 메인 메뉴
    GAME_SELECT, // 게임 모드 선택
    SONG_SELECT, // 노래 선택
    PLAYING, // 게임 플레이 중
    PAUSED, // 일시정지
    GAME_OVER, // 게임 오버
    RESULT // 결과 화면
}