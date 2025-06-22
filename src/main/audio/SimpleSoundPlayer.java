package main.audio;

import java.awt.Toolkit;

/**
 * 간단한 시스템 사운드를 사용하는 대체 사운드 플레이어
 * AudioManager가 제대로 작동하지 않을 때 사용합니다
 */
public class SimpleSoundPlayer {
    private static boolean enabled = true;

    /**
     * 시스템 비프음을 재생합니다
     */
    public static void playBeep() {
        if (enabled) {
            try {
                Toolkit.getDefaultToolkit().beep();
                System.out.println("시스템 비프음 재생");
            } catch (Exception e) {
                System.err.println("비프음 재생 실패: " + e.getMessage());
            }
        }
    }

    /**
     * 사운드 유형에 따라 다른 패턴의 비프음을 재생합니다
     */
    public static void playSound(String soundType) {
        if (!enabled)
            return;

        switch (soundType) {
            case "click":
            case "button_hover":
                playBeep();
                break;

            case "pause":
                playBeep();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                playBeep();
                break;

            case "resume":
                playBeep();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                playBeep();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
                playBeep();
                break;

            case "confirm":
                playBeep();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                playBeep();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
                playBeep();
                break;

            case "cancel":
                for (int i = 0; i < 2; i++) {
                    playBeep();
                    try {
                        Thread.sleep(80);
                    } catch (InterruptedException e) {
                    }
                }
                break;

            default:
                playBeep();
                break;
        }
    }

    /**
     * 백그라운드에서 비프음을 재생합니다 (UI 블로킹 방지)
     */
    public static void playSoundAsync(String soundType) {
        new Thread(() -> playSound(soundType)).start();
    }

    public static void setEnabled(boolean enabled) {
        SimpleSoundPlayer.enabled = enabled;
    }

    public static boolean isEnabled() {
        return enabled;
    }
}