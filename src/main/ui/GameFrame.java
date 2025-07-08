package main.ui;

import main.RhythmGame;
import main.game.GameEngine;
import main.game.GameState;
import main.game.GameMode;
import main.game.Song;
import main.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 게임의 메인 프레임
 * 전체 UI를 관리하고 키 입력을 처리합니다
 */
public class GameFrame extends JFrame implements KeyListener {
    private GameEngine gameEngine;
    private GamePanel gamePanel;
    private PausePanel pausePanel;
    private MenuPanel menuPanel;
    private GameSelectPanel gameSelectPanel;
    private SongSelectPanel songSelectPanel;
    private ResultPanel resultPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Timer gameTimer;
    private String currentPanelName; // 현재 활성화된 패널 이름

    public GameFrame() {
        gameEngine = new GameEngine();
        currentPanelName = "MENU"; // 초기 패널은 메뉴
        initializeFrame();
        initializePanels();
        setupTimer();
        addKeyListener(this);
        setFocusable(true);
        requestFocus();

        // GameEngine에 AudioManager 설정
        gameEngine.setAudioManager(getAudioManager());
    }

    /**
     * 프레임을 초기화합니다
     */
    private void initializeFrame() {
        setTitle("리듬 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);

        // 창 닫기 이벤트 처리
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                RhythmGame.getInstance().getAudioManager().cleanup();
                System.exit(0);
            }
        });
    }

    /**
     * 패널들을 초기화합니다
     */
    private void initializePanels() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 각 패널 생성
        menuPanel = new MenuPanel(this);
        gameSelectPanel = new GameSelectPanel(this);
        songSelectPanel = new SongSelectPanel(this);
        gamePanel = new GamePanel(gameEngine);
        pausePanel = new PausePanel(this);
        resultPanel = new ResultPanel(this);

        // PausePanel을 GamePanel에 설정
        gamePanel.setPausePanel(pausePanel);

        // 패널 추가
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gameSelectPanel, "GAME_SELECT");
        mainPanel.add(songSelectPanel, "SONG_SELECT");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(resultPanel, "RESULT");

        add(mainPanel);

        // 메뉴 화면으로 시작
        showMenu();
    }

    /**
     * 게임 타이머를 설정합니다
     */
    private void setupTimer() {
        gameTimer = new Timer(Constants.FRAME_TIME, e -> {
            gameEngine.update();

            // 게임 상태에 따른 화면 전환
            switch (gameEngine.getGameState()) {
                case MENU:
                    if (!cardLayout.toString().contains("MENU")) {
                        showMenu();
                    }
                    break;
                case GAME_SELECT:
                    if (!cardLayout.toString().contains("GAME_SELECT")) {
                        showGameSelect();
                    }
                    break;
                case SONG_SELECT:
                    if (!cardLayout.toString().contains("SONG_SELECT")) {
                        showSongSelect();
                    }
                    break;
                case PLAYING:
                case PAUSED:
                    if (!cardLayout.toString().contains("GAME")) {
                        showGame();
                    }
                    break;
                case RESULT:
                    if (!cardLayout.toString().contains("RESULT")) {
                        showResult();
                    }
                    break;
            }

            // 현재 표시된 패널 업데이트
            Component currentPanel = getCurrentPanel();
            if (currentPanel != null) {
                currentPanel.repaint();
            }
        });
        gameTimer.start();
    }

    /**
     * 현재 표시된 패널을 반환합니다
     */
    private Component getCurrentPanel() {
        for (Component component : mainPanel.getComponents()) {
            if (component.isVisible()) {
                return component;
            }
        }
        return null;
    }

    /**
     * 메뉴 화면을 표시합니다
     */
    public void showMenu() {
        // SongSelectPanel에서 다른 화면으로 전환할 때 미리듣기 중지
        if (songSelectPanel != null && "SONG_SELECT".equals(currentPanelName)) {
            songSelectPanel.onPanelDeactivated();
        }

        currentPanelName = "MENU";
        cardLayout.show(mainPanel, "MENU");
        requestFocus();
    }

    /**
     * 게임 선택 화면을 표시합니다
     */
    public void showGameSelect() {
        // SongSelectPanel에서 다른 화면으로 전환할 때 미리듣기 중지
        if (songSelectPanel != null && "SONG_SELECT".equals(currentPanelName)) {
            songSelectPanel.onPanelDeactivated();
        }

        currentPanelName = "GAME_SELECT";
        cardLayout.show(mainPanel, "GAME_SELECT");
        requestFocus();
    }

    /**
     * 노래 선택 화면을 표시합니다
     */
    public void showSongSelect() {
        // 이미 SONG_SELECT 패널이 활성화되어 있으면 중복 호출 방지
        if ("SONG_SELECT".equals(currentPanelName)) {
            System.out.println("이미 노래 선택 화면이 활성화되어 있음 - 중복 호출 방지");
            return;
        }

        // 다른 패널에서 SongSelectPanel로 전환할 때만 비활성화 호출
        if (songSelectPanel != null && !"SONG_SELECT".equals(currentPanelName)) {
            songSelectPanel.onPanelDeactivated();
        }

        currentPanelName = "SONG_SELECT";
        cardLayout.show(mainPanel, "SONG_SELECT");

        // SongSelectPanel 활성화 및 미리듣기 시작
        if (songSelectPanel != null) {
            songSelectPanel.onPanelActivated();
        }

        requestFocus();
    }

    /**
     * 게임 화면을 표시합니다
     */
    public void showGame() {
        // SongSelectPanel에서 게임 화면으로 전환할 때 미리듣기 중지
        if (songSelectPanel != null) {
            songSelectPanel.onPanelDeactivated();
        }

        cardLayout.show(mainPanel, "GAME");
        requestFocus();
    }

    /**
     * 결과 화면을 표시합니다
     */
    public void showResult() {
        // SongSelectPanel에서 결과 화면으로 전환할 때 미리듣기 중지
        if (songSelectPanel != null) {
            songSelectPanel.onPanelDeactivated();
        }

        resultPanel.updateResult(gameEngine.getScoreManager());
        cardLayout.show(mainPanel, "RESULT");
        requestFocus();
    }

    /**
     * 게임 선택 화면을 표시합니다
     */
    public void showGameSelectScreen() {
        gameEngine.setGameState(GameState.GAME_SELECT);
        showGameSelect();
    }

    /**
     * 노래 선택 화면을 표시합니다
     */
    public void showSongSelectScreen() {
        gameEngine.setGameState(GameState.SONG_SELECT);
        showSongSelect();
    }

    /**
     * 게임을 시작합니다
     */
    public void startGame() {
        gameEngine.startGame();
        showGame();
    }

    /**
     * 특정 모드로 게임을 시작합니다
     */
    public void startGameWithMode(GameMode mode) {
        if (mode == GameMode.SINGLE_PLAY) {
            // 싱글 플레이는 노래 선택 화면으로 이동
            gameEngine.setGameState(GameState.SONG_SELECT);
            showSongSelect();
        } else {
            // 다른 모드는 바로 게임 시작
            gameEngine.startGameWithMode(mode);

            // 기본 배경음악 재생 (WAV 파일 사용)
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                System.out.println("게임 모드 시작 - 기본 배경음악 재생 시도");
                RhythmGame.getInstance().getAudioManager().loadAndPlayBackgroundMusic("game_bgm.wav");
            }

            showGame();
        }
    }

    /**
     * 선택된 곡으로 게임을 시작합니다
     */
    public void startSongGame(Song song, Song.Difficulty difficulty) {
        // 곡 정보를 GameEngine에 설정 (추후 구현)
        gameEngine.startGameWithMode(GameMode.SINGLE_PLAY);

        // 선택된 곡의 배경음악 재생 (현재는 기본 배경음악 사용)
        if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
            System.out.println("게임 시작 - 배경음악 재생: " + (song != null ? song.getTitle() : "기본"));
            RhythmGame.getInstance().getAudioManager().loadAndPlayBackgroundMusic("game_bgm.wav");
        }

        showGame();
    }

    /**
     * 메뉴로 돌아갑니다
     */
    public void returnToMenu() {
        gameEngine.returnToMenu();
        showMenu();
    }

    /**
     * 게임을 재개합니다
     */
    public void resumeGame() {
        // 재개 사운드는 PausePanel에서 이미 재생됨
        gameEngine.togglePause(); // 일시정지 해제
        requestFocus();
    }

    /**
     * 게임을 재시작합니다
     */
    public void restartGame() {
        // 현재 게임 모드로 다시 시작
        GameMode currentMode = gameEngine.getCurrentGameMode();
        if (currentMode != null) {
            gameEngine.startGameWithMode(currentMode);

            // 배경음악 재시작
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                System.out.println("게임 재시작 - 기본 배경음악 재생");
                RhythmGame.getInstance().getAudioManager().loadAndPlayBackgroundMusic("game_bgm.wav");
            }
        } else {
            gameEngine.startGame();
        }
        requestFocus();
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    /**
     * AudioManager를 반환합니다
     */
    public main.audio.AudioManager getAudioManager() {
        return RhythmGame.getInstance().getAudioManager();
    }

    // KeyListener 구현
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // 게임 플레이 중 키 입력 처리
        if (gameEngine.getGameState() == GameState.PLAYING) {
            // 레인 키 처리 (S, D, F, J, K, L)
            for (int i = 0; i < Constants.LANE_KEYS.length; i++) {
                if (keyCode == Constants.LANE_KEYS[i]) {
                    gameEngine.processKeyInput(i);
                    break;
                }
            }

            // ESC 키로 일시정지
            if (keyCode == KeyEvent.VK_ESCAPE) {
                // 일시정지 사운드와 배경음악 일시정지
                if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                    RhythmGame.getInstance().getAudioManager().pauseGame();
                }
                gameEngine.togglePause();
                requestFocus(); // 포커스 유지
            }
        }

        // 일시정지 상태에서 ESC 키로 재개
        else if (gameEngine.getGameState() == GameState.PAUSED && keyCode == KeyEvent.VK_ESCAPE) {
            // 재개 사운드와 배경음악 재개
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().resumeGame();
            }
            gameEngine.togglePause();
            requestFocus(); // 포커스 유지
        }

        // 결과 화면에서 ENTER 키로 메뉴 복귀
        else if (gameEngine.getGameState() == GameState.RESULT && keyCode == KeyEvent.VK_ENTER) {
            returnToMenu();
        }

        // 메뉴 화면에서 ESC 키로 게임 종료 확인
        else if (gameEngine.getGameState() == GameState.MENU && keyCode == KeyEvent.VK_ESCAPE) {
            confirmExit();
        }
    }

    /**
     * 게임 종료를 확인합니다
     */
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "정말 게임을 종료하시겠습니까?",
                "게임 종료",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            // 오디오 매니저 정리
            if (RhythmGame.getInstance() != null && RhythmGame.getInstance().getAudioManager() != null) {
                RhythmGame.getInstance().getAudioManager().cleanup();
            }
            System.exit(0);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();

        // 레인 키 릴리즈 처리
        for (int i = 0; i < Constants.LANE_KEYS.length; i++) {
            if (keyCode == Constants.LANE_KEYS[i]) {
                gameEngine.processKeyRelease(i);
                break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 사용하지 않음
    }
}