package main.ui;

import main.game.Story;
import main.utils.Constants;
import main.utils.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

/**
 * 스토리 대화 화면을 담당하는 패널
 */
public class StoryDialoguePanel extends JPanel implements KeyListener {
    private GameFrame gameFrame;
    private Story currentStory;
    private boolean isBeforeGame; // true: 게임 시작 전, false: 게임 완료 후
    private String[] dialogueLines;
    private int currentLineIndex;
    private String currentDisplayText;
    private Timer textAnimationTimer;
    private boolean isTextComplete;

    // UI 컴포넌트
    private JLabel characterNameLabel;
    private JTextArea dialogueArea;
    private JLabel instructionLabel;
    private JButton nextButton;
    private JButton skipButton;

    // 애니메이션 관련
    private int charIndex;
    private static final int TEXT_SPEED = 50; // 밀리초 단위

    public StoryDialoguePanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.currentLineIndex = 0;
        this.charIndex = 0;
        this.isTextComplete = false;

        setLayout(null);
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setFocusable(true);
        addKeyListener(this);

        initializeComponents();
        setupTextAnimation();
    }

    /**
     * UI 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 캐릭터 이름 라벨
        characterNameLabel = new JLabel("", SwingConstants.CENTER);
        characterNameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        characterNameLabel.setForeground(Color.WHITE);
        characterNameLabel.setBounds(50, 50, 200, 30);
        characterNameLabel.setOpaque(true);
        characterNameLabel.setBackground(new Color(0, 0, 0, 150));
        characterNameLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(characterNameLabel);

        // 대화 영역
        dialogueArea = new JTextArea();
        dialogueArea.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        dialogueArea.setForeground(Color.WHITE);
        dialogueArea.setBackground(new Color(0, 0, 0, 180));
        dialogueArea.setEditable(false);
        dialogueArea.setLineWrap(true);
        dialogueArea.setWrapStyleWord(true);
        dialogueArea.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane dialogueScrollPane = new JScrollPane(dialogueArea);
        dialogueScrollPane.setBounds(50, 400, Constants.WINDOW_WIDTH - 100, 150);
        dialogueScrollPane.setOpaque(false);
        dialogueScrollPane.getViewport().setOpaque(false);
        dialogueScrollPane.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 2));
        add(dialogueScrollPane);

        // 안내 라벨
        instructionLabel = new JLabel("SPACE 키를 눌러 계속하거나 SKIP 버튼을 클릭하세요", SwingConstants.CENTER);
        instructionLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        instructionLabel.setForeground(Color.LIGHT_GRAY);
        instructionLabel.setBounds(0, 570, Constants.WINDOW_WIDTH, 20);
        add(instructionLabel);

        // 다음 버튼
        nextButton = new JButton("다음 (SPACE)");
        nextButton.setBounds(Constants.WINDOW_WIDTH - 250, 600, 100, 30);
        nextButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        nextButton.addActionListener(e -> nextLine());
        add(nextButton);

        // 건너뛰기 버튼
        skipButton = new JButton("건너뛰기 (ESC)");
        skipButton.setBounds(Constants.WINDOW_WIDTH - 140, 600, 100, 30);
        skipButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        skipButton.addActionListener(e -> skipDialogue());
        add(skipButton);
    }

    /**
     * 텍스트 애니메이션을 설정합니다
     */
    private void setupTextAnimation() {
        textAnimationTimer = new Timer(TEXT_SPEED, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dialogueLines != null && currentLineIndex < dialogueLines.length) {
                    String fullText = dialogueLines[currentLineIndex];
                    if (charIndex < fullText.length()) {
                        currentDisplayText = fullText.substring(0, charIndex + 1);
                        dialogueArea.setText(currentDisplayText);
                        charIndex++;
                    } else {
                        textAnimationTimer.stop();
                        isTextComplete = true;
                        updateButtonStates();
                    }
                }
            }
        });
    }

    /**
     * 스토리 대화를 시작합니다
     */
    public void startDialogue(Story story, boolean beforeGame) {
        this.currentStory = story;
        this.isBeforeGame = beforeGame;
        this.currentLineIndex = 0;
        this.charIndex = 0;
        this.isTextComplete = false;

        // 대화 텍스트 준비
        String dialogueText = beforeGame ? story.getDialogueBefore() : story.getDialogueAfter();
        dialogueLines = dialogueText.split("\n");

        // 캐릭터 이름 설정
        String characterName = getCharacterName(story);
        characterNameLabel.setText(characterName);

        // 첫 번째 라인 표시 시작
        showCurrentLine();

        requestFocus();
    }

    /**
     * 스토리에 따른 캐릭터 이름을 반환합니다
     */
    private String getCharacterName(Story story) {
        if (story.getCharacterImage().contains("rhythm")) {
            return "리듬";
        } else if (story.getCharacterImage().contains("rival")) {
            return "라이벌";
        } else if (story.getCharacterImage().contains("boss")) {
            return "리듬의 신";
        } else {
            return "???";
        }
    }

    /**
     * 현재 라인을 표시합니다
     */
    private void showCurrentLine() {
        if (dialogueLines != null && currentLineIndex < dialogueLines.length) {
            charIndex = 0;
            currentDisplayText = "";
            isTextComplete = false;
            dialogueArea.setText("");
            textAnimationTimer.start();
            updateButtonStates();
        }
    }

    /**
     * 다음 라인으로 이동합니다
     */
    private void nextLine() {
        if (gameFrame.getAudioManager() != null) {
            gameFrame.getAudioManager().playUISound("click");
        }

        if (!isTextComplete) {
            // 텍스트 애니메이션이 진행 중이면 즉시 완료
            textAnimationTimer.stop();
            if (dialogueLines != null && currentLineIndex < dialogueLines.length) {
                dialogueArea.setText(dialogueLines[currentLineIndex]);
            }
            isTextComplete = true;
            updateButtonStates();
            return;
        }

        currentLineIndex++;
        if (currentLineIndex < dialogueLines.length) {
            showCurrentLine();
        } else {
            finishDialogue();
        }
    }

    /**
     * 대화를 건너뜁니다
     */
    private void skipDialogue() {
        if (gameFrame.getAudioManager() != null) {
            gameFrame.getAudioManager().playUISound("menu_back");
        }
        finishDialogue();
    }

    /**
     * 대화를 완료합니다
     */
    private void finishDialogue() {
        textAnimationTimer.stop();

        if (isBeforeGame) {
            // 게임 시작 전 대화 완료 -> 게임 시작
            gameFrame.startStoryGame(currentStory);
        } else {
            // 게임 완료 후 대화 완료 -> 스토리 선택 화면으로 복귀
            gameFrame.showStorySelect();
        }
    }

    /**
     * 버튼 상태를 업데이트합니다
     */
    private void updateButtonStates() {
        if (isTextComplete) {
            if (currentLineIndex >= dialogueLines.length - 1) {
                nextButton.setText(isBeforeGame ? "게임 시작" : "완료");
            } else {
                nextButton.setText("다음 (SPACE)");
            }
        } else {
            nextButton.setText("전체 표시");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 그라디언트
        GradientPaint gradient = new GradientPaint(
                0, 0, new Color(20, 20, 40),
                0, getHeight(), new Color(40, 20, 60));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 스토리 배경 이미지
        if (currentStory != null) {
            BufferedImage backgroundImage = ImageLoader.getInstance().getImage(currentStory.getBackgroundImage());
            if (backgroundImage != null) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }

        // 캐릭터 이미지
        if (currentStory != null) {
            BufferedImage characterImage = ImageLoader.getInstance().getImage(currentStory.getCharacterImage());
            if (characterImage != null) {
                int charWidth = 300;
                int charHeight = 400;
                int charX = getWidth() - charWidth - 50;
                int charY = 50;
                g2d.drawImage(characterImage, charX, charY, charWidth, charHeight, null);
            }
        }

        // 대화창 배경 강조
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(40, 390, getWidth() - 80, 170);
    }

    // KeyListener 구현
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
            case KeyEvent.VK_ENTER:
                nextLine();
                break;
            case KeyEvent.VK_ESCAPE:
                skipDialogue();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // 사용하지 않음
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }
}