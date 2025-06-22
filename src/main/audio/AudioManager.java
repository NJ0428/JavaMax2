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

    public AudioManager() {
        soundEffects = new HashMap<>();
        soundEnabled = true;
        musicEnabled = true;
        soundVolume = 1.0f; // 최대 볼륨으로 설정
        musicVolume = 0.7f;

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
        // 기본 효과음 파일들 (실제 파일이 없어도 오류 방지)
        loadSoundEffect("hit", Constants.EFFECT_PATH + "hit.wav");
        loadSoundEffect("miss", Constants.EFFECT_PATH + "miss.wav");
        loadSoundEffect("perfect", Constants.EFFECT_PATH + "perfect.wav");
        loadSoundEffect("click", Constants.EFFECT_PATH + "click.wav");

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
     * 효과음 파일을 로드합니다
     */
    private void loadSoundEffect(String name, String filePath) {
        try {
            File soundFile = new File(filePath);
            Clip clip = null;

            if (soundFile.exists()) {
                // 실제 파일이 존재하는 경우
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                clip = AudioSystem.getClip();
                clip.open(audioStream);
            } else {
                // 파일이 없는 경우 프로그래밍적으로 생성
                clip = generateSoundEffect(name);
            }

            if (clip != null) {
                // 볼륨 조절
                setClipVolume(clip, soundVolume);
                soundEffects.put(name, clip);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
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
                return SoundGenerator.generateTone(600, 50, 0.5);
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
                    // MP3 파일 처리를 위한 대체 방법
                    System.out.println("MP3 파일 감지됨. 현재 MP3는 지원되지 않습니다.");
                    System.out.println("WAV 형식으로 변환하시거나 WAV 파일을 사용해주세요.");
                    return;
                }

                // WAV, AU 등 기본 지원 파일은 기존 방식으로 재생
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
                backgroundMusic = AudioSystem.getClip();
                backgroundMusic.open(audioStream);

                // 볼륨 조절
                setClipVolume(backgroundMusic, musicVolume);

                if (musicEnabled) {
                    backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    System.out.println("배경음악 재생 시작");
                }
            } else {
                System.err.println("음악 파일을 찾을 수 없습니다: " + filePath);
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
                soundPlayed = tryPlaySoundEffect("button_hover");
                break;
            case "confirm":
                soundPlayed = tryPlaySoundEffect("confirm");
                break;
            case "cancel":
                soundPlayed = tryPlaySoundEffect("cancel");
                break;
            case "click":
                soundPlayed = tryPlaySoundEffect("click");
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
        playUISound("pause");
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
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }

    }

    /**
     * 배경음악을 재개합니다
     */
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && musicEnabled) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        }

    }

    /**
     * 배경음악을 정지합니다
     */
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.close();
            backgroundMusic = null;
        }

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
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (backgroundMusic != null) {
            setClipVolume(backgroundMusic, this.musicVolume);
        }

    }
}