package main.game;

/**
 * 게임 모드를 나타내는 열거형
 */
public enum GameMode {
    SINGLE_PLAY("싱글 플레이", "혼자서 즐기는 기본 리듬 게임"),
    STORY_MODE("스토리 모드", "단계별로 진행하는 스토리가 있는 모드"),
    MULTI_PLAY("멀티 플레이", "2명이 함께 경쟁하는 모드"),
    PRACTICE_MODE("연습 모드", "실력 향상을 위한 연습 모드"),
    RANKING_MODE("랭킹 모드", "최고 점수에 도전하는 모드");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}