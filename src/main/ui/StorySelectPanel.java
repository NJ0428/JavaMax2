package main.ui;

import main.game.Song;
import main.game.Story;
import main.game.StoryManager;
import main.utils.Constants;
import main.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 스토리 선택 화면을 담당하는 패널
 */
public class StorySelectPanel extends JPanel {
    private GameFrame gameFrame;
    private StoryManager storyManager;
    private List<Story> stories;
    private int selectedStoryIndex;
    private JScrollPane scrollPane;
    private JPanel storyListPanel;

    // UI 컴포넌트
    private JLabel titleLabel;
    private JLabel progressLabel;
    private JButton backButton;
    private JButton startButton;

    // 스토리 정보 패널
    private JPanel storyInfoPanel;
    private JLabel storyTitleLabel;
    private JLabel storyDescLabel;
    private JLabel storyRequirementLabel;
    private JTextArea storyDialogueArea;

    public StorySelectPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.storyManager = StoryManager.getInstance();
        this.stories = storyManager.getUnlockedStories();
        this.selectedStoryIndex = 0;

        setLayout(null);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);

        initializeComponents();
        updateStoryList();
        updateStoryInfo();
    }

    /**
     * UI 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 제목
        titleLabel = new JLabel("스토리 모드", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 텍스트 그림자
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x + 2, y + 2);

                // 메인 텍스트
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBounds(0, 15, Constants.WINDOW_WIDTH, 60);
        add(titleLabel);

        // 진행률 표시
        progressLabel = new JLabel("", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 진행률 바 배경
                int barWidth = 300;
                int barHeight = 8;
                int barX = (getWidth() - barWidth) / 2;
                int barY = getHeight() / 2 + 10;

                g2d.setColor(new Color(40, 40, 60));
                g2d.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);

                // 진행률 바
                double progress = main.game.StoryManager.getInstance().getProgressRate();
                int progressWidth = (int) (barWidth * progress);

                GradientPaint progressGradient = new GradientPaint(
                        barX, barY, new Color(100, 200, 255),
                        barX + progressWidth, barY, new Color(50, 150, 255));
                g2d.setPaint(progressGradient);
                g2d.fillRoundRect(barX, barY, progressWidth, barHeight, 4, 4);

                // 텍스트
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = getHeight() / 2 - 5;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }
        };
        progressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        progressLabel.setForeground(new Color(200, 200, 220));
        progressLabel.setBounds(0, 75, Constants.WINDOW_WIDTH, 40);
        updateProgressLabel();
        add(progressLabel);

        // 스토리 목록 패널
        createStoryListPanel();

        // 스토리 정보 패널
        createStoryInfoPanel();

        // 버튼들
        createButtons();
    }

    /**
     * 스토리 목록 패널을 생성합니다
     */
    private void createStoryListPanel() {
        storyListPanel = new JPanel();
        storyListPanel.setLayout(new BoxLayout(storyListPanel, BoxLayout.Y_AXIS));
        storyListPanel.setOpaque(false);

        // 스크롤 패널을 감싸는 컨테이너
        JPanel scrollContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 그림자 효과
                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 15, 15);

                // 메인 배경
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(40, 40, 70),
                        0, getHeight(), new Color(25, 25, 50));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                // 테두리
                g2d.setColor(new Color(100, 100, 150, 120));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 5, getHeight() - 5, 15, 15);

                g2d.dispose();
            }
        };
        scrollContainer.setLayout(new BorderLayout());
        scrollContainer.setBounds(50, 120, 400, 400);
        scrollContainer.setOpaque(false);

        scrollPane = new JScrollPane(storyListPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 스크롤바 스타일링
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(80, 80, 120);
                this.trackColor = new Color(40, 40, 60);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });

        scrollContainer.add(scrollPane, BorderLayout.CENTER);
        add(scrollContainer);
    }

    /**
     * 스토리 정보 패널을 생성합니다
     */
    private void createStoryInfoPanel() {
        storyInfoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 그림자 효과
                g2d.setColor(new Color(0, 0, 0, 80));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);

                // 메인 배경 그라디언트
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(50, 50, 80),
                        0, getHeight(), new Color(30, 30, 60));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);

                // 테두리
                g2d.setColor(new Color(120, 120, 180, 150));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 7, getHeight() - 7, 20, 20);

                // 내부 하이라이트
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 11, getHeight() / 4, 15, 15);

                g2d.dispose();
            }
        };
        storyInfoPanel.setLayout(null);
        storyInfoPanel.setBounds(480, 120, 450, 400);
        storyInfoPanel.setOpaque(false);

        // 스토리 제목
        storyTitleLabel = new JLabel("스토리 제목");
        storyTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        storyTitleLabel.setForeground(new Color(255, 255, 255));
        storyTitleLabel.setBounds(25, 25, 400, 35);
        storyInfoPanel.add(storyTitleLabel);

        // 스토리 설명
        storyDescLabel = new JLabel("<html>스토리 설명</html>");
        storyDescLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
        storyDescLabel.setForeground(new Color(200, 200, 220));
        storyDescLabel.setBounds(25, 70, 400, 45);
        storyInfoPanel.add(storyDescLabel);

        // 요구사항 패널
        JPanel requirementPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 요구사항 배경
                g2d.setColor(new Color(80, 60, 40, 100));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                // 요구사항 테두리
                g2d.setColor(new Color(255, 215, 0, 150));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

                g2d.dispose();
            }
        };
        requirementPanel.setLayout(new BorderLayout());
        requirementPanel.setBounds(25, 125, 400, 30);
        requirementPanel.setOpaque(false);

        storyRequirementLabel = new JLabel("요구사항: ");
        storyRequirementLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        storyRequirementLabel.setForeground(new Color(255, 215, 0));
        storyRequirementLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        requirementPanel.add(storyRequirementLabel, BorderLayout.CENTER);
        storyInfoPanel.add(requirementPanel);

        // 대화 내용 패널
        JPanel dialoguePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 대화 패널 배경
                g2d.setColor(new Color(25, 25, 45));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                // 대화 패널 테두리
                g2d.setColor(new Color(100, 150, 200, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);

                g2d.dispose();
            }
        };
        dialoguePanel.setLayout(new BorderLayout());
        dialoguePanel.setBounds(25, 170, 400, 200);
        dialoguePanel.setOpaque(false);

        storyDialogueArea = new JTextArea();
        storyDialogueArea.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        storyDialogueArea.setForeground(new Color(220, 220, 240));
        storyDialogueArea.setBackground(new Color(25, 25, 45));
        storyDialogueArea.setEditable(false);
        storyDialogueArea.setLineWrap(true);
        storyDialogueArea.setWrapStyleWord(true);
        storyDialogueArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane dialogueScrollPane = new JScrollPane(storyDialogueArea);
        dialogueScrollPane.setOpaque(false);
        dialogueScrollPane.getViewport().setOpaque(false);
        dialogueScrollPane.setBorder(null);
        dialogueScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialogueScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // 스크롤바 스타일링
        dialogueScrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(100, 100, 150);
                this.trackColor = new Color(40, 40, 60);
            }
        });

        dialoguePanel.add(dialogueScrollPane, BorderLayout.CENTER);
        storyInfoPanel.add(dialoguePanel);

        add(storyInfoPanel);
    }

    /**
     * 버튼들을 생성합니다
     */
    private void createButtons() {
        // 뒤로가기 버튼
        backButton = createStyledButton("뒤로가기", new Color(30, 50, 100), new Color(50, 70, 120));
        backButton.setBounds(50, 550, 140, 50);
        backButton.addActionListener(e -> {
            if (gameFrame.getAudioManager() != null) {
                gameFrame.getAudioManager().playUISound("menu_back");
            }
            gameFrame.showGameSelect();
        });
        add(backButton);

        // 시작 버튼
        startButton = createStyledButton("스토리 시작", new Color(30, 50, 100), new Color(50, 70, 120));
        startButton.setBounds(790, 550, 140, 50);
        startButton.addActionListener(e -> {
            if (gameFrame.getAudioManager() != null) {
                gameFrame.getAudioManager().playUISound("confirm");
            }
            startSelectedStory();
        });
        add(startButton);
    }

    /**
     * 스타일이 적용된 버튼을 생성합니다
     */
    private JButton createStyledButton(String text, Color baseColor, Color hoverColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 버튼 상태에 따른 색상 결정
                Color currentColor = baseColor;
                if (!isEnabled()) {
                    currentColor = new Color(60, 60, 60);
                } else if (getModel().isPressed()) {
                    currentColor = new Color(baseColor.getRed() - 20, baseColor.getGreen() - 20,
                            baseColor.getBlue() - 20);
                } else if (getModel().isRollover()) {
                    currentColor = hoverColor;
                }

                // 그라디언트 배경
                GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), new Color(currentColor.getRed() - 30, currentColor.getGreen() - 30,
                                currentColor.getBlue() - 30));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // 테두리
                g2d.setColor(isEnabled() ? new Color(200, 200, 255, 100) : new Color(100, 100, 100, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);

                // 내부 하이라이트
                if (isEnabled()) {
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() / 2, 10, 10);
                }

                // 텍스트
                g2d.setColor(isEnabled() ? new Color(200, 200, 200) : Color.GRAY);
                g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), textX, textY);

                g2d.dispose();
            }

            @Override
            public void setEnabled(boolean enabled) {
                super.setEnabled(enabled);
                repaint();
            }
        };

        button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        button.setForeground(new Color(200, 200, 200));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    /**
     * 스토리 목록을 업데이트합니다
     */
    private void updateStoryList() {
        storyListPanel.removeAll();
        stories = storyManager.getUnlockedStories();

        for (int i = 0; i < stories.size(); i++) {
            Story story = stories.get(i);
            JPanel storyItem = createStoryItem(story, i);
            storyListPanel.add(storyItem);
            storyListPanel.add(Box.createVerticalStrut(5)); // 간격
        }

        storyListPanel.revalidate();
        storyListPanel.repaint();
        updateProgressLabel();
    }

    /**
     * 개별 스토리 아이템을 생성합니다
     */
    private JPanel createStoryItem(Story story, int index) {
        JPanel item = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 배경 그라디언트
                Color baseColor;
                Color shadowColor;

                if (index == selectedStoryIndex) {
                    baseColor = new Color(90, 90, 140);
                    shadowColor = new Color(70, 70, 120);
                } else {
                    baseColor = new Color(60, 60, 90);
                    shadowColor = new Color(40, 40, 70);
                }

                // 그림자 효과
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 12, 12);

                // 메인 배경
                GradientPaint gradient = new GradientPaint(
                        0, 0, baseColor,
                        0, getHeight(), shadowColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);

                // 테두리
                if (index == selectedStoryIndex) {
                    g2d.setColor(new Color(255, 215, 0, 200)); // 골드 색상
                    g2d.setStroke(new BasicStroke(3));
                } else {
                    g2d.setColor(new Color(120, 120, 180, 100));
                    g2d.setStroke(new BasicStroke(1));
                }
                g2d.drawRoundRect(1, 1, getWidth() - 5, getHeight() - 5, 12, 12);

                // 내부 하이라이트
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.fillRoundRect(3, 3, getWidth() - 9, getHeight() / 3, 8, 8);

                g2d.dispose();
            }
        };

        item.setLayout(new BorderLayout());
        item.setPreferredSize(new Dimension(380, 85));
        item.setMaximumSize(new Dimension(380, 85));
        item.setOpaque(false);

        // 스토리 정보 패널
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        // 제목과 상태
        JLabel titleLabel = new JLabel(story.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(12, 18, 5, 15));

        JLabel statusLabel = new JLabel(story.isCompleted() ? "✓ 완료됨" : "▶ 진행 가능");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        statusLabel.setForeground(story.isCompleted() ? new Color(100, 255, 100) : new Color(100, 200, 255));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 18, 12, 15));

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);

        // 난이도 표시 패널
        JPanel difficultyPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color diffColor = getDifficultyColor(story.getRequiredDifficulty());

                // 난이도 배경
                g2d.setColor(new Color(diffColor.getRed(), diffColor.getGreen(), diffColor.getBlue(), 30));
                g2d.fillRoundRect(5, 15, getWidth() - 15, getHeight() - 30, 8, 8);

                // 난이도 테두리
                g2d.setColor(diffColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(5, 15, getWidth() - 15, getHeight() - 30, 8, 8);

                g2d.dispose();
            }
        };
        difficultyPanel.setLayout(new BorderLayout());
        difficultyPanel.setOpaque(false);
        difficultyPanel.setPreferredSize(new Dimension(80, 85));

        JLabel difficultyLabel = new JLabel(story.getRequiredDifficulty().getDisplayName(), SwingConstants.CENTER);
        difficultyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        difficultyLabel.setForeground(getDifficultyColor(story.getRequiredDifficulty()));
        difficultyPanel.add(difficultyLabel, BorderLayout.CENTER);

        item.add(infoPanel, BorderLayout.CENTER);
        item.add(difficultyPanel, BorderLayout.EAST);

        // 클릭 이벤트
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedStoryIndex = index;
                if (gameFrame.getAudioManager() != null) {
                    gameFrame.getAudioManager().playUISound("menu_select");
                }
                updateStoryList();
                updateStoryInfo();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (index != selectedStoryIndex) {
                    item.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                item.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                item.repaint();
            }
        });

        return item;
    }

    /**
     * 난이도에 따른 색상을 반환합니다
     */
    private Color getDifficultyColor(Song.Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                return Color.GREEN;
            case NORMAL:
                return Color.YELLOW;
            case HARD:
                return Color.ORANGE;
            case EXPERT:
                return Color.RED;
            default:
                return Color.WHITE;
        }
    }

    /**
     * 스토리 정보를 업데이트합니다
     */
    private void updateStoryInfo() {
        if (stories.isEmpty() || selectedStoryIndex >= stories.size()) {
            storyTitleLabel.setText("스토리를 선택하세요");
            storyDescLabel.setText("");
            storyRequirementLabel.setText("");
            storyDialogueArea.setText("");
            startButton.setEnabled(false);
            return;
        }

        Story selectedStory = stories.get(selectedStoryIndex);

        storyTitleLabel.setText(selectedStory.getTitle());
        storyDescLabel.setText("<html>" + selectedStory.getDescription() + "</html>");
        storyRequirementLabel.setText("요구사항: " + selectedStory.getRequiredDifficulty().getDisplayName() +
                " 난이도, " + selectedStory.getRequiredScore() + "점 이상");
        storyDialogueArea.setText(selectedStory.getDialogueBefore());

        startButton.setEnabled(true);
        startButton.setText(selectedStory.isCompleted() ? "다시 플레이" : "스토리 시작");
    }

    /**
     * 진행률 라벨을 업데이트합니다
     */
    private void updateProgressLabel() {
        int completed = storyManager.getCompletedStoryCount();
        int total = storyManager.getTotalStoryCount();
        double progress = storyManager.getProgressRate() * 100;

        progressLabel.setText(String.format("진행률: %d/%d (%.1f%%)", completed, total, progress));
    }

    /**
     * 선택된 스토리를 시작합니다
     */
    private void startSelectedStory() {
        if (stories.isEmpty() || selectedStoryIndex >= stories.size()) {
            return;
        }

        Story selectedStory = stories.get(selectedStoryIndex);

        // 스토리 대화 화면으로 이동
        gameFrame.showStoryDialogue(selectedStory, true); // true = 게임 시작 전 대화
    }

    /**
     * 패널이 활성화될 때 호출됩니다
     */
    public void onPanelActivated() {
        updateStoryList();
        updateStoryInfo();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그라디언트
        GradientPaint gradient = new GradientPaint(
                0, 0, Constants.BACKGROUND_COLOR,
                0, getHeight(), new Color(20, 20, 40));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 배경 이미지 (있다면)
        BufferedImage backgroundImage = ImageLoader.getInstance().getImage("background_story");
        if (backgroundImage != null) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}