package main.ui;

import main.game.GameEngine;
import main.game.GameState;
import main.game.Note;
import main.game.ScoreManager;
import main.utils.Constants;
import main.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 게임 플레이 화면을 담당하는 패널 (6키 레이아웃)
 */
public class GamePanel extends JPanel {
    private GameEngine gameEngine;
    private PausePanel pausePanel;

    // 게임 영역 설정
    private static final int GAME_AREA_WIDTH = 400;
    private static final int GAME_AREA_HEIGHT = 600;
    private static final int GAME_AREA_X = 50;
    private static final int GAME_AREA_Y = 50;

    // 배경 동영상 영역
    private static final int VIDEO_AREA_WIDTH = 700;
    private static final int VIDEO_AREA_HEIGHT = 400;
    private static final int VIDEO_AREA_X = 500;
    private static final int VIDEO_AREA_Y = 50;

    // 키 버튼 영역
    private static final int KEY_BUTTON_WIDTH = 60;
    private static final int KEY_BUTTON_HEIGHT = 40;
    private static final int KEY_AREA_Y = GAME_AREA_Y + GAME_AREA_HEIGHT + 20;

    public GamePanel(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);
        setFocusable(true);
    }

    /**
     * PausePanel을 설정합니다
     */
    public void setPausePanel(PausePanel pausePanel) {
        if (this.pausePanel != null) {
            remove(this.pausePanel);
        }
        this.pausePanel = pausePanel;
        if (pausePanel != null) {
            pausePanel.setBounds(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            add(pausePanel);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그라디언트
        drawBackground(g2d);

        // 게임 상태에 따른 렌더링
        switch (gameEngine.getGameState()) {
            case PLAYING:
                drawGameplay(g2d);
                if (pausePanel != null) {
                    pausePanel.setVisible(false);
                }
                break;
            case PAUSED:
                drawGameplay(g2d);
                if (pausePanel != null) {
                    pausePanel.setVisible(true);
                }
                break;
        }
    }

    /**
     * 배경을 그립니다
     */
    private void drawBackground(Graphics2D g2d) {
        // 배경 이미지 그리기
        BufferedImage backgroundImage = ImageLoader.getInstance().getImage("background_game");
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        } else {
            // 배경 이미지가 없을 경우 기본 그라디언트
            GradientPaint gradient = new GradientPaint(
                    0, 0, Constants.BACKGROUND_COLOR,
                    0, getHeight(), new Color(40, 40, 60));
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 게임플레이 요소들을 그립니다
     */
    private void drawGameplay(Graphics2D g2d) {
        // 게임 영역 그리기
        drawGameArea(g2d);

        // 배경 동영상 영역 그리기
        drawVideoArea(g2d);

        // 레인과 노트들 그리기
        drawLanesAndNotes(g2d);

        // 키 버튼들 그리기
        drawKeyButtons(g2d);

        // 콤보 표시
        drawCombo(g2d);

        // 점수 패널
        drawScorePanel(g2d);
    }

    /**
     * 게임 영역을 그립니다
     */
    private void drawGameArea(Graphics2D g2d) {
        // 게임 영역 배경
        g2d.setColor(new Color(30, 30, 50, 200));
        g2d.fillRect(GAME_AREA_X, GAME_AREA_Y, GAME_AREA_WIDTH, GAME_AREA_HEIGHT);

        // 게임 영역 테두리
        g2d.setColor(new Color(100, 100, 150));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(GAME_AREA_X, GAME_AREA_Y, GAME_AREA_WIDTH, GAME_AREA_HEIGHT);

        // 게임 화면 라벨
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g2d.drawString("게임 화면", GAME_AREA_X + 10, GAME_AREA_Y - 10);
    }

    /**
     * 배경 동영상 영역을 그립니다
     */
    private void drawVideoArea(Graphics2D g2d) {
        // 배경 동영상 영역
        g2d.setColor(new Color(20, 20, 40));
        g2d.fillRect(VIDEO_AREA_X, VIDEO_AREA_Y, VIDEO_AREA_WIDTH, VIDEO_AREA_HEIGHT);

        // 동영상 영역 테두리
        g2d.setColor(new Color(100, 100, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(VIDEO_AREA_X, VIDEO_AREA_Y, VIDEO_AREA_WIDTH, VIDEO_AREA_HEIGHT);

        // 배경 동영상 라벨
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g2d.drawString("배경 동영상", VIDEO_AREA_X + 10, VIDEO_AREA_Y - 10);

        // 동영상 대신 그라디언트 효과
        GradientPaint videoGradient = new GradientPaint(
                VIDEO_AREA_X, VIDEO_AREA_Y, new Color(50, 50, 100),
                VIDEO_AREA_X + VIDEO_AREA_WIDTH, VIDEO_AREA_Y + VIDEO_AREA_HEIGHT, new Color(100, 50, 150));
        g2d.setPaint(videoGradient);
        g2d.fillRect(VIDEO_AREA_X + 2, VIDEO_AREA_Y + 2, VIDEO_AREA_WIDTH - 4, VIDEO_AREA_HEIGHT - 4);

        // 동영상 센터 텍스트
        g2d.setColor(new Color(255, 255, 255, 150));
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        String videoText = "배경 뮤직비디오";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = VIDEO_AREA_X + (VIDEO_AREA_WIDTH - fm.stringWidth(videoText)) / 2;
        int textY = VIDEO_AREA_Y + (VIDEO_AREA_HEIGHT + fm.getHeight()) / 2;
        g2d.drawString(videoText, textX, textY);
    }

    /**
     * 레인과 노트들을 그립니다
     */
    private void drawLanesAndNotes(Graphics2D g2d) {
        int laneWidth = GAME_AREA_WIDTH / Constants.NOTE_LANES;

        // 레인 배경
        for (int i = 0; i < Constants.NOTE_LANES; i++) {
            int laneX = GAME_AREA_X + (i * laneWidth);

            // 레인 배경색 (교대로)
            if (i % 2 == 0) {
                g2d.setColor(new Color(70, 70, 120, 100));
            } else {
                g2d.setColor(new Color(50, 50, 100, 100));
            }
            g2d.fillRect(laneX, GAME_AREA_Y, laneWidth, GAME_AREA_HEIGHT);

            // 레인 구분선
            g2d.setColor(new Color(100, 100, 150, 150));
            g2d.setStroke(new BasicStroke(1));
            g2d.drawLine(laneX, GAME_AREA_Y, laneX, GAME_AREA_Y + GAME_AREA_HEIGHT);
        }

        // 판정선
        int judgmentY = GAME_AREA_Y + GAME_AREA_HEIGHT - 80;
        g2d.setColor(Constants.JUDGMENT_LINE_COLOR);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(GAME_AREA_X, judgmentY, GAME_AREA_X + GAME_AREA_WIDTH, judgmentY);

        // 판정선 글로우 효과
        g2d.setColor(new Color(255, 255, 0, 100));
        g2d.setStroke(new BasicStroke(8));
        g2d.drawLine(GAME_AREA_X, judgmentY, GAME_AREA_X + GAME_AREA_WIDTH, judgmentY);

        // 노트들 그리기
        drawNotes(g2d, laneWidth, judgmentY);

        // 키 입력 이펙트
        drawKeyEffects(g2d, laneWidth, judgmentY);
    }

    /**
     * 노트들을 그립니다
     */
    private void drawNotes(Graphics2D g2d, int laneWidth, int judgmentY) {
        List<Note> notes = gameEngine.getNotes();

        for (Note note : notes) {
            if (!note.isHit() && !note.isMissed()) {
                int laneX = GAME_AREA_X + (note.getLane() * laneWidth);
                int noteX = laneX + (laneWidth - Constants.NOTE_WIDTH) / 2;
                int noteY = GAME_AREA_Y + note.getY();

                // 노트 이미지 사용
                BufferedImage noteImage = ImageLoader.getInstance().getImage("note_default");
                if (noteImage != null) {
                    g2d.drawImage(noteImage, noteX, noteY, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, null);
                } else {
                    // 이미지가 없을 경우 기본 그리기
                    // 노트 그림자
                    g2d.setColor(new Color(0, 0, 0, 100));
                    g2d.fillRoundRect(noteX + 2, noteY + 2, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, 8, 8);

                    // 노트 본체
                    g2d.setColor(Constants.NOTE_COLOR);
                    g2d.fillRoundRect(noteX, noteY, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, 8, 8);

                    // 노트 테두리
                    g2d.setColor(new Color(255, 150, 150));
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(noteX, noteY, Constants.NOTE_WIDTH, Constants.NOTE_HEIGHT, 8, 8);
                }
            }
        }
    }

    /**
     * 키 입력 이펙트를 그립니다
     */
    private void drawKeyEffects(Graphics2D g2d, int laneWidth, int judgmentY) {
        boolean[] lanePressed = gameEngine.getLanePressed();

        for (int i = 0; i < Constants.NOTE_LANES; i++) {
            if (lanePressed[i]) {
                int laneX = GAME_AREA_X + (i * laneWidth);

                // 키 입력 이펙트 (세로 라인)
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRect(laneX + 5, GAME_AREA_Y, laneWidth - 10, GAME_AREA_HEIGHT);

                // 판정선 근처 강조 효과
                g2d.setColor(new Color(255, 255, 0, 150));
                g2d.fillRect(laneX, judgmentY - 30, laneWidth, 60);
            }
        }
    }

    /**
     * 키 버튼들을 그립니다
     */
    private void drawKeyButtons(Graphics2D g2d) {
        boolean[] lanePressed = gameEngine.getLanePressed();
        int totalWidth = Constants.NOTE_LANES * KEY_BUTTON_WIDTH + (Constants.NOTE_LANES - 1) * 10;
        int startX = GAME_AREA_X + (GAME_AREA_WIDTH - totalWidth) / 2;

        for (int i = 0; i < Constants.NOTE_LANES; i++) {
            int buttonX = startX + i * (KEY_BUTTON_WIDTH + 10);

            // 버튼 배경
            if (lanePressed[i]) {
                g2d.setColor(new Color(150, 150, 200));
            } else {
                g2d.setColor(new Color(80, 80, 120));
            }
            g2d.fillRoundRect(buttonX, KEY_AREA_Y, KEY_BUTTON_WIDTH, KEY_BUTTON_HEIGHT, 8, 8);

            // 버튼 테두리
            g2d.setColor(new Color(200, 200, 230));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(buttonX, KEY_AREA_Y, KEY_BUTTON_WIDTH, KEY_BUTTON_HEIGHT, 8, 8);

            // 키 라벨
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            String keyLabel = Constants.LANE_KEY_NAMES[i];
            FontMetrics fm = g2d.getFontMetrics();
            int textX = buttonX + (KEY_BUTTON_WIDTH - fm.stringWidth(keyLabel)) / 2;
            int textY = KEY_AREA_Y + (KEY_BUTTON_HEIGHT + fm.getHeight()) / 2 - 2;
            g2d.drawString(keyLabel, textX, textY);
        }
    }

    /**
     * 콤보를 표시합니다
     */
    private void drawCombo(Graphics2D g2d) {
        ScoreManager scoreManager = gameEngine.getScoreManager();
        int combo = scoreManager.getCombo();

        if (combo > 0) {
            // 콤보 배경
            int comboX = GAME_AREA_X + GAME_AREA_WIDTH / 2 - 80;
            int comboY = GAME_AREA_Y + 100;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRoundRect(comboX, comboY, 160, 60, 15, 15);

            // 콤보 텍스트
            g2d.setColor(Color.YELLOW);
            g2d.setFont(new Font("맑은 고딕", Font.BOLD, 24));
            String comboText = combo + "";
            FontMetrics fm = g2d.getFontMetrics();
            int textX = comboX + (160 - fm.stringWidth(comboText)) / 2;
            g2d.drawString(comboText, textX, comboY + 35);

            // 콤보 라벨
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
            g2d.drawString("콤보", comboX + 65, comboY + 55);
        }
    }


    /**
     * 점수 패널을 그립니다
     */
    private void drawScorePanel(Graphics2D g2d) {
        ScoreManager scoreManager = gameEngine.getScoreManager();

        // 점수 패널 배경
        int panelX = VIDEO_AREA_X;
        int panelY = VIDEO_AREA_Y + VIDEO_AREA_HEIGHT + 30;
        int panelWidth = VIDEO_AREA_WIDTH;
        int panelHeight = 120;

        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // 패널 테두리
        g2d.setColor(new Color(100, 100, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 15, 15);

        // 게임 점수 라벨
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        g2d.drawString("게임 점수", panelX + 10, panelY - 5);

        // 점수 정보들
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        int textY = panelY + 25;
        g2d.drawString("점수: " + scoreManager.getScore(), panelX + 20, textY);

        textY += 25;
        g2d.drawString("콤보: " + scoreManager.getCombo(), panelX + 20, textY);

        textY += 25;
        g2d.drawString("최대 콤보: " + scoreManager.getMaxCombo(), panelX + 20, textY);

        // 정확도
        double accuracy = scoreManager.getAccuracy();
        g2d.drawString(String.format("정확도: %.2f%%", accuracy), panelX + 250, panelY + 25);

        // 판정 카운트
        g2d.drawString("PERFECT: " + scoreManager.getPerfectCount(), panelX + 250, panelY + 50);
        g2d.drawString("GOOD: " + scoreManager.getGoodCount(), panelX + 250, panelY + 75);
        g2d.drawString("MISS: " + scoreManager.getMissCount(), panelX + 400, panelY + 50);

        // 현재 판정 표시
        if (scoreManager.shouldShowLastJudgment()) {
            String currentJudgment = scoreManager.getLastJudgment();
            if (currentJudgment != null && !currentJudgment.isEmpty()) {
                g2d.setFont(new Font("맑은 고딕", Font.BOLD, 20));

                Color judgmentColor = Color.WHITE;
                switch (currentJudgment) {
                    case "PERFECT":
                        judgmentColor = Color.YELLOW;
                        break;
                    case "GOOD":
                        judgmentColor = Color.GREEN;
                        break;
                    case "MISS":
                        judgmentColor = Color.RED;
                        break;
                }

                g2d.setColor(judgmentColor);
                g2d.drawString(currentJudgment, panelX + 500, panelY + 60);
            }
        }
    }

}