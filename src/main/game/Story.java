package main.game;

/**
 * 스토리 모드의 개별 스토리를 나타내는 클래스
 */
public class Story {
    private int storyId;
    private String title;
    private String description;
    private String dialogueBefore; // 게임 시작 전 대화
    private String dialogueAfter; // 게임 완료 후 대화
    private Song song;
    private Song.Difficulty requiredDifficulty;
    private int requiredScore; // 클리어에 필요한 최소 점수
    private boolean isUnlocked;
    private boolean isCompleted;
    private String backgroundImage; // 스토리 배경 이미지
    private String characterImage; // 캐릭터 이미지

    public Story(int storyId, String title, String description,
            String dialogueBefore, String dialogueAfter,
            Song song, Song.Difficulty requiredDifficulty,
            int requiredScore, String backgroundImage, String characterImage) {
        this.storyId = storyId;
        this.title = title;
        this.description = description;
        this.dialogueBefore = dialogueBefore;
        this.dialogueAfter = dialogueAfter;
        this.song = song;
        this.requiredDifficulty = requiredDifficulty;
        this.requiredScore = requiredScore;
        this.isUnlocked = (storyId == 1); // 첫 번째 스토리는 기본 해금
        this.isCompleted = false;
        this.backgroundImage = backgroundImage;
        this.characterImage = characterImage;
    }

    // Getters and Setters
    public int getStoryId() {
        return storyId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDialogueBefore() {
        return dialogueBefore;
    }

    public String getDialogueAfter() {
        return dialogueAfter;
    }

    public Song getSong() {
        return song;
    }

    public Song.Difficulty getRequiredDifficulty() {
        return requiredDifficulty;
    }

    public int getRequiredScore() {
        return requiredScore;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public String getCharacterImage() {
        return characterImage;
    }
}