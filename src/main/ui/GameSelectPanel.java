package main.ui;

import main.game.GameMode;
import main.utils.Constants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 게임 모드 선택 화면을 담당하는 패널
 */
public class GameSelectPanel extends JPanel {
    private GameFrame gameFrame;
    private GameMode selectedMode;
    private JButton[] modeButtons;
    private JLabel descriptionLabel;

    public GameSelectPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);

        initializeComponents();
    }

    /**
     * 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 제목
        JLabel titleLabel = new JLabel("게임 모드 선택", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(Constants.WINDOW_WIDTH / 2 - 200, 80, 400, 60);
        add(titleLabel);

        // 모드 버튼들 생성
        createModeButtons();

        // 설명 패널
        createDescriptionPanel();

        // 하단 버튼들
        createBottomButtons();

        // 기본 선택 모드 설정
        selectMode(GameMode.SINGLE_PLAY);
    }

    /**
     * 게임 모드 버튼들을 생성합니다
     */
    private void createModeButtons() {
        GameMode[] modes = GameMode.values();
        modeButtons = new JButton[modes.length];

        int startY = 180;
        int buttonHeight = 70;
        int buttonSpacing = 85;
        int buttonWidth = 350;

        for (int i = 0; i < modes.length; i++) {
            final GameMode mode = modes[i];
            final int index = i;

            JButton button = createModeButton(mode.getDisplayName(),
                    Constants.WINDOW_WIDTH / 2 - buttonWidth / 2,
                    startY + (i * buttonSpacing),
                    buttonWidth,
                    buttonHeight);

            button.addActionListener(e -> {
                selectMode(mode);
                // 싱글 플레이는 바로 노래 선택 화면으로 이동
                if (mode == GameMode.SINGLE_PLAY) {
                    gameFrame.showSongSelectScreen();
                }
            });
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (selectedMode != mode) {
                        updateButtonAppearance(index, true);
                    }
                    showModeDescription(mode);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (selectedMode != mode) {
                        updateButtonAppearance(index, false);
                    }
                    if (selectedMode != null) {
                        showModeDescription(selectedMode);
                    }
                }
            });

            modeButtons[i] = button;
            add(button);
        }
    }

    /**
     * 모드 버튼을 생성합니다
     */
    private JButton createModeButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button.setBounds(x, y, width, height);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        button.setBackground(new Color(80, 80, 120));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        return button;
    }

    /**
     * 설명 패널을 생성합니다
     */
    private void createDescriptionPanel() {
        // 설명 패널 배경
        JPanel descPanel = new JPanel();
        descPanel.setBounds(Constants.WINDOW_WIDTH - 350, 200, 320, 200);
        descPanel.setBackground(new Color(0, 0, 0, 150));
        descPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        descPanel.setLayout(new BorderLayout());

        // 설명 제목
        JLabel descTitle = new JLabel("모드 설명", SwingConstants.CENTER);
        descTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        descTitle.setForeground(Color.WHITE);
        descPanel.add(descTitle, BorderLayout.NORTH);

        // 설명 내용
        descriptionLabel = new JLabel("<html><center>게임 모드를 선택하세요</center></html>");
        descriptionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.LIGHT_GRAY);
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descPanel.add(descriptionLabel, BorderLayout.CENTER);

        add(descPanel);
    }

    /**
     * 하단 버튼들을 생성합니다
     */
    private void createBottomButtons() {
        // 뒤로가기 버튼
        JButton backButton = createBottomButton("뒤로가기", 50);
        backButton.addActionListener(e -> gameFrame.returnToMenu());
        add(backButton);
    }

    /**
     * 하단 버튼을 생성합니다
     */
    private JButton createBottomButton(String text, int x) {
        JButton button = new JButton(text);
        button.setBounds(x, Constants.WINDOW_HEIGHT - 80, 150, 40);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setBackground(new Color(100, 100, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // 마우스 호버 효과
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(120, 120, 170));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(100, 100, 150));
            }
        });

        return button;
    }

    /**
     * 게임 모드를 선택합니다
     */
    private void selectMode(GameMode mode) {
        this.selectedMode = mode;

        // 모든 버튼의 외관 업데이트
        for (int i = 0; i < modeButtons.length; i++) {
            GameMode buttonMode = GameMode.values()[i];
            updateButtonAppearance(i, buttonMode == mode);
        }

        showModeDescription(mode);
    }

    /**
     * 버튼 외관을 업데이트합니다
     */
    private void updateButtonAppearance(int index, boolean selected) {
        JButton button = modeButtons[index];
        if (selected) {
            button.setBackground(new Color(150, 150, 200));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 255, 0), 3),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        } else {
            button.setBackground(new Color(80, 80, 120));
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createRaisedBevelBorder(),
                    BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        }
    }

    /**
     * 모드 설명을 표시합니다
     */
    private void showModeDescription(GameMode mode) {
        String description = "<html><center>" + mode.getDescription();

        // 모드별 추가 정보
        switch (mode) {
            case SINGLE_PLAY:
                description += "<br><br>• 기본 4레인 리듬 게임<br>• 개인 최고 점수 도전";
                break;
            case STORY_MODE:
                description += "<br><br>• 10개의 스테이지<br>• 단계별 난이도 증가";
                break;
            case MULTI_PLAY:
                description += "<br><br>• 2명이 동시 플레이<br>• 실시간 점수 비교";
                break;
            case PRACTICE_MODE:
                description += "<br><br>• 속도 조절 가능<br>• 반복 연습 기능";
                break;
            case RANKING_MODE:
                description += "<br><br>• 온라인 랭킹<br>• 전체 순위 확인";
                break;
        }

        description += "</center></html>";
        descriptionLabel.setText(description);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그라디언트
        drawBackground(g2d);

        // 장식 요소들
        drawDecorations(g2d);
    }

    /**
     * 배경을 그립니다
     */
    private void drawBackground(Graphics2D g2d) {
        // 배경 그라디언트
        GradientPaint gradient = new GradientPaint(
                0, 0, Constants.BACKGROUND_COLOR,
                0, getHeight(), new Color(40, 40, 70));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * 장식 요소들을 그립니다
     */
    private void drawDecorations(Graphics2D g2d) {
        // 게임 모드 아이콘들
        g2d.setColor(new Color(255, 255, 255, 30));

        // 싱글 플레이 아이콘 (사람 모양)
        g2d.fillOval(80, 200, 30, 30);
        g2d.fillRect(85, 230, 20, 40);

        // 멀티 플레이 아이콘 (두 사람 모양)
        g2d.fillOval(80, 350, 25, 25);
        g2d.fillOval(110, 350, 25, 25);
        g2d.fillRect(85, 375, 15, 30);
        g2d.fillRect(115, 375, 15, 30);

        // 스토리 모드 아이콘 (책 모양)
        g2d.fillRect(80, 280, 30, 40);
        g2d.setColor(new Color(255, 255, 255, 50));
        g2d.drawLine(85, 285, 105, 285);
        g2d.drawLine(85, 295, 105, 295);
        g2d.drawLine(85, 305, 105, 305);

        // 배경 원형들
        g2d.setColor(new Color(100, 100, 150, 20));
        for (int i = 0; i < 5; i++) {
            int x = 200 + i * 200;
            int y = 100 + (i % 2) * 400;
            int size = 100 + i * 20;
            g2d.drawOval(x - size / 2, y - size / 2, size, size);
        }
    }
}