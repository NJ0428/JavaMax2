package main.audio;

import main.utils.Constants;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 게임의 오디오를 관리하는 클래스
 * 배경음악, 효과음 재생을 담당합니다
 */
public class AudioManager {
    private Map<String, Clip> soundEffects;
    private Clip backgroundMusic;
    private boolean soundEnabled;
    private boolean musicEnabled;
    private float soundVolume;
    private float musicVolume;
    private Mp3Player mp3Player; // MP3 재생을 위한 플레이어
    private PreviewPlayer previewPlayer; // 미리듣기 플레이어

    public AudioManager() {
        soundEffects = new HashMap<>();
        soundEnabled = true;
        musicEnabled = true;
        soundVolume = 1.0f; // 최대 볼륨으로 설정
        musicVolume = 0.7f;
        mp3Player = new Mp3Player();
        previewPlayer = new PreviewPlayer();

        System.out.println("=== AudioManager 초기화 시작 ===");
        loadSoundEffects();

        // 테스트 사운드 재생
        System.out.println("사운드 시스템 테스트...");
        testSoundSystem();
        System.out.println("=== AudioManager 초기화 완료 ===");
    }

    /**
     * 사운드 시스템이 정상 작동하는지 테스트합니다
     */
    private void testSoundSystem() {
        System.out.println("로드된 사운드 효과 수: " + soundEffects.size());
        for (String name : soundEffects.keySet()) {
            System.out.println("- " + name);
        }

        // 간단한 테스트 사운드 재생
        try {
            Thread.sleep(500); // 초기화 대기
            System.out.println("테스트 사운드 재생 시도...");
            playSoundEffect("click");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 효과음을 미리 로드합니다
     */
    private void loadSoundEffects() {
        // 기본 효과음 파일들 (WAV 파일)
        loadSoundEffect("hit", Constants.EFFECT_PATH + "hit.wav");
        loadSoundEffect("miss", Constants.EFFECT_PATH + "miss.wav");
        loadSoundEffect("perfect", Constants.EFFECT_PATH + "perfect.wav");
        loadSoundEffect("click", Constants.EFFECT_PATH + "click.wav");

        // MP3 파일들을 Mp3Player에 등록
        registerMp3Sounds();

        // UI 관련 효과음
        loadSoundEffect("pause", Constants.EFFECT_PATH + "pause.wav");
        loadSoundEffect("resume", Constants.EFFECT_PATH + "resume.wav");
        loadSoundEffect("menu_select", Constants.EFFECT_PATH + "menu_select.wav");
        loadSoundEffect("menu_back", Constants.EFFECT_PATH + "menu_back.wav");
        loadSoundEffect("button_hover", Constants.EFFECT_PATH + "button_hover.wav");
        loadSoundEffect("confirm", Constants.EFFECT_PATH + "confirm.wav");
        loadSoundEffect("cancel", Constants.EFFECT_PATH + "cancel.wav");
    }

    /**
     * MP3 파일들을 Mp3Player에 등록합니다
     */
    private void registerMp3Sounds() {
        // 새로운 이펙트 사운드들 (MP3 파일)
        registerMp3Sound("Click", Constants.EFFECT_PATH + "Click.mp3");
        registerMp3Sound("FarmUp", Constants.EFFECT_PATH + "FarmUp.mp3");
        registerMp3Sound("Over", Constants.EFFECT_PATH + "Over.mp3");
        registerMp3Sound("success", Constants.EFFECT_PATH + "success.mp3");
        registerMp3Sound("Touch", Constants.EFFECT_PATH + "Touch.mp3");
    }

    /**
     * MP3 파일을 등록합니다
     */
    private void registerMp3Sound(String name, String filePath) {
        File soundFile = new File(filePath);
        if (soundFile.exists()) {
            mp3Player.registerSound(name, filePath);
            System.out.println("MP3 사운드 등록 완료: " + name);
        } else {
            System.err.println("MP3 파일을 찾을 수 없음: " + filePath);
            // 대체 사운드 생성
            Clip clip = generateSoundEffect(name);
            if (clip != null) {
                setClipVolume(clip, soundVolume);
                soundEffects.put(name, clip);
                System.out.println("대체 사운드 생성: " + name);
            }
        }
    }

    /**
     * 효과음 파일을 로드합니다
     */
    private void loadSoundEffect(String name, String filePath) {
        try {
            File soundFile = new File(filePath);
            Clip clip = null;

            if (soundFile.exists()) {
                // MP3 파일인 경우 특별 처리
                if (filePath.toLowerCase().endsWith(".mp3")) {
                    System.out.println("MP3 파일 로드 시도: " + filePath);
                    // MP3 파일은 현재 Java AudioSystem에서 직접 지원하지 않음
                    // 대신 프로그래밍적으로 생성된 사운드를 사용
                    clip = generateSoundEffect(name);
                } else {
                    // WAV 및 기타 지원 파일
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                    clip = AudioSystem.getClip();
                    clip.open(audioStream);
                }
            } else {
                // 파일이 없는 경우 프로그래밍적으로 생성
                clip = generateSoundEffect(name);
            }

            if (clip != null) {
                // 볼륨 조절
                setClipVolume(clip, soundVolume);
                soundEffects.put(name, clip);
                System.out.println("사운드 로드 성공: " + name);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("파일 로드 실패, 대체 사운드 생성: " + name);
            // 파일 로드 실패 시 프로그래밍적으로 생성
            Clip clip = generateSoundEffect(name);
            if (clip != null) {
                setClipVolume(clip, soundVolume);
                soundEffects.put(name, clip);
            }
        }
    }

    /**
     * 사운드 이름에 따라 프로그래밍적으로 효과음을 생성합니다
     */
    private Clip generateSoundEffect(String name) {
        switch (name) {
            case "click":
                return SoundGenerator.generateTone(800, 100, 0.7);
            case "button_hover":
                return SoundGenerator.generateTone(900, 80, 0.8); // Click 소리와 동일
            case "pause":
                return SoundGenerator.generateTone(300, 200, 0.8);
            case "resume":
                return SoundGenerator.generateTone(500, 150, 0.8);
            case "confirm":
                return SoundGenerator.generateConfirmSound();
            case "cancel":
                return SoundGenerator.generateCancelSound();
            case "menu_select":
                return SoundGenerator.generateTone(700, 120, 0.6);
            case "menu_back":
                return SoundGenerator.generateTone(450, 150, 0.6);
            case "hit":
                return SoundGenerator.generateTone(440, 100, 0.7);
            case "miss":
                return SoundGenerator.generateTone(200, 200, 0.7);
            case "perfect":
                return SoundGenerator.generateConfirmSound();

            // 새로운 효과음들
            case "Click":
                return SoundGenerator.generateTone(900, 80, 0.8); // 높은 톤의 클릭음
            case "FarmUp":
                return SoundGenerator.generateConfirmSound(); // 팜업 = 성공음
            case "Over":
                return SoundGenerator.generateCancelSound(); // 게임 오버 = 실패음
            case "success":
                return SoundGenerator.generateSuccessSound(); // 성공음
            case "Touch":
                return SoundGenerator.generateTone(750, 120, 0.6); // 터치음

            default:
                return SoundGenerator.generateTone(800, 100, 0.7);
        }
    }

    /**
     * 배경음악을 로드하고 재생합니다
     */
    public void loadAndPlayBackgroundMusic(String filePath) {
        try {
            stopBackgroundMusic(); // 기존 음악 정지

            // 여러 경로에서 파일 찾기
            File musicFile = findMusicFile(filePath);
            if (musicFile != null && musicFile.exists()) {
                System.out.println("배경음악 로드: " + musicFile.getAbsolutePath());

                if (filePath.toLowerCase().endsWith(".mp3")) {
                    // MP3 파일 처리 - Mp3Player를 사용하여 재생
                    System.out.println("MP3 파일 감지됨. Mp3Player로 재생합니다.");
                    try {
                        mp3Player.playGameMusic(musicFile.getAbsolutePath());
                        System.out.println("MP3 배경음악 재생 시작: " + musicFile.getAbsolutePath());
                        return;
                    } catch (Exception e) {
                        System.err.println("MP3 재생 실패: " + e.getMessage());
                        System.out.println("기본 배경음악으로 대체 재생합니다.");
                        // MP3 재생 실패 시 기본 배경음악 재생
                        File fallbackFile = findMusicFile("game_bgm.wav");
                        if (fallbackFile != null && fallbackFile.exists()) {
                            musicFile = fallbackFile;
                        } else {
                            System.err.println("기본 배경음악 파일도 찾을 수 없습니다.");
                            return;
                        }
                    }
                }

                // WAV, AU 등 기본 지원 파일은 기존 방식으로 재생
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);

                // 볼륨 조절
                setClipVolume(backgroundMusic, musicVolume);

                if (musicEnabled) {
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    System.out.println("배경음악 재생 시작: " + musicFile.getName());
                }
            } else {
                System.err.println("음악 파일을 찾을 수 없습니다: " + filePath);
                System.out.println("기본 배경음악 재생을 시도합니다.");
                // 기본 배경음악 재생 시도
                File fallbackFile = findMusicFile("game_bgm.wav");
                if (fallbackFile != null && fallbackFile.exists()) {
                    loadAndPlayBackgroundMusic("game_bgm.wav");
                }
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("지원되지 않는 오디오 형식: " + filePath + " - " + e.getMessage());
        } catch (IOException | LineUnavailableException e) {
            System.err.println("배경음악 로드 실패: " + filePath + " - " + e.getMessage());
        }
    }

    /**
     * 여러 경로에서 음악 파일을 찾습니다
     */
    private File findMusicFile(String fileName) {
        // 여러 위치에서 파일 찾기
        String[] searchPaths = {
                Constants.GAMEPLAY_PATH + fileName,
                Constants.MUSIC_PATH + fileName,
                Constants.BGM_PATH + fileName,
                "resources/audio/" + fileName,
                fileName // 절대 경로인 경우
        };

        for (String path : searchPaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("음악 파일 찾음: " + path);
                return file;
            }
        }

        System.err.println("다음 경로들에서 파일을 찾을 수 없습니다:");
        for (String path : searchPaths) {
            System.err.println("  - " + path);
        }

        return null;
    }

    /**
     * 효과음을 재생합니다
     */
    public void playSoundEffect(String name) {
        if (!soundEnabled)
            return;

        Clip clip = soundEffects.get(name);
        if (clip != null) {
            try {
                // 이미 재생 중이면 정지
                if (clip.isRunning()) {
                    clip.stop();
                }
                // 클립을 처음부터 재생
                clip.setFramePosition(0);
                clip.start();

                // 디버그 메시지
                System.out.println("사운드 재생: " + name);
            } catch (Exception e) {
                System.err.println("사운드 재생 실패: " + name + " - " + e.getMessage());
            }
        } else {
            System.err.println("사운드 클립을 찾을 수 없음: " + name);
        }
    }

    /**
     * 판정에 따른 효과음을 재생합니다
     */
    public void playJudgmentSound(String judgment) {
        switch (judgment) {
            case "PERFECT":
                playSoundEffect("perfect");
                break;
            case "GOOD":
                playSoundEffect("hit");
                break;
            case "MISS":
                playSoundEffect("miss");
                break;
        }
    }

    /**
     * UI 관련 효과음을 재생합니다
     */
    public void playUISound(String soundType) {
        System.out.println("UI 사운드 재생 요청: " + soundType);

        // 먼저 기본 사운드 시스템 시도
        boolean soundPlayed = false;
        switch (soundType) {
            case "pause":
                soundPlayed = tryPlaySoundEffect("pause");
                break;
            case "resume":
                soundPlayed = tryPlaySoundEffect("resume");
                break;
            case "menu_select":
                soundPlayed = tryPlaySoundEffect("menu_select");
                break;
            case "menu_back":
                soundPlayed = tryPlaySoundEffect("menu_back");
                break;
            case "button_hover":
                // MP3 Click 사운드 우선 재생
                if (playMp3Sound("Click")) {
                    soundPlayed = true;
                } else {
                    soundPlayed = tryPlaySoundEffect("Click");
                    if (!soundPlayed) {
                        soundPlayed = tryPlaySoundEffect("click");
                    }
                }
                break;
            case "confirm":
                soundPlayed = tryPlaySoundEffect("confirm");
                break;
            case "cancel":
                soundPlayed = tryPlaySoundEffect("cancel");
                break;
            case "click":
                // MP3 Click 사운드 우선 재생
                if (playMp3Sound("Click")) {
                    soundPlayed = true;
                } else {
                    soundPlayed = tryPlaySoundEffect("Click");
                    if (!soundPlayed) {
                        soundPlayed = tryPlaySoundEffect("click");
                    }
                }
                break;
        }

        // 기본 사운드가 재생되지 않으면 대체 시스템 사용
        if (!soundPlayed) {
            System.out.println("기본 사운드 실패, 대체 시스템 사용");
            SimpleSoundPlayer.playSoundAsync(soundType);
        }
    }

    /**
     * 사운드 재생을 시도하고 성공 여부를 반환합니다
     */
    private boolean tryPlaySoundEffect(String name) {
        if (!soundEnabled)
            return false;

        Clip clip = soundEffects.get(name);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
                System.out.println("사운드 재생 성공: " + name);
                return true;
            } catch (Exception e) {
                System.err.println("사운드 재생 실패: " + name + " - " + e.getMessage());
                return false;
            }
        } else {
            System.err.println("사운드 클립을 찾을 수 없음: " + name);
            return false;
        }
    }

    /**
     * 게임 일시정지 시 배경음악도 일시정지합니다
     */
    public void pauseGame() {
        playFarmUpSound(); // FarmUp 소리로 변경
        pauseBackgroundMusic();
    }

    /**
     * 게임 재개 시 배경음악도 재개합니다
     */
    public void resumeGame() {
        playUISound("resume");
        resumeBackgroundMusic();
    }

    /**
     * 배경음악을 일시정지합니다
     */
    public void pauseBackgroundMusic() {
        // Clip 타입 배경음악 일시정지
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }

        // MP3 배경음악 일시정지
        if (mp3Player != null) {
            mp3Player.pauseBackgroundMusic();
        }

        System.out.println("배경음악 일시정지");
    }

    /**
     * 배경음악을 재개합니다
     */
    public void resumeBackgroundMusic() {
        // Clip 타입 배경음악 재개
        if (backgroundMusic != null && musicEnabled) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }

        // MP3 배경음악 재개
        if (mp3Player != null) {
            mp3Player.resumeBackgroundMusic();
        }

        System.out.println("배경음악 재개");
    }

    /**
     * 배경음악을 정지합니다
     */
    public void stopBackgroundMusic() {
        // Clip 타입 배경음악 정지
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }

        // MP3 배경음악 정지
        if (mp3Player != null) {
            mp3Player.stopGameMusic();
        }

        System.out.println("배경음악 정지");
    }

    /**
     * 클립의 볼륨을 설정합니다
     */
    private void setClipVolume(Clip clip, float volume) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();

                // 더 명확한 볼륨 계산: 0.0 = 최소, 1.0 = 최대
                float gain;
                if (volume <= 0.0f) {
                    gain = min;
                } else if (volume >= 1.0f) {
                    gain = max;
                } else {
                    // 로그 스케일 적용하여 더 자연스러운 볼륨 변화
                    gain = min + (max - min) * volume;
                }

                volumeControl.setValue(gain);
                System.out.println("볼륨 설정됨: " + gain + "dB (요청 볼륨: " + volume + ")");
            } else {
                System.out.println("볼륨 조절 지원되지 않음");
            }
        } catch (Exception e) {
            System.err.println("볼륨 설정 실패: " + e.getMessage());
        }
    }

    /**
     * 모든 오디오 리소스를 정리합니다
     */
    public void cleanup() {
        stopBackgroundMusic();

        for (Clip clip : soundEffects.values()) {
            if (clip != null) {
                clip.close();
            }
        }
        soundEffects.clear();

        // MP3Player 리소스 정리
        if (mp3Player != null) {
            mp3Player.cleanup();
        }

        // PreviewPlayer 리소스 정리
        if (previewPlayer != null) {
            previewPlayer.cleanup();
        }
    }

    // Getters and Setters
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (mp3Player != null) {
            mp3Player.setEnabled(enabled);
        }
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled) {
            pauseBackgroundMusic();
        } else {
            resumeBackgroundMusic();
        }
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0.0f, Math.min(1.0f, volume));
        // 모든 효과음 볼륨 업데이트
        for (Clip clip : soundEffects.values()) {
            setClipVolume(clip, this.soundVolume);
        }

        // MP3Player 볼륨 업데이트
        if (mp3Player != null) {
            mp3Player.setVolume(this.soundVolume);
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundMusic != null) {
            setClipVolume(backgroundMusic, this.musicVolume);
        }
    }

    /**
     * 모든 클릭음을 재생합니다
     */
    public void playClickSound() {
        System.out.println("클릭음 재생");
        if (playMp3Sound("Click")) {
            return; // MP3 재생 성공
        }

        // MP3 재생 실패시 대체 사운드 재생
        boolean soundPlayed = tryPlaySoundEffect("Click");
        if (!soundPlayed) {
            // 대체 사운드로 기본 클릭음 재생
            playSoundEffect("click");
        }
    }

    /**
     * 팜업 사운드를 재생합니다
     */
    public void playFarmUpSound() {
        System.out.println("팜업 사운드 재생");
        if (playMp3Sound("FarmUp")) {
            return; // MP3 재생 성공
        }

        // MP3 재생 실패시 대체 사운드 재생
        boolean soundPlayed = tryPlaySoundEffect("FarmUp");
        if (!soundPlayed) {
            // 대체 사운드로 성공음 재생
            playSoundEffect("confirm");
        }
    }

    /**
     * 게임 오버 사운드를 재생합니다
     */
    public void playGameOverSound() {
        System.out.println("게임 오버 사운드 재생");
        if (playMp3Sound("Over")) {
            return; // MP3 재생 성공
        }

        // MP3 재생 실패시 대체 사운드 재생
        boolean soundPlayed = tryPlaySoundEffect("Over");
        if (!soundPlayed) {
            // 대체 사운드로 미스음 재생
            playSoundEffect("miss");
        }
    }

    /**
     * 게임 성공 사운드를 재생합니다
     */
    public void playGameSuccessSound() {
        System.out.println("게임 성공 사운드 재생");
        if (playMp3Sound("success")) {
            return; // MP3 재생 성공
        }

        // MP3 재생 실패시 대체 사운드 재생
        boolean soundPlayed = tryPlaySoundEffect("success");
        if (!soundPlayed) {
            // 대체 사운드로 완벽 판정음 재생
            playSoundEffect("perfect");
        }
    }

    /**
     * 터치 입력 사운드를 재생합니다
     */
    public void playTouchSound() {
        System.out.println("터치 사운드 재생");
        if (playMp3Sound("Touch")) {
            return; // MP3 재생 성공
        }

        // MP3 재생 실패시 대체 사운드 재생
        boolean soundPlayed = tryPlaySoundEffect("Touch");
        if (!soundPlayed) {
            // 대체 사운드로 기본 클릭음 재생
            playSoundEffect("click");
        }
    }

    /**
     * MP3 사운드를 재생하고 성공 여부를 반환합니다
     */
    private boolean playMp3Sound(String name) {
        if (!soundEnabled || mp3Player == null) {
            return false;
        }

        try {
            mp3Player.playSoundAsync(name);
            return true;
        } catch (Exception e) {
            System.err.println("MP3 재생 실패: " + name + " - " + e.getMessage());
            return false;
        }
    }

    // =============== 미리듣기 기능 ===============

    /**
     * 노래 미리듣기를 시작합니다
     * 
     * @param filePath 미리듣기할 파일 경로
     */
    public void startPreview(String filePath) {
        if (previewPlayer != null) {
            previewPlayer.startPreview(filePath);
        }
    }

    /**
     * 미리듣기를 일시정지합니다
     */
    public void pausePreview() {
        if (previewPlayer != null) {
            previewPlayer.pausePreview();
        }
    }

    /**
     * 미리듣기를 재개합니다
     */
    public void resumePreview() {
        if (previewPlayer != null) {
            previewPlayer.resumePreview();
        }
    }

    /**
     * 미리듣기를 중지합니다
     */
    public void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stopPreview();
        }
    }

    /**
     * 미리듣기 상태를 확인합니다
     */
    public boolean isPreviewPlaying() {
        return previewPlayer != null && previewPlayer.isPlaying();
    }

    /**
     * 미리듣기 일시정지 상태를 확인합니다
     */
    public boolean isPreviewPaused() {
        return previewPlayer != null && previewPlayer.isPaused();
    }

    /**
     * 미리듣기 리스너를 설정합니다
     */
    public void setPreviewListener(PreviewPlayer.PreviewListener listener) {
        if (previewPlayer != null) {
            previewPlayer.setPreviewListener(listener);
        }
    }

    /**
     * 미리듣기 볼륨을 설정합니다
     */
    public void setPreviewVolume(float volume) {
        if (previewPlayer != null) {
            previewPlayer.setVolume(volume);
        }
    }

    /**
     * PreviewPlayer 인스턴스를 반환합니다
     */
    public PreviewPlayer getPreviewPlayer() {
        return previewPlayer;
    }
}