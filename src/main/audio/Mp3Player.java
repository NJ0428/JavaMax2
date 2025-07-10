package main.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Future;

/**
 * MP3 파일 재생을 위한 클래스
 * JLayer 라이브러리를 사용하여 MP3 파일을 재생합니다
 */
public class Mp3Player {
    private Map<String, String> soundPaths;
    private ExecutorService soundExecutor;
    private boolean enabled;
    private float volume;
    private static final int MAX_CONCURRENT_SOUNDS = 5; // 동시 재생 제한

    // 배경음악 관련 필드
    private Player backgroundPlayer;
    private Future<?> backgroundTask;
    private boolean isBackgroundPlaying;
    private boolean isBackgroundPaused;
    private String currentBackgroundMusic;
    private String pausedBackgroundMusic; // 일시정지된 배경음악 경로 저장

    public Mp3Player() {
        soundPaths = new HashMap<>();
        enabled = true;
        volume = 0.8f;
        isBackgroundPlaying = false;
        isBackgroundPaused = false;
        currentBackgroundMusic = null;
        pausedBackgroundMusic = null;

        // 고정 크기 스레드 풀 사용으로 리소스 제한
        soundExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT_SOUNDS);

        System.out.println("MP3Player 초기화 완료 (최대 동시 재생: " + MAX_CONCURRENT_SOUNDS + ")");
    }

    /**
     * MP3 파일 경로를 등록합니다
     */
    public void registerSound(String name, String filePath) {
        soundPaths.put(name, filePath);
        System.out.println("MP3 사운드 등록: " + name + " -> " + filePath);
    }

    /**
     * MP3 파일을 비동기적으로 재생합니다 (효과음용 - 빠른 재생)
     */
    public void playSoundAsync(String name) {
        if (!enabled) {
            return;
        }

        String filePath = soundPaths.get(name);
        if (filePath == null) {
            System.err.println("MP3 사운드를 찾을 수 없음: " + name);
            return;
        }

        // 스레드 풀 크기 확인
        if (soundExecutor instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor tpe = (ThreadPoolExecutor) soundExecutor;
            if (tpe.getActiveCount() >= MAX_CONCURRENT_SOUNDS) {
                System.out.println("MP3 재생 제한 초과, 건너뛰기: " + name);
                return; // 너무 많은 사운드가 동시 재생 중이면 건너뛰기
            }
        }

        // 효과음은 빠르게 재생하고 끝내기
        soundExecutor.submit(() -> {
            FileInputStream fileInputStream = null;
            Player player = null;

            try {
                fileInputStream = new FileInputStream(filePath);
                player = new Player(fileInputStream);

                System.out.println("MP3 효과음 재생: " + name);
                player.play();

            } catch (JavaLayerException | IOException e) {
                System.err.println("MP3 효과음 재생 실패: " + name + " - " + e.getMessage());
            } finally {
                // 리소스 정리
                if (player != null) {
                    player.close();
                }
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        System.err.println("파일 스트림 닫기 실패: " + e.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 배경음악을 재생합니다 (루프 재생)
     */
    public void playGameMusic(String filePath) {
        // 기존 배경음악 정지
        stopGameMusic();

        if (!enabled) {
            return;
        }

        currentBackgroundMusic = filePath;
        isBackgroundPlaying = true;

        backgroundTask = soundExecutor.submit(() -> {
            while (isBackgroundPlaying && !Thread.currentThread().isInterrupted()) {
                FileInputStream fileInputStream = null;

                try {
                    fileInputStream = new FileInputStream(filePath);
                    backgroundPlayer = new Player(fileInputStream);

                    System.out.println("배경음악 재생 시작: " + filePath);
                    backgroundPlayer.play();

                    // 재생이 끝나면 다시 반복 (루프)
                    if (isBackgroundPlaying) {
                        System.out.println("배경음악 루프 재생: " + filePath);
                        Thread.sleep(100); // 짧은 간격
                    }

                } catch (JavaLayerException | IOException e) {
                    System.err.println("배경음악 재생 실패: " + filePath + " - " + e.getMessage());
                    isBackgroundPlaying = false;
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    if (backgroundPlayer != null) {
                        backgroundPlayer.close();
                        backgroundPlayer = null;
                    }
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            System.err.println("파일 스트림 닫기 실패: " + e.getMessage());
                        }
                    }
                }
            }
            System.out.println("배경음악 재생 종료: " + filePath);
        });
    }

    /**
     * 배경음악을 정지합니다
     */
    public void stopGameMusic() {
        isBackgroundPlaying = false;
        isBackgroundPaused = false;

        if (backgroundPlayer != null) {
            backgroundPlayer.close();
            backgroundPlayer = null;
        }

        if (backgroundTask != null && !backgroundTask.isDone()) {
            backgroundTask.cancel(true);
            backgroundTask = null;
        }

        currentBackgroundMusic = null;
        pausedBackgroundMusic = null;
        System.out.println("배경음악 정지");
    }

    /**
     * 배경음악을 일시정지합니다
     */
    public void pauseBackgroundMusic() {
        if (isBackgroundPlaying && !isBackgroundPaused) {
            // 현재 재생 중인 배경음악 경로 저장
            pausedBackgroundMusic = currentBackgroundMusic;
            isBackgroundPaused = true;

            // 배경음악 정지 (MP3는 일시정지 대신 정지 사용)
            isBackgroundPlaying = false;

            if (backgroundPlayer != null) {
                backgroundPlayer.close();
                backgroundPlayer = null;
            }

            if (backgroundTask != null && !backgroundTask.isDone()) {
                backgroundTask.cancel(true);
                backgroundTask = null;
            }

            System.out.println("배경음악 일시정지: " + pausedBackgroundMusic);
        }
    }

    /**
     * 배경음악을 재개합니다
     */
    public void resumeBackgroundMusic() {
        if (isBackgroundPaused && pausedBackgroundMusic != null) {
            System.out.println("배경음악 재개: " + pausedBackgroundMusic);

            // 일시정지된 배경음악 다시 재생
            String musicPath = pausedBackgroundMusic;
            isBackgroundPaused = false;
            pausedBackgroundMusic = null;

            playGameMusic(musicPath);
        }
    }

    /**
     * 배경음악이 재생 중인지 확인합니다
     */
    public boolean isBackgroundMusicPlaying() {
        return isBackgroundPlaying;
    }

    /**
     * 현재 재생 중인 배경음악 경로를 반환합니다
     */
    public String getCurrentBackgroundMusic() {
        return currentBackgroundMusic;
    }

    /**
     * 모든 사운드 재생을 중지합니다
     */
    public void stopAllSounds() {
        if (soundExecutor != null && !soundExecutor.isShutdown()) {
            soundExecutor.shutdownNow();
            soundExecutor = Executors.newFixedThreadPool(MAX_CONCURRENT_SOUNDS);
            System.out.println("모든 MP3 사운드 중지 및 재생기 재시작");
        }
    }

    /**
     * 볼륨을 설정합니다 (0.0 ~ 1.0)
     * 참고: JLayer는 직접적인 볼륨 제어를 지원하지 않음
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        System.out.println("MP3Player 볼륨 설정: " + this.volume + " (참고: JLayer는 볼륨 제어 제한적)");
    }

    /**
     * 사운드 재생 활성화/비활성화
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
        System.out.println("MP3Player 활성화: " + enabled);
    }

    /**
     * 리소스 정리
     */
    public void cleanup() {
        if (soundExecutor != null && !soundExecutor.isShutdown()) {
            soundExecutor.shutdownNow();
            try {
                // 최대 2초 대기
                if (!soundExecutor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.out.println("MP3Player 스레드 강제 종료");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        soundPaths.clear();
        System.out.println("MP3Player 리소스 정리 완료");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public float getVolume() {
        return volume;
    }

    /**
     * 현재 활성 재생 수를 반환합니다
     */
    public int getActivePlayCount() {
        if (soundExecutor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) soundExecutor).getActiveCount();
        }
        return 0;
    }
}