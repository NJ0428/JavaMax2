package main.ui;

import main.utils.Constants;
import main.utils.ImageLoader;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * 게임의 메뉴 화면을 담당하는 패널
 */
public class MenuPanel extends JPanel {
    private GameFrame gameFrame;

    public MenuPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);

        initializeComponents();
    }

    /**
     * 메뉴 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 로고 이미지 (있으면 표시)
        BufferedImage logoImage = ImageLoader.getInstance().getImage("logo");
        if (logoImage != null) {
            JLabel logoLabel = new JLabel(new ImageIcon(logoImage.getScaledInstance(200, 100, Image.SCALE_SMOOTH)));
            logoLabel.setBounds(Constants.WINDOW_WIDTH / 2 - 100, 50, 200, 100);
            add(logoLabel);
        }

        // 제목
        JLabel titleLabel = new JLabel("리듬 게임", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(Constants.WINDOW_WIDTH / 2 - 200, 150, 400, 80);
        add(titleLabel);

        // 시작 버튼
        JButton startButton = createMenuButton("게임 시작", 300);
        startButton.addActionListener(e -> gameFrame.showGameSelectScreen());
        add(startButton);

        // 설정 버튼
        JButton settingsButton = createMenuButton("설정", 370);
        settingsButton.addActionListener(e -> showSettingsDialog());
        add(settingsButton);

        // 종료 버튼
        JButton exitButton = createMenuButton("종료", 440);
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        // 조작법 안내
        JLabel controlsLabel = new JLabel("<html><center>조작법: D, F, J, K 키로 노트를 타격하세요<br>ESC 키로 일시정지</center></html>",
                SwingConstants.CENTER);
        controlsLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        controlsLabel.setForeground(Color.LIGHT_GRAY);
        controlsLabel.setBounds(Constants.WINDOW_WIDTH / 2 - 200, 550, 400, 60);
        add(controlsLabel);
    }

    /**
     * 메뉴 버튼을 생성합니다
     */
    private JButton createMenuButton(String text, int y) {
        JButton button = new JButton(text);
        button.setBounds(Constants.WINDOW_WIDTH / 2 - 100, y, 200, 50);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setBackground(new Color(100, 100, 150));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);

        // 마우스 호버 효과
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(120, 120, 170));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(100, 100, 150));
            }
        });

        return button;
    }

    /**
     * 설정 다이얼로그를 표시합니다
     */
    private void showSettingsDialog() {
        JDialog settingsDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "설정", true);
        settingsDialog.setSize(400, 300);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // 음악 볼륨
        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsDialog.add(new JLabel("음악 볼륨:"), gbc);

        JSlider musicVolumeSlider = new JSlider(0, 100,
                (int) (gameFrame.getGameEngine().getGameState() != null ? 50 : 50));
        gbc.gridx = 1;
        gbc.gridy = 0;
        settingsDialog.add(musicVolumeSlider, gbc);

        // 효과음 볼륨
        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsDialog.add(new JLabel("효과음 볼륨:"), gbc);

        JSlider soundVolumeSlider = new JSlider(0, 100, 70);
        gbc.gridx = 1;
        gbc.gridy = 1;
        settingsDialog.add(soundVolumeSlider, gbc);

        // 확인 버튼
        JButton okButton = new JButton("확인");
        okButton.addActionListener(e -> {
            // 설정 적용 로직
            settingsDialog.dispose();
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        settingsDialog.add(okButton, gbc);

        settingsDialog.setVisible(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 배경 이미지 그리기
        BufferedImage backgroundImage = ImageLoader.getInstance().getImage("background_menu");
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

        // 장식 요소들
        drawDecorations(g2d);
    }

    /**
     * 배경 장식을 그립니다
     */
    private void drawDecorations(Graphics2D g2d) {
        // 음표 모양 장식
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 5; i++) {
            int x = 100 + i * 200;
            int y = 100 + (i % 2) * 300;
            g2d.fillOval(x, y, 20, 20);
            g2d.fillRect(x + 15, y, 3, 40);
        }

        // 레인 미리보기
        g2d.setColor(new Color(255, 255, 255, 20));
        int previewX = Constants.WINDOW_WIDTH - 250;
        int previewY = 200;
        int previewWidth = 200;
        int previewHeight = 400;

        for (int i = 0; i < 4; i++) {
            int laneX = previewX + (i * previewWidth / 4);
            g2d.drawLine(laneX, previewY, laneX, previewY + previewHeight);
        }

        // 미니 노트들
        g2d.setColor(Constants.NOTE_COLOR);
        for (int i = 0; i < 4; i++) {
            int laneX = previewX + (i * previewWidth / 4) + 10;
            int noteY = previewY + 50 + (i * 80);
            g2d.fillRoundRect(laneX, noteY, 30, 10, 5, 5);
        }
    }
}