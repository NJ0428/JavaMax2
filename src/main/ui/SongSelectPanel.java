package main.ui;

import main.game.Song;
import main.utils.Constants;
import main.utils.ImageLoader;
import main.utils.MusicFileScanner;
import main.audio.AudioManager;
import main.audio.PreviewPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 노래 선택 화면을 담당하는 패널
 */
public class SongSelectPanel extends JPanel {
    private GameFrame gameFrame;
    private List<Song> songs;
    private int currentSongIndex;
    private int selectedDifficulty;
    private JButton[] difficultyButtons;
    private JLabel thumbnailLabel;
    private JLabel songInfoLabel;
    private JLabel songDetailLabel;
    private JLabel difficultyInfoLabel;

    // 미리듣기 관련 컴포넌트
    private AudioManager audioManager;
    private JButton previewPlayButton;
    private JButton previewStopButton;
    private JLabel previewStatusLabel;
    private JProgressBar previewProgressBar;
    private javax.swing.Timer previewTimer;
    private long previewStartTime;
    private boolean isPreviewPlaying;

    public SongSelectPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        this.currentSongIndex = 0;
        this.selectedDifficulty = 1; // 기본: 중
        this.isPreviewPlaying = false;
        setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        setBackground(Constants.BACKGROUND_COLOR);
        setLayout(null);

        // AudioManager 인스턴스 가져오기 (GameFrame에서)
        this.audioManager = gameFrame.getAudioManager();
        initializePreviewPlayer();

        initializeSongs();
        initializeComponents();
        updateSongDisplay();
    }

    /**
     * 노래 목록을 초기화합니다
     */
    private void initializeSongs() {
        songs = new ArrayList<>();

        System.out.println("=== 음악 파일 로딩 시작 ===");

        // 1. gameplay 폴더에서 실제 MP3 파일들을 스캔
        List<Song> gameplayFonts = MusicFileScanner.scanGameplayFolder();
        songs.addAll(gameplayFonts);

        // 2. 기본 곡들도 추가 (실제 파일이 없어도 데모용으로)
        if (songs.isEmpty()) {
            System.out.println("실제 음악 파일이 없어서 기본 곡들을 추가합니다.");
            addDefaultSongs();
        } else {
            System.out.println("실제 음악 파일 " + gameplayFonts.size() + "개가 로드되었습니다.");
            // 실제 파일이 있어도 몇 개 기본 곡 추가
            addDefaultSongs();
        }

        System.out.println("총 " + songs.size() + "개의 곡이 사용 가능합니다.");
        System.out.println("=== 음악 파일 로딩 완료 ===");
    }

    /**
     * 기본 데모 곡들을 추가합니다
     */
    private void addDefaultSongs() {
        songs.add(new Song("Energetic Beat", "Rhythm Master", "Best Hits Vol.1", "Electronic", 128,
                "energetic_beat.wav"));
        songs.add(new Song("Dream Melody", "Sound Wave", "Peaceful Journey", "Ambient", 95, "dream_melody.wav"));
        songs.add(new Song("Power Rush", "Beat Fighter", "Action Pack", "Rock", 140, "power_rush.wav"));
    }

    /**
     * 컴포넌트들을 초기화합니다
     */
    private void initializeComponents() {
        // 제목
        JLabel titleLabel = new JLabel("노래 선택 화면", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 30, 300, 40);
        add(titleLabel);

        // 썸네일 영역
        createThumbnailArea();

        // 곡 정보 영역
        createSongInfoArea();

        // 난이도 선택 버튼들
        createDifficultyButtons();

        // 곡 설명 영역
        createSongDetailArea();

        // 네비게이션 버튼들
        createNavigationButtons();

        // 미리듣기 컨트롤 패널
        createPreviewControls();

        // 하단 버튼들
        createBottomButtons();
    }

    /**
     * 썸네일 영역을 생성합니다
     */
    private void createThumbnailArea() {
        // 썸네일 배경
        JPanel thumbnailPanel = new JPanel();
        thumbnailPanel.setBounds(50, 100, 250, 250);
        thumbnailPanel.setBackground(new Color(60, 60, 90));
        thumbnailPanel.setBorder(BorderFactory.createLineBorder(new Color(120, 120, 150), 2));
        thumbnailPanel.setLayout(new BorderLayout());

        // 썸네일 라벨
        thumbnailLabel = new JLabel("썸네일", SwingConstants.CENTER);
        thumbnailLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        thumbnailLabel.setForeground(Color.WHITE);
        thumbnailPanel.add(thumbnailLabel, BorderLayout.CENTER);

        add(thumbnailPanel);
    }

    /**
     * 곡 정보 영역을 생성합니다
     */
    private void createSongInfoArea() {
        // 곡 정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(320, 100, 400, 120);
        infoPanel.setBackground(new Color(0, 0, 0, 150));
        infoPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 150), 2));
        infoPanel.setLayout(new BorderLayout());

        songInfoLabel = new JLabel("<html><center>곡 정보</center></html>");
        songInfoLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        songInfoLabel.setForeground(Color.WHITE);
        songInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoPanel.add(songInfoLabel, BorderLayout.CENTER);

        add(infoPanel);
    }

    /**
     * 난이도 선택 버튼들을 생성합니다
     */
    private void createDifficultyButtons() {
        String[] difficultyNames = { "하", "중", "상", "최상" };
        Color[] difficultyColors = {
                new Color(100, 200, 100), // 하 - 초록
                new Color(100, 150, 200), // 중 - 파랑
                new Color(200, 150, 100), // 상 - 주황
                new Color(200, 100, 100) // 최상 - 빨강
        };

        difficultyButtons = new JButton[4];

        for (int i = 0; i < 4; i++) {
            final int index = i;
            JButton button = new JButton(difficultyNames[i]);
            button.setBounds(50 + (i * 70), 370, 60, 40);
            button.setFont(new Font("맑은 고딕", Font.BOLD, 16));
            button.setBackground(difficultyColors[i]);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);

            button.addActionListener(e -> selectDifficulty(index));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (selectedDifficulty != index) {
                        button.setBackground(difficultyColors[index].brighter());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    updateDifficultyButtonAppearance();
                }
            });

            difficultyButtons[i] = button;
            add(button);
        }
    }

    /**
     * 곡 상세 설명 영역을 생성합니다
     */
    private void createSongDetailArea() {
        // 곡 설명 패널
        JPanel detailPanel = new JPanel();
        detailPanel.setBounds(750, 100, 400, 300);
        detailPanel.setBackground(new Color(0, 0, 0, 150));
        detailPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        detailPanel.setLayout(new BorderLayout());

        // 제목
        JLabel detailTitle = new JLabel("곡 설명", SwingConstants.CENTER);
        detailTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        detailTitle.setForeground(Color.WHITE);
        detailPanel.add(detailTitle, BorderLayout.NORTH);

        // 상세 정보
        songDetailLabel = new JLabel("<html><center>곡을 선택하세요</center></html>");
        songDetailLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        songDetailLabel.setForeground(Color.LIGHT_GRAY);
        songDetailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(songDetailLabel, BorderLayout.CENTER);

        // 난이도 정보
        difficultyInfoLabel = new JLabel("<html><center>난이도 정보</center></html>");
        difficultyInfoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        difficultyInfoLabel.setForeground(Color.CYAN);
        difficultyInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        detailPanel.add(difficultyInfoLabel, BorderLayout.SOUTH);

        add(detailPanel);
    }

    /**
     * 미리듣기 플레이어를 초기화합니다
     */
    private void initializePreviewPlayer() {
        if (audioManager != null) {
            audioManager.setPreviewListener(new PreviewPlayer.PreviewListener() {
                @Override
                public void onPreviewStarted() {
                    SwingUtilities.invokeLater(() -> {
                        isPreviewPlaying = true;
                        previewStartTime = System.currentTimeMillis();
                        updatePreviewControls();
                        startPreviewTimer();
                        previewStatusLabel.setText("미리듣기 재생 중...");
                    });
                }

                @Override
                public void onPreviewPaused() {
                    SwingUtilities.invokeLater(() -> {
                        updatePreviewControls();
                        previewStatusLabel.setText("미리듣기 일시정지");
                    });
                }

                @Override
                public void onPreviewResumed() {
                    SwingUtilities.invokeLater(() -> {
                        updatePreviewControls();
                        previewStatusLabel.setText("미리듣기 재생 중...");
                    });
                }

                @Override
                public void onPreviewStopped() {
                    SwingUtilities.invokeLater(() -> {
                        isPreviewPlaying = false;
                        updatePreviewControls();
                        stopPreviewTimer();
                        previewStatusLabel.setText("미리듣기 중지됨");
                        previewProgressBar.setValue(0);
                    });
                }

                @Override
                public void onPreviewCompleted() {
                    SwingUtilities.invokeLater(() -> {
                        isPreviewPlaying = false;
                        updatePreviewControls();
                        stopPreviewTimer();
                        previewStatusLabel.setText("미리듣기 완료");
                        previewProgressBar.setValue(100);
                    });
                }

                @Override
                public void onPreviewError(String error) {
                    SwingUtilities.invokeLater(() -> {
                        isPreviewPlaying = false;
                        updatePreviewControls();
                        stopPreviewTimer();
                        previewStatusLabel.setText("오류: " + error);
                        previewProgressBar.setValue(0);
                    });
                }
            });
        }
    }

    /**
     * 미리듣기 컨트롤 패널을 생성합니다 (UI 없이 자동 재생만)
     */
    private void createPreviewControls() {
        // UI 컴포넌트들을 생성하지만 화면에 추가하지 않음
        // 내부 로직에서만 사용하기 위해 기본 객체들만 생성
        previewPlayButton = new JButton("▶");
        previewStopButton = new JButton("■");
        previewProgressBar = new JProgressBar(0, 100);
        previewStatusLabel = new JLabel("준비됨");

        // 미리듣기는 자동으로만 실행되므로 UI는 표시하지 않음
    }

    /**
     * 네비게이션 버튼들을 생성합니다
     */
    private void createNavigationButtons() {
        // 이전 곡 버튼
        JButton prevButton = new JButton("◀");
        prevButton.setBounds(320, 250, 50, 30);
        prevButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        prevButton.setBackground(new Color(100, 100, 150));
        prevButton.setForeground(Color.WHITE);
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(e -> previousSong());
        add(prevButton);

        // 다음 곡 버튼
        JButton nextButton = new JButton("▶");
        nextButton.setBounds(670, 250, 50, 30);
        nextButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nextButton.setBackground(new Color(100, 100, 150));
        nextButton.setForeground(Color.WHITE);
        nextButton.setFocusPainted(false);
        nextButton.addActionListener(e -> nextSong());
        add(nextButton);

        // 상위 버튼 (노래 하위 버튼과 같은 역할)
        JButton upButton = new JButton("노래 상위");
        upButton.setBounds(850, 50, 120, 30);
        upButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        upButton.setBackground(new Color(100, 100, 150));
        upButton.setForeground(Color.WHITE);
        upButton.setFocusPainted(false);
        add(upButton);
    }

    /**
     * 하단 버튼들을 생성합니다
     */
    private void createBottomButtons() {
        // 뒤로가기 버튼
        JButton backButton = new JButton("뒤로가기");
        backButton.setBounds(50, Constants.WINDOW_HEIGHT - 80, 120, 40);
        backButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        backButton.setBackground(new Color(100, 100, 150));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> gameFrame.showGameSelectScreen());
        add(backButton);

        // 게임 시작 버튼
        JButton startButton = new JButton("게임 시작");
        startButton.setBounds(Constants.WINDOW_WIDTH - 170, Constants.WINDOW_HEIGHT - 80, 120, 40);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        startButton.setBackground(new Color(200, 100, 100));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.addActionListener(e -> startSelectedSong());
        add(startButton);

        // 노래 하위 버튼
        JButton downButton = new JButton("노래 하위 버튼");
        downButton.setBounds(Constants.WINDOW_WIDTH / 2 - 100, Constants.WINDOW_HEIGHT - 80, 200, 40);
        downButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        downButton.setBackground(new Color(100, 100, 150));
        downButton.setForeground(Color.WHITE);
        downButton.setFocusPainted(false);
        add(downButton);
    }

    /**
     * 이전 곡으로 이동
     */
    private void previousSong() {
        // 미리듣기 중지
        stopPreview();

        currentSongIndex = (currentSongIndex - 1 + songs.size()) % songs.size();
        updateSongDisplay();

        // 새 곡 자동 미리듣기 시작
        startAutoPreview();
    }

    /**
     * 다음 곡으로 이동
     */
    private void nextSong() {
        // 미리듣기 중지
        stopPreview();

        currentSongIndex = (currentSongIndex + 1) % songs.size();
        updateSongDisplay();

        // 새 곡 자동 미리듣기 시작
        startAutoPreview();
    }

    /**
     * 난이도를 선택합니다
     */
    private void selectDifficulty(int difficulty) {
        this.selectedDifficulty = difficulty;
        updateDifficultyButtonAppearance();
        updateDifficultyInfo();
    }

    /**
     * 난이도 버튼 외관을 업데이트합니다
     */
    private void updateDifficultyButtonAppearance() {
        Color[] difficultyColors = {
                new Color(100, 200, 100), // 하 - 초록
                new Color(100, 150, 200), // 중 - 파랑
                new Color(200, 150, 100), // 상 - 주황
                new Color(200, 100, 100) // 최상 - 빨강
        };

        for (int i = 0; i < difficultyButtons.length; i++) {
            if (i == selectedDifficulty) {
                difficultyButtons[i].setBackground(difficultyColors[i].brighter());
                difficultyButtons[i].setBorder(BorderFactory.createLineBorder(Color.YELLOW, 3));
            } else {
                difficultyButtons[i].setBackground(difficultyColors[i]);
                difficultyButtons[i].setBorder(null);
            }
        }
    }

    /**
     * 곡 표시를 업데이트합니다
     */
    private void updateSongDisplay() {
        if (songs.isEmpty())
            return;

        Song currentSong = songs.get(currentSongIndex);

        // 썸네일 업데이트 (이미지가 있다면 로드, 없으면 기본 텍스트)
        BufferedImage thumbnail = ImageLoader.getInstance().getImage("song_" + currentSongIndex);
        if (thumbnail != null) {
            ImageIcon icon = new ImageIcon(thumbnail.getScaledInstance(230, 230, Image.SCALE_SMOOTH));
            thumbnailLabel.setIcon(icon);
            thumbnailLabel.setText("");
        } else {
            thumbnailLabel.setIcon(null);
            thumbnailLabel.setText("<html><center>" + currentSong.getTitle() + "<br>썸네일</center></html>");
        }

        // 곡 정보 업데이트
        songInfoLabel.setText("<html><center><b>" + currentSong.getTitle() + "</b><br>" +
                currentSong.getArtist() + "<br><br>" +
                "앨범: " + currentSong.getAlbum() + "<br>" +
                "BPM: " + currentSong.getBpm() + "</center></html>");

        // 곡 설명 업데이트
        songDetailLabel.setText("<html><center><b>장르:</b> " + currentSong.getGenre() + "<br><br>" +
                "<b>설명:</b><br>이 곡은 " + currentSong.getGenre() + " 장르의 음악으로<br>" +
                "BPM " + currentSong.getBpm() + "의 리듬감 있는 곡입니다.<br><br>" +
                "다양한 난이도로 즐길 수 있으며,<br>" +
                "초보자부터 고수까지 모두 즐길 수 있습니다.</center></html>");

        // 난이도 정보 업데이트
        updateDifficultyInfo();

        // 미리듣기 자동 시작 (항상)
        SwingUtilities.invokeLater(() -> startAutoPreview());
    }

    /**
     * 난이도 정보를 업데이트합니다
     */
    private void updateDifficultyInfo() {
        if (songs.isEmpty())
            return;

        Song currentSong = songs.get(currentSongIndex);
        Song.Difficulty difficulty = currentSong.getDifficulty(selectedDifficulty);

        if (difficulty != null) {
            difficultyInfoLabel.setText("<html><center><b>선택된 난이도:</b><br>" +
                    difficulty.getDifficultyInfo() + "<br><br>" +
                    "<b>베스트 스코어:</b> " + difficulty.getBestScore() + "<br>" +
                    "<b>베스트 정확도:</b> " + String.format("%.2f%%", difficulty.getBestAccuracy()) +
                    "</center></html>");
        }
    }

    /**
     * 선택된 곡으로 게임을 시작합니다
     */
    private void startSelectedSong() {
        if (!songs.isEmpty()) {
            // 게임 시작 전 미리듣기 중지
            stopPreview();

            Song selectedSong = songs.get(currentSongIndex);
            Song.Difficulty selectedDiff = selectedSong.getDifficulty(selectedDifficulty);

            // 선택된 곡과 난이도 정보를 GameFrame에 전달
            gameFrame.startSongGame(selectedSong, selectedDiff);
        }
    }

    // =============== 미리듣기 관련 메서드들 ===============

    /**
     * 자동 미리듣기를 시작합니다
     */
    private void startAutoPreview() {
        if (songs.isEmpty())
            return;

        Song currentSong = songs.get(currentSongIndex);
        String audioPath = currentSong.getAudioPath();

        // 파일 존재 여부 확인
        File audioFile = new File(audioPath);
        if (audioFile.exists()) {
            if (audioManager != null) {
                audioManager.startPreview(audioPath);
            }
        } else {
            previewStatusLabel.setText("오디오 파일 없음: " + currentSong.getTitle());
            System.out.println("미리듣기 파일 없음: " + audioPath);
        }
    }

    /**
     * 미리듣기 재생/일시정지를 토글합니다
     */
    private void togglePreview() {
        if (audioManager == null)
            return;

        if (audioManager.isPreviewPlaying()) {
            if (audioManager.isPreviewPaused()) {
                audioManager.resumePreview();
            } else {
                audioManager.pausePreview();
            }
        } else {
            startAutoPreview();
        }
    }

    /**
     * 미리듣기를 중지합니다
     */
    private void stopPreview() {
        if (audioManager != null) {
            audioManager.stopPreview();
        }
    }

    /**
     * 미리듣기 컨트롤 버튼들의 상태를 업데이트합니다 (UI 없음)
     */
    private void updatePreviewControls() {
        // UI가 없으므로 버튼 상태 업데이트는 생략
        // 내부 상태만 유지
    }

    /**
     * 미리듣기 진행 상태를 업데이트하는 타이머를 시작합니다 (UI 없음)
     */
    private void startPreviewTimer() {
        if (previewTimer != null) {
            previewTimer.stop();
        }

        previewTimer = new javax.swing.Timer(100, e -> {
            if (isPreviewPlaying && previewStartTime > 0) {
                long elapsed = System.currentTimeMillis() - previewStartTime;
                int seconds = (int) (elapsed / 1000);

                // UI 업데이트는 생략하고 30초 체크만 수행
                if (seconds >= 30) {
                    stopPreviewTimer();
                }
            }
        });
        previewTimer.start();
    }

    /**
     * 미리듣기 진행 상태 타이머를 중지합니다
     */
    private void stopPreviewTimer() {
        if (previewTimer != null) {
            previewTimer.stop();
            previewTimer = null;
        }
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
                0, getHeight(), new Color(30, 30, 50));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    /**
     * 장식 요소들을 그립니다
     */
    private void drawDecorations(Graphics2D g2d) {
        // 음표 장식
        g2d.setColor(new Color(255, 255, 255, 30));
        for (int i = 0; i < 8; i++) {
            int x = 100 + (i * 150);
            int y = 50 + (i % 3) * 200;
            g2d.fillOval(x, y, 15, 15);
            g2d.fillRect(x + 12, y, 2, 30);
        }

        // 현재 곡 인덱스 표시
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String indexText = String.format("%d / %d", currentSongIndex + 1, songs.size());
        g2d.drawString(indexText, 50, Constants.WINDOW_HEIGHT - 120);
    }

    /**
     * 패널이 닫힐 때 리소스를 정리합니다
     */
    public void cleanup() {
        stopPreview();
        stopPreviewTimer();
    }
}