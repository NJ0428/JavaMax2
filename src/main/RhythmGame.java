package main;

import main.ui.GameFrame;
import main.audio.AudioManager;
import main.game.Song;
import main.utils.Constants;
import main.utils.MusicFileScanner;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 리듬 게임의 메인 클래스
 * 게임을 시작하고 전체적인 흐름을 관리합니다.
 */
public class RhythmGame {
    private static RhythmGame instance;
    private GameFrame gameFrame;
    private AudioManager audioManager;
    private List<Song> songs;

    public RhythmGame() {
        instance = this;
        initializeGame();
    }

    private void initializeGame() {
        // Look and Feel 설정
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 오디오 매니저 초기화
        audioManager = new AudioManager();

        // 곡 목록 초기화
        loadSongs();

        // 게임 프레임 생성
        gameFrame = new GameFrame();
        gameFrame.setVisible(true);
    }

    /**
     * 곡 목록을 로드합니다
     */
    private void loadSongs() {
        songs = new ArrayList<>();

        // MusicFileScanner를 사용하여 곡 로드
        try {
            List<Song> scannedSongs = MusicFileScanner.scanGameplayFolder();
            if (scannedSongs != null && !scannedSongs.isEmpty()) {
                songs.addAll(scannedSongs);
                System.out.println("RhythmGame: " + songs.size() + "개의 곡이 로드되었습니다.");
            } else {
                // 기본 곡 추가
                songs.add(new Song("기본 곡", "알 수 없음", "기본", "기본", 120, "game_bgm.wav"));
                System.out.println("RhythmGame: 기본 곡으로 초기화되었습니다.");
            }
        } catch (Exception e) {
            System.err.println("곡 로드 중 오류 발생: " + e.getMessage());
            // 기본 곡 추가
            songs.add(new Song("기본 곡", "알 수 없음", "기본", "기본", 120, "game_bgm.wav"));
        }
    }

    public static RhythmGame getInstance() {
        return instance;
    }

    public AudioManager getAudioManager() {
        return audioManager;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new RhythmGame();
        });
    }
}