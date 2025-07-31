package main.ui;

import main.game.ScoreManager;
import main.utils.Constants;
import main.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 게임 결과 화면을 담당하는 패널
 */
public class ResultPanel extends JPanel {
    private GameFrame gameFrame;
    private ScoreManager scoreManager;

    public ResultPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);
    }

    /**
     * 결과를 업데이트합니다
     */
    public void updateResult(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;

        // 스토리 모드인 경우 완료 후 대화 화면으로 이동 준비
        if (gameFrame.getGameEngine().getCurrentGameMode() == main.game.GameMode.STORY_MODE) {
            main.game.Story currentStory = gameFrame.getGameEngine().getCurrentStory();
            if (currentStory != null) {
                // 2초 후 스토리 완료 대화 화면으로 이동
                Timer delayTimer = new Timer(2000, e -> {
                    gameFrame.showStoryDialogue(currentStory, false); // false = 게임 완료 후 대화
                });
                delayTimer.setRepeats(false);
                delayTimer.start();
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scoreManager == null)
            return;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그라디언트
        drawBackground(g2d);

        // 결과 내용
        drawResults(g2d);

        // 등급 표시
        drawGrade(g2d);

        // 안내 메시지
        drawInstructions(g2d);
    }

    /**
     * 배경을 그립니다
     */
    private void drawBackground(Graphics2D g2d) {
        // 배경 이미지 그리기
        BufferedImage backgroundImage = ImageLoader.getInstance().getImage("background_result");
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

        // 장식 효과
        drawBackgroundEffects(g2d);
    }

    /**
     * 배경 효과를 그립니다
     */
    private void drawBackgroundEffects(Graphics2D g2d) {
        // 별들
        g2d.setColor(new Color(255, 255, 255, 50));
        for (int i = 0; i < 20; i++) {
            int x = (i * 137) % getWidth();
            int y = (i * 211) % getHeight();
            int size = 2 + (i % 3);
            g2d.fillOval(x, y, size, size);
        }

        // 원형 장식
        g2d.setColor(new Color(100, 100, 150, 30));
        for (int i = 0; i < 3; i++) {
            int x = 100 + i * 400;
            int y = 200 + (i % 2) * 200;
            int size = 150 + i * 50;
            g2d.drawOval(x - size / 2, y - size / 2, size, size);
        }
    }

    /**
     * 결과를 그립니다
     */
    private void drawResults(Graphics2D g2d) {
        // 제목
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        String title = "게임 결과";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, 100);

        // 결과 패널
        int panelX = getWidth() / 2 - 200;
        int panelY = 150;
        int panelWidth = 400;
        int panelHeight = 350;

        // 패널 배경
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // 패널 테두리
        g2d.setColor(new Color(100, 100, 150));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // 점수 정보
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 24));

        int contentX = panelX + 30;
        int contentY = panelY + 50;

        g2d.drawString("최종 점수: " + scoreManager.getScore(), contentX, contentY);

        contentY += 40;
        g2d.drawString("최대 콤보: " + scoreManager.getMaxCombo(), contentX, contentY);

        contentY += 40;
        g2d.drawString("정확도: " + String.format("%.2f%%", scoreManager.getAccuracy()), contentX, contentY);

        // 판정 세부사항
        contentY += 50;
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 18));

        // PERFECT
        g2d.setColor(new Color(255, 215, 0));
        g2d.drawString("PERFECT: " + scoreManager.getPerfectCount(), contentX, contentY);

        contentY += 30;
        g2d.setColor(new Color(0, 255, 0));
        g2d.drawString("GOOD: " + scoreManager.getGoodCount(), contentX, contentY);

        contentY += 30;
        g2d.setColor(new Color(255, 0, 0));
        g2d.drawString("MISS: " + scoreManager.getMissCount(), contentX, contentY);
    }

    /**
     * 등급을 그립니다
     */
    private void drawGrade(Graphics2D g2d) {
        String grade = calculateGrade();
        Color gradeColor = getGradeColor(grade);

        g2d.setColor(gradeColor);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 72));

        FontMetrics fm = g2d.getFontMetrics();
        int gradeX = (getWidth() - fm.stringWidth(grade)) / 2;
        int gradeY = 600;

        // 그림자 효과
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(grade, gradeX + 3, gradeY + 3);

        // 메인 텍스트
        g2d.setColor(gradeColor);
        g2d.drawString(grade, gradeX, gradeY);

        // 등급 설명
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        g2d.setColor(Color.LIGHT_GRAY);
        String gradeDesc = getGradeDescription(grade);
        fm = g2d.getFontMetrics();
        int descX = (getWidth() - fm.stringWidth(gradeDesc)) / 2;
        g2d.drawString(gradeDesc, descX, gradeY + 40);
    }

    /**
     * 등급을 계산합니다
     */
    private String calculateGrade() {
        double accuracy = scoreManager.getAccuracy();

        if (accuracy >= 98.0)
            return "S";
        else if (accuracy >= 95.0)
            return "A";
        else if (accuracy >= 90.0)
            return "B";
        else if (accuracy >= 80.0)
            return "C";
        else if (accuracy >= 70.0)
            return "D";
        else
            return "F";
    }

    /**
     * 등급별 색상을 반환합니다
     */
    private Color getGradeColor(String grade) {
        switch (grade) {
            case "S":
                return new Color(255, 215, 0); // 금색
            case "A":
                return new Color(255, 0, 0); // 빨간색
            case "B":
                return new Color(255, 165, 0); // 주황색
            case "C":
                return new Color(255, 255, 0); // 노란색
            case "D":
                return new Color(0, 255, 0); // 초록색
            case "F":
                return new Color(128, 128, 128); // 회색
            default:
                return Color.WHITE;
        }
    }

    /**
     * 등급 설명을 반환합니다
     */
    private String getGradeDescription(String grade) {
        switch (grade) {
            case "S":
                return "완벽해요!";
            case "A":
                return "훌륭해요!";
            case "B":
                return "좋아요!";
            case "C":
                return "괜찮아요!";
            case "D":
                return "더 연습해보세요!";
            case "F":
                return "다시 도전해보세요!";
            default:
                return "";
        }
    }

    /**
     * 안내 메시지를 그립니다
     */
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        String instruction = "ENTER 키를 눌러 메뉴로 돌아가세요";
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (getWidth() - fm.stringWidth(instruction)) / 2;
        int textY = getHeight() - 50;

        g2d.drawString(instruction, textX, textY);
    }
}