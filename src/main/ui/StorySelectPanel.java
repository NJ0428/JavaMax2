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
        titleLabel = new JLabel("스토리 모드", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 20, Constants.WINDOW_WIDTH, 50);
        add(titleLabel);

        // 진행률 표시
        progressLabel = new JLabel("", SwingConstants.CENTER);
        progressLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        progressLabel.setForeground(Color.LIGHT_GRAY);
        progressLabel.setBounds(0, 70, Constants.WINDOW_WIDTH, 30);
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
        storyListPanel.setBackground(new Color(30, 30, 50));

        scrollPane = new JScrollPane(storyListPanel);
        scrollPane.setBounds(50, 120, 400, 400);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(new Color(30, 30, 50));
        add(scrollPane);
    }

    /**
     * 스토리 정보 패널을 생성합니다
     */
    private void createStoryInfoPanel() {
        storyInfoPanel = new JPanel();
        storyInfoPanel.setLayout(null);
        storyInfoPanel.setBounds(480, 120, 450, 400);
        storyInfoPanel.setBackground(new Color(40, 40, 60));
        storyInfoPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 2));

        // 스토리 제목
        storyTitleLabel = new JLabel("스토리 제목");
        storyTitleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        storyTitleLabel.setForeground(Color.WHITE);
        storyTitleLabel.setBounds(20, 20, 410, 30);
        storyInfoPanel.add(storyTitleLabel);

        // 스토리 설명
        storyDescLabel = new JLabel("<html>스토리 설명</html>");
        storyDescLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        storyDescLabel.setForeground(Color.LIGHT_GRAY);
        storyDescLabel.setBounds(20, 60, 410, 40);
        storyInfoPanel.add(storyDescLabel);

        // 요구사항
        storyRequirementLabel = new JLabel("요구사항: ");
        storyRequirementLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        storyRequirementLabel.setForeground(Color.YELLOW);
        storyRequirementLabel.setBounds(20, 110, 410, 20);
        storyInfoPanel.add(storyRequirementLabel);

        // 대화 내용
        storyDialogueArea = new JTextArea();
        storyDialogueArea.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        storyDialogueArea.setForeground(Color.WHITE);
        storyDialogueArea.setBackground(new Color(20, 20, 40));
        storyDialogueArea.setEditable(false);
        storyDialogueArea.setLineWrap(true);
        storyDialogueArea.setWrapStyleWord(true);
        storyDialogueArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane dialogueScrollPane = new JScrollPane(storyDialogueArea);
        dialogueScrollPane.setBounds(20, 140, 410, 200);
        dialogueScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        storyInfoPanel.add(dialogueScrollPane);

        add(storyInfoPanel);
    }

    /**
     * 버튼들을 생성합니다
     */
    private void createButtons() {
        // 뒤로가기 버튼
        backButton = new JButton("뒤로가기");
        backButton.setBounds(50, 550, 120, 40);
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        backButton.addActionListener(e -> {
            if (gameFrame.getAudioManager() != null) {
                gameFrame.getAudioManager().playUISound("menu_back");
            }
            gameFrame.showGameSelect();
        });
        add(backButton);

        // 시작 버튼
        startButton = new JButton("스토리 시작");
        startButton.setBounds(810, 550, 120, 40);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        startButton.addActionListener(e -> {
            if (gameFrame.getAudioManager() != null) {
                gameFrame.getAudioManager().playUISound("confirm");
            }
            startSelectedStory();
        });
        add(startButton);
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
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout());
        item.setPreferredSize(new Dimension(380, 80));
        item.setMaximumSize(new Dimension(380, 80));

        // 선택된 아이템과 일반 아이템 구분
        if (index == selectedStoryIndex) {
            item.setBackground(new Color(70, 70, 120));
            item.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        } else {
            item.setBackground(new Color(50, 50, 80));
            item.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 1));
        }

        // 스토리 정보 패널
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);

        // 제목과 상태
        JLabel titleLabel = new JLabel(story.getTitle());
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 5, 15));

        JLabel statusLabel = new JLabel(story.isCompleted() ? "완료됨" : "진행 가능");
        statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        statusLabel.setForeground(story.isCompleted() ? Color.GREEN : Color.CYAN);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 15, 10, 15));

        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(statusLabel, BorderLayout.SOUTH);

        // 난이도 표시
        JLabel difficultyLabel = new JLabel(story.getRequiredDifficulty().getDisplayName());
        difficultyLabel.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        difficultyLabel.setForeground(getDifficultyColor(story.getRequiredDifficulty()));
        difficultyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        item.add(infoPanel, BorderLayout.CENTER);
        item.add(difficultyLabel, BorderLayout.EAST);

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
                    item.setBackground(new Color(60, 60, 100));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (index != selectedStoryIndex) {
                    item.setBackground(new Color(50, 50, 80));
                }
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