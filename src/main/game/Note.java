package main.game;

import main.utils.Constants;
import java.awt.*;

/**
 * 리듬 게임의 노트를 나타내는 클래스
 */
public class Note {
    private int lane; // 노트가 속한 레인 (0-3)
    private int x; // X 좌표
    private int y; // Y 좌표
    private boolean hit; // 노트가 타격되었는지 여부
    private boolean missed; // 노트가 놓쳤는지 여부
    private Color color; // 노트 색상

    public Note(int lane, int startY) {
        this.lane = lane;
        this.x = lane * Constants.LANE_WIDTH + (Constants.LANE_WIDTH - Constants.NOTE_WIDTH) / 2;
        this.y = startY;
        this.hit = false;
        this.missed = false;
        this.color = Constants.NOTE_COLOR;
    }

    /**
     * 노트를 업데이트 (아래로 이동)
     */
    public void update() {
        if (!hit && !missed) {
            y += Constants.NOTE_SPEED;

            // 화면 밖으로 나가면 놓친 것으로 처리
            if (y > Constants.GAME_HEIGHT) {
                missed = true;
            }
        }
    }

    /**
     * 노트를 그립니다
     */
    public void draw(Graphics2D g2d) {
        if (!hit && !missed) {
            g2d.setColor(color);
            g2d.fillRoundRect(x, y, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, 10, 10);

            // 노트 테두리
            g2d.setColor(Color.WHITE);
            g2d.drawRoundRect(x, y, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, 10, 10);
        }
    }

    /**
     * 판정선과의 거리를 계산합니다
     */
    public int getDistanceFromJudgmentLine() {
        return Math.abs((y + Constants.NOTE_HEIGHT / 2) - Constants.JUDGMENT_LINE_Y);
    }

    /**
     * 노트가 판정 가능한 범위에 있는지 확인
     */
    public boolean isInJudgmentRange() {
        return getDistanceFromJudgmentLine() <= Constants.MISS_RANGE * Constants.NOTE_SPEED;
    }

    /**
     * 판정 결과를 반환합니다
     * 
     * @return "PERFECT", "GOOD", "MISS" 중 하나
     */
    public String getJudgment() {
        int distance = getDistanceFromJudgmentLine();
        int perfectRange = Constants.PERFECT_RANGE * Constants.NOTE_SPEED;
        int goodRange = Constants.GOOD_RANGE * Constants.NOTE_SPEED;

        if (distance <= perfectRange) {
            return "PERFECT";
        } else if (distance <= goodRange) {
            return "GOOD";
        } else {
            return "MISS";
        }
    }

    // Getters and Setters
    public int getLane() {
        return lane;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setHit(boolean hit) {
        this.hit = hit;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}