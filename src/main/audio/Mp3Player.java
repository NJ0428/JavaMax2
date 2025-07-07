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

    public Mp3Player() {
        soundPaths = new HashMap<>();
        enabled = true;
        volume = 0.8f;

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