package main.game;

import main.utils.Constants;
import main.audio.AudioManager;
import java.util.*;
import java.util.List;

/**
 * 리듬 게임의 핵심 엔진
 * 노트 생성, 판정, 게임 로직을 관리합니다
 */
public class GameEngine {
    private List<Note> notes;
    private ScoreManager scoreManager;
    private GameState gameState;
    private GameMode currentGameMode;
    private boolean[] lanePressed; // 각 레인의 키 입력 상태
    private long gameStartTime;
    private Random random;
    private int noteSpawnTimer;
    private int noteSpawnInterval;
    private AudioManager audioManager;
    private main.game.Story currentStory; // 현재 진행 중인 스토리

    public GameEngine() {
        notes = new ArrayList<>();
        scoreManager = new ScoreManager();
        lanePressed = new boolean[Constants.NOTE_LANES];
        random = new Random();
        gameState = GameState.MENU;
        noteSpawnInterval = 60; // 60프레임마다 노트 생성 (1초)
        // AudioManager는 외부에서 설정하도록 함
    }

    /**
     * AudioManager를 설정합니다
     */
    public void setAudioManager(AudioManager audioManager) {
        this.audioManager = audioManager;

        // 게임 음악 종료 리스너 설정
        if (audioManager != null) {
            audioManager.setGameMusicEndListener(() -> {
                System.out.println("게임 음악이 종료되었습니다. 게임을 종료합니다.");
                endGame();
            });
        }
    }

    /**
     * 게임을 시작합니다
     */
    public void startGame() {
        startGameWithMode(GameMode.SINGLE_PLAY);
    }

    /**
     * 특정 모드로 게임을 시작합니다
     */
    public void startGameWithMode(GameMode mode) {
        this.currentGameMode = mode;
        gameState = GameState.PLAYING;
        gameStartTime = System.currentTimeMillis();
        scoreManager.reset();
        notes.clear();
        noteSpawnTimer = 0;
        Arrays.fill(lanePressed, false);

        // 모드별 초기 설정
        setupGameMode(mode);
    }

    /**
     * 게임 모드별 설정을 적용합니다
     */
    private void setupGameMode(GameMode mode) {
        switch (mode) {
            case SINGLE_PLAY:
                noteSpawnInterval = 60; // 기본 속도
                break;
            case STORY_MODE:
                noteSpawnInterval = 80; // 조금 느리게 시작
                break;
            case MULTI_PLAY:
                noteSpawnInterval = 50; // 조금 빠르게
                break;
            case PRACTICE_MODE:
                noteSpawnInterval = 120; // 연습용으로 느리게
                break;
            case RANKING_MODE:
                noteSpawnInterval = 45; // 도전용으로 빠르게
                break;
        }
    }

    /**
     * 게임을 일시정지/재개합니다
     */
    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
            if (audioManager != null) {
                audioManager.pauseGame();
            }
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
            if (audioManager != null) {
                audioManager.resumeGame();
            }
        }
    }

    /**
     * 음악은 계속 재생하면서 게임 상태만 리셋합니다
     */
    public void resetGameOnly() {
        gameState = GameState.PLAYING;
        gameStartTime = System.currentTimeMillis();
        scoreManager.reset();
        notes.clear();
        noteSpawnTimer = 0;
        Arrays.fill(lanePressed, false);
        
        // 음악은 재시작하지 않고 계속 재생
        System.out.println("게임 상태만 리셋됨 - 음악은 계속 재생");
    }

    /**
     * 게임을 종료합니다
     */
    public void endGame() {
        gameState = GameState.RESULT;

        // 스토리 모드인 경우 스토리 완료 처리
        if (currentGameMode == GameMode.STORY_MODE && currentStory != null) {
            int finalScore = scoreManager.getScore();
            StoryManager.getInstance().completeStory(currentStory.getStoryId(), finalScore);

            System.out.println("스토리 완료 처리: " + currentStory.getTitle() +
                    " (점수: " + finalScore + "/" + currentStory.getRequiredScore() + ")");
        }

        if (audioManager != null) {
            // 게임 결과에 따라 다른 사운드 재생
            if (scoreManager.getScore() >= 1000) { // 성공 기준 (예시)
                audioManager.playGameSuccessSound();
            } else {
                audioManager.playGameOverSound();
            }
        }
    }

    /**
     * 메인 메뉴로 돌아갑니다
     */
    public void returnToMenu() {
        gameState = GameState.MENU;
        notes.clear();
        scoreManager.reset();
        if (audioManager != null) {
            audioManager.playUISound("menu_back");
        }
    }

    /**
     * 게임 로직을 업데이트합니다
     */
    public void update() {
        if (gameState != GameState.PLAYING) {
            return;
        }

        // 노트 생성
        spawnNotes();

        // 모든 노트 업데이트
        for (Note note : notes) {
            note.update();
        }

        // 놓친 노트 처리
        checkMissedNotes();

        // 완료된 노트 제거
        notes.removeIf(note -> note.isHit() || note.isMissed());
    }

    /**
     * 노트를 생성합니다
     */
    private void spawnNotes() {
        noteSpawnTimer++;
        if (noteSpawnTimer >= noteSpawnInterval) {
            // 랜덤하게 1-2개의 레인에 노트 생성
            int numNotes = random.nextInt(2) + 1;
            Set<Integer> usedLanes = new HashSet<>();

            for (int i = 0; i < numNotes; i++) {
                int lane;
                do {
                    lane = random.nextInt(Constants.NOTE_LANES);
                } while (usedLanes.contains(lane));

                usedLanes.add(lane);
                notes.add(new Note(lane, -Constants.NOTE_HEIGHT));
            }

            noteSpawnTimer = 0;
            // 게임이 진행될수록 노트 생성 간격 감소
            noteSpawnInterval = Math.max(30, 60 - (int) (getGameTime() / 1000));
        }
    }

    /**
     * 놓친 노트를 체크합니다
     */
    private void checkMissedNotes() {
        for (Note note : notes) {
            if (!note.isHit() && !note.isMissed()) {
                int distance = note.getDistanceFromJudgmentLine();
                int missRange = Constants.MISS_RANGE * Constants.NOTE_SPEED;

                // 판정선을 너무 많이 지나간 노트는 MISS 처리
                if (note.getY() > Constants.JUDGMENT_LINE_Y + missRange) {
                    note.setMissed(true);
                    scoreManager.processJudgment("MISS");
                }
            }
        }
    }

    /**
     * 키 입력을 처리합니다
     * 
     * @param lane 입력된 레인 (0-3)
     */
    public void processKeyInput(int lane) {
        if (gameState != GameState.PLAYING || lane < 0 || lane >= Constants.NOTE_LANES) {
            return;
        }

        lanePressed[lane] = true;

        // 터치 사운드 재생
        if (audioManager != null) {
            audioManager.playTouchSound();
        }

        // 해당 레인에서 가장 가까운 노트 찾기
        Note closestNote = null;
        int minDistance = Integer.MAX_VALUE;

        for (Note note : notes) {
            if (note.getLane() == lane && !note.isHit() && !note.isMissed() && note.isInJudgmentRange()) {
                int distance = note.getDistanceFromJudgmentLine();
                if (distance < minDistance) {
                    minDistance = distance;
                    closestNote = note;
                }
            }
        }

        // 노트가 있으면 판정 처리
        if (closestNote != null) {
            String judgment = closestNote.getJudgment();
            closestNote.setHit(true);
            scoreManager.processJudgment(judgment);

            // Touch 소리만 재생하므로 판정 사운드는 제거
            // (이미 위에서 Touch 소리가 재생됨)
        }
    }

    /**
     * 키 릴리즈를 처리합니다
     * 
     * @param lane 릴리즈된 레인 (0-3)
     */
    public void processKeyRelease(int lane) {
        if (lane >= 0 && lane < Constants.NOTE_LANES) {
            lanePressed[lane] = false;
        }
    }

    /**
     * 게임 시작부터 경과된 시간을 반환합니다
     */
    public long getGameTime() {
        if (gameState == GameState.PLAYING) {
            return System.currentTimeMillis() - gameStartTime;
        }
        return 0;
    }

    // Getters
    public List<Note> getNotes() {
        return notes;
    }

    public ScoreManager getScoreManager() {
        return scoreManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public boolean[] getLanePressed() {
        return lanePressed;
    }

    public GameMode getCurrentGameMode() {
        return currentGameMode;
    }

    public void setGameState(GameState state) {
        this.gameState = state;
    }

    /**
     * 현재 스토리를 설정합니다
     */
    public void setCurrentStory(main.game.Story story) {
        this.currentStory = story;
    }

    /**
     * 현재 스토리를 반환합니다
     */
    public main.game.Story getCurrentStory() {
        return currentStory;
    }
}