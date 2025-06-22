package main.ui;

import main.RhythmGame;
import main.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 게임 일시정지 시 표시되는 메뉴 패널
 * ESC 키를 눌렀을 때 나타나는 일시정지 메뉴
 */
public class PausePanel extends JPanel {
    private GameFrame gameFrame;
    private JButton continueButton;
    private JButton restartButton;
    private JButton titleButton;
    private JButton exitButton;

    // 버튼 크기 및 위치 설정
    private static final int BUTTON_WIDTH = 300;
    private static final int BUTTON_HEIGHT = 60;
    private static final int BUTTON_SPACING = 20;

    public PausePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setOpaque(false); // 투명 배경
        setLayout(null);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));

        initializeComponents();
    }

    /**
     * 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 버튼들의 시작 Y 위치 계산
        int totalHeight = 4 * BUTTON_HEIGHT + 3 * BUTTON_SPACING;
        int startY = (Constants.WINDOW_HEIGHT - totalHeight) / 2;
        int centerX = (Constants.WINDOW_WIDTH - BUTTON_WIDTH) / 2;

        // 이어하기 버튼 (가장 강조)
        continueButton = createMenuButton("▶ 이어하기", centerX, startY, new Color(50, 180, 50, 220));
        continueButton.addActionListener(e -> resumeGame());
        add(continueButton);

        // 재시작 버튼
        restartButton = createMenuButton("🔄 재시작", centerX, startY + BUTTON_HEIGHT + BUTTON_SPACING,
                new Color(70, 130, 180, 200));
        restartButton.addActionListener(e -> restartGame());
        add(restartButton);

        // 타이틀로 버튼
        titleButton = createMenuButton("🏠 타이틀로", centerX, startY + 2 * (BUTTON_HEIGHT + BUTTON_SPACING),
                new Color(180, 130, 70, 200));
        titleButton.addActionListener(e -> returnToTitle());
        add(titleButton);

        // 게임 종료 버튼
        exitButton = createMenuButton("❌ 게임 종료", centerX, startY + 3 * (BUTTON_HEIGHT + BUTTON_SPACING),
                new Color(180, 70, 70, 200));
        exitButton.addActionListener(e -> exitGame());
        add(exitButton);
    }

    /**
     * 메뉴 버튼을 생성합니다
     */
    private JButton createMenuButton(String text, int x, int y, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new RoundedBorder(15));

        // 마우스 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // 호버 사운드 재생
                if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                    RhythmGame.getInstance().getAudioManager().playUISound("button_hover");
                }

                // 밝기 증가
                Color originalColor = button.getBackground();
                Color hoverColor = new Color(
                        Math.min(255, originalColor.getRed() + 30),
                        Math.min(255, originalColor.getGreen() + 30),
                        Math.min(255, originalColor.getBlue() + 30),
                        Math.min(255, originalColor.getAlpha() + 30));
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // 클릭 시 약간 어둡게
                Color originalColor = backgroundColor;
                Color pressedColor = new Color(
                        Math.max(0, originalColor.getRed() - 20),
                        Math.max(0, originalColor.getGreen() - 20),
                        Math.max(0, originalColor.getBlue() - 20),
                        originalColor.getAlpha());
                button.setBackground(pressedColor);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.contains(e.getPoint())) {
                    // 마우스가 버튼 위에 있으면 호버 색상
                    Color hoverColor = new Color(
                            Math.min(255, backgroundColor.getRed() + 30),
                            Math.min(255, backgroundColor.getGreen() + 30),
                            Math.min(255, backgroundColor.getBlue() + 30),
                            Math.min(255, backgroundColor.getAlpha() + 30));
                    button.setBackground(hoverColor);
                } else {
                    button.setBackground(backgroundColor);
                }
            }
        });

        return button;
    }

    /**
     * 게임을 재개합니다
     */
    private void resumeGame() {
        // 재개 사운드 재생
        if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
            RhythmGame.getInstance().getAudioManager().playUISound("menu_select");
        }
        gameFrame.resumeGame();
    }

    /**
     * 게임을 재시작합니다
     */
    private void restartGame() {
        // 버튼 클릭 사운드
        if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
            RhythmGame.getInstance().getAudioManager().playUISound("click");
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "현재 진행 상황이 사라집니다.\n정말 재시작하시겠습니까?",
                "게임 재시작",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // 확인 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("confirm");
            }
            gameFrame.restartGame();
        } else {
            // 취소 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("cancel");
            }
        }
    }

    /**
     * 타이틀 화면으로 돌아갑니다
     */
    private void returnToTitle() {
        // 버튼 클릭 사운드
        if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
            RhythmGame.getInstance().getAudioManager().playUISound("click");
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "현재 진행 상황이 사라집니다.\n타이틀 화면으로 돌아가시겠습니까?",
                "타이틀로 이동",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // 메뉴 이동 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("menu_back");
            }
            gameFrame.returnToMenu();
        } else {
            // 취소 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("cancel");
            }
        }
    }

    /**
     * 게임을 종료합니다
     */
    private void exitGame() {
        // 버튼 클릭 사운드
        if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
            RhythmGame.getInstance().getAudioManager().playUISound("click");
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "정말 게임을 종료하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // 종료 확인 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("confirm");
            }
            System.exit(0);
        } else {
            // 취소 사운드
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().playUISound("cancel");
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 반투명 배경 오버레이
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 제목과 안내 메시지 그리기
        drawTitle(g2d);
        drawInstructions(g2d);

        // 장식적인 요소들
        drawDecorations(g2d);
    }

    /**
     * 제목을 그립니다
     */
    private void drawTitle(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 36));

        String title = "게임 일시정지";
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (getWidth() - fm.stringWidth(title)) / 2;
        int titleY = 100;

        // 텍스트 그림자 효과
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.drawString(title, titleX + 2, titleY + 2);

        g2d.setColor(Color.WHITE);
        g2d.drawString(title, titleX, titleY);
    }

    /**
     * 조작 안내를 그립니다
     */
    private void drawInstructions(Graphics2D g2d) {
        g2d.setColor(new Color(200, 200, 200, 180));
        g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 16));

        String[] instructions = {
                "ESC : 게임 재개",
                "마우스 클릭으로 메뉴 선택"
        };

        FontMetrics fm = g2d.getFontMetrics();
        int startY = getHeight() - 80;

        for (int i = 0; i < instructions.length; i++) {
            String instruction = instructions[i];
            int instructionX = (getWidth() - fm.stringWidth(instruction)) / 2;
            int instructionY = startY + (i * (fm.getHeight() + 5));
            g2d.drawString(instruction, instructionX, instructionY);
        }
    }

    /**
     * 장식적인 요소들을 그립니다
     */
    private void drawDecorations(Graphics2D g2d) {
        // 중앙 패널 영역 강조
        int panelX = (getWidth() - BUTTON_WIDTH - 40) / 2;
        int panelY = 130;
        int panelWidth = BUTTON_WIDTH + 40;
        int panelHeight = 4 * BUTTON_HEIGHT + 3 * BUTTON_SPACING + 60;

        // 패널 배경
        g2d.setColor(new Color(255, 255, 255, 30));
        g2d.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // 패널 테두리
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);

        // 상단 장식 라인
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(panelX + 20, panelY + 40, panelX + panelWidth - 20, panelY + 40);
    }
}

/**
 * 둥근 모서리 Border 클래스
 */
class RoundedBorder implements javax.swing.border.Border {
    private int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius + 1, radius + 1, radius + 2, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}