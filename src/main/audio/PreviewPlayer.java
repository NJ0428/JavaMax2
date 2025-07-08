package main.audio;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 노래 미리듣기 기능을 위한 플레이어 클래스
 * 30초 미리듣기 기능을 제공합니다
 */
public class PreviewPlayer {
    private ExecutorService executor;
    private Future<?> currentPlayback;
    private AtomicBoolean isPlaying;
    private AtomicBoolean isPaused;
    private String currentFilePath;
    private Player currentPlayer;
    private float volume;
    private PreviewListener listener;

    // 미리듣기 설정
    private static final int PREVIEW_DURATION_SECONDS = 30;
    private static final int SKIP_SECONDS = 30; // 곡의 30초 지점부터 재생

    public interface PreviewListener {
        void onPreviewStarted();

        void onPreviewPaused();

        void onPreviewResumed();

        void onPreviewStopped();

        void onPreviewCompleted();

        void onPreviewError(String error);
    }

    public PreviewPlayer() {
        executor = Executors.newSingleThreadExecutor();
        isPlaying = new AtomicBoolean(false);
        isPaused = new AtomicBoolean(false);
        volume = 0.6f; // 미리듣기는 조금 낮은 볼륨으로
        System.out.println("PreviewPlayer 초기화 완료");
    }

    /**
     * 미리듣기 리스너를 설정합니다
     */
    public void setPreviewListener(PreviewListener listener) {
        this.listener = listener;
    }

    /**
     * 노래 미리듣기를 시작합니다
     * 
     * @param filePath 재생할 파일 경로
     */
    public void startPreview(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            System.err.println("미리듣기: 잘못된 파일 경로");
            if (listener != null) {
                listener.onPreviewError("잘못된 파일 경로");
            }
            return;
        }

        // 기존 재생 중지
        stopPreview();

        currentFilePath = filePath;
        isPlaying.set(true);
        isPaused.set(false);

        currentPlayback = executor.submit(() -> {
            FileInputStream fileInputStream = null;
            Player player = null;

            try {
                System.out.println("미리듣기 시작: " + filePath);

                fileInputStream = new FileInputStream(filePath);
                player = new Player(fileInputStream);
                currentPlayer = player;

                if (listener != null) {
                    listener.onPreviewStarted();
                }

                // 곡의 시작 부분을 건너뛰고 중간 부분부터 재생
                // MP3의 경우 프레임 단위로 건너뛰기가 복잡하므로 처음부터 재생

                long startTime = System.currentTimeMillis();
                long maxDuration = PREVIEW_DURATION_SECONDS * 1000; // 30초

                // 재생 시작
                final Player finalPlayer = player; // final 변수로 복사
                Thread playThread = new Thread(() -> {
                    try {
                        finalPlayer.play();
                    } catch (JavaLayerException e) {
                        System.err.println("미리듣기 재생 오류: " + e.getMessage());
                        if (listener != null) {
                            listener.onPreviewError("재생 오류: " + e.getMessage());
                        }
                    }
                });

                playThread.start();

                // 30초 후 또는 재생 완료 시 중지
                while (isPlaying.get() && playThread.isAlive()) {
                    if (System.currentTimeMillis() - startTime >= maxDuration) {
                        System.out.println("미리듣기 30초 완료");
                        break;
                    }

                    // 일시정지 처리
                    while (isPaused.get() && isPlaying.get()) {
                        Thread.sleep(100);
                    }

                    Thread.sleep(100);
                }

                // 재생 중지
                if (player != null) {
                    player.close();
                }

                if (isPlaying.get()) {
                    System.out.println("미리듣기 정상 완료");
                    if (listener != null) {
                        listener.onPreviewCompleted();
                    }
                }

            } catch (IOException e) {
                System.err.println("미리듣기 파일 오류: " + e.getMessage());
                if (listener != null) {
                    listener.onPreviewError("파일 오류: " + e.getMessage());
                }
            } catch (InterruptedException e) {
                System.out.println("미리듣기 중단됨");
                if (listener != null) {
                    listener.onPreviewStopped();
                }
            } catch (Exception e) {
                System.err.println("미리듣기 예상치 못한 오류: " + e.getMessage());
                if (listener != null) {
                    listener.onPreviewError("예상치 못한 오류: " + e.getMessage());
                }
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

                isPlaying.set(false);
                isPaused.set(false);
                currentPlayer = null;
            }
        });
    }

    /**
     * 미리듣기를 일시정지합니다
     */
    public void pausePreview() {
        if (isPlaying.get() && !isPaused.get()) {
            isPaused.set(true);
            System.out.println("미리듣기 일시정지");
            if (listener != null) {
                listener.onPreviewPaused();
            }
        }
    }

    /**
     * 미리듣기를 재개합니다
     */
    public void resumePreview() {
        if (isPlaying.get() && isPaused.get()) {
            isPaused.set(false);
            System.out.println("미리듣기 재개");
            if (listener != null) {
                listener.onPreviewResumed();
            }
        }
    }

    /**
     * 미리듣기를 중지합니다
     */
    public void stopPreview() {
        if (isPlaying.get()) {
            isPlaying.set(false);
            isPaused.set(false);

            if (currentPlayback != null) {
                currentPlayback.cancel(true);
            }

            if (currentPlayer != null) {
                currentPlayer.close();
                currentPlayer = null;
            }

            System.out.println("미리듣기 중지");
            if (listener != null) {
                listener.onPreviewStopped();
            }
        }
    }

    /**
     * 현재 재생 중인지 확인합니다
     */
    public boolean isPlaying() {
        return isPlaying.get();
    }

    /**
     * 현재 일시정지 중인지 확인합니다
     */
    public boolean isPaused() {
        return isPaused.get();
    }

    /**
     * 볼륨을 설정합니다 (0.0 ~ 1.0)
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        System.out.println("미리듣기 볼륨 설정: " + this.volume);
    }

    /**
     * 현재 볼륨을 반환합니다
     */
    public float getVolume() {
        return volume;
    }

    /**
     * 현재 재생 중인 파일 경로를 반환합니다
     */
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    /**
     * 리소스를 정리합니다
     */
    public void cleanup() {
        stopPreview();

        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                if (!executor.awaitTermination(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    System.out.println("PreviewPlayer 스레드 강제 종료");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("PreviewPlayer 리소스 정리 완료");
    }
}