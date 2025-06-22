package main.game;

import main.utils.Constants;

/**
 * 게임의 점수와 판정을 관리하는 클래스
 */
public class ScoreManager {
    private int score;
    private int combo;
    private int maxCombo;
    private int perfectCount;
    private int goodCount;
    private int missCount;
    private String lastJudgment;
    private long lastJudgmentTime;

    public ScoreManager() {
        reset();
    }

    /**
     * 점수 관리자를 초기화합니다
     */
    public void reset() {
        score = 0;
        combo = 0;
        maxCombo = 0;
        perfectCount = 0;
        goodCount = 0;
        missCount = 0;
        lastJudgment = "";
        lastJudgmentTime = 0;
    }

    /**
     * 판정 결과를 처리합니다
     * 
     * @param judgment "PERFECT", "GOOD", "MISS" 중 하나
     */
    public void processJudgment(String judgment) {
        lastJudgment = judgment;
        lastJudgmentTime = System.currentTimeMillis();

        switch (judgment) {
            case "PERFECT":
                score += Constants.PERFECT_SCORE + (combo * 10); // 콤보 보너스
                combo++;
                perfectCount++;
                break;
            case "GOOD":
                score += Constants.GOOD_SCORE + (combo * 5); // 콤보 보너스
                combo++;
                goodCount++;
                break;
            case "MISS":
                combo = 0; // 콤보 리셋
                missCount++;
                break;
        }

        // 최대 콤보 업데이트
        if (combo > maxCombo) {
            maxCombo = combo;
        }
    }

    /**
     * 현재 점수를 반환합니다
     */
    public int getScore() {
        return score;
    }

    /**
     * 현재 콤보를 반환합니다
     */
    public int getCombo() {
        return combo;
    }

    /**
     * 최대 콤보를 반환합니다
     */
    public int getMaxCombo() {
        return maxCombo;
    }

    /**
     * 정확도를 계산합니다 (퍼센트)
     */
    public double getAccuracy() {
        int totalNotes = perfectCount + goodCount + missCount;
        if (totalNotes == 0)
            return 100.0;

        return ((double) (perfectCount + goodCount) / totalNotes) * 100.0;
    }

    /**
     * 최근 판정이 표시되어야 하는지 확인
     */
    public boolean shouldShowLastJudgment() {
        return System.currentTimeMillis() - lastJudgmentTime < 1000; // 1초간 표시
    }

    /**
     * 게임 결과 요약을 반환합니다
     */
    public String getGameSummary() {
        return String.format(
                "점수: %d\n최대 콤보: %d\n정확도: %.2f%%\n\nPERFECT: %d\nGOOD: %d\nMISS: %d",
                score, maxCombo, getAccuracy(), perfectCount, goodCount, missCount);
    }

    // Getters
    public int getPerfectCount() {
        return perfectCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public int getMissCount() {
        return missCount;
    }

    public String getLastJudgment() {
        return lastJudgment;
    }

    public long getLastJudgmentTime() {
        return lastJudgmentTime;
    }
}